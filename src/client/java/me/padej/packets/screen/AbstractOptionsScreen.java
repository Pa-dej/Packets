package me.padej.packets.screen;

import me.padej.packets.widgets.CheckboxWidgetWithConditionSetter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

import static me.padej.packets.screen.OptionsScreen.packetOptions;

@Environment(EnvType.CLIENT)
public abstract class AbstractOptionsScreen extends Screen {
    protected final MinecraftClient client;
    protected TextFieldWidget searchField;
    protected List<CheckboxWidgetWithConditionSetter> packetButtons;
    protected float scrollPosition;
    protected float targetScrollPosition;

    public AbstractOptionsScreen(MinecraftClient client, String title) {
        super(Text.literal(title));
        this.client = client;
        this.packetButtons = new ArrayList<>();
        this.scrollPosition = 0;
        this.targetScrollPosition = 0;
    }

    protected void initSearchField() {
        this.searchField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 20, 200, 20, Text.literal("Search"));
        this.searchField.setChangedListener(this::onSearchTextChanged);
        this.addDrawableChild(this.searchField);
    }

    protected void arrangeCheckboxes(String searchText) {
        this.packetButtons.clear();
        this.clearChildren();

        this.searchField.setPosition(this.width / 2 - 100, 20 - (int) scrollPosition);
        this.addDrawableChild(this.searchField);

        this.addDrawableChild(ButtonWidget.builder(Text.literal(I18n.translate("button.enable_all")), button -> enableAllCheckboxes())
                .dimensions(this.width / 2 - 150, 45 - (int) scrollPosition, 100, 20)
                .build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal(I18n.translate("button.disable_all")), button -> disableAllCheckboxes())
                .dimensions(this.width / 2 - 50, 45 - (int) scrollPosition, 100, 20)
                .build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal(I18n.translate("button.toggle_all")), button -> toggleAllCheckboxes())
                .dimensions(this.width / 2 + 50, 45 - (int) scrollPosition, 100, 20)
                .build());

        int padding = 10;
        int checkboxHeight = 20;
        int y = 70 - (int) scrollPosition;

        for (OptionsScreen.PacketOption packetOption : packetOptions) {
            if (packetOption.name.toLowerCase().contains(searchText.toLowerCase())) {
                CheckboxWidgetWithConditionSetter packetButton = CheckboxWidgetWithConditionSetter.builder(
                                Text.literal(packetOption.name).styled(style -> style.withColor(getPacketColor(packetOption.name))), this.textRenderer)
                        .checked(packetOption.enabled)
                        .callback((checkbox, checked) -> packetOption.enabled = checked)
                        .build();

                int checkboxWidth = packetButton.getWidth();
                int x = (this.width - checkboxWidth) / 2;
                packetButton.setPosition(x, y);

                this.packetButtons.add(packetButton);
                this.addDrawableChild(packetButton);
                y += checkboxHeight + padding;
            }
        }

        this.targetScrollPosition = Math.min(this.targetScrollPosition, packetOptions.size() * (checkboxHeight + padding));
    }

    private int getPacketColor(String packetName) {
        if (packetName.equalsIgnoreCase("CustomPayloadC2SPacket") || packetName.equalsIgnoreCase("CustomPayloadS2CPacket")) {
            return 0x1ebc73;
        } else if (packetName.equalsIgnoreCase("ChunkDeltaUpdateS2CPacket")) {
            return 0xc32454;
        } else if (packetName.endsWith("S2CPacket")) {
            return 0xeaaded;
        } else if (packetName.endsWith("C2SPacket")) {
            return 0x8fd3ff;
        }
        return 0xFFFFFF;
    }

    protected abstract void onSearchTextChanged(String searchText);

    protected void enableAllCheckboxes() {
        for (CheckboxWidgetWithConditionSetter checkbox : packetButtons) {
            if (!checkbox.isChecked()) {
                checkbox.setChecked(true);
                updatePacketOption(checkbox, true);
            }
        }
    }

    protected void disableAllCheckboxes() {
        for (CheckboxWidgetWithConditionSetter checkbox : packetButtons) {
            if (checkbox.isChecked()) {
                checkbox.setChecked(false);
                updatePacketOption(checkbox, false);
            }
        }
    }

    protected void toggleAllCheckboxes() {
        for (CheckboxWidgetWithConditionSetter checkbox : packetButtons) {
            boolean newState = !checkbox.isChecked();
            checkbox.setChecked(newState);
            updatePacketOption(checkbox, newState);
        }
    }

    private void updatePacketOption(CheckboxWidgetWithConditionSetter checkbox, boolean enabled) {
        packetOptions.stream()
                .filter(option -> option.name.equals(checkbox.getMessage().getString()))
                .findFirst()
                .ifPresent(option -> option.enabled = enabled);
    }

    @Override
    public void tick() {
        this.scrollPosition += (this.targetScrollPosition - this.scrollPosition) * 0.1f;
        arrangeCheckboxes(this.searchField.getText());
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        this.targetScrollPosition -= (float) (verticalAmount * 30);
        this.targetScrollPosition = Math.max(0, this.targetScrollPosition);
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        this.targetScrollPosition -= (float) (deltaY * 2);
        this.targetScrollPosition = Math.max(0, this.targetScrollPosition);
        return true;
    }
}