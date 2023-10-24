package ru.nsu.fit.mihanizzm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class MulticastSender {
    private DatagramSocket socket;
    private InetAddress group;
    private int port;
    private byte[] buffer;

    public MulticastSender(InetAddress addr, int port) {
        try {
            socket = new DatagramSocket();
        }
        catch (SocketException e) {
            e.printStackTrace();
        }
        this.group = addr;
        this.port = port;
    }

    // Sends a multicast message via UDP to a multicast group address with a given string message on a given port.
    public void multicast(String message) throws IOException {
        buffer = message.getBytes();

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
        socket.send(packet);
    }

    public void stopCasting() {
        socket.close();
    }
}
