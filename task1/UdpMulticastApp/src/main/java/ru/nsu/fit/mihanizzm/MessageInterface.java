package ru.nsu.fit.mihanizzm;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

public class MessageInterface {
    // Prints usage of the application
    public static void printUsage(InetAddress address, int port) {
        System.out.println("You have entered multicast group with " + address.getHostAddress() + " address on " + port + " port.\nTo quit it and exit the program type \"exit\". All other messages will be ignored.");
    }

    public static void stayAliveNotifierThread(MulticastSender sender) {
        Thread notifier = new Thread(() -> {
            while (true) {
                try {
                    sender.multicast("stay");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        notifier.setDaemon(true);
        notifier.start();
    }

    // Starts an endless loop scanning the console and exits the multicast group if a user prints "exit".
    public static void loop(MulticastSender sender) throws IOException {
        stayAliveNotifierThread(sender);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (scanner.hasNext()) {
                if (scanner.nextLine().equals("exit")) {
                    sender.multicast("exit");
                    break;
                }
            }
        }
    }
}
