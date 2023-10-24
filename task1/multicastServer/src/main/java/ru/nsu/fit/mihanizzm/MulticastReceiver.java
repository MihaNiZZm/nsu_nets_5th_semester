package ru.nsu.fit.mihanizzm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MulticastReceiver {
    byte[] buffer = new byte[256];
    MulticastSocket socket;

    // Map to store active users and their last stay message timestamp
    ConcurrentMap<AddressAndPort, Long> activeUsers = new ConcurrentHashMap<>();

    // Starts a thread to display info about active users every 5 seconds.
    public void startInfoDisplayThread() {
        Thread infoDisplayThread = new Thread(() -> {
            while (true) {
                printActiveUsers();
                try {
                    Thread.sleep(5000); // Sleep for 5 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        infoDisplayThread.setDaemon(true); // Set as a daemon thread to terminate when the main thread exits.
        infoDisplayThread.start();
    }

    private void printActiveUsers() {
        System.out.println();
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        System.out.println("[" + formatter.format(currentTime) + "] Current list of active users:");
        activeUsers.keySet().forEach(System.out::println);
    }

    private void removeInactiveClientsThread() {
        Thread removeInactiveClientsThread = new Thread(() -> {
            while (true) {
                removeInactiveClients();
            }
        });

        removeInactiveClientsThread.setDaemon(true);
        removeInactiveClientsThread.start();
    }

    // Remove inactive clients
    private void removeInactiveClients() {
        long currentTime = System.currentTimeMillis();
        activeUsers.forEach((client, lastActivityTime) -> {
            if (currentTime - lastActivityTime > 5000) {
                activeUsers.remove(client, lastActivityTime);
                System.out.println("User " + client + " has become inactive and was removed.");
            }
        });
    }

    // Starts an endless loop and receiving packet on a multicast socket with given group address on a given port.
    public void receive(InetAddress address, int port) throws IOException {
        startInfoDisplayThread();
        removeInactiveClientsThread();

        socket = new MulticastSocket(port);
        socket.joinGroup(address);
        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String received = new String(packet.getData(), 0, packet.getLength());

            if (received.equals("enter")) {
                AddressAndPort newUser = new AddressAndPort(packet.getAddress().getHostAddress(), packet.getPort());
                activeUsers.put(newUser, System.currentTimeMillis());
                System.out.println("User " + newUser + " has entered the multicast group.");
            }
            if (received.equals("exit")) {
                AddressAndPort newUser = new AddressAndPort(packet.getAddress().getHostAddress(), packet.getPort());
                activeUsers.remove(newUser);
                System.out.println("User " + newUser + " has left the multicast group.");
            }
            if (received.equals("stay")) {
                AddressAndPort user = new AddressAndPort(packet.getAddress().getHostAddress(), packet.getPort());
                if (activeUsers.containsKey(user)) {
                    // Update the last stay time for an existing user
                    activeUsers.put(user, System.currentTimeMillis());
                } else {
                    // Add a new user to the active users list
                    activeUsers.put(user, System.currentTimeMillis());
                    System.out.println("User " + user + " has entered the multicast group.");
                }
            }
        }
    }
}
