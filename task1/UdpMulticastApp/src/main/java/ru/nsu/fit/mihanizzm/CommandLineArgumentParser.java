package ru.nsu.fit.mihanizzm;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class CommandLineArgumentParser {

    public static InetAddress parseIP(String arg) {
        InetAddress ip;
        try {
            ip = InetAddress.getByName(arg);
        } catch (UnknownHostException e) {
            throw new RuntimeException("Couldn't parse an IP address.");
        }
        return ip;
    }

    public static int parsePort(String arg) {
        return Integer.parseInt(arg);
    }
}
