package com.servidor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerThread extends Thread {
    private Server server;
    private int port;
    private ServerSocket serverSocket;
    private List<ClientHandler> clients;

    public ServerThread(Server server, int port) {
        this.server = server;
        this.port = port;
        clients = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            server.connectionListArea.append("Servidor iniciado na porta " + port + "\n");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                server.connectionListArea.append("Nova conexão: " + clientSocket.getInetAddress().getHostAddress() + "\n");

                ClientHandler clientHandler = new ClientHandler(clientSocket, server);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            for (ClientHandler client : clients) {
                client.stop();
            }
            clients.clear();
            server.connectionListArea.append("Servidor encerrado.\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
