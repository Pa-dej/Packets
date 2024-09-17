package me.padej.packets.client;

import me.padej.packets.command.Command;
import me.padej.packets.particle.PointParticle;
import me.padej.packets.particle.PosPointParticle;
import me.padej.packets.screen.OptionsScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

import static me.padej.packets.Packets.POINT;
import static me.padej.packets.Packets.POS_POINT;

public class PacketsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ParticleFactoryRegistry.getInstance().register(POINT, PointParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(POS_POINT, PosPointParticle.Factory::new);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (Command.isRecording && MinecraftClient.getInstance().player != null) {
                if (Command.isCountdown) {
                    String message = I18n.translate("message.packets.countdown", Command.countdownSeconds);
                    MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.of(message), true);
                } else {
                    String message = I18n.translate("message.packets.recording");
                    MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.of(message), true);
                }
            }
        });

        new OptionsScreen(MinecraftClient.getInstance()).loadPacketOptions();

        // Регистрируем команды
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            new Command().registerCommands(dispatcher, registryAccess);
        });
    }
}