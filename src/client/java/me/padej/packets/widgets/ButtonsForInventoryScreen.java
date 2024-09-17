package me.padej.packets.widgets;

import me.padej.packets.file.FolderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.util.Identifier;
import me.padej.packets.screen.OptionsScreen;

public abstract class ButtonsForInventoryScreen {

    private final int width;
    private final int height;

    private static final Identifier OPTIONS_FOCUSED = new Identifier("packets", "options/focused");
    private static final Identifier OPTIONS_UNFOCUSED = new Identifier("packets", "options/unfocused");
    private static final Identifier FOLDER_FOCUSED = new Identifier("packets", "folder/focused");
    private static final Identifier FOLDER_UNFOCUSED = new Identifier("packets", "folder/unfocused");

    public ButtonsForInventoryScreen(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public TexturedButtonWidget createOptionsButton() {
        return new TexturedButtonWidget(this.width - this.width / 20, this.height - this.height / 20 - 30, 20, 20, new ButtonTextures(OPTIONS_UNFOCUSED, OPTIONS_FOCUSED), (button) -> MinecraftClient.getInstance().setScreen(new OptionsScreen(MinecraftClient.getInstance())));
    }

    public TexturedButtonWidget createFolderButton() {
        return new TexturedButtonWidget(this.width - this.width / 20, this.height - this.height / 20 - 50 - 5, 20, 20, new ButtonTextures(FOLDER_UNFOCUSED, FOLDER_FOCUSED), (button) -> FolderUtils.openPacketLogsFolder());
    }
}

