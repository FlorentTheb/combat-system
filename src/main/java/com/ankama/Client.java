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

    public Client(Socket socket, String pseudo) {

        this.socket = socket;
        this.clientPseudo = pseudo;
        this.clientID = UUID.randomUUID().toString();

        try {
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String login() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Login on Server as your Pseudo : ");
        return scanner.nextLine();

    }

    public static void main(String[] args) throws IOException {
        String pseudo = login();
        Socket socket = new Socket("localhost", 2077);
        Client client = new Client(socket, pseudo);
    }
}
