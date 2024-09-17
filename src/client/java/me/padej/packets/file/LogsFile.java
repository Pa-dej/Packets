package me.padej.packets.file;

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.mojang.text2speech.Narrator.LOGGER;

public class LogsFile {

    public static void dump() {
        if (PacketManager.packets.size() < 15) return;

        File logDir = new File(FabricLoader.getInstance().getGameDir().toString(), "packet-logs");
        if (logDir.mkdir() || logDir.exists()) {
            Path logFile = logDir.toPath().resolve(String.format("packets-%s.log", getCurrentTimeStamp()));
            try {
                Files.write(logFile, String.join("\n", PacketManager.packets).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                PacketManager.packets.clear();
            } catch (IOException e) {
                LOGGER.error("Error writing packets to log file: " + e.getMessage(), e);
            }
        }
    }

    private static String getCurrentTimeStamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
        return LocalDateTime.now().format(formatter);
    }

    private static void savePacketsToFile() {
        if (!PacketManager.packets.isEmpty()) {
            File logDir = new File(FabricLoader.getInstance().getGameDir().toString(), "packet-logs");
            if (logDir.mkdir() || logDir.exists()) {
                Path logFile = logDir.toPath().resolve(String.format("packets-%s.log", getCurrentTimeStamp()));
                try {
                    Files.write(logFile, String.join("\n", PacketManager.packets).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                    PacketManager.packets.clear();
                } catch (IOException e) {
                    LOGGER.error("Error writing packets to log file: " + e.getMessage(), e);
                }
            }
        }
    }
}
