package com.cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserInterfaceList {
    private JFrame frame;
    private JList<String> userList;
    private JButton editButton;
    private JButton deleteButton;

    public UserInterfaceList() {
        frame = new JFrame("Lista de Usuários");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 300);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Lista de Usuários
        userList = new JList<>();
        JScrollPane userListScrollPane = new JScrollPane(userList);

        // Botões de Edição e Exclusão
        JPanel buttonPanel = new JPanel();
        editButton = new JButton("Editar Usuário");
        deleteButton = new JButton("Excluir Usuário");
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        mainPanel.add(userListScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.getContentPane().add(mainPanel);
    }

    public void setUserList(String[] users) {
        userList.setListData(users);
    }

    public void setEditButtonListener(ActionListener listener) {
        editButton.addActionListener(listener);
    }

    public void setDeleteButtonListener(ActionListener listener) {
        deleteButton.addActionListener(listener);
    }

    public void show() {
        frame.setVisible(true);
    }

    public void hide() {
        frame.setVisible(false);
    }
}
