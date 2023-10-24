package ru.nsu.fit.mihanizzm;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private long lastReceivedPacketTime;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        String fileName = "";
        long fileSize;
        try {
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

            fileName = in.readUTF();
            fileSize = in.readLong();
            System.out.println("[Thread " + Thread.currentThread().getId() + "] - Receiving file: \"" + fileName + "\" with size " + Util.convertSize(fileSize));

            File file = new File(FileReceiverThreadsManager.UPLOADS_DIRECTORY + File.separator + fileName);
            FileOutputStream fileOut = new FileOutputStream(file);

            byte[] buffer = new byte[8192];
            int bytesRead;
            long totalBytesRead = 0;

            long startTime = System.currentTimeMillis();
            long lastPrintTime = startTime;

            while ((bytesRead = in.read(buffer)) != -1) {
                fileOut.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;

                // Update the last received packet time
                lastReceivedPacketTime = System.currentTimeMillis();

                long currentTime = System.currentTimeMillis();
                if (currentTime - lastPrintTime >= 3000) {  // Print every 3 seconds
                    double elapsedTime = (currentTime - startTime) / 1000.0;
                    long transferSpeed = (long) (totalBytesRead / elapsedTime);
                    System.out.printf("[Thread %d] - Average speed: %s/s, progress: %.2f%%\n", Thread.currentThread().getId(), Util.convertSize(transferSpeed), (double) totalBytesRead * 100 / fileSize);
                    lastPrintTime = currentTime;
                }
                if (totalBytesRead == fileSize) {
                    break;
                }
            }

            double elapsedTime = (System.currentTimeMillis() - startTime) / 1000.0;
            long transferSpeed = (long) (totalBytesRead / elapsedTime);

            System.out.println("[Thread " + Thread.currentThread().getId() + "] - File received successfully. Average speed: " + Util.convertSize(transferSpeed));

            // Check if the received file size matches the expected size
            if (totalBytesRead != fileSize) {
                System.out.println("Received file size doesn't match the expected size. Deleting the file.");
                out.writeUTF("Received file was corrupted during the receive process. File will be deleted from the server.");
                file.delete(); // Remove the partially received file
            } else {
                // Send a success message to the client
                out.writeUTF("File received successfully.");
            }

            in.close();
            out.close();
            clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();

            // Send an error message to the client
            try {
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                out.writeUTF("An error occurred during file transfer.");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

            // Remove the partially received file if an error occurred
            File file = new File(FileReceiverThreadsManager.UPLOADS_DIRECTORY + File.separator + fileName);
            if (file.exists()) {
                System.out.println("An error occurred during file transfer. Deleting the partially received file.");
                file.delete();
            }
        }

        // Remove the partially received file if the connection was lost
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastReceivedPacketTime > 5000) { // Connection lost for more than 5 seconds
            File file = new File(FileReceiverThreadsManager.UPLOADS_DIRECTORY + File.separator + fileName);
            if (file.exists()) {
                System.out.println("Connection to the client was lost. Deleting the partially received file.");
                file.delete();
            }
        }
    }
}

