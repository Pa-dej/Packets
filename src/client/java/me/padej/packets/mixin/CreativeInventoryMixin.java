package me.padej.packets.mixin;

import me.padej.packets.widgets.ButtonsForInventoryScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryMixin extends Screen {

    protected CreativeInventoryMixin() {
        super(Text.of(""));
    }

    @Inject(at = @At("TAIL"), method = "init")
    private void addCustomButton(CallbackInfo ci) {
        ButtonsForInventoryScreen buttons = new ButtonsForInventoryScreen(this.width, this.height) {};

        this.addDrawableChild(buttons.createFolderButton());
        this.addDrawableChild(buttons.createOptionsButton());
    }
}

