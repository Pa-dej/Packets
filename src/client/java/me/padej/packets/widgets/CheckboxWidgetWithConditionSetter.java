package me.padej.packets.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class CheckboxWidgetWithConditionSetter extends PressableWidget {
    private static final Identifier SELECTED_HIGHLIGHTED_TEXTURE = new Identifier("widget/checkbox_selected_highlighted");
    private static final Identifier SELECTED_TEXTURE = new Identifier("widget/checkbox_selected");
    private static final Identifier HIGHLIGHTED_TEXTURE = new Identifier("widget/checkbox_highlighted");
    private static final Identifier TEXTURE = new Identifier("widget/checkbox");
    private boolean checked;
    private final CheckboxWidgetWithConditionSetter.Callback callback;

    CheckboxWidgetWithConditionSetter(int x, int y, Text message, TextRenderer textRenderer, boolean checked, CheckboxWidgetWithConditionSetter.Callback callback) {
        super(x, y, getSize(textRenderer) + 4 + textRenderer.getWidth(message), getSize(textRenderer), message);
        this.checked = checked;
        this.callback = callback;
    }

    public static CheckboxWidgetWithConditionSetter.Builder builder(Text text, TextRenderer textRenderer) {
        return new CheckboxWidgetWithConditionSetter.Builder(text, textRenderer);
    }

    private static int getSize(TextRenderer textRenderer) {
        Objects.requireNonNull(textRenderer);
        return 9 + 8;
    }

    public void onPress() {
        this.checked = !this.checked;
        this.callback.onValueChange(this, this.checked);
    }

    public boolean isChecked() {
        return this.checked;
    }

    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, this.getNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                builder.put(NarrationPart.USAGE, Text.translatable("narration.checkbox.usage.focused"));
            } else {
                builder.put(NarrationPart.USAGE, Text.translatable("narration.checkbox.usage.hovered"));
            }
        }

    }

    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        RenderSystem.enableDepthTest();
        TextRenderer textRenderer = minecraftClient.textRenderer;
        context.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        Identifier identifier;
        if (this.checked) {
            identifier = this.isFocused() ? SELECTED_HIGHLIGHTED_TEXTURE : SELECTED_TEXTURE;
        } else {
            identifier = this.isFocused() ? HIGHLIGHTED_TEXTURE : TEXTURE;
        }

        int i = getSize(textRenderer);
        int j = this.getX() + i + 4;
        int var10000 = this.getY() + (this.height >> 1);
        Objects.requireNonNull(textRenderer);
        int k = var10000 - (9 >> 1);
        context.drawGuiTexture(identifier, this.getX(), this.getY(), i, i);
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        context.drawTextWithShadow(textRenderer, this.getMessage(), j, k, 14737632 | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }

    @Environment(EnvType.CLIENT)
    public interface Callback {
        CheckboxWidgetWithConditionSetter.Callback EMPTY = (checkbox, checked) -> {
        };

        void onValueChange(CheckboxWidgetWithConditionSetter checkbox, boolean checked);
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        this.callback.onValueChange(this, this.checked);
    }

    @Environment(EnvType.CLIENT)
    public static class Builder {
        private final Text message;
        private final TextRenderer textRenderer;
        private int x = 0;
        private int y = 0;
        private CheckboxWidgetWithConditionSetter.Callback callback;
        private boolean checked;
        @Nullable
        private SimpleOption<Boolean> option;
        @Nullable
        private Tooltip tooltip;

        Builder(Text message, TextRenderer textRenderer) {
            this.callback = CheckboxWidgetWithConditionSetter.Callback.EMPTY;
            this.checked = false;
            this.option = null;
            this.tooltip = null;
            this.message = message;
            this.textRenderer = textRenderer;
        }

        public CheckboxWidgetWithConditionSetter.Builder callback(CheckboxWidgetWithConditionSetter.Callback callback) {
            this.callback = callback;
            return this;
        }

        public CheckboxWidgetWithConditionSetter.Builder checked(boolean checked) {
            this.checked = checked;
            this.option = null;
            return this;
        }

        public CheckboxWidgetWithConditionSetter.Builder option(SimpleOption<Boolean> option) {
            this.option = option;
            this.checked = (Boolean)option.getValue();
            return this;
        }

        public CheckboxWidgetWithConditionSetter.Builder tooltip(Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public CheckboxWidgetWithConditionSetter build() {
            CheckboxWidgetWithConditionSetter.Callback callback = this.option == null ? this.callback : (checkbox, checked) -> {
                this.option.setValue(checked);
                this.callback.onValueChange(checkbox, checked);
            };
            CheckboxWidgetWithConditionSetter checkboxWidget = new CheckboxWidgetWithConditionSetter(this.x, this.y, this.message, this.textRenderer, this.checked, callback);
            checkboxWidget.setTooltip(this.tooltip);
            return checkboxWidget;
        }
    }
}

