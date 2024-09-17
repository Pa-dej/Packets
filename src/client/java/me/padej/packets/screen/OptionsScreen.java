package me.padej.packets.screen;

import com.google.gson.Gson;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Environment(EnvType.CLIENT)
public class OptionsScreen extends AbstractOptionsScreen implements PacketOptionsManager {
    public static List<PacketOption> packetOptions;
    private static final String CONFIG_FILE_NAME = "packet_options.json";

    public OptionsScreen(MinecraftClient client) {
        super(client, "OptionsScreenC2S");
        packetOptions = createPacketOptions();
    }

    @Override
    protected void init() {
        initSearchField();
        loadPacketOptions();
        arrangeCheckboxes(this.searchField.getText());

    }

    @Override
    protected void onSearchTextChanged(String searchText) {
        arrangeCheckboxes(searchText);
    }

    public List<PacketOption> createPacketOptions() {
        List<PacketOption> options = new ArrayList<>();
        // C2S
        options.add(new PacketOption("ClickSlotC2SPacket", true));
        options.add(new PacketOption("UpdateSelectedSlotC2SPacket", true));
        options.add(new PacketOption("ClientStatusC2SPacket", true));
        options.add(new PacketOption("ChatMessageC2SPacket", true));
        options.add(new PacketOption("CommonPongC2SPacket", true));
        options.add(new PacketOption("CraftRequestC2SPacket", true));
        options.add(new PacketOption("PlayerInteractItemC2SPacket", true));
        options.add(new PacketOption("PickFromInventoryC2SPacket", true));
        options.add(new PacketOption("CommandExecutionC2SPacket", true));
        options.add(new PacketOption("HandSwingC2SPacket", true));
        options.add(new PacketOption("PlayerInputC2SPacket", true));
        options.add(new PacketOption("PlayerActionC2SPacket", true));
        options.add(new PacketOption("PlayerInteractBlockC2SPacket", true));
        options.add(new PacketOption("KeepAliveC2SPacket", true));
        options.add(new PacketOption("CreativeInventoryActionC2SPacket", true));
        options.add(new PacketOption("CloseHandledScreenC2SPacket", true));
        options.add(new PacketOption("ClientCommandC2SPacket", true));
        options.add(new PacketOption("UpdatePlayerAbilitiesC2SPacket", true));
        options.add(new PacketOption("RequestCommandCompletionsC2SPacket", true));
        options.add(new PacketOption("PlayerInteractEntityC2SPacket", true));
        options.add(new PacketOption("TeleportConfirmC2SPacket", true));
        options.add(new PacketOption("BoatPaddleStateC2SPacket", true));
        options.add(new PacketOption("VehicleMoveC2SPacket", true));
        options.add(new PacketOption("RenameItemC2SPacket", true));
        options.add(new PacketOption("PlayerMoveC2SPacket", true));
        options.add(new PacketOption("AcknowledgeChunksC2SPacket", true));
        options.add(new PacketOption("CustomPayloadC2SPacket", true));
        options.add(new PacketOption("ClientOptionsC2SPacket", true));
        options.add(new PacketOption("ButtonClickC2SPacket", true));
        options.add(new PacketOption("UpdateBeaconC2SPacket", true));
        options.add(new PacketOption("AdvancementTabC2SPacket", true));
        options.add(new PacketOption("RecipeBookDataC2SPacket", true));
        options.add(new PacketOption("BookUpdateC2SPacket", true));
        options.add(new PacketOption("JigsawGeneratingC2SPacket", true));
        options.add(new PacketOption("QueryBlockNbtC2SPacket", true));
        options.add(new PacketOption("QueryEntityNbtC2SPacket", true));
        options.add(new PacketOption("ResourcePackStatusC2SPacket", true));
        options.add(new PacketOption("SelectMerchantTradeC2SPacket", true));
        options.add(new PacketOption("SpectatorTeleportC2SPacket", true));
        options.add(new PacketOption("UpdateCommandBlockC2SPacket", true));
        options.add(new PacketOption("UpdateCommandBlockMinecartC2SPacket", true));
        options.add(new PacketOption("UpdateDifficultyC2SPacket", true));
        options.add(new PacketOption("UpdateDifficultyLockC2SPacket", true));
        options.add(new PacketOption("UpdateJigsawC2SPacket", true));
        options.add(new PacketOption("UpdateSignC2SPacket", true));
        options.add(new PacketOption("UpdateStructureBlockC2SPacket", true));
        options.add(new PacketOption("LoginHelloC2SPacket", true));
        options.add(new PacketOption("LoginKeyC2SPacket", true));
        options.add(new PacketOption("LoginQueryResponseC2SPacket", true));
        options.add(new PacketOption("HandshakeC2SPacket", true));
        options.add(new PacketOption("EnterConfigurationC2SPacket", true));
        options.add(new PacketOption("ReadyC2SPacket", true));
        options.add(new PacketOption("QueryPingC2SPacket", true));
        // S2C
        options.add(new PacketOption("PlayerListS2CPacket", true));
        options.add(new PacketOption("ChatMessageS2CPacket", true));
        options.add(new PacketOption("BlockUpdateS2CPacket", true));
        options.add(new PacketOption("EntityStatusS2CPacket", true));
        options.add(new PacketOption("WorldTimeUpdateS2CPacket", true));
        options.add(new PacketOption("GameStateChangeS2CPacket", true));
        options.add(new PacketOption("EntitySpawnS2CPacket", true));
        options.add(new PacketOption("UnloadChunkS2CPacket", true));
        options.add(new PacketOption("ResourcePackSendS2CPacket", true));
        options.add(new PacketOption("OpenScreenS2CPacket", true));
        options.add(new PacketOption("PlayerAbilitiesS2CPacket", true));
        options.add(new PacketOption("ExperienceBarUpdateS2CPacket", true));
        options.add(new PacketOption("EntityPositionS2CPacket", true));
        options.add(new PacketOption("WorldEventS2CPacket", true));
        options.add(new PacketOption("SynchronizeRecipesS2CPacket", true));
        options.add(new PacketOption("EntityS2CPacket", true));
        options.add(new PacketOption("EntitySetHeadYawS2CPacket", true));
        options.add(new PacketOption("EntityVelocityUpdateS2CPacket", true));
        options.add(new PacketOption("BundleS2CPacket", true));
        options.add(new PacketOption("EntityTrackerUpdateS2CPacket", true));
        options.add(new PacketOption("ParticleS2CPacket", true));
        options.add(new PacketOption("GameMessageS2CPacket", true));
        options.add(new PacketOption("InventoryS2CPacket", true));
        options.add(new PacketOption("ScreenHandlerSlotUpdateS2CPacket", true));
        options.add(new PacketOption("PlaySoundS2CPacket", true));
        options.add(new PacketOption("PlayerActionResponseS2CPacket", true));
        options.add(new PacketOption("EntitiesDestroyS2CPacket", true));
        options.add(new PacketOption("KeepAliveS2CPacket", true));
        options.add(new PacketOption("ChunkRenderDistanceCenterS2CPacket", true));
        options.add(new PacketOption("ChunkDataS2CPacket", true));
        options.add(new PacketOption("CommandTreeS2CPacket", true));
        options.add(new PacketOption("EntityAttributesS2CPacket", true));
        options.add(new PacketOption("PlayerListHeaderS2CPacket", true));
        options.add(new PacketOption("CommandSuggestionsS2CPacket", true));
        options.add(new PacketOption("OverlayMessageS2CPacket", true));
        options.add(new PacketOption("UnlockRecipesS2CPacket", true));
        options.add(new PacketOption("AdvancementUpdateS2CPacket", true));
        options.add(new PacketOption("BlockEventS2CPacket", true));
        options.add(new PacketOption("ChunkDeltaUpdateS2CPacket", true));
        options.add(new PacketOption("EntityStatusEffectS2CPacket", true));
        options.add(new PacketOption("HealthUpdateS2CPacket", true));
        options.add(new PacketOption("RemoveEntityStatusEffectS2CPacket", true));
        options.add(new PacketOption("EntityDamageS2CPacket", true));
        options.add(new PacketOption("ItemPickupAnimationS2CPacket", true));
        options.add(new PacketOption("PlayerPositionLookS2CPacket", true));
        options.add(new PacketOption("EntityPassengersSetS2CPacket", true));
        options.add(new PacketOption("ScreenHandlerPropertyUpdateS2CPacket", true));
        options.add(new PacketOption("EntityAnimationS2CPacket", true));
        options.add(new PacketOption("StartChunkSendS2CPacket", true));
        options.add(new PacketOption("ChunkSentS2CPacket", true));
        options.add(new PacketOption("PlaySoundFromEntityS2CPacket", true));
        options.add(new PacketOption("EntityAttachS2CPacket", true));
        options.add(new PacketOption("EntityEquipmentUpdateS2CPacket", true));
        options.add(new PacketOption("CustomPayloadS2CPacket", true));
        options.add(new PacketOption("EnterCombatS2CPacket", true));
        options.add(new PacketOption("EndCombatS2CPacket", true));
        options.add(new PacketOption("ExplosionS2CPacket", true));
        options.add(new PacketOption("SelectAdvancementTabS2CPacket", true));
        options.add(new PacketOption("CooldownUpdateS2CPacket", true));
        options.add(new PacketOption("MapUpdateS2CPacket", true));
        options.add(new PacketOption("BossBarS2CPacket", true));
        options.add(new PacketOption("BlockBreakingProgressS2CPacket", true));
        options.add(new PacketOption("BlockEntityUpdateS2CPacket", true));
        options.add(new PacketOption("ChunkLoadDistanceS2CPacket", true));
        options.add(new PacketOption("ClearTitleS2CPacket", true));
        options.add(new PacketOption("CloseScreenS2CPacket", true));
        options.add(new PacketOption("CraftFailedResponseS2CPacket", true));
        options.add(new PacketOption("DeathMessageS2CPacket", true));
        options.add(new PacketOption("DifficultyS2CPacket", true));
        options.add(new PacketOption("DisconnectS2CPacket", true));
        options.add(new PacketOption("ExperienceOrbSpawnS2CPacket", true));
        options.add(new PacketOption("GameJoinS2CPacket", true));
        options.add(new PacketOption("LightUpdateS2CPacket", true));
        options.add(new PacketOption("LookAtS2CPacket", true));
        options.add(new PacketOption("NbtQueryResponseS2CPacket", true));
        options.add(new PacketOption("OpenHorseScreenS2CPacket", true));
        options.add(new PacketOption("OpenWrittenBookS2CPacket", true));
        options.add(new PacketOption("PlayerRespawnS2CPacket", true));
        options.add(new PacketOption("PaintingSpawnS2CPacket", true));
        options.add(new PacketOption("PlayerSpawnPositionS2CPacket", true));
        options.add(new PacketOption("ScoreboardDisplayS2CPacket", true));
        options.add(new PacketOption("SetCameraEntityS2CPacket", true));
        options.add(new PacketOption("SignEditorOpenS2CPacket", true));
        options.add(new PacketOption("StatisticsS2CPacket", true));
        options.add(new PacketOption("StopSoundS2CPacket", true));
        options.add(new PacketOption("SubtitleS2CPacket", true));
        options.add(new PacketOption("SynchronizeTagsS2CPacket", true));
        options.add(new PacketOption("TeamS2CPacket", true));
        options.add(new PacketOption("TitleFadeS2CPacket", true));
        options.add(new PacketOption("UpdateSelectedSlotS2CPacket", true));
        options.add(new PacketOption("VehicleMoveS2CPacket", true));
        options.add(new PacketOption("WorldBorderCenterChangedS2CPacket", true));
        options.add(new PacketOption("WorldBorderInitializeS2CPacket", true));
        options.add(new PacketOption("WorldBorderInterpolateSizeS2CPacket", true));
        options.add(new PacketOption("WorldBorderSizeChangedS2CPacket", true));
        options.add(new PacketOption("WorldBorderWarningBlocksChangedS2CPacket", true));
        options.add(new PacketOption("LoginCompressionS2CPacket", true));
        options.add(new PacketOption("LoginDisconnectS2CPacket", true));
        options.add(new PacketOption("LoginHelloS2CPacket", true));
        options.add(new PacketOption("LoginQueryRequestS2CPacket", true));
        options.add(new PacketOption("LoginSuccessS2CPacket", true));
        options.add(new PacketOption("FeaturesS2CPacket", true));
        options.add(new PacketOption("ReadyS2CPacket", true));
        options.add(new PacketOption("DynamicRegistriesS2CPacket", true));
        options.add(new PacketOption("CommonPingS2CPacket", true));
        options.add(new PacketOption("ServerMetadataS2CPacket", true));
        options.add(new PacketOption("UpdateTickRateS2CPacket", true));
        options.add(new PacketOption("TickStepS2CPacket", true));
        options.add(new PacketOption("PingResultS2CPacket", true));
        options.add(new PacketOption("QueryResponseS2CPacket", true));

        return options;
    }

    @Override
    public void savePacketOptions() {
        try {
            Gson gson = new Gson();
            File file = new File(this.client.runDirectory, CONFIG_FILE_NAME);
            FileWriter writer = new FileWriter(file);
            gson.toJson(packetOptions, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadPacketOptions() {
        try {
            Gson gson = new Gson();
            File file = new File(this.client.runDirectory, CONFIG_FILE_NAME);
            if (file.exists()) {
                FileReader reader = new FileReader(file);
                PacketOption[] optionsArray = gson.fromJson(reader, PacketOption[].class);
                reader.close();

                Map<String, PacketOption> loadedOptionsMap = new HashMap<>();
                for (PacketOption option : optionsArray) {
                    loadedOptionsMap.put(option.name, option);
                }

                for (PacketOption packetOption : packetOptions) {
                    if (loadedOptionsMap.containsKey(packetOption.name)) {
                        packetOption.enabled = loadedOptionsMap.get(packetOption.name).enabled;
                    }
                }

                for (PacketOption loadedOption : loadedOptionsMap.values()) {
                    boolean existsInCurrentOptions = packetOptions.stream()
                            .anyMatch(option -> option.name.equals(loadedOption.name));
                    if (!existsInCurrentOptions) {
                        packetOptions.add(loadedOption);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        savePacketOptions();
        super.close();
    }

    public static class PacketOption {
        public String name;
        public boolean enabled;

        public PacketOption(String name, boolean enabled) {
            this.name = name;
            this.enabled = enabled;
        }
    }
}
