package me.padej.packets.screen;

import java.util.List;

public interface PacketOptionsManager {
    List<OptionsScreen.PacketOption> createPacketOptions();
    void savePacketOptions();
    void loadPacketOptions();
}
