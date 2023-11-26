package com.cliente;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.swing.*;

public class Cliente {
    private String token = "";
    private UserInterfaceLogin userInterfaceLogin;
    private UserInterfaceMain userInterfaceMain;
    private ServidorCommunication servidorCommunication;

    public Cliente() {
        userInterfaceLogin = new UserInterfaceLogin(this);
        userInterfaceMain = new UserInterfaceMain(this);
    }

    public void start() {
        userInterfaceLogin.show();
    }

    private ServidorCommunication getServidorCommunication() {
        if (servidorCommunication == null || !servidorCommunication.isConnected()) {
            servidorCommunication = new ServidorCommunication(this, getServerIP(), getServerPort());
        }
        return servidorCommunication;
    }

    public void sendRequestToServer(String json, String action) {
        ServidorCommunication communication = getServidorCommunication();
        if (communication.sendRequest(json, action)) {
            switch (action) {
                case "login":
                    if (UserInterfaceLogin.isAdmin(getToken())) {
                        userInterfaceLogin.mudarTela("cadastro");
                        userInterfaceMain.show();
                    } else {
                        userInterfaceLogin.mudarTela("listar");
                        userInterfaceLogin.listarUsuariosButton.doClick();
                    }
                    JOptionPane.showMessageDialog(null, "Login realizado com sucesso!");
                    break;

                case "logout":
                    this.removeToken();
                    communication.closeConnection();
                    userInterfaceLogin.mudarTela("login");
                    JOptionPane.showMessageDialog(null, "Logout realizado com sucesso!");
                    break;

                case "autocadastro-usuario":
                    JOptionPane.showMessageDialog(null, "Cadastro realizado com sucesso!");
                    break;

                case "cadastro-usuario":
                    JOptionPane.showMessageDialog(null, "Usuário cadastrado com sucesso!");
                    break;

                case "pedido-proprio-usuario":
                    userInterfaceLogin.mudarTela("listar-usuarios");
                    break;

                case "listar-usuarios":
                    userInterfaceLogin.mudarTela("listar-usuarios");
                    break;

                case "autoedicao-usuario":
                case "edicao-usuario":
                    JOptionPane.showMessageDialog(null, "Usuário editado com sucesso!");
                    break;

                case "excluir-usuario":
                    JOptionPane.showMessageDialog(null, "Usuário excluído com sucesso!");
                    break;

                case "excluir-proprio-usuario":
                    this.removeToken();
                    communication.closeConnection();
                    userInterfaceLogin.mudarTela("login");
                    JOptionPane.showMessageDialog(null, "Usuário excluído com sucesso!");
                    break;

                case "cadastro-ponto":
                    JOptionPane.showMessageDialog(null, "Ponto cadastrado com sucesso!");
                    break;

                case "listar-pontos":
                    break;

                case "edicao-ponto":
                    JOptionPane.showMessageDialog(null, "Ponto editado com sucesso!");
                    break;

                case "pedido-edicao-ponto":
                    break;

                case "excluir-ponto":
                    JOptionPane.showMessageDialog(null, "Ponto excluído com sucesso!");
                    break;

                case "cadastro-segmento":
                    JOptionPane.showMessageDialog(null, "Segmento cadastrado com sucesso!");
                    break;

                case "listar-segmentos":
                    break;

                case "edicao-segmento":
                    JOptionPane.showMessageDialog(null, "Segmento editado com sucesso!");
                    break;

                case "pedido-edicao-segmento":
                    break;

                case "excluir-segmento":
                    JOptionPane.showMessageDialog(null, "Segmento excluído com sucesso!");
                    break;

                default:
                    JOptionPane.showMessageDialog(null, "Ação não reconhecida");
                    break;
            }
        }
    }

    public void updateUsersList(JsonNode usuarios) {
        userInterfaceLogin.updateUsersList((ArrayNode) usuarios);
    }

    public void updatePontosList(JsonNode pontos) {
        userInterfaceMain.updatePontosList((ArrayNode) pontos);
    }
    public void updateSegmentosList(JsonNode segmentos) {
        userInterfaceMain.updateSegmentosList((ArrayNode) segmentos);
    }

    public void updatePonto(JsonNode ponto) {
        userInterfaceMain.updatePonto(ponto);
    }

    public String getServerIP() {
        return userInterfaceLogin.getServerIP();
    }

    public int getServerPort() {
        return userInterfaceLogin.getServerPort();
    }

    public String getToken() {
        return this.token;
    }

    public void removeToken() {
        this.token = "";
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Cliente cliente = new Cliente();
                cliente.start();
            }
        });
    }
}
