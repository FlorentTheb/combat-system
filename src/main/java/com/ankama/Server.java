package com.ankama;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void runServer() {
        while (!serverSocket.isClosed()) {
            try {
                System.out.println("Waiting for a client");
                Socket socket = serverSocket.accept();
                System.out.println("A new client is connected !");
                ClientServerHandler clientServerHandler = new ClientServerHandler(socket);

                Thread thread = new Thread(clientServerHandler);
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(2077);
            Server server = new Server(serverSocket);
            server.runServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
