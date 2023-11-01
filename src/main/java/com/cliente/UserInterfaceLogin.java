package com.cliente;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.apache.commons.codec.digest.DigestUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import java.io.IOException;
import java.util.Objects;

public class UserInterfaceLogin {
    private JFrame frame;
    private JTextField serverIpField;
    private JTextField serverPortField;
    private JTextField emailField;
    private JPasswordField passwordField;
    JButton loginButton;
    private JButton cadastroButton;
    public JButton backButton;
    private JTextField nameField;
    private JTextField idField;
    private JLabel nameLabel;
    private JLabel tipoComboBoxLabel;
    private JPanel loginPanel;
    JButton logoutButton;
    private JPanel buttonPanel;
    public Cliente cliente;
    private String[] tipos = { "user", "admin" };
    JButton listarUsuariosButton;
    JButton editarUsuarioButton;
    JButton excluirUsuarioButton;
    private JList<String> userList;

    private static final String secretKey = "AoT3QFTTEkj16rCby/TPVBWvfSQHL3GeEz3zVwEd6LDrQDT97sgDY8HJyxgnH79jupBWFOQ1+7fRPBLZfpuA2lwwHqTgk+NJcWQnDpHn31CVm63Or5c5gb4H7/eSIdd+7hf3v+0a5qVsnyxkHbcxXquqk9ezxrUe93cFppxH4/kF/kGBBamm3kuUVbdBUY39c4U3NRkzSO+XdGs69ssK5SPzshn01axCJoNXqqj+ytebuMwF8oI9+ZDqj/XsQ1CLnChbsL+HCl68ioTeoYU9PLrO4on+rNHGPI0Cx6HrVse7M3WQBPGzOd1TvRh9eWJrvQrP/hm6kOR7KrWKuyJzrQh7OoDxrweXFH8toXeQRD8=";


    public UserInterfaceLogin(Cliente cliente) {
        this.cliente = cliente;
        frame = new JFrame("Cliente");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 300);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Painel de Configuração de Servidor
        JPanel serverConfigPanel = new JPanel(new FlowLayout());
        JLabel serverIpLabel = new JLabel("IP do Servidor:");
        serverIpField = new JTextField("localhost", 15);
        JLabel serverPortLabel = new JLabel("Porta do Servidor:");
        serverPortField = new JTextField("12345", 5);
        serverConfigPanel.add(serverIpLabel);
        serverConfigPanel.add(serverIpField);
        serverConfigPanel.add(serverPortLabel);
        serverConfigPanel.add(serverPortField);

        // Painel de Login
        loginPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField("admin@admin.com", 15);
        JLabel passwordLabel = new JLabel("Senha:");
        passwordField = new JPasswordField("admin1234", 15);
        loginButton = new JButton("Login");
        cadastroButton = new JButton("Cadastrar");
        nameField = new JTextField("Seu Nome", 15);
        idField = new JTextField("ID Usuario", 4);
        nameLabel = new JLabel("Nome:");
        tipoComboBoxLabel = new JLabel("Tipo de Usuário:");
        JComboBox<String> tipoComboBox = new JComboBox<>(tipos);

        // Listagem Usuarios
        listarUsuariosButton = new JButton("Listar Usuários");
        listarUsuariosButton.setFont(new Font("Arial", Font.BOLD, 14));
        listarUsuariosButton.setVisible(false);
        editarUsuarioButton = new JButton("Editar Usuário");
        excluirUsuarioButton = new JButton("Excluir Usuário");
        editarUsuarioButton.setFont(new Font("Arial", Font.BOLD, 14));
        excluirUsuarioButton.setFont(new Font("Arial", Font.BOLD, 14));
        editarUsuarioButton.setVisible(false);
        excluirUsuarioButton.setVisible(false);

        DefaultListModel<String> userModel = new DefaultListModel<>();
        userList = new JList<>(userModel);

        // Adicione estilos aos componentes
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        idField.setFont(new Font("Arial", Font.PLAIN, 14));
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        cadastroButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton = new JButton("Voltar");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));

        // Adicione espaçamento entre os componentes
        emailLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        passwordLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        nameField.setVisible(false);
        idField.setVisible(false);
        nameLabel.setVisible(false);
        tipoComboBoxLabel.setVisible(false);
        tipoComboBox.setVisible(false);
        logoutButton.setVisible(false);


        loginPanel.add(emailLabel);
        loginPanel.add(emailField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(nameLabel);
        loginPanel.add(nameField);
        loginPanel.add(tipoComboBoxLabel);
        loginPanel.add(tipoComboBox);
        loginPanel.add(idField);

        buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(loginButton);
        buttonPanel.add(cadastroButton);
        buttonPanel.add(backButton);
        buttonPanel.add(logoutButton);
        buttonPanel.add(listarUsuariosButton);
        buttonPanel.add(editarUsuarioButton);
        buttonPanel.add(excluirUsuarioButton);

        mainPanel.add(serverConfigPanel, BorderLayout.NORTH);
        mainPanel.add(loginPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.getContentPane().add(mainPanel);

        ActionListener cadastroActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String action = "autocadastro-usuario";
                String nome = nameField.getText();
                String email = emailField.getText();
                String senha = new String(passwordField.getPassword());
                String tipo =  (String) tipoComboBox.getSelectedItem();

                ObjectMapper mapper = new ObjectMapper();
                ObjectNode cadastroData = mapper.createObjectNode();


                ObjectNode data = mapper.createObjectNode();
                data.put("name", nome);
                data.put("email", email);
                data.put("password", passwordMD5(senha));

                String token = cliente.getToken();

                if (Objects.equals(token, "")) {
                    action = "autocadastro-usuario";
                } else {
                    if (isAdmin(token)) {
                        action = "cadastro-usuario";
                        data.put("type", tipo);
                        data.put("token", cliente.getToken());
                    } else {
                        JOptionPane.showMessageDialog(null, "Você precisa estar logado como administrador para cadastrar um usuário");
                        return;
                    }
                }
                cadastroData.put("action", action);
                cadastroData.set("data", data);

                try {
                    String jsonString = mapper.writeValueAsString(cadastroData);

                    cliente.sendRequestToServer(jsonString, action);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };

        cadastroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                emailField.setText("teste@teste.com");
                passwordField.setText("senha1234");
                nameField.setVisible(true);
                idField.setVisible(true);
                nameLabel.setVisible(true);
                loginButton.setVisible(false);
                backButton.setVisible(true);
                tipoComboBoxLabel.setVisible(true);
                tipoComboBox.setVisible(true);

                cadastroButton.removeActionListener(cadastroActionListener);
                cadastroButton.addActionListener(cadastroActionListener);
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                emailField.setText("teste@teste.com");
                passwordField.setText("senha1234");
                nameField.setVisible(false);
                nameLabel.setVisible(false);
                loginButton.setVisible(true);
                backButton.setVisible(false);
                tipoComboBoxLabel.setVisible(false);
                tipoComboBox.setVisible(false);
                loginButton.setText("Login");

                cadastroButton.removeActionListener(cadastroActionListener);
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String action = "login";

                String email = emailField.getText();
                String senha = new String(passwordField.getPassword());

                ObjectMapper mapper = new ObjectMapper();
                ObjectNode requestData = mapper.createObjectNode();

                ObjectNode data = mapper.createObjectNode();
                data.put("email", email);
                data.put("password", passwordMD5(senha));

                requestData.put("action", action);
                requestData.set("data", data);

                logoutButton.setVisible(true);

                try {
                    String jsonString = mapper.writeValueAsString(requestData);

                    cliente.sendRequestToServer(jsonString, action);
                } catch (JsonProcessingException ex) {
                    ex.printStackTrace();
                }
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String action = "logout";
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode requestData = mapper.createObjectNode();
                ObjectNode data = mapper.createObjectNode();
                data.put("token", cliente.getToken());

                requestData.put("action", action);
                requestData.set("data", data);
                try {
                    String jsonString = mapper.writeValueAsString(requestData);

                    cliente.sendRequestToServer(jsonString, action);
                } catch (JsonProcessingException ex) {
                    ex.printStackTrace();
                }
            }
        });

        listarUsuariosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String action = "listar-usuarios";
                ObjectMapper mapper = new ObjectMapper();
                String token = cliente.getToken();

                if (Objects.equals(token, "")) {
                    JOptionPane.showMessageDialog(null, "Você precisa estar logado para listar usuários");
                    return;
                }

                ObjectNode requestData = mapper.createObjectNode();
                requestData.put("action", action);
                ObjectNode data = mapper.createObjectNode();
                data.put("token", token);
                requestData.set("data", data);

                try {
                    String jsonString = mapper.writeValueAsString(requestData);
                    cliente.sendRequestToServer(jsonString, action);
                } catch (JsonProcessingException ex) {
                    ex.printStackTrace();
                }
            }
        });

        editarUsuarioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String action = "autoedicao-usuario";
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode data = mapper.createObjectNode();
                String token = cliente.getToken();
                String nome = nameField.getText();
                String email = emailField.getText();
                String senha = new String(passwordField.getPassword());

                data.put("name", nome);
                data.put("email", email);
                data.put("password", passwordMD5(senha));

                if (Objects.equals(token, "")) {
                    JOptionPane.showMessageDialog(null, "Você precisa estar logado para editar usuário");
                    return;
                }
                if (isAdmin(token)) {
                    action = "edicao-usuario";
                }
                data.put("token", token);
                data.put("user_id", idField.getText());

                ObjectNode requestData = mapper.createObjectNode();
                requestData.put("action", action);
                requestData.set("data", data);
                System.out.println(requestData);

                try {
                    String jsonString = mapper.writeValueAsString(requestData);
                    cliente.sendRequestToServer(jsonString, action);
                } catch (JsonProcessingException ex) {
                    ex.printStackTrace();
                }
            }
        });

        excluirUsuarioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String action = "excluir-usuario";
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode data = mapper.createObjectNode();
                String token = cliente.getToken();
                String userId = idField.getText();

                if (Objects.equals(token, "")) {
                    JOptionPane.showMessageDialog(null, "Você precisa estar logado para excluir usuário");
                    return;
                }
                data.put("user_id", userId);
                data.put("token", token);

                ObjectNode requestData = mapper.createObjectNode();
                requestData.put("action", action);
                requestData.set("data", data);
                System.out.println(requestData);

                try {
                    String jsonString = mapper.writeValueAsString(requestData);
                    cliente.sendRequestToServer(jsonString, action);
                } catch (JsonProcessingException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private static Jws<Claims> parseToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
    }

    public static boolean isAdmin(String token) {
        Jws<Claims> parsedToken = parseToken(token);

        return parsedToken.getBody().get("admin", Boolean.class);
    }

    public static String getCurrentUserId(String token) {
        Jws<Claims> parsedToken = parseToken(token);

        return parsedToken.getBody().get("user_id", String.class);
    }

    public String passwordMD5(String password) {
        return DigestUtils.md5Hex(password).toUpperCase();
    }

    public void setLoginButtonListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }

    public void updateUsersList(ObjectNode responseData) {
        if (responseData.has("users")) {
            JsonNode users = responseData.get("users");
            DefaultListModel<String> userModel = new DefaultListModel<>();

            for (JsonNode user : users) {
                String userName = user.get("name").asText();
                userModel.addElement(userName);
            }

            // Atualize sua lista de usuários na interface gráfica aqui
            // Por exemplo, suponha que você tenha uma JList userList:
            userList.setModel(userModel);
        }
    }

    public ObjectNode getResponseData(String response) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(response);
            return (jsonNode.has("data")) ? (ObjectNode) jsonNode.get("data") : null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public String getServerIP() {
        return serverIpField.getText();
    }

    public int getServerPort() {
        try {
            return Integer.parseInt(serverPortField.getText());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public String getEmail() {
        return emailField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void show() {
        frame.setVisible(true);
    }

    public void hide() {
        frame.setVisible(false);
    }

}
