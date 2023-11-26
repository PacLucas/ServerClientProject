package com.servidor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.servidor.database.Pontos;
import com.servidor.database.Segmentos;
import com.servidor.database.User;
import io.jsonwebtoken.*;
import com.servidor.database.DatabaseManager;

import java.io.*;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
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
        String action = jsonNode.get("action").asText();
        System.out.println("Servidor: Recebido -> " + jsonNode.toString());

        try {
            if (jsonNode.has("action")) {
                JsonNode data = jsonNode.get("data");
                ObjectNode responseData = null;
                boolean error = true;
                String message = "";

                switch (action) {
// ------------------------------------------------------------------------------ LOGIN ------------------------------------------------------------------------------------------------------------------ //
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

// ------------------------------------------------------------------------------ USUÁRIO ------------------------------------------------------------------------------------------------------------------ //

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
                    case "autoedicao-usuario":
                        String tokenEditarUsuario = (data.has("token") && !data.get("token").isNull()) ? data.get("token").asText() : "";
                        String edicaoEmail = data.get("email").asText();
                        String edicaoSenha = (data.has("password") && !data.get("password").isNull()) ? data.get("password").asText() : null;
                        String edicaoNome = data.get("name").asText();
                        int edicaoId = isAdmin(tokenEditarUsuario) ? data.get("user_id").asInt() : data.get("id").asInt();
                        String edicaoTipo = (data.has("type") && !data.get("type").isNull()) ? data.get("type").asText() : "user";

                        if (!Objects.equals(tokenEditarUsuario, "") && isValidUser(tokenEditarUsuario)) {
                            boolean result = dbManager.editarUsuario(edicaoId, edicaoNome, edicaoEmail, edicaoTipo, edicaoSenha);

                            if(result) {
                                error = false;
                                message = "Usuario editado com sucesso";
                            } else {
                                message = "Nenhum usuário encontrado.";
                            }
                        } else {
                            message = "Usuário não está logado ou token inválido.";
                        }
                        break;

                    case "pedido-proprio-usuario":
                        String tokenListarUsuarios = (data.has("token") && !data.get("token").isNull()) ? data.get("token").asText() : "";

                        if (!Objects.equals(tokenListarUsuarios, "") && isValidUser(tokenListarUsuarios)) {
                            User user = dbManager.encontrarUsuarioPorId(getTokenUserId(tokenListarUsuarios));

                            if (user != null) {
                                responseData = mapper.createObjectNode();
                                ArrayNode usersArray = responseData.putArray("user");

                                ObjectNode userNode = mapper.createObjectNode();
                                userNode.put("id", user.getId());
                                userNode.put("name", user.getNome());
                                userNode.put("type", user.getTipo());
                                userNode.put("email", user.getEmail());
                                usersArray.add(userNode);
                                error = false;
                                message = "Sucesso";
                            } else {
                                message = "Nenhum usuário encontrado.";
                            }
                        } else {
                            message = "Usuário não está logado ou token inválido.";
                        }
                        break;

                    case "listar-usuarios":
                        String tokenPedidoUsuario = (data.has("token") && !data.get("token").isNull()) ? data.get("token").asText() : "";

                        if (!Objects.equals(tokenPedidoUsuario, "") && isValidUser(tokenPedidoUsuario)) {
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
                                error = false;
                                message = "Sucesso";
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
                            int userId = 0;

                            if (isAdmin(tokenExcluirUsuario)) {
                                userId = data.get("user_id").asInt();
                            } else {
                                userId = dbManager.encontrarUsuarioPorEmailESenha(data.get("email").asText(), data.get("password").asText()).getId();
                            }

                            boolean result = dbManager.excluirUsuario(userId);
                            if(result) {
                                error = false;
                                message = "Usuario excluido com sucesso";
                            } else {
                                message = "Nenhum usuário encontrado.";
                            }
                        } else {
                            message = "Usuário não está logado ou token inválido.";
                        }
                        break;

// ------------------------------------------------------------------------------ PONTOS ------------------------------------------------------------------------------------------------------------------ //
                    case "cadastro-ponto":
                        String tokenCadastrarPonto = (data.has("token") && !data.get("token").isNull()) ? data.get("token").asText() : "";

                        if (!Objects.equals(tokenCadastrarPonto, "") && isValidUser(tokenCadastrarPonto) && isAdmin(tokenCadastrarPonto)) {
                            String criacaoPontoNome = data.get("name").asText();
                            String criacaoPontoObs = data.get("obs").asText();

                            boolean result = dbManager.inserirPonto(criacaoPontoNome, criacaoPontoObs);
                            if(result) {
                                error = false;
                                message = "Ponto cadastrado com sucesso";
                            } else {
                                message = "Erro ao cadastrar ponto.";
                            }
                        } else {
                            message = "Usuário não está logado ou token inválido.";
                        }
                        break;

                    case "pedido-edicao-ponto":
                        String tokenPedidoEdicaoPonto = (data.has("token") && !data.get("token").isNull()) ? data.get("token").asText() : "";

                        if (!Objects.equals(tokenPedidoEdicaoPonto, "") && isValidUser(tokenPedidoEdicaoPonto) && isAdmin(tokenPedidoEdicaoPonto)) {
                            Integer pedidoEdicaoPontoId = data.get("ponto_id").asInt();

                            Pontos ponto = dbManager.encontrarPontoPorId(pedidoEdicaoPontoId);
                            if (ponto != null) {
                                responseData = mapper.createObjectNode();
                                ArrayNode usersArray = responseData.putArray("ponto");

                                ObjectNode pontoNode = mapper.createObjectNode();
                                pontoNode.put("id", ponto.getId());
                                pontoNode.put("name", ponto.getNome());
                                pontoNode.put("obs", ponto.getObs());
                                usersArray.add(pontoNode);
                                error = false;
                                message = "Sucesso";
                            } else {
                                message = "Nenhum ponto encontrado.";
                            }
                        } else {
                            message = "Usuário não está logado ou token inválido.";
                        }
                        break;

                    case "edicao-ponto":
                        String tokenEditarPonto = (data.has("token") && !data.get("token").isNull()) ? data.get("token").asText() : "";
                        String edicaoPontoNome = data.get("name").asText();
                        String edicaoPontoObs = data.get("obs").asText();
                        int edicaoPontoId = data.get("ponto_id").asInt();

                        if (!Objects.equals(tokenEditarPonto, "") && isValidUser(tokenEditarPonto)) {
                            boolean result = dbManager.editarPonto(edicaoPontoId, edicaoPontoNome, edicaoPontoObs);

                            if (result) {
                                error = false;
                                message = "Ponto editado com sucesso";
                            } else {
                                message = "Nenhum usuário encontrado.";
                            }
                        } else {
                            message = "Usuário não está logado ou token inválido.";
                        }
                        break;

                    case "listar-pontos":
                        String tokenListarPonto = (data.has("token") && !data.get("token").isNull()) ? data.get("token").asText() : "";

                        if (!Objects.equals(tokenListarPonto, "") && isValidUser(tokenListarPonto) && isAdmin(tokenListarPonto)) {
                            List<Pontos> pontos = dbManager.listarPontos();

                            if (!pontos.isEmpty()) {
                                responseData = mapper.createObjectNode();
                                ArrayNode usersArray = responseData.putArray("pontos");

                                for (Pontos ponto : pontos) {
                                    ObjectNode pontoNode = mapper.createObjectNode();
                                    pontoNode.put("id", ponto.getId());
                                    pontoNode.put("name", ponto.getNome());
                                    pontoNode.put("obs", ponto.getObs());
                                    usersArray.add(pontoNode);
                                }
                                error = false;
                                message = "Sucesso";
                            } else {
                                message = "Nenhum ponto encontrado.";
                            }
                        } else {
                            message = "Usuário não está logado ou token inválido.";
                        }
                        break;

                    case "excluir-ponto":
                        String tokenExcluirPonto = (data.has("token") && !data.get("token").isNull()) ? data.get("token").asText() : "";

                        if (!Objects.equals(tokenExcluirPonto, "") && isValidUser(tokenExcluirPonto) && isAdmin(tokenExcluirPonto)) {
                            int pontoId = 0;

                            pontoId = data.get("ponto_id").asInt();

                            dbManager.excluirSegmentosPorPonto(pontoId);

                            boolean result = dbManager.excluirPonto(pontoId);
                            if(result) {
                                error = false;
                                message = "Ponto excluido com sucesso";
                            } else {
                                message = "Nenhum ponto encontrado.";
                            }
                        } else {
                            message = "Usuário não está logado ou token inválido.";
                        }
                        break;

// ------------------------------------------------------------------------------ SEGMENTOS ------------------------------------------------------------------------------------------------------------------ //

                    case "cadastro-segmento":
                        String tokenCadastrarSegmento = (data.has("token") && !data.get("token").isNull()) ? data.get("token").asText() : "";

                        if (!Objects.equals(tokenCadastrarSegmento, "") && isValidUser(tokenCadastrarSegmento) && isAdmin(tokenCadastrarSegmento)) {
                            Integer pontoOrigemId = data.get("ponto_origem").get("id").asInt();
                            Integer pontoDestinoId = data.get("ponto_destino").get("id").asInt();
                            String direcao = data.get("direcao").asText();
                            Integer distancia = data.get("distancia").asInt();
                            String obs = data.get("obs").asText();

                            Pontos pontoOrigem = dbManager.encontrarPontoPorId(pontoOrigemId);
                            Pontos pontoDestino = dbManager.encontrarPontoPorId(pontoDestinoId);

                            if (pontoOrigem != null || pontoDestino != null) {
                                boolean result = dbManager.inserirSegmento(pontoOrigemId, pontoDestinoId, direcao, distancia, obs);
                                if(result) {
                                    error = false;
                                    message = "Segmento cadastrado com sucesso";
                                } else {
                                    message = "Erro ao cadastrar segmento.";
                                }
                            } else {
                                message = "Pontos nao encontrados.";
                            }

                        } else {
                            message = "Usuário não está logado ou token inválido.";
                        }
                        break;

                    case "edicao-segmento":
                        String tokenEditarSegmento = (data.has("token") && !data.get("token").isNull()) ? data.get("token").asText() : "";

                        if (!Objects.equals(tokenEditarSegmento, "") && isValidUser(tokenEditarSegmento) && isAdmin(tokenEditarSegmento)) {
                            Integer pontoOrigemIdEdicao = data.get("ponto_origem").get("id").asInt();
                            Integer pontoDestinoIdEdicao = data.get("ponto_destino").get("id").asInt();
                            String direcaoEdicao = data.get("direcao").asText();
                            Integer distanciaEdicao = data.get("distancia").asInt();
                            String obsEdicao = data.get("obs").asText();
                            Integer segmentoEdicaoId = data.get("segmento_id").asInt();

                            Pontos pontoOrigem = dbManager.encontrarPontoPorId(pontoOrigemIdEdicao);
                            Pontos pontoDestino = dbManager.encontrarPontoPorId(pontoDestinoIdEdicao);

                            if (pontoOrigem != null || pontoDestino != null) {
                                boolean result = dbManager.editarSegmento(segmentoEdicaoId, pontoOrigemIdEdicao, pontoDestinoIdEdicao, direcaoEdicao, distanciaEdicao, obsEdicao);
                                if(result) {
                                    error = false;
                                    message = "Segmento editado com sucesso";
                                } else {
                                    message = "Erro ao editar segmento.";
                                }
                            } else {
                                message = "Pontos nao encontrados.";
                            }

                        } else {
                            message = "Usuário não está logado ou token inválido.";
                        }
                        break;

                    case "pedido-edicao-segmento":
                        String tokenPedidoEdicaoSegmento = (data.has("token") && !data.get("token").isNull()) ? data.get("token").asText() : "";

                        if (!Objects.equals(tokenPedidoEdicaoSegmento, "") && isValidUser(tokenPedidoEdicaoSegmento) && isAdmin(tokenPedidoEdicaoSegmento)) {
                            String pedidoEdicaoSegmentoId = data.get("segmento_id").asText();

                            Segmentos segmento = dbManager.encontrarSegmentoPorId(pedidoEdicaoSegmentoId);
                            if (segmento != null) {
                                responseData = mapper.createObjectNode();
                                ArrayNode segmentosArray = responseData.putArray("segmento");
                                Integer pontoOrigem = segmento.getPonto_origem();
                                Integer pontoDestino = segmento.getPonto_destino();

                                ObjectMapper segmentoMapper = new ObjectMapper();
                                ObjectNode segmentoNode = segmentoMapper.createObjectNode();
                                segmentoNode.put("id", segmento.getId());

                                Pontos pontoOrigemPedido = dbManager.encontrarPontoPorId(pontoOrigem);
                                Pontos pontoDestinoPedido = dbManager.encontrarPontoPorId(pontoDestino);

                                ObjectNode ponto_origem = segmentoNode.putObject("ponto_origem");
                                ponto_origem.put("id", pontoOrigemPedido.getId());
                                ponto_origem.put("name", pontoOrigemPedido.getNome());
                                ponto_origem.put("obs", pontoOrigemPedido.getObs());

                                ObjectNode ponto_destino = segmentoNode.putObject("ponto_destino");
                                ponto_destino.put("id", pontoDestinoPedido.getId());
                                ponto_destino.put("name", pontoDestinoPedido.getNome());
                                ponto_destino.put("obs", pontoDestinoPedido.getObs());

                                segmentoNode.put("direcao", segmento.getDirecao());
                                segmentoNode.put("distancia", segmento.getDistancia());
                                segmentoNode.put("obs", segmento.getObs());
                                segmentoNode.put("obs", segmento.getObs());
                                segmentosArray.add(segmentoNode);
                                error = false;
                                message = "Sucesso";
                            } else {
                                message = "Nenhum segmento encontrado.";
                            }
                        } else {
                            message = "Usuário não está logado ou token inválido.";
                        }
                        break;

                    case "listar-segmentos":
                        String tokenListarSegmento = (data.has("token") && !data.get("token").isNull()) ? data.get("token").asText() : "";

                        if (!Objects.equals(tokenListarSegmento, "") && isValidUser(tokenListarSegmento) && isAdmin(tokenListarSegmento)) {
                            List<Segmentos> segmentos = dbManager.listarSegmentos();
                            ObjectMapper mapperSegmentos = new ObjectMapper();
                            responseData = mapper.createObjectNode();
                            ArrayNode segmentosArray =  responseData.putArray("segmentos");
                            if (!segmentos.isEmpty()) {
                                for (Segmentos segmento : segmentos) {
                                    ObjectNode segmentoNode = mapperSegmentos.createObjectNode();
                                    segmentoNode.put("id", segmento.getId());
                                    segmentoNode.put("direcao", segmento.getDirecao());
                                    segmentoNode.put("distancia", segmento.getDistancia());
                                    segmentoNode.put("obs", segmento.getObs());

                                    Pontos pontoOrigem = dbManager.encontrarPontoPorId(segmento.getPonto_origem());
                                    Pontos pontoDestino = dbManager.encontrarPontoPorId(segmento.getPonto_destino());

                                    ObjectNode ponto_origem = segmentoNode.putObject("ponto_origem");
                                    ponto_origem.put("id", pontoOrigem.getId());
                                    ponto_origem.put("name", pontoOrigem.getNome());
                                    ponto_origem.put("obs", pontoOrigem.getObs());

                                    ObjectNode ponto_destino = segmentoNode.putObject("ponto_destino");
                                    ponto_destino.put("id", pontoDestino.getId());
                                    ponto_destino.put("name", pontoDestino.getNome());
                                    ponto_destino.put("obs", pontoDestino.getObs());

                                    segmentosArray.add(segmentoNode);
                                }

                                error = false;
                                message = "Sucesso";
                            } else {
                                message = "Nenhum segmento encontrado.";
                            }
                        } else {
                            message = "Usuário não está logado ou token inválido.";
                        }
                        break;

                    case "excluir-segmento":
                        String tokenExcluirSegmento = (data.has("token") && !data.get("token").isNull()) ? data.get("token").asText() : "";

                        if (!Objects.equals(tokenExcluirSegmento, "") && isValidUser(tokenExcluirSegmento) && isAdmin(tokenExcluirSegmento)) {
                            int segmentoId = 0;

                            segmentoId = data.get("segmento_id").asInt();

                            boolean result = dbManager.excluirSegmento(segmentoId);
                            if(result) {
                                error = false;
                                message = "Segmento excluido com sucesso";
                            } else {
                                message = "Nenhum segmento encontrado.";
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
        } catch (Exception e) {
            sendResponse(writer, true, "Ocorreu um erro ao ler os dados JSON.", action, null);
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

    public static String getTokenUserId(String token) {
        Jws<Claims> parsedToken = parseToken(token);

        return parsedToken.getBody().get("user_id", String.class);
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
