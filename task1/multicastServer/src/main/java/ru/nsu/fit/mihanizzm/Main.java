package ru.nsu.fit.mihanizzm;

import java.io.IOException;
import java.net.InetAddress;

public class Main {
    public static void main(String[] args) {
        if (args[0] == null || args[1] == null) {
            System.err.println("You didn't pass group IP address or port. Try again. Usage: first argument is an IP address of multicast group and the second one is a port you want to listen on.");
        }
        InetAddress groupAddress = CommandLineArgumentParser.parseIP(args[0]);
        int port = CommandLineArgumentParser.parsePort(args[1]);

        System.out.println("Listening multicast group " + groupAddress.getHostAddress() + " on port " + port + "...");
        MulticastReceiver receiver = new MulticastReceiver();
        try {
            receiver.receive(groupAddress, port);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}