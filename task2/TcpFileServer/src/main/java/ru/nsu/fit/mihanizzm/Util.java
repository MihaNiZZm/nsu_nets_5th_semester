package ru.nsu.fit.mihanizzm;

public class Util {
    public static String convertSize(long size) {
        if (size < 1024) {
            return String.format("%d B", size);
        }
        else if (size < 1024 * 1024) {
            return String.format("%.2f KiB", (double) size / 1024);
        }
        else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MiB", (double) size / (1024 * 1024));
        }
        else {
            return String.format("%.2f GiB", (double) size / (1024 * 1024 * 1024));
        }
    }
}
