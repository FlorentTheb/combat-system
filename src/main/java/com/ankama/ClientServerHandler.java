package com.ankama;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
    private String clientIDReference;

    public ClientServerHandler(Socket socket) {
        this.socket = socket;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            clientIDReference = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        handlers.add(this);
    }

    public void listenFromClient() {
        try {
            while (socket.isConnected()) {
                String msg = bufferedReader.readLine();
                writeToClients(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToClients(String msg) {
        try {
            for (ClientServerHandler clientServerHandler : handlers) {
                if (clientServerHandler.clientIDReference != this.clientIDReference) {
                    clientServerHandler.bufferedWriter.write(msg);
                    clientServerHandler.bufferedWriter.newLine();
                    clientServerHandler.bufferedWriter.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        listenFromClient();
    }
}
