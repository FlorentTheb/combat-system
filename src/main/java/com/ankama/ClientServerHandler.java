package com.ankama;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientServerHandler implements Runnable {

    private static ArrayList<ClientServerHandler> handlers = new ArrayList<>();
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private Socket socket;
    private String clientID;
    private String clientPseudo;

    public ClientServerHandler(Socket socket) {
        this.socket = socket;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            clientID = bufferedReader.readLine();
            clientPseudo = bufferedReader.readLine();
        } catch (IOException e) {
            closeCommunication();
        }
        handlers.add(this);
        writeToClients("Client [" + clientPseudo + "] has entered the server. Welcome !");
    }

    public void listenFromClient() {
        try {
            while (socket.isConnected()) {
                String msg = bufferedReader.readLine();
                writeToClients(msg);
            }
        } catch (IOException e) {
            closeCommunication();
        }
    }

    public void writeToClients(String msg) {
        try {
            for (ClientServerHandler clientServerHandler : handlers) {
                if (clientServerHandler.clientID != this.clientID) {
                    clientServerHandler.bufferedWriter.write(msg);
                    clientServerHandler.bufferedWriter.newLine();
                    clientServerHandler.bufferedWriter.flush();
                }
            }
        } catch (IOException e) {
            closeCommunication();
        }
    }

    public void closeCommunication() {
        writeToClients(clientPseudo + " has disconnected !");
        try {
            if (socket != null)
                socket.close();
            if (bufferedReader != null)
                bufferedReader.close();
            if (bufferedWriter != null)
                bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        handlers.remove(this);
    }

    @Override
    public void run() {
        listenFromClient();
    }
}
