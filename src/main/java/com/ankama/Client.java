package com.ankama;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.UUID;

public class Client {

    // Scanner global representing the input of keyboard for all future instances of
    // Client
    private static final Scanner scanner = new Scanner(System.in);

    private Socket socket;
    private String pseudo;
    private final String id;

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client(String pseudo) {

        this.pseudo = pseudo;
        this.id = UUID.randomUUID().toString();

        try {
            // Define the socket used to connect and communicate to the server
            this.socket = new Socket("localhost", 2077);
            // Get each stream of the socket and make it as an array buffer
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            disconnect();
            return;
        }
        login();
    }

    public void readInputs() {
        // We use the run method of the interface Runnable to have a secondary thread
        // listening in a loop for the input of its buffer reader
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (socket.isConnected()) {
                        // We print in the console every message the Handler give the Client
                        System.out.println(bufferedReader.readLine());
                    }
                } catch (IOException e) {
                    disconnect();
                }
            }
        }).start();
    }

    public void writeOutputs() {
        try {
            while (socket.isConnected()) {
                // We wait for the next input
                String msg = scanner.nextLine();
                // If there is an keyboardinput (with Enter pressed to make it a "NextLine"
                // array), we write the message in the writer buffer
                bufferedWriter.write(pseudo + " : " + msg);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            disconnect();
        }
    }

    public static String register() {
        String newPseudo;
        System.out.print("Login on Server as your Pseudo : ");
        newPseudo = scanner.nextLine();
        return newPseudo;
    }

    public void login() {
        try {
            // Send to the Handler the ID first
            bufferedWriter.write(id);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            // Then the pseudo (cf the constructor of the Handler, waiting for these
            // informations)
            bufferedWriter.write(pseudo);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            disconnect();
        }
    }

    public void disconnect() {

        System.out.println("Server not reachable, try again later !");
        // No more use of its scanner
        scanner.close();
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
    }

    public void choseSessionType() throws IOException {
        String choice;
        while (true) {
            System.out.println("Game type ?\nPlayer vs Player -> 1\\nPlayer VS IA -> 2");
            choice = scanner.nextLine();
            switch (choice) {
                case "1":
                case "2":
                    bufferedWriter.write(choice);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    return;
                default:
                    System.out.println("Unknown command, try again");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String newClientPseudo = register();
        // Creating a new Client will try to connect to the socket of the server
        Client client = new Client(newClientPseudo);

        // Wait for the session type (PvP or vs IA)
        client.choseSessionType();
        // method that will create a thread to read inputs indefinitely
        client.readInputs();
        // Main thread will loop for the keyboard input to output to the server
        client.writeOutputs();
    }
}
