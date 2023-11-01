package com.servidor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.servidor.database.User;
import io.jsonwebtoken.*;
import com.servidor.database.DatabaseManager;

import java.io.*;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private static final String secretKey = "AoT3QFTTEkj16rCby/TPVBWvfSQHL3GeEz3zVwEd6LDrQDT97sgDY8HJyxgnH79jupBWFOQ1+7fRPBLZfpuA2lwwHqTgk+NJcWQnDpHn31CVm63Or5c5gb4H7/eSIdd+7hf3v+0a5qVsnyxkHbcxXquqk9ezxrUe93cFppxH4/kF/kGBBamm3kuUVbdBUY39c4U3NRkzSO+XdGs69ssK5SPzshn01axCJoNXqqj+ytebuMwF8oI9+ZDqj/XsQ1CLnChbsL+HCl68ioTeoYU9PLrO4on+rNHGPI0Cx6HrVse7M3WQBPGzOd1TvRh9eWJrvQrP/hm6kOR7KrWKuyJzrQh7OoDxrweXFH8toXeQRD8=";
    private final Server server;
    private boolean shouldRun = true;
    private static DatabaseManager dbManager = null;
    private boolean logoutSuccessful = false; // Variável de controle



    public ClientHandler(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;
        dbManager = new DatabaseManager(this.server.getConnection());
    }

    @Override
    public void run() {
        try (
                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                PrintWriter writer = new PrintWriter(outputStream, true);
        ) {
            String inputLine;
            while (isRunning() && (inputLine = reader.readLine()) != null) {
                processRequest(inputLine, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            server.removeClient(this);
            server.connectionListArea.append("Conexão finalizada: " + clientSocket.getInetAddress().getHostAddress() + "\n");
        }
    }

    private void processRequest(String inputLine, PrintWriter writer) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(inputLine);
        System.out.println("Servidor: Recebido -> " + jsonNode.toString());
        String action = jsonNode.get("action").asText();

        try {
            if (jsonNode.has("action")) {
                JsonNode data = jsonNode.get("data");
                ObjectNode responseData = null;
                boolean error = true;
                String message = "";

                switch (action) {
                    case "login":
                        String password = data.get("password").asText();
                        String loginEmail = data.get("email").asText();
                        ResultSet currentUser = isValidLogin(loginEmail, password);

                        if (currentUser != null) {
                            message = "Logado com sucesso";
                            error = false;

                            String token = generateJWT(currentUser.getString("id"), currentUser.getString("tipo").equals("admin"));

                            responseData = mapper.createObjectNode().put("token", token);
                        } else {
                            message = "Credenciais inválidas. Tente novamente.";
                        }

                        break;

                    case "logout":
                        String tokenLogout = (data.has("token") && !data.get("token").isNull()) ? data.get("token").asText() : "";

                        if (!Objects.equals(tokenLogout, "") && isValidUser(tokenLogout)) {
                            error = false;
                            message = "Usuário deslogado com sucesso!";
                            logoutSuccessful = true;
                        } else {
                            message = "Usuário não está logado.";
                        }
                        break;

                    case "cadastro-usuario":
                    case "autocadastro-usuario":
                        String cadastroEmail = data.get("email").asText();
                        String senha = data.get("password").asText();
                        String nome = data.get("name").asText();
                        String tipo = (data.has("type") && !data.get("type").isNull()) ? data.get("type").asText() : "user";
                        String token = (data.has("token") && !data.get("token").isNull()) ? data.get("token").asText() : "";

                        if (action == "cadastro-usuario" || tipo == "admin" || data.has("token")) {
                            try {
                                if (!isAdmin(token)) {
                                    message = "Apenas administradores podem cadastrar novos usuários.";
                                    break;
                                }
                            } catch (JwtException e) {
                                message = "Token inválido.";
                                break;
                            }
                        }

                        if (!isEmailValid(cadastroEmail)) {
                            message = "Email inválido.";
                        } else if (dbManager.emailJaExiste(cadastroEmail)) {
                            message = "Email já cadastrado.";
                        } else {
                            if (dbManager.inserirUsuario(nome, cadastroEmail, senha, tipo)) {
                                error = false;
                                message = "Usuário cadastrado com sucesso!";
                            } else {
                                message = "Erro ao cadastrar o usuário.";
                            }
                        }
                        break;
                    case "edicao-usuario":
                    case "autoeditcao-usuario":
                        String tokenEditarUsuario = (data.has("token") && !data.get("token").isNull()) ? data.get("token").asText() : "";
                        String edicaoEmail = data.get("email").asText();
                        String edicaoSenha = data.get("password").asText();
                        String edicaoNome = data.get("name").asText();
                        int edicaoId = data.get("user_id").asInt();
                        String edicaoTipo = (data.has("type") && !data.get("type").isNull()) ? data.get("type").asText() : "user";

                        if (!Objects.equals(tokenEditarUsuario, "") && isValidUser(tokenEditarUsuario)) {
                            boolean result = dbManager.editarUsuario(edicaoId, edicaoNome, edicaoEmail, edicaoTipo, edicaoSenha);

                            if(result) {
                                message = "Usuario editado com sucesso";
                            } else {
                                message = "Nenhum usuário encontrado.";
                            }
                        } else {
                            message = "Usuário não está logado ou token inválido.";
                        }
                        break;

                    case "listar-usuarios":
                        String tokenListarUsuarios = (data.has("token") && !data.get("token").isNull()) ? data.get("token").asText() : "";

                        if (!Objects.equals(tokenListarUsuarios, "") && isValidUser(tokenListarUsuarios)) {
                            List<User> users = dbManager.listarUsuarios();

                            if (!users.isEmpty()) {
                                responseData = mapper.createObjectNode();
                                ArrayNode usersArray = responseData.putArray("users");

                                for (User user : users) {
                                    ObjectNode userNode = mapper.createObjectNode();
                                    userNode.put("id", user.getId());
                                    userNode.put("name", user.getNome());
                                    userNode.put("type", user.getTipo());
                                    userNode.put("email", user.getEmail());
                                    usersArray.add(userNode);
                                }
                            } else {
                                message = "Nenhum usuário encontrado.";
                            }
                        } else {
                            message = "Usuário não está logado ou token inválido.";
                        }
                        break;

                    case "excluir-proprio-usuario":
                    case "excluir-usuario":
                        String tokenExcluirUsuario = (data.has("token") && !data.get("token").isNull()) ? data.get("token").asText() : "";

                        if (!Objects.equals(tokenExcluirUsuario, "") && isValidUser(tokenExcluirUsuario)) {
                            boolean result = dbManager.excluirUsuario(data.get("user_id").asInt());

                            if(result) {
                                message = "Usuario excluido com sucesso";
                            } else {
                                message = "Nenhum usuário encontrado.";
                            }
                        } else {
                            message = "Usuário não está logado ou token inválido.";
                        }
                        break;

                    default:
                        message = "Ação '" + action + "' desconhecida";
                        break;
                }

                sendResponse(writer, error, message, action, responseData);
            }
        } catch (SQLException e) {
            sendResponse(writer, true, "Erro durante o processamento na base de dados", action, null);
            e.printStackTrace();
        }
    }

    private static Jws<Claims> parseToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
    }

    public static boolean isAdmin(String token) {
        Jws<Claims> parsedToken = parseToken(token);

        return parsedToken.getBody().get("admin", Boolean.class);
    }

    public static boolean isValidUser(String token) {
        Jws<Claims> parsedToken = parseToken(token);
        String userId = parsedToken.getBody().get("user_id", String.class);

        return dbManager.verificarUsuarioValido(userId);
    }

    private void sendResponse(PrintWriter writer, boolean error, String message, String action, ObjectNode data) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode responseJson = mapper.createObjectNode();
        responseJson.put("action", action);
        responseJson.put("error", error);
        responseJson.put("message", message);

        if (data != null) {
            responseJson.set("data", data);
        }

        System.out.println("Servidor: Enviado -> " + responseJson.toString());
        try {
            writer.println(responseJson.toString());
        } catch (Exception e) {
            System.out.println("Servidor: ERRO AO ENVIAR RESPONSE");
            e.printStackTrace();
        }

    }

    public void stop() {
        shouldRun = false;
    }

    public boolean isRunning() {
        return shouldRun;
    }

    private String generateJWT(String userId, boolean isAdm) {
        return Jwts.builder()
                .claim("user_id", userId)
                .claim("admin", isAdm)
                .setSubject(userId)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    private boolean isEmailValid(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }


    private ResultSet isValidLogin(String email, String password) {
        return dbManager.verificarCredenciais(email, password);
    }
}
