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

                // Blocking method to wait for a socket to be created and linked to the
                // serverSocket
                Socket socket = serverSocket.accept();
                System.out.println("A new client is connected !");

                // Create an "agent" that will be responsible of delevering and recieving data
                // from its Client
                ClientServerHandler clientServerHandler = new ClientServerHandler(socket);

                // Make a Thread to expose the run() method and parallelize its listening method
                Thread thread = new Thread(clientServerHandler);
                thread.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            // Create the server socket at a given port
            ServerSocket serverSocket = new ServerSocket(2077);
            Server server = new Server(serverSocket);

            server.runServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
