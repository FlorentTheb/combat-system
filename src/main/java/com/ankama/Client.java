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

    private static final Scanner scanner = new Scanner(System.in);

    private Socket socket;
    private String pseudo;
    private String id;

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client(String pseudo) {

        this.pseudo = pseudo;
        this.id = UUID.randomUUID().toString();

        try {
            this.socket = new Socket("localhost", 2077);
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            disconnect();
            return;
        }
        login();
    }

    public void readInputs() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (socket.isConnected()) {
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
                String msg = scanner.nextLine();
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
        System.out.println("Login on Server as your Pseudo : ");
        newPseudo = scanner.nextLine();
        return newPseudo;
    }

    public void login() {
        try {
            bufferedWriter.write(id);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            bufferedWriter.write(pseudo);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            disconnect();
        }
    }

    public void disconnect() {

        System.out.println("Server not reachable, try again later !");
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

    public static void main(String[] args) throws IOException {
        String newClientPseudo = register();
        Client client = new Client(newClientPseudo);

        client.readInputs();
        client.writeOutputs();
    }
}
