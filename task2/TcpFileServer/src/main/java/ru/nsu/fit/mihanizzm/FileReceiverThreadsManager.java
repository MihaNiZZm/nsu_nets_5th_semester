package ru.nsu.fit.mihanizzm;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileReceiverThreadsManager {
    public static final String UPLOADS_DIRECTORY = "uploads";

    public static void start(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        File uploadsDir = new File(UPLOADS_DIRECTORY);
        if (!uploadsDir.exists()) {
            uploadsDir.mkdirs();
        }

        System.out.println("Server is running and listening on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            executorService.submit(new ClientHandler(clientSocket));
        }
    }
}
