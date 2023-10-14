package com.servidor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.*;
import com.servidor.database.DatabaseManager;

import java.io.*;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private static final String secretKey = "TOKEN_SECRET_KEY";
    private final Server server;
    private boolean shouldRun = true;
    private DatabaseManager dbManager;


    public ClientHandler(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.dbManager = new DatabaseManager(this.server.getConnection());
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
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                        // Ação de login
                        String password = data.get("senha").asText();
                        String loginEmail = data.get("email").asText();
                        ResultSet user = isValidLogin(loginEmail, password);

                        if (user != null) {
                            message = "Logado com sucesso";
                            error = false;

                            // Gere o token JWT usando o ID do usuário (neste exemplo, use "1")
                            String token = generateJWT(user.getString("id"), user.getString("tipo").equals("admin"));

                            // Incluir o token na resposta
                            responseData = mapper.createObjectNode().put("token", token);
                        } else {
                            message = "Credenciais inválidas. Tente novamente.";
                        }

                        break;

                    case "cadastro-usuario":
                    case "autocadastro-usuario":
                        String cadastroEmail = data.get("email").asText();
                        String senha = data.get("senha").asText();
                        String nome = data.get("nome").asText();
                        String tipo = (data.has("tipo") && !data.get("tipo").isNull()) ? data.get("tipo").asText() : "user";
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

                        // Validar o email
                        if (!isEmailValid(cadastroEmail)) {
                            message = "Email inválido.";
                        } else if (dbManager.emailJaExiste(cadastroEmail)) {
                            message = "Email já cadastrado.";
                        } else {
                            // Inserir os dados do novo usuário na base de dados
                            if (dbManager.inserirUsuario(nome, cadastroEmail, senha, tipo)) {
                                error = false;
                                message = "Usuário cadastrado com sucesso!";
                            } else {
                                message = "Erro ao cadastrar o usuário.";
                            }
                        }
                        break;

                    // Validar o email


                    default:
                        // Ação desconhecida
                        message = "Ação '" + action + "' desconhecida";
                        sendResponse(writer, error, message, action, null);
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

    private void sendResponse(PrintWriter writer, boolean error, String message, String action, ObjectNode data) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode responseJson = mapper.createObjectNode();
        responseJson.put("action", action);
        responseJson.put("error", error);
        responseJson.put("message", message);

        if (data != null) {
            responseJson.set("data", data);
        }

        writer.println(responseJson.toString());
        System.out.println("Servidor: Enviado -> " + responseJson.toString());
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
