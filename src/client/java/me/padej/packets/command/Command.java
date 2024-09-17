package me.padej.packets.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import me.padej.packets.file.LogsFile;
import me.padej.packets.file.PacketManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

import java.util.Timer;
import java.util.TimerTask;

public class Command extends AbstractCommand {
    public static boolean isRecording = false;
    public static boolean isCountdown = false;
    public static int countdownSeconds = 0;

    private Timer timer;

    @Override
    public void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(ClientCommandManager.literal("packets")
                .then(ClientCommandManager.literal("start")
                        .executes(context -> {
                            startRecording(false, 0);
                            return 1;
                        })
                        .then(ClientCommandManager.argument("seconds", IntegerArgumentType.integer(1))
                                .executes(context -> {
                                    int seconds = IntegerArgumentType.getInteger(context, "seconds");
                                    startRecording(true, seconds);
                                    scheduleStop(seconds);
                                    return 1;
                                })))
                .then(ClientCommandManager.literal("stop")
                        .executes(context -> {
                            stopRecording();
                            return 1;
                        }))
        );
    }

    private void startRecording(boolean countdown, int seconds) {
        isRecording = true;
        isCountdown = countdown;
        countdownSeconds = seconds;
        PacketManager.packets.clear();
        if (timer != null) {
            timer.cancel();
        }
        String message = I18n.translate("message.packets.start");
        MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.of(message), true);
    }

    private void stopRecording() {
        isRecording = false;
        isCountdown = false;
        countdownSeconds = 0;
        LogsFile.dump();
        String message = I18n.translate("message.packets.saved");
        MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.of(message), true);
    }

    private void scheduleStop(int seconds) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (countdownSeconds > 0) {
                    countdownSeconds--;
                } else {
                    stopRecording();
                    timer.cancel();
                }
            }
        }, 0, 1000);
    }
}
