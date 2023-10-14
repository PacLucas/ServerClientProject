package com.cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserInterfaceMain {
    private JFrame frame;
    private JButton cadastroButton;
    private JButton listarUsuariosButton;

    private Cliente cliente;

    public UserInterfaceMain(Cliente cliente) {
        this.cliente = cliente;

        frame = new JFrame("Tela Principal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        cadastroButton = new JButton("Cadastro de Usuário");
        listarUsuariosButton = new JButton("Listar Usuários");

        cadastroButton.setFont(new Font("Arial", Font.BOLD, 14));
        listarUsuariosButton.setFont(new Font("Arial", Font.BOLD, 14));

        buttonPanel.add(cadastroButton);
        buttonPanel.add(listarUsuariosButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        frame.getContentPane().add(mainPanel);
    }

    public void setCadastroButtonListener(ActionListener listener) {
        cadastroButton.addActionListener(listener);
    }

    public void setListarUsuariosButtonListener(ActionListener listener) {
        listarUsuariosButton.addActionListener(listener);
    }

    public void show() {
        frame.setVisible(true);
    }

    public void hide() {
        frame.setVisible(false);
    }
}
