package com.cliente;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
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
    private JLabel emailLabel;
    private JPasswordField passwordField;
    private JLabel passwordLabel;
    JButton loginButton;
    private JButton cadastroButton;
    private JTextField nameField;
    private JLabel nameLabel;
    private JTextField idField;
    private JLabel idLabel;
    JComboBox<String> tipoComboBox;
    private JLabel tipoComboBoxLabel;
    private JPanel loginPanel;
    private JPanel loginButtonPanel;
    private JPanel cadastroPanel;
    private JPanel logedButtonPanel;
    private JPanel listagemPanel;
    private JPanel edicaoPanel;
    private JPanel exclusaoPanel;
    JButton logoutButton;
    public Cliente cliente;
    private String[] tipos = { "user", "admin" };
    JButton listarUsuariosButton;
    JButton exibirUsuarioButton;
    JButton editarUsuarioButton;
    JButton excluirUsuarioButton;
    JPanel mainPanel = new JPanel(new BorderLayout());
    JPanel serverConfigPanel;
    JPanel[] panels = {
        mainPanel,
        loginPanel,
        loginButtonPanel,
        cadastroPanel,
        logedButtonPanel,
        listagemPanel,
        edicaoPanel,
        exclusaoPanel,
    };
    List<Usuario> listaUsuarios = new ArrayList<>();
    private static final String secretKey = "AoT3QFTTEkj16rCby/TPVBWvfSQHL3GeEz3zVwEd6LDrQDT97sgDY8HJyxgnH79jupBWFOQ1+7fRPBLZfpuA2lwwHqTgk+NJcWQnDpHn31CVm63Or5c5gb4H7/eSIdd+7hf3v+0a5qVsnyxkHbcxXquqk9ezxrUe93cFppxH4/kF/kGBBamm3kuUVbdBUY39c4U3NRkzSO+XdGs69ssK5SPzshn01axCJoNXqqj+ytebuMwF8oI9+ZDqj/XsQ1CLnChbsL+HCl68ioTeoYU9PLrO4on+rNHGPI0Cx6HrVse7M3WQBPGzOd1TvRh9eWJrvQrP/hm6kOR7KrWKuyJzrQh7OoDxrweXFH8toXeQRD8=";


    public UserInterfaceLogin(Cliente cliente) {
        this.cliente = cliente;
        frame = new JFrame("Cliente");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 300);

        // Painel de Configuração de Servidor
        serverConfigPanel = new JPanel(new FlowLayout());
        JLabel serverIpLabel = new JLabel("IP do Servidor:");
        serverIpField = new JTextField("localhost", 15);
        JLabel serverPortLabel = new JLabel("Porta do Servidor:");
        serverPortField = new JTextField("12345", 5);
        serverConfigPanel.add(serverIpLabel);
        serverConfigPanel.add(serverIpField);
        serverConfigPanel.add(serverPortLabel);
        serverConfigPanel.add(serverPortField);

        // Painel
        loginPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        cadastroPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        edicaoPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        listagemPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        exclusaoPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        // Fields e Labels
        emailLabel = new JLabel("Email:");
        emailLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        emailField = new JTextField("admin@admin.com", 15);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));

        passwordLabel = new JLabel("Senha:");
        passwordField = new JPasswordField("admin1234", 15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        nameLabel = new JLabel("Nome:");
        nameField = new JTextField("Seu Nome", 15);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));

        idLabel = new JLabel("ID do Usuario:");
        idField = new JTextField(null, 4);
        idField.setFont(new Font("Arial", Font.PLAIN, 14));

        tipoComboBoxLabel = new JLabel("Tipo de Usuário:");
        tipoComboBox = new JComboBox<>(tipos);

        // Buttons
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));

        cadastroButton = new JButton("Cadastrar");
        cadastroButton.setFont(new Font("Arial", Font.BOLD, 14));

        listarUsuariosButton = new JButton("Listar Usuários");
        listarUsuariosButton.setFont(new Font("Arial", Font.BOLD, 14));

        exibirUsuarioButton = new JButton("Exibir Usuário");
        exibirUsuarioButton.setFont(new Font("Arial", Font.BOLD, 14));

        editarUsuarioButton = new JButton("Editar Usuário");
        editarUsuarioButton.setFont(new Font("Arial", Font.BOLD, 14));

        excluirUsuarioButton = new JButton("Excluir Usuário");
        excluirUsuarioButton.setFont(new Font("Arial", Font.BOLD, 14));

        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));

        loginPanel.add(emailLabel);
        loginPanel.add(emailField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        for (int i = 2; i >= 0; i--) {
            loginPanel.add(new JLabel(""));
            loginPanel.add(new JLabel(""));
        }

        loginButtonPanel = new JPanel(new FlowLayout());
        loginButtonPanel.add(loginButton);
        loginButtonPanel.add(cadastroButton);

        mainPanel.add(serverConfigPanel, BorderLayout.NORTH);
        mainPanel.add(loginPanel, BorderLayout.CENTER);
        mainPanel.add(loginButtonPanel, BorderLayout.SOUTH);

        frame.getContentPane().add(mainPanel);

        cadastroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mudarTela("cadastro");

                cadastroButton.removeActionListener(cadastroActionListener);
                cadastroButton.addActionListener(cadastroActionListener);
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
                    mudarTela("login");
                } catch (JsonProcessingException ex) {
                    ex.printStackTrace();
                }
            }
        });

        exibirUsuarioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String action = "pedido-proprio-usuario";
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
                    mudarTela("listar");
                } catch (JsonProcessingException ex) {
                    ex.printStackTrace();
                }
            }
        });

        listarUsuariosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String action = "pedido-proprio-usuario";
                ObjectMapper mapper = new ObjectMapper();
                String token = cliente.getToken();

                if (Objects.equals(token, "")) {
                    JOptionPane.showMessageDialog(null, "Você precisa estar logado para listar usuários");
                    return;
                }
                if (isAdmin(token)) {
                    action = "listar-usuarios";
                }
                ObjectNode requestData = mapper.createObjectNode();
                requestData.put("action", action);
                ObjectNode data = mapper.createObjectNode();
                data.put("token", token);
                requestData.set("data", data);

                try {
                    String jsonString = mapper.writeValueAsString(requestData);
                    cliente.sendRequestToServer(jsonString, action);
                    mudarTela("listar");
                } catch (JsonProcessingException ex) {
                    ex.printStackTrace();
                }
            }
        });

        editarUsuarioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mudarTela("editar");

                editarUsuarioButton.removeActionListener(edicaoActionListener);
                editarUsuarioButton.addActionListener(edicaoActionListener);
            }
        });

        excluirUsuarioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Objects.equals(cliente.getToken(), "")){
                    mudarTela("login");
                } else {
                    mudarTela("excluir");
                    excluirUsuarioButton.removeActionListener(exclusaoActionListener);
                    excluirUsuarioButton.addActionListener(exclusaoActionListener);
                }
            }
        });
    }

    public void mudarTela(String tela) {
        cadastroButton.removeActionListener(cadastroActionListener);
        editarUsuarioButton.removeActionListener(edicaoActionListener);
        excluirUsuarioButton.removeActionListener(exclusaoActionListener);
        String token = cliente.getToken();

        logedButtonPanel = new JPanel(new FlowLayout());
        if (!Objects.equals(token, "") && isAdmin(cliente.getToken())) {
            logedButtonPanel.add(cadastroButton);
            logedButtonPanel.add(listarUsuariosButton);
        }
        logedButtonPanel.add(exibirUsuarioButton);
        logedButtonPanel.add(editarUsuarioButton);
        logedButtonPanel.add(excluirUsuarioButton);
        logedButtonPanel.add(logoutButton);

        switch(tela) {
            case "login":
                limparTela();
                loginPanel.removeAll();

                loginPanel.add(emailLabel);
                loginPanel.add(emailField);
                loginPanel.add(passwordLabel);
                loginPanel.add(passwordField);
                for (int i = 2; i >= 0; i--) {
                    loginPanel.add(new JLabel(""));
                    loginPanel.add(new JLabel(""));
                }

                loginButtonPanel = new JPanel(new FlowLayout());
                loginButtonPanel.add(loginButton);
                loginButtonPanel.add(cadastroButton);

                criarTela(loginPanel, loginButtonPanel);
                break;
            case "cadastro":
                limparTela();
                cadastroPanel.removeAll();

                cadastroPanel.add(emailLabel);
                cadastroPanel.add(emailField);
                cadastroPanel.add(passwordLabel);
                cadastroPanel.add(passwordField);
                cadastroPanel.add(nameLabel);
                cadastroPanel.add(nameField);
                if (!Objects.equals(token, "")) {
                    cadastroPanel.add(tipoComboBoxLabel);
                    cadastroPanel.add(tipoComboBox);
                }

                criarTela(cadastroPanel, logedButtonPanel);
                break;
            case "listar":
                limparTela();
                listagemPanel.removeAll();

                String[] colunas = {"ID", "Nome", "Email", "Tipo"};
                Object[][] dados = new Object[listaUsuarios.size()][colunas.length];
                for (int i = 0; i < listaUsuarios.size(); i++) {
                    Usuario usuario = listaUsuarios.get(i);
                    dados[i][0] = usuario.getId();
                    dados[i][1] = usuario.getNome();
                    dados[i][2] = usuario.getEmail();
                    dados[i][3] = usuario.getTipo();
                }

                JTable table = new JTable(dados, colunas);
                JScrollPane scrollPane = new JScrollPane(table);
                listagemPanel.add(scrollPane);

                criarTela(listagemPanel, logedButtonPanel);
                break;
            case "editar":
                limparTela();
                edicaoPanel.removeAll();

                edicaoPanel.add(emailLabel);
                edicaoPanel.add(emailField);
                edicaoPanel.add(nameLabel);
                edicaoPanel.add(nameField);
                if (isAdmin(token)) {
                    edicaoPanel.add(tipoComboBoxLabel);
                    edicaoPanel.add(tipoComboBox);
                    edicaoPanel.add(idLabel);
                    edicaoPanel.add(idField);
                } else {
                    edicaoPanel.add(passwordLabel);
                    edicaoPanel.add(passwordField);
                }

                criarTela(edicaoPanel, logedButtonPanel);
                break;
            case "excluir":
                limparTela();
                exclusaoPanel.removeAll();
                int index = 0;

                if (isAdmin(token)) {
                    exclusaoPanel.add(idLabel);
                    exclusaoPanel.add(idField);
                    index = 3;
                } else {
                    exclusaoPanel.add(emailLabel);
                    exclusaoPanel.add(emailField);
                    exclusaoPanel.add(passwordLabel);
                    exclusaoPanel.add(passwordField);
                    index = 2;
                }

                for (int i = index; i >= 0; i--) {
                    exclusaoPanel.add(new JLabel(""));
                    exclusaoPanel.add(new JLabel(""));
                }

                criarTela(exclusaoPanel, logedButtonPanel);
                break;
        }
    }

    private void limparTela() {
        for (JPanel elemento : panels) {
            if ((elemento != null) && (elemento.getComponentCount() > 0)) {
                elemento.removeAll();
            }
        }
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void criarTela(JPanel center, JPanel botton) {
        mainPanel.add(serverConfigPanel, BorderLayout.NORTH);
        mainPanel.add(center, BorderLayout.CENTER);
        mainPanel.add(botton, BorderLayout.SOUTH);

        mainPanel.revalidate();
        mainPanel.repaint();
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

    public void updateUsersList(ArrayNode usersNode) {
        ObjectMapper mapper = new ObjectMapper();
        listaUsuarios.clear();

        try {
            if (usersNode != null) {
                for (JsonNode userNode : usersNode) {
                    Usuario usuario = mapper.readValue(userNode.toString(), Usuario.class);
                    listaUsuarios.add(usuario);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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

    ActionListener edicaoActionListener = new ActionListener() {
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
                data.put("user_id", Integer.parseInt(idField.getText()));
                data.put("type", (String) tipoComboBox.getSelectedItem());
            } else {
                data.put("id", getCurrentUserId(token));
            }
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
    };

    ActionListener exclusaoActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String action = "excluir-proprio-usuario";
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode data = mapper.createObjectNode();
            String token = cliente.getToken();
            Integer userId = Integer.parseInt(idField.getText());
            String email = emailField.getText();
            String senha = new String(passwordField.getPassword());

            if (Objects.equals(token, "")) {
                JOptionPane.showMessageDialog(null, "Você precisa estar logado para excluir usuário");
                return;
            }
            if (isAdmin(token)) {
                action = "excluir-usuario";
                data.put("user_id", userId);
            } else {
                data.put("email", email);
                data.put("password", passwordMD5(senha));
            }
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
    };

}
