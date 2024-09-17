package me.padej.packets.file;

import net.minecraft.client.MinecraftClient;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FolderUtils {
    public static void openPacketLogsFolder() {
        File packetLogsFolder = new File(MinecraftClient.getInstance().runDirectory, "packet-logs");
        if (!packetLogsFolder.exists()) {
            packetLogsFolder.mkdirs(); // Создаем папку, если она не существует
        }

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(packetLogsFolder);
            } else {
                // Альтернативный способ открыть папку
                String os = java.lang.System.getProperty("os.name").toLowerCase();
                if (os.contains("win")) {
                    Runtime.getRuntime().exec("explorer " + packetLogsFolder.getAbsolutePath());
                } else if (os.contains("mac")) {
                    Runtime.getRuntime().exec("open " + packetLogsFolder.getAbsolutePath());
                } else if (os.contains("nix") || os.contains("nux")) {
                    Runtime.getRuntime().exec("xdg-open " + packetLogsFolder.getAbsolutePath());
                } else {
                    java.lang.System.out.println("Открытие папки не поддерживается на этой операционной системе.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
