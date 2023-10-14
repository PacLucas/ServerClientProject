package com;

import com.servidor.Server;
import com.cliente.Cliente;

public class Main {
    public static void main(String[] args) {
        // Inicie o servidor em uma thread separada
        Thread serverThread = new Thread(() -> {
            Server server = new Server();
            server.start();
        });
        serverThread.start();

        // Inicie o cliente na thread principal
        Cliente cliente = new Cliente();
        cliente.start();
    }
}
