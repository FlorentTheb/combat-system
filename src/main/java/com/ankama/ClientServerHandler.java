package com.ankama;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientServerHandler implements Runnable {

    // Each instance will share the list of all instances, usefull to loop for
    // future coms
    private static ArrayList<ClientServerHandler> handlers = new ArrayList<>();

    // Buffered objects, make the transition of data efficient by not send char by
    // char, but in a buffer instead
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    // Socket representing the side of the server
    private Socket socket;

    // ClientID to filter each Player as a unique one
    private String clientID;
    // Pseudo to display informations of or for a specific Player (Only
    // visual-usefull)
    private String clientPseudo;

    public ClientServerHandler(Socket socket) {
        this.socket = socket;
        try {
            // Get each stream of the socket and make it as an array buffer
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // We wait and read a first time the coming data from the reader, therefore the
            // first outgoing data from the writer of the Client (which is the ID generated)
            clientID = bufferedReader.readLine();
            // Read a second time for the pseudo (Input read from a scanner Client side)
            clientPseudo = bufferedReader.readLine();
        } catch (IOException e) {
            closeCommunication();
        }
        // Add this handler in the list for keeping its reference for the other handlers
        handlers.add(this);
        writeToClients("Client [" + clientPseudo + "] has entered the server. Welcome !");
    }

    public void listenFromClient() {
        try {
            while (socket.isConnected()) {
                // As long as the socket is connected (i.e Client is still connected to the
                // server with the right adress and port)
                // We check if a new message is in the reader buffer (therefore a Client has
                // sent a essage through its writer buffer)
                String msg = bufferedReader.readLine();
                // Once we have a new message (the method previously IS blocking),
                writeToClients(msg);
            }
        } catch (IOException e) {
            closeCommunication();
        }
    }

    public void writeToClients(String msg) {
        try {
            // We loop through all the Handlers references
            for (ClientServerHandler clientServerHandler : handlers) {
                // We use all the others handlers (we dont want the Client sending a message to
                // recieve its own message)
                if (!clientServerHandler.clientID.equals(this.clientID)) {
                    // Send in the communication "pipe" (through the buffer writer) a message
                    // Write in the buffer
                    clientServerHandler.bufferedWriter.write(msg);
                    // Write an EOL (like \n) to make sure the reader on the other side (Client
                    // side) will stop after this message
                    clientServerHandler.bufferedWriter.newLine();
                    // Flush => Send the message through the socket and move on to the next new line
                    // (kinda like clean the current buffer after sending it)
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
            // We close here every object used in the communication that is not null
            if (socket != null)
                socket.close();
            if (bufferedReader != null)
                bufferedReader.close();
            if (bufferedWriter != null)
                bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Remove the reference of this handler since will not be used as the Client is
        // disconnected from the server
        handlers.remove(this);
    }

    // Method from the Thread that is parallelized therefore non blocking (even tho
    // listenFromClient IS blocking by waiting it buffer reader to read line)
    @Override
    public void run() {
        listenFromClient();
    }
}
