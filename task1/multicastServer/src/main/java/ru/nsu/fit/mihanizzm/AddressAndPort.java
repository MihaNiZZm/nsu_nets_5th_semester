package ru.nsu.fit.mihanizzm;

public record AddressAndPort(String address, int port) {
    @Override
    public String toString() {
        return address + ":" + port;
    }
}
