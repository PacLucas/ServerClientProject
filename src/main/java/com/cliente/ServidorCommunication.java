package com.cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

    public ServidorCommunication(Cliente cliente, String serverIP, int serverPort) {
        this.cliente = cliente;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    public boolean sendRequest(String json, String action) {
        boolean response = false;
        try {
            Socket socket = new Socket(serverIP, serverPort);

            socket.setSoTimeout(5000);

            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println(json);
            System.out.println("Cliente: Enviado -> " + json);

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String serverResponse = reader.readLine();
            System.out.println("Cliente: Recebido -> " + serverResponse);

            response = processServerResponse(serverResponse, action);

            socket.close();
        } catch (SocketTimeoutException ste) {
            response = false;
            JOptionPane.showMessageDialog(null, "Erro: Server timeout");
        } catch (IOException e) {
            response = false;
            JOptionPane.showMessageDialog(null, "Erro: Não foi possivel conectar ao servidor");
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
}
