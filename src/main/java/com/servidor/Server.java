package com.servidor;

import com.servidor.database.DatabaseConnection;
import com.servidor.database.DatabaseManager;
import com.servidor.database.DatabaseSetup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
    private JFrame frame;
    JTextArea connectionListArea;
    private JButton startButton;
    private JButton stopButton;
    private JTextField portField;
    private ServerSocket serverSocket;
    private List<ClientHandler> clients;
    private boolean isServerRunning = false;
    private Map<String, String> users;
    private Connection connection;

    public Server() {
        frame = new JFrame("Servidor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        connectionListArea = new JTextArea();
        connectionListArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(connectionListArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout());
        JLabel portLabel = new JLabel("Porta:");
        portField = new JTextField("12345", 5);
        startButton = new JButton("Iniciar Servidor");
        stopButton = new JButton("Parar Servidor");

        controlPanel.add(portLabel);
        controlPanel.add(portField);
        controlPanel.add(startButton);
        controlPanel.add(stopButton);

        frame.add(controlPanel, BorderLayout.SOUTH);

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int port = Integer.parseInt(portField.getText());
                startButton.setVisible(false);
                startServer(port);
            }
        });

        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopServer();
                startButton.setVisible(true);
            }
        });

        clients = new ArrayList<>();

        connection = DatabaseConnection.connect();
        DatabaseSetup.createTable(connection);

        // Cadastro de admin padrao apenas para testes
        DatabaseManager dbManager = new DatabaseManager(connection);
        if (!dbManager.emailJaExiste("admin@admin.com")) {
            dbManager.inserirUsuario("Admin", "admin@admin.com","C93CCD78B2076528346216B3B2F701E6", "admin"); // Senha: admin1234
        }

        startButton.doClick();
    }

    public void start() {
        frame.setVisible(true);
    }

    public void startServer(final int port) {
        if (!isServerRunning) {
            SwingWorker<Void, Void> serverWorker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        serverSocket = new ServerSocket(port);
                        connectionListArea.append("Servidor iniciado na porta " + port + "\n");
                        isServerRunning = true;

                        while (isServerRunning) {
                            try {
                                Socket clientSocket = serverSocket.accept();
                                connectionListArea.append("Nova conexão: " + clientSocket.getInetAddress().getHostAddress() + "\n");

                                ClientHandler clientHandler = new ClientHandler(clientSocket, Server.this);
                                clients.add(clientHandler);
                                new Thread(clientHandler).start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            serverWorker.execute();
        } else {
            connectionListArea.append("O servidor já está em execução.\n");
        }
    }

    public void stopServer() {
        if (isServerRunning) {
            try {
                isServerRunning = false;
                // Encerre todas as conexões e threads ativas
                for (ClientHandler client : clients) {
                    client.stop();
                }
                clients.clear();

                // Feche o socket do servidor
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }

                connectionListArea.append("Servidor encerrado.\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            connectionListArea.append("O servidor não está em execução.\n");
        }
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public Connection getConnection() {
        return connection;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Server server = new Server();
                server.start();
            }
        });
    }
}
