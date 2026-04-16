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
    private Socket socket;
    private String pseudo;
    private String id;

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private Scanner clientScanner;

    public Client(String pseudo) {

        this.clientScanner = new Scanner(System.in);

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
                while (socket.isConnected()) {
                    try {
                        System.out.println(bufferedReader.readLine());
                    } catch (Exception e) {
                        disconnect();
                    }
                }
            }
        }).start();
    }

    public void writeOutputs() {
        try {
            while (socket.isConnected()) {
                String msg = clientScanner.nextLine();
                bufferedWriter.write(pseudo + " : " + msg);
                bufferedWriter.newLine();
                bufferedWriter.flush();

            }
        } catch (IOException e) {
            disconnect();
        }
    }

    public static String register() {
        Scanner registerScanner = new Scanner(System.in);
        String newPseudo;
        System.out.println("Login on Server as your Pseudo : ");
        newPseudo = registerScanner.nextLine();
        registerScanner.close();
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
