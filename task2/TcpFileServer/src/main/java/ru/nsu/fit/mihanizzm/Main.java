package ru.nsu.fit.mihanizzm;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java FileServer <server_port>");
            System.exit(1);
        }

        int serverPort = Integer.parseInt(args[0]);
        try {
            FileReceiverThreadsManager.start(serverPort);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}