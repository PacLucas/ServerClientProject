package com.cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.swing.*;

public class ServidorCommunication {
    private String serverIP;
    private int serverPort;
    private Cliente cliente;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    public ServidorCommunication(Cliente cliente, String serverIP, int serverPort) {
        this.cliente = cliente;
        this.serverIP = serverIP;
        this.serverPort = serverPort;

        try {
            socket = new Socket(serverIP, serverPort);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro: Não foi possível conectar ao servidor");
        }
    }

    public boolean sendRequest(String json, String action) {
        boolean response = false;
        try {
            writer.println(json);
            System.out.println("Cliente: Enviado -> " + json);

            String serverResponse = reader.readLine();
            System.out.println("Cliente: Recebido -> " + serverResponse);

            response = processServerResponse(serverResponse, action);
        } catch (SocketTimeoutException ste) {
            response = false;
            JOptionPane.showMessageDialog(null, "Erro: Server timeout");
        } catch (IOException e) {
            response = false;
            JOptionPane.showMessageDialog(null, "Erro: Não foi possível conectar ao servidor");
            e.printStackTrace();
        }
        return response;
    }

    private boolean processServerResponse(String serverResponse, String action) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode responseJson = mapper.readValue(serverResponse, ObjectNode.class);

            if (responseJson.has("action") && responseJson.get("action").asText().equals(action)) {
                if (responseJson.has("error") && !responseJson.get("error").asBoolean()) {
                    if (responseJson.get("action").asText().equals("login")) {
                        if (responseJson.has("data") && responseJson.get("data").has("token")) {
                            this.cliente.setToken(responseJson.get("data").get("token").asText());
                        } else {
                            JOptionPane.showMessageDialog(null, "Resposta do servidor inválida: Token ausente.");
                        }
                    }
                    if (responseJson.get("action").asText().equals("listar-usuarios") || responseJson.get("action").asText().equals("pedido-proprio-usuario")) {
                        if (responseJson.has("data") && responseJson.get("data").has("users")) {
                            this.cliente.updateUsersList(responseJson.get("data").get("users"));
                        } else if (responseJson.has("data") && responseJson.get("data").has("user")) {
                            this.cliente.updateUsersList(responseJson.get("data").get("user"));
                        } else {
                            JOptionPane.showMessageDialog(null, "Resposta do servidor inválida: Lista de usuários ausente.");
                        }
                    }

                    return true;
                } else {
                    if (responseJson.has("message")) {
                        String errorMessage = responseJson.get("message").asText();
                        JOptionPane.showMessageDialog(null, errorMessage);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Resposta do servidor inválida: Ação " + action + " inválida para a requisição");
            }
            return false;
        } catch (JsonProcessingException ex) {
            JOptionPane.showMessageDialog(null, "Erro: " + ex.getMessage());
            ex.printStackTrace();
        }
        return false;
    }

    public boolean isConnected() {
        return socket != null && !socket.isClosed();
    }

    public void closeConnection() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
