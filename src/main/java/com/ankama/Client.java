package com.ankama;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.UUID;

import javax.imageio.IIOException;

public class Client {
    private Socket socket;
    private String clientPseudo;
    private String clientID;

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private Scanner clientScanner;

    public Client() {
        this.clientScanner = new Scanner(System.in);

        try {
            this.socket = new Socket("localhost", 2077);
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
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
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void writeOutputs() {
        try {
            while (socket.isConnected()) {
                String msg = clientScanner.nextLine();
                bufferedWriter.write(clientPseudo + " : " + msg);
                bufferedWriter.newLine();
                bufferedWriter.flush();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void login() {
        System.out.println("Login on Server as your Pseudo : ");
        clientPseudo = clientScanner.nextLine();
        clientID = UUID.randomUUID().toString();
        try {
            bufferedWriter.write(clientID);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();

        client.readInputs();
    }
}
