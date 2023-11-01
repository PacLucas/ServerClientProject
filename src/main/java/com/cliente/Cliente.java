package com.cliente;

import com.fasterxml.jackson.databind.JsonNode;
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
                    JOptionPane.showMessageDialog(null, "Login realizado com sucesso!");
                    userInterfaceLogin.listarUsuariosButton.setVisible(true);
                    userInterfaceLogin.editarUsuarioButton.setVisible(true);
                    userInterfaceLogin.excluirUsuarioButton.setVisible(true);
                    break;

                case "logout":
                    this.removeToken();
                    userInterfaceLogin.logoutButton.setVisible(false);
                    userInterfaceLogin.loginButton.setVisible(true);
                    communication.closeConnection();
                    JOptionPane.showMessageDialog(null, "Logout realizado com sucesso!");
                    break;

                case "autocadastro-usuario":
                    JOptionPane.showMessageDialog(null, "Cadastro realizado com sucesso!");
                    userInterfaceLogin.backButton.doClick();
                    break;

                case "cadastro-usuario":
                    JOptionPane.showMessageDialog(null, "Usuário cadastrado com sucesso!");
                    break;

                case "listar-usuarios":
                    break;

                default:
                    break;
            }
        }
    }

    public void updateUsersList(JsonNode usuarios) {
        userInterfaceLogin.updateUsersList((ObjectNode) usuarios);
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
