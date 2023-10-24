package ru.nsu.fit.mihanizzm;

import java.io.IOException;
import java.net.InetAddress;

public class Main {
    public static void main(String[] args) {
        if (args[0] == null || args[1] == null) {
            System.err.println("You didn't pass group IP address or port. Try again. Usage: first argument is an IP address of");
        }
        InetAddress groupAddress = CommandLineArgumentParser.parseIP(args[0]);
        int port = CommandLineArgumentParser.parsePort(args[1]);

        MulticastSender sender = new MulticastSender(groupAddress, port);
        try {
            sender.multicast("enter");
            MessageInterface.printUsage(groupAddress, port);
            MessageInterface.loop(sender);
            sender.stopCasting();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}