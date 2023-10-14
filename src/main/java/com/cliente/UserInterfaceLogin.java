package com.cliente;

import com.fasterxml.jackson.core.JsonProcessingException;
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
    private JButton loginButton;
    private JButton cadastroButton;
    public JButton backButton;
    private JTextField nameField;
    private JLabel nameLabel;
    private JLabel tipoComboBoxLabel;
    private JPanel loginPanel;
    private JButton logoutButton; // Botão de logout
    private JPanel buttonPanel;
    public Cliente cliente;
    private String[] tipos = { "user", "admin" };

    private static final String secretKey = "TOKEN_SECRET_KEY";


    public UserInterfaceLogin(Cliente cliente) {
        this.cliente = cliente;
        frame = new JFrame("Cliente");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 300);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Painel de Configuração de Servidor
        JPanel serverConfigPanel = new JPanel(new FlowLayout());
        JLabel serverIpLabel = new JLabel("IP do Servidor:");
        serverIpField = new JTextField("0.tcp.sa.ngrok.io", 15);
        JLabel serverPortLabel = new JLabel("Porta do Servidor:");
        serverPortField = new JTextField("12345", 5);
        serverConfigPanel.add(serverIpLabel);
        serverConfigPanel.add(serverIpField);
        serverConfigPanel.add(serverPortLabel);
        serverConfigPanel.add(serverPortField);

        // Painel de Login
        loginPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField("teste@teste.com", 15);
        JLabel passwordLabel = new JLabel("Senha:");
        passwordField = new JPasswordField("senha1234", 15);
        loginButton = new JButton("Login");
        cadastroButton = new JButton("Cadastrar");
        nameField = new JTextField("Seu Nome", 15);
        nameLabel = new JLabel("Nome:");
        tipoComboBoxLabel = new JLabel("Tipo de Usuário:");
        JComboBox<String> tipoComboBox = new JComboBox<>(tipos);

        // Adicione estilos aos componentes
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        cadastroButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton = new JButton("Voltar");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton = new JButton("Logout");

        // Adicione espaçamento entre os componentes
        emailLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        passwordLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        nameField.setVisible(false);
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

        buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(loginButton);
        buttonPanel.add(cadastroButton);
        buttonPanel.add(backButton);
        buttonPanel.add(logoutButton);

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

                // Crie um objeto JSON usando o Jackson Databind
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode cadastroData = mapper.createObjectNode();


                ObjectNode data = mapper.createObjectNode();
                data.put("nome", nome);
                data.put("email", email);
                data.put("senha", passwordMD5(senha));

                String token = cliente.getToken();

                if (Objects.equals(token, "")) {
                    action = "autocadastro-usuario";
                } else {
                    if (isAdmin(token)) {
                        action = "cadastro-usuario";
                    } else {
                        JOptionPane.showMessageDialog(null, "Você precisa estar logado como administrador para cadastrar um usuário");
                        return;
                    }
                }
                cadastroData.put("action", action);
                cadastroData.set("data", data);

                try {
                    // Converta o objeto JSON em uma string
                    String jsonString = mapper.writeValueAsString(cadastroData);

                    // Agora você pode enviar a string JSON para o servidor
                    // Implemente a lógica de comunicação com o servidor aqui
                    cliente.sendRequestToServer(jsonString, action);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };

        cadastroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Exibe os campos de nome, email e senha para cadastro
                emailField.setText("teste@teste.com");
                passwordField.setText("senha1234");
                nameField.setVisible(true);
                nameLabel.setVisible(true);
                loginButton.setVisible(false); // Oculta o botão de login
                backButton.setVisible(true); // Torna visível o botão de voltar
                tipoComboBoxLabel.setVisible(true);
                tipoComboBox.setVisible(true);

                cadastroButton.removeActionListener(cadastroActionListener);
                cadastroButton.addActionListener(cadastroActionListener);
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Retorna ao modo de login
                emailField.setText("teste@teste.com");
                passwordField.setText("senha1234");
                nameField.setVisible(false);
                nameLabel.setVisible(false);
                loginButton.setVisible(true);
                backButton.setVisible(false);
                tipoComboBoxLabel.setVisible(false);
                tipoComboBox.setVisible(false);
                loginButton.setText("Login");

                // Remove o ActionListener do botão de cadastro
                cadastroButton.removeActionListener(cadastroActionListener);
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String action = "login";

                // Coletar os dados do formulário
                String email = emailField.getText();
                String senha = new String(passwordField.getPassword());

                // Criar um objeto JSON usando o Jackson Databind
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode requestData = mapper.createObjectNode();

                ObjectNode data = mapper.createObjectNode();
                data.put("email", email);
                data.put("senha", passwordMD5(senha));

                requestData.put("action", action);
                requestData.set("data", data);

                logoutButton.setVisible(true);

                try {
                    // Converter o objeto JSON em uma string
                    String jsonString = mapper.writeValueAsString(requestData);

                    // Enviar o JSON para o servidor
                    cliente.sendRequestToServer(jsonString, action);
                } catch (JsonProcessingException ex) {
                    ex.printStackTrace();
                }
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Limpe o token da sessão do cliente
                cliente.removeToken();
                // Oculte o botão de logout e mostre o botão de login
                logoutButton.setVisible(false);
                loginButton.setVisible(true);
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

    public String passwordMD5(String password) {
        return DigestUtils.md5Hex(password).toUpperCase();
    }

    public void setLoginButtonListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }

    public String getServerIP() {
        return serverIpField.getText();
    }

    public int getServerPort() {
        try {
            return Integer.parseInt(serverPortField.getText());
        } catch (NumberFormatException e) {
            return -1; // Retorna -1 se a porta não for um número válido
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