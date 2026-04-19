package com.ankama;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientServerHandler implements Runnable {

    // Each instance will share the list of all instances to communicate to others
    // CopyOnWriteArrayList instead of ArrayList tomake it thread safe when adding
    // or removing
    private static CopyOnWriteArrayList<ClientServerHandler> clientsConnected = new CopyOnWriteArrayList<>();

    // Buffered objects, make the transition of data efficient by not send char by
    // char, but in a buffer instead
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private CombatGroup group;

    public void setGroup(CombatGroup group) {
        this.group = group;
    }

    private HumanPlayer player;

    public HumanPlayer getPlayer() {
        return player;
    }

    // Socket representing the side of the server
    private Socket socket;

    // ClientID to filter each Player as a unique one
    private String clientID;

    public String getClientID() {
        return clientID;
    }

    // Pseudo to display informations of or for a specific Player (Only
    // visual-usefull)
    private String clientPseudo;

    public String getClientPseudo() {
        return clientPseudo;
    }

    public ClientServerHandler(Socket socket) {
        this.socket = socket;
        try {
            // Get each stream of the socket and make it as an array buffer
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        } catch (IOException e) {
            closeCommunication();
        }
    }

    public void initCombatPlayer(int baseHealthPoints, String opponent) {
        this.player = new HumanPlayer(this, baseHealthPoints);
        sendMessageToClient("Combat started against " + opponent + " ! Good luck !");
    }

    private void initClientInformations() throws IOException {
        // Wait for unique ID generated
        clientID = bufferedReader.readLine();

        // Wait for Pseudo
        clientPseudo = bufferedReader.readLine();

        // Add this handler in the list for keeping its reference for the other handlers
        clientsConnected.add(this);

        // Wait for the game session type
        waitForSessionChoice();
    }

    private void waitForSessionChoice() throws IOException {
        String sessionMsg;
        String choice = bufferedReader.readLine();
        if (choice.equals("1")) {
            WaitingRoom.getInstance().addToWaitingRoom(this);
            sessionMsg = "New player '" + clientPseudo + "' has join the server !";
            writeToWaitingRoom(sessionMsg);

        } else if (choice.equals("2")) {
            sessionMsg = "Welcome " + clientPseudo + " ! You will fight an AI...";
            sendMessageToClient(sessionMsg);
            CombatGroup combatGroup = new CombatGroup(this);
            setGroup(combatGroup);
            Thread thread = new Thread(combatGroup);
            thread.start();
        }
    }

    public boolean isOnline() {
        return (socket.isConnected());
    }

    public void listenFromClient() {
        String msgFromClient;
        try {
            while (socket.isConnected()) {
                msgFromClient = bufferedReader.readLine();
                if (msgFromClient.equals("Bye")) {
                    closeCommunication();
                    return;
                }

                if (group != null) {
                    if (!msgFromClient.equals("1") && !msgFromClient.equals("2")) {
                        sendMessageToClient("Unknown attack, try again");
                        sendMessageToClient("TOGGLE_INPUTS_ON");
                    } else {
                        group.newEvent(msgFromClient, player);
                    }
                }
            }
        } catch (IOException e) {
            closeCommunication();
        }
    }

    public void toggleClientTurn(boolean isItsTurn) {
        newTurnDisplay();
        if (isItsTurn) {
            sendMessageToClient("TOGGLE_INPUTS_ON");
        } else
            sendMessageToClient("Waiting for opponent's turn ...");
    }

    public void newTurnDisplay() {
        String msg = "\n-----------------------------------\n";
        sendMessageToClient(msg);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Send (through the buffer writer) a message
    public void sendMessageToClient(String msg) {

        try {
            // Write in the buffer
            bufferedWriter.write(msg);

            // Write an EOL (like \n) to make sure the reader on the other side (Client
            // side) will stop after this message
            bufferedWriter.newLine();

            // Flush => Send the message through the socket and move on to the next new line
            // (kinda like clean the current buffer after sending it)
            bufferedWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToWaitingRoom(String msg) {
        // We loop through all the clients connected references
        for (ClientServerHandler clientServerHandler : WaitingRoom.getInstance().getClients()) {
            // We use all the others clients connected (we dont want the Client sending a
            // message to recieve its own message)
            if (!clientServerHandler.clientID.equals(this.clientID))
                clientServerHandler.sendMessageToClient(msg);
        }
    }

    public void closeCommunication() {
        if (WaitingRoom.getInstance().getClients().contains(this))
            WaitingRoom.getInstance().removeFromWaitingRoom(this);
        else if (group != null)
            group.newEvent("DISCONNECT", player);

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
        clientsConnected.remove(this);
    }

    // Method from the Thread that is parallelized therefore non blocking (even tho
    // listenFromClient IS blocking by waiting it buffer reader to read line)
    @Override
    public void run() {
        try {
            initClientInformations();
        } catch (IOException e) {
            e.printStackTrace();
        }
        listenFromClient();
    }
}
