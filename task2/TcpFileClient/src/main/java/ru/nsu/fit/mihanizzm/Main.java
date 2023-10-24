package ru.nsu.fit.mihanizzm;

public class Main {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: java FileClient <server_address> <server_port> <file_path>");
            System.exit(1);
        }

        String serverAddress = args[0];
        int serverPort = Integer.parseInt(args[1]);
        String filePath = args[2];

        FileSender.sendFile(serverAddress, serverPort, filePath);
    }
}