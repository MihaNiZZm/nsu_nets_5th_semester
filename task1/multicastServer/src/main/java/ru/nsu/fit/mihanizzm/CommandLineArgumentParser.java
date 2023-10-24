package ru.nsu.fit.mihanizzm;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class CommandLineArgumentParser {
    // Parses IP from command line.
    static InetAddress parseIP(String arg) {
        InetAddress ip;
        try {
            ip = InetAddress.getByName(arg);
        } catch (UnknownHostException e) {
            throw new RuntimeException("Couldn't parse an IP address.");
        }
        return ip;
    }

    // Parses port from command line.
    static int parsePort(String arg) {
        return Integer.parseInt(arg);
    }
}
