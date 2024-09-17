package me.padej.packets.file;

import com.mojang.authlib.GameProfile;
import me.padej.packets.command.Command;
import me.padej.packets.particle.CreatePointParticle;
import me.padej.packets.particle.PosPointParticle;
import me.padej.packets.screen.OptionsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.*;
import net.minecraft.network.packet.c2s.config.ReadyC2SPacket;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.*;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.common.*;
import net.minecraft.network.packet.s2c.config.*;
import net.minecraft.network.packet.s2c.login.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.hit.BlockHitResult;

import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.mojang.text2speech.Narrator.LOGGER;
import static me.padej.packets.Packets.POS_POINT;

public class PacketManager {

    public static final List<String> packets = new ArrayList<>();

    public static void addPacket(Packet<?> packet, boolean incoming) {
        if (!Command.isRecording) return;

        try {
            StringBuilder str = new StringBuilder(String.format("[%s] %s ", getCurrentHourStamp(), incoming ? "INC" : "SEND"));
            // S2C
            if (packet instanceof ClickSlotC2SPacket cs) {
                if (!isPacketEnabled("ClickSlotC2SPacket")) return;
                str.append(String.format("ClickSlotC2SPacket: Slot: %d, Stack: %s, Action: %s, Sync: %d", cs.getSlot(), cs.getStack(), cs.getActionType().name(), cs.getSyncId()));
            } else if (packet instanceof UpdateSelectedSlotC2SPacket us) {
                if (!isPacketEnabled("UpdateSelectedSlotC2SPacket")) return;
                str.append(String.format("UpdateSelectedSlotC2SPacket: %d", us.getSelectedSlot()));
            }  else if (packet instanceof ClientStatusC2SPacket cs) {
                if (!isPacketEnabled("ClientStatusC2SPacket")) return;
                str.append(String.format("ClientStatusC2SPacket: %s", cs.getMode().name()));
            } else if (packet instanceof ChatMessageC2SPacket cm) {
                if (!isPacketEnabled("ChatMessageC2SPacket")) return;
                str.append(String.format("ChatMessageC2SPacket: %s", cm.chatMessage()));
            } else if (packet instanceof CommonPongC2SPacket cp) {
                if (!isPacketEnabled("CommonPongC2SPacket")) return;
                str.append(String.format("CommonPongC2SPacket: %s", cp.getParameter()));
            } else if (packet instanceof CraftRequestC2SPacket cr) {
                if (!isPacketEnabled("CraftRequestC2SPacket")) return;
                str.append(String.format("CraftRequestC2SPacket: Recipe: %s, Sync: %d", cr.getRecipe(), cr.getSyncId()));
            } else if (packet instanceof PlayerInteractItemC2SPacket pii) {
                if (!isPacketEnabled("PlayerInteractItemC2SPacket")) return;
                str.append(String.format("PlayerInteractItemC2SPacket: Hand: %s", pii.getHand().name()));
            } else if (packet instanceof PickFromInventoryC2SPacket pfi) {
                if (!isPacketEnabled("PickFromInventoryC2SPacket")) return;
                str.append(String.format("PickFromInventoryC2SPacket: Slot: %d", pfi.getSlot()));
            } else if (packet instanceof CommandExecutionC2SPacket ce) {
                if (!isPacketEnabled("CommandExecutionC2SPacket")) return;
                str.append(String.format("CommandExecutionC2SPacket: %s", ce.command()));
            } else if (packet instanceof HandSwingC2SPacket hs) {
                if (!isPacketEnabled("HandSwingC2SPacket")) return;
                str.append(String.format("HandSwingC2SPacket: Hand: %s", hs.getHand()));
            } else if (packet instanceof PlayerInputC2SPacket pi) {
                if (!isPacketEnabled("PlayerInputC2SPacket")) return;
                str.append(String.format("PlayerInputC2SPacket: Forward: %f, Sideways: %f, Jumping: %b, Sneaking: %b", pi.getForward(), pi.getSideways(), pi.isJumping(), pi.isSneaking()));
            } else if (packet instanceof PlayerActionC2SPacket pa) {
                if (!isPacketEnabled("PlayerActionC2SPacket")) return;
                str.append(String.format("PlayerActionC2SPacket: Action: %s, Direction: %s, Pos: %s", pa.getAction().name(), pa.getDirection().getName(), pa.getPos()));
            } else if (packet instanceof PlayerInteractBlockC2SPacket pib) {
                if (!isPacketEnabled("PlayerInteractBlockC2SPacket")) return;
                BlockHitResult bhr = pib.getBlockHitResult();
                str.append(String.format("PlayerInteractBlockC2SPacket: Hand: %s, Pos: %s, Side: %s, BlockPos: %s", pib.getHand(), bhr.getPos().toString(), bhr.getSide().getName(), bhr.getBlockPos().toString()));
            } else if (packet instanceof PlayerMoveC2SPacket pm) {
                if (!isPacketEnabled("PlayerMoveC2SPacket")) return;
                str.append(String.format("PlayerMoveC2SPacket: ChangesLook: %b, ChangesPosition: %b, isOnGround: %b", pm.changesLook(), pm.changesPosition(), pm.isOnGround()));
            } else if (packet instanceof KeepAliveC2SPacket ka) {
                if (!isPacketEnabled("KeepAliveC2SPacket")) return;
                str.append(String.format("KeepAliveC2SPacket: ID: %d", ka.getId()));
            } else if (packet instanceof CreativeInventoryActionC2SPacket cia) {
                if (!isPacketEnabled("CreativeInventoryActionC2SPacket")) return;
                str.append(String.format("CreativeInventoryActionC2SPacket: Slot: %d, Stack: %s", cia.getSlot(), cia.getStack().toString()));
            } else if (packet instanceof CloseHandledScreenC2SPacket chc) {
                if (!isPacketEnabled("CloseHandledScreenC2SPacket")) return;
                str.append(String.format("CloseHandledScreenC2SPacket: SyncId: %d", chc.getSyncId()));
            } else if (packet instanceof ClientCommandC2SPacket cc) {
                if (!isPacketEnabled("ClientCommandC2SPacket")) return;
                str.append(String.format("ClientCommandC2SPacket: Entity ID: %d, Mode: %s, MountJumpHeight: %d", cc.getEntityId(), cc.getMode().toString(), cc.getMountJumpHeight()));
            } else if (packet instanceof UpdatePlayerAbilitiesC2SPacket upa) {
                if (!isPacketEnabled("UpdatePlayerAbilitiesC2SPacket")) return;
                str.append(String.format("UpdatePlayerAbilitiesC2SPacket: Flying: %b", upa.isFlying()));
            } else if (packet instanceof RequestCommandCompletionsC2SPacket rcc) {
                if (!isPacketEnabled("RequestCommandCompletionsC2SPacket")) return;
                str.append(String.format("RequestCommandCompletionsC2SPacket: CompletionId: %d, PartialCommand: %s", rcc.getCompletionId(), rcc.getPartialCommand()));
            } else if (packet instanceof PlayerInteractEntityC2SPacket pie) {
                if (!isPacketEnabled("PlayerInteractEntityC2SPacket")) return;
                IntegratedServer server = MinecraftClient.getInstance().getServer();
                if (server != null) {
                    assert MinecraftClient.getInstance().world != null;
                    str.append(String.format(
                            "PlayerInteractEntityC2SPacket: Entity: %s, PlayerSneaking: %b",
                            Objects.requireNonNull(pie.getEntity(server.getWorld(MinecraftClient.getInstance().world.getRegistryKey()))),
                            pie.isPlayerSneaking()
                    ));
                } else {
                    str.append("PlayerInteractEntityC2SPacket: No server available, cannot retrieve entity information.");
                }
            } else if (packet instanceof TeleportConfirmC2SPacket tc) {
                if (!isPacketEnabled("TeleportConfirmC2SPacket")) return;
                str.append(String.format("TeleportConfirmC2SPacket: TeleportId: %d", tc.getTeleportId()));
            } else if (packet instanceof BoatPaddleStateC2SPacket bps) {
                if (!isPacketEnabled("BoatPaddleStateC2SPacket")) return;
                str.append(String.format("BoatPaddleStateC2SPacket: LeftPaddling: %b, RightPaddling: %b", bps.isLeftPaddling(), bps.isRightPaddling()));
            } else if (packet instanceof VehicleMoveC2SPacket vm) {
                if (!isPacketEnabled("VehicleMoveC2SPacket")) return;
                str.append(String.format("VehicleMoveC2SPacket: Pitch: %f, Yaw: %f, X: %f, Y: %f, Z: %f", vm.getPitch(), vm.getYaw(), vm.getX(), vm.getY(), vm.getZ()));
            } else if (packet instanceof RenameItemC2SPacket ri) {
                if (!isPacketEnabled("RenameItemC2SPacket")) return;
                str.append(String.format("RenameItemC2SPacket: Name: %s", ri.getName()));
            } else if (packet instanceof AcknowledgeChunksC2SPacket ac) {
                if (!isPacketEnabled("AcknowledgeChunksC2SPacket")) return;
                str.append(String.format("AcknowledgeChunksC2SPacket: DesiredChunksPerTick: %f", ac.desiredChunksPerTick()));
            } else if (packet instanceof CustomPayloadC2SPacket cp) {
                if (!isPacketEnabled("CustomPayloadC2SPacket")) return;
                str.append(String.format("CustomPayloadC2SPacket: PayLoad: %s", cp.payload().toString()));
            } else if (packet instanceof ClientOptionsC2SPacket co) {
                if (!isPacketEnabled("ClientOptionsC2SPacket")) return;
                str.append(String.format("ClientOptionsC2SPacket: PayLoad: %s", co.options().toString()));
            } else if (packet instanceof ButtonClickC2SPacket bc) {
                if (!isPacketEnabled("ButtonClickC2SPacket")) return;
                str.append(String.format("ButtonClickC2SPacket: ButtonId: %d, SyncId: %d", bc.getButtonId(), bc.getSyncId()));
            } else if (packet instanceof UpdateBeaconC2SPacket ub) {
                if (!isPacketEnabled("UpdateBeaconC2SPacket")) return;
                str.append(String.format("UpdateBeaconC2SPacket: tPrimaryEffectId: %s, SecondaryEffectId: %s", ub.getPrimaryEffectId().toString(), ub.getSecondaryEffectId().toString()));
            } else if (packet instanceof AdvancementTabC2SPacket at) {
                if (!isPacketEnabled("AdvancementTabC2SPacket")) return;
                str.append(String.format("AdvancementTabC2SPacket: Action: %s, TabToOpen: %s", at.getAction().toString(), Objects.requireNonNull(at.getTabToOpen())));
            } else if (packet instanceof RecipeBookDataC2SPacket rbd) {
                if (!isPacketEnabled("RecipeBookDataC2SPacket")) return;
                str.append(String.format("RecipeBookDataC2SPacket: RecipeId: %s", rbd.getRecipeId().toString()));
            } else if (packet instanceof RecipeCategoryOptionsC2SPacket rco) {
                if (!isPacketEnabled("RecipeCategoryOptionsC2SPacket")) return;
                str.append(String.format("RecipeCategoryOptionsC2SPacket: Category: %s, FilteringCraftable: %b, GuiOpen: %b", rco.getCategory().toString(), rco.isFilteringCraftable(), rco.isGuiOpen()));
            } else if (packet instanceof BookUpdateC2SPacket bu) {
                if (!isPacketEnabled("BookUpdateC2SPacket")) return;
                str.append(String.format("BookUpdateC2SPacket: Pages: %s, Slot: %d, Title: %s", bu.getPages().toString(), bu.getSlot(), bu.getTitle().toString()));
            } else if (packet instanceof JigsawGeneratingC2SPacket jg) {
                if (!isPacketEnabled("JigsawGeneratingC2SPacket")) return;
                str.append(String.format("JigsawGeneratingC2SPacket: Pos: %s, MaxDepth: %d, KeepJigsaw: %b", jg.getPos().toString(), jg.getMaxDepth(), jg.shouldKeepJigsaws()));
            } else if (packet instanceof QueryBlockNbtC2SPacket qbn) {
                if (!isPacketEnabled("QueryBlockNbtC2SPacket")) return;
                str.append(String.format("QueryBlockNbtC2SPacket: Pos: %s, TransactionId: %d", qbn.getPos().toString(), qbn.getTransactionId()));
            } else if (packet instanceof QueryEntityNbtC2SPacket qen) {
                if (!isPacketEnabled("QueryEntityNbtC2SPacket")) return;
                str.append(String.format("QueryEntityNbtC2SPacket: EntityId: %d, TransactionId: %d", qen.getEntityId(), qen.getTransactionId()));
            } else if (packet instanceof ResourcePackStatusC2SPacket rp) {
                if (!isPacketEnabled("ResourcePackStatusC2SPacket")) return;
                str.append(String.format("ResourcePackStatusC2SPacket: Id: %s, Status: %s", rp.id().toString(), rp.status().toString()));
            } else if (packet instanceof SelectMerchantTradeC2SPacket smt) {
                if (!isPacketEnabled("SelectMerchantTradeC2SPacket")) return;
                str.append(String.format("SelectMerchantTradeC2SPacket: TradeId: %d", smt.getTradeId()));
            } else if (packet instanceof SpectatorTeleportC2SPacket st) {
                if (!isPacketEnabled("SpectatorTeleportC2SPacket")) return;
                str.append(String.format("SpectatorTeleportC2SPacket: Target: %s", st.getTarget(Objects.requireNonNull(MinecraftClient.getInstance().getServer()).getOverworld())));
            } else if (packet instanceof UpdateCommandBlockC2SPacket ucb) {
                if (!isPacketEnabled("UpdateCommandBlockC2SPacket")) return;
                str.append(String.format("UpdateCommandBlockC2SPacket: Command: %s, Pos: %s, Type: %s, AlwaysActive: %b, Conditional: %b, TrackOutput: %b", ucb.getCommand(), ucb.getPos().toString(), ucb.getType().toString(), ucb.isAlwaysActive(), ucb.isConditional(), ucb.shouldTrackOutput()));
            } else if (packet instanceof UpdateCommandBlockMinecartC2SPacket ucbm) {
                if (!isPacketEnabled("UpdateCommandBlockMinecartC2SPacket")) return;
                str.append(String.format("UpdateCommandBlockMinecartC2SPacket: Command: %s, MinecartCommandExecutor: %s, TrackOutput: %b", ucbm.getCommand(), ucbm.getMinecartCommandExecutor(MinecraftClient.getInstance().world).toString(), ucbm.shouldTrackOutput()));
            } else if (packet instanceof UpdateDifficultyC2SPacket ud) {
                if (!isPacketEnabled("UpdateDifficultyC2SPacket")) return;
                str.append(String.format("UpdateDifficultyC2SPacket: Difficulty: %s", ud.getDifficulty().toString()));
            } else if (packet instanceof UpdateDifficultyLockC2SPacket udl) {
                if (!isPacketEnabled("UpdateDifficultyLockC2SPacket")) return;
                str.append(String.format("UpdateDifficultyLockC2SPacket: DifficultyLocked: %b", udl.isDifficultyLocked()));
            } else if (packet instanceof UpdateJigsawC2SPacket uj) {
                if (!isPacketEnabled("UpdateJigsawC2SPacket")) return;
                str.append(String.format("UpdateJigsawC2SPacket: FinalState: %s, Name: %s, Pos: %s, Target: %s, JoinType: %s, PlacementPriority: %d, Pool: %s, SelectionPriority: %s", uj.getFinalState(), uj.getName(), uj.getPos().toString(), uj.getTarget().toString(), uj.getJointType().toString(), uj.getPlacementPriority(), uj.getPool().toString(), uj.getSelectionPriority()));
            } else if (packet instanceof UpdateSignC2SPacket us) {
                if (!isPacketEnabled("UpdateSignC2SPacket")) return;
                str.append(String.format("UpdateSignC2SPacket: Pos: %s, Text: %s, Front& %b", us.getPos().toString(), us.getText().toString(), us.isFront()));
            } else if (packet instanceof UpdateStructureBlockC2SPacket usb) {
                if (!isPacketEnabled("UpdateStructureBlockC2SPacket")) return;
                str.append(String.format("UpdateStructureBlockC2SPacket: Action: %s, Pos: %s, Mode: %s, Integrity: %f, Metadata: %s, Mirror: %s, Offset: %s, Rotation: %s, Seed: %d, Size: %s, TemplateName: %s, IgnoreEntities %b. ShowAir: %b, ShowBoundingBox: %b", usb.getAction().toString(), usb.getPos().toString(), usb.getMode().toString(), usb.getIntegrity(), usb.getMetadata(), usb.getMirror().toString(), usb.getOffset().toString(), usb.getRotation().toString(), usb.getSeed(), usb.getSize().toString(), usb.getTemplateName(), usb.shouldIgnoreEntities(), usb.shouldShowAir(), usb.shouldShowBoundingBox()));
            } else if (packet instanceof LoginHelloC2SPacket lh) {
                if (!isPacketEnabled("LoginHelloC2SPacket")) return;
                str.append(String.format("LoginHelloC2SPacket: Name: %s", lh.name()));
            }  else if (packet instanceof LoginQueryResponseC2SPacket lqr) {
                if (!isPacketEnabled("LoginQueryResponseC2SPacket")) return;
                assert lqr.response() != null;
                str.append(String.format("LoginQueryResponseC2SPacket: QueryId: %d, Response: %s", lqr.queryId(), lqr.response()));
            } else if (packet instanceof LoginKeyC2SPacket lk) {
                if (!isPacketEnabled("LoginKeyC2SPacket")) return;
                str.append(String.format("LoginKeyC2SPacket: DecryptSecretKey: %s", lk.decryptSecretKey(new PrivateKey() {
                    @Override
                    public String getAlgorithm() {
                        return "";
                    }

                    @Override
                    public String getFormat() {
                        return "";
                    }

                    @Override
                    public byte[] getEncoded() {
                        return new byte[0];
                    }
                }).toString()));
            } else if (packet instanceof HandshakeC2SPacket h) {
                if (!isPacketEnabled("HandshakeC2SPacket")) return;
                assert h.getNewNetworkState() != null;
                str.append(String.format("HandshakeC2SPacket: Address: %s NewNetworkState: %s, IntendedState: %s, Port: %d, ProtocolVersion: %d", h.address(), h.getNewNetworkState(), h.intendedState().toString(), h.port(), h.protocolVersion()));
            } else if (packet instanceof QueryPingC2SPacket qp) {
                if (!isPacketEnabled("QueryPingC2SPacket")) return;
                str.append(String.format("QueryPingC2SPacket: StartTime: %d", qp.getStartTime()));
            } else if (packet instanceof QueryRequestC2SPacket qr) {
                if (!isPacketEnabled("QueryPingC2SPacket")) return;
                str.append(String.format("QueryPingC2SPacket: %s", qr));
            }
            // S2C
            else if (packet instanceof PlayerListS2CPacket pl) {
                if (!isPacketEnabled("PlayerListS2CPacket")) return;
                str.append("PlayerListS2CPacket: ");
                for (PlayerListS2CPacket.Entry entry : pl.getEntries()) {
                    GameProfile profile = entry.profile();
                    if (profile != null) {
                        String playerName = profile.getName();
                        UUID playerUUID = profile.getId();
                        str.append(String.format("Player: %s, UUID: %s, Hidden: %d; ", playerName, playerUUID, entry.listed() ? 1 : 0));
                    } else {
                        str.append("Player: null, UUID: null, Hidden: ").append(entry.listed() ? 1 : 0).append("; ");
                    }
                }
            } else if (packet instanceof ChatMessageS2CPacket cm) {
                if (!isPacketEnabled("ChatMessageS2CPacket")) return;
                assert cm.unsignedContent() != null;
                str.append(String.format("ChatMessageS2CPacket: %s", cm.unsignedContent().getString()));
            } else if (packet instanceof BlockUpdateS2CPacket bu) {
                if (!isPacketEnabled("BlockUpdateS2CPacket")) return;
                str.append(String.format("BlockUpdateS2CPacket: Position: %s", bu.getPos().toString()));
            } else if (packet instanceof EntityStatusS2CPacket es) {
                if (!isPacketEnabled("EntityStatusS2CPacket")) return;
                Entity entity = es.getEntity(MinecraftClient.getInstance().world);
                if (entity != null) {
                    str.append(String.format("EntityStatusS2CPacket: Entity: %s, Status: %d", entity.getName().getString(), es.getStatus()));
                } else {
                    str.append(String.format("EntityStatusS2CPacket: Entity: null, Status: %d", es.getStatus()));
                }
            } else if (packet instanceof WorldTimeUpdateS2CPacket wu) {
                if (!isPacketEnabled("WorldTimeUpdateS2CPacket")) return;
                str.append(String.format("WorldTimeUpdateS2CPacket: Time: %d", wu.getTime()));
            } else if (packet instanceof GameStateChangeS2CPacket gc) {
                if (!isPacketEnabled("GameStateChangeS2CPacket")) return;
                str.append(String.format("GameStateChangeS2CPacket: Reason: %s, Value: %f", gc.getReason().toString(), gc.getValue()));
            } else if (packet instanceof EntitySpawnS2CPacket es) {
                if (!isPacketEnabled("EntitySpawnS2CPacket")) return;
                str.append(String.format("EntitySpawnS2CPacket: ID: %d, Type: %s", es.getId(), es.getEntityType().toString()));
            } else if (packet instanceof UnloadChunkS2CPacket uc) {
                if (!isPacketEnabled("UnloadChunkS2CPacket")) return;
                str.append(String.format("UnloadChunkS2CPacket: X: %d, Z: %d", uc.pos().x, uc.pos().z));
            } else if (packet instanceof ResourcePackSendS2CPacket rp) {
                if (!isPacketEnabled("ResourcePackSendS2CPacket")) return;
                str.append(String.format("ResourcePackSendS2CPacket: URL: %s, Hash: %s", rp.url(), rp.hash()));
            } else if (packet instanceof OpenScreenS2CPacket os) {
                if (!isPacketEnabled("OpenScreenS2CPacket")) return;
                str.append(String.format("OpenScreenS2CPacket: ID: %d, Screen ID: %s", os.getSyncId(), Objects.requireNonNull(os.getScreenHandlerType())));
            } else if (packet instanceof PlayerAbilitiesS2CPacket pa) {
                if (!isPacketEnabled("PlayerAbilitiesS2CPacket")) return;
                str.append(String.format("PlayerAbilitiesS2CPacket: Flying: %b, Invulnerable: %b", pa.isFlying(), pa.isInvulnerable()));
            } else if (packet instanceof ExperienceBarUpdateS2CPacket eb) {
                if (!isPacketEnabled("ExperienceBarUpdateS2CPacket")) return;
                str.append(String.format("ExperienceBarUpdateS2CPacket: Level: %d, Progress: %f", eb.getExperienceLevel(), eb.getBarProgress()));
            } else if (packet instanceof EntityPositionS2CPacket ep) {
                if (!isPacketEnabled("EntityPositionS2CPacket")) return;
                str.append(String.format("EntityPositionS2CPacket: X: %f, Y: %f, Z: %f", ep.getX(), ep.getY(), ep.getZ()));
            } else if (packet instanceof WorldEventS2CPacket we) {
                if (!isPacketEnabled("WorldEventS2CPacket")) return;
                str.append(String.format("WorldEventS2CPacket: Event ID: %d, Pos: %s", we.getEventId(), we.getPos().toString()));
            } else if (packet instanceof SynchronizeRecipesS2CPacket sr) {
                if (!isPacketEnabled("SynchronizeRecipesS2CPacket")) return;
                str.append(String.format("SynchronizeRecipesS2CPacket: Recipes: %s", sr.getRecipes().toString()));
            } else if (packet instanceof EntityS2CPacket e) {
                if (!isPacketEnabled("EntityS2CPacket")) return;
                str.append(String.format("EntityS2CPacket: Pitch: %d, Yaw: %d, DeltaX: %d, DeltaY: %d, DeltaZ: %d, isPositionChanged: %b, isOnGround: %b, hasRotation: %b", e.getPitch(), e.getYaw(), e.getDeltaX(), e.getDeltaY(), e.getDeltaZ(), e.isPositionChanged(), e.isOnGround(), e.hasRotation()));
            } else if (packet instanceof EntitySetHeadYawS2CPacket eshy) {
                if (!isPacketEnabled("EntitySetHeadYawS2CPacket")) return;
                str.append(String.format("EntitySetHeadYawS2CPacket: Entity: %s, HeadYaw: %d", eshy.getEntity(MinecraftClient.getInstance().world).toString(), eshy.getHeadYaw()));
            } else if (packet instanceof EntityVelocityUpdateS2CPacket evu) {
                if (!isPacketEnabled("EntityVelocityUpdateS2CPacket")) return;
                str.append(String.format("EntityVelocityUpdateS2CPacket: EntityID: %d, VelocityX: %d, VelocityY: %d, VelocityZ: %d", evu.getId(), evu.getVelocityX(), evu.getVelocityY(), evu.getVelocityZ()));
            } else if (packet instanceof BundleS2CPacket b) {
                if (!isPacketEnabled("BundleS2CPacket")) return;
                str.append(String.format("BundleS2CPacket: Packets: %s", b.getPackets().toString()));
            } else if (packet instanceof EntityTrackerUpdateS2CPacket etu) {
                if (!isPacketEnabled("EntityTrackerUpdateS2CPacket")) return;
                str.append(String.format("EntityTrackerUpdateS2CPacket: ID: %d, TrackedValues: %s", etu.id(), etu.trackedValues().toString()));
            } else if (packet instanceof ParticleS2CPacket p) {
                if (!isPacketEnabled("ParticleS2CPacket")) return;
                str.append(String.format("ParticleS2CPacket: ParticleType: %s, X: %f, Y: %f, Z: %f, OffsetX: %f, OffsetY: %f, OffsetZ: %f, Count: %d, Speed: %f, isLongDistance: %b", p.getParameters().getType().toString(), p.getX(), p.getY(), p.getZ(), p.getOffsetX(), p.getOffsetY(), p.getOffsetZ(), p.getCount(), p.getSpeed(), p.isLongDistance()));
            } else if (packet instanceof GameMessageS2CPacket gm) {
                if (!isPacketEnabled("GameMessageS2CPacket")) return;
                str.append(String.format("GameMessageS2CPacket: Message: %s", gm.content().getString()));
            } else if (packet instanceof InventoryS2CPacket i) {
                if (!isPacketEnabled("InventoryS2CPacket")) return;
                str.append(String.format("InventoryS2CPacket: Contents: %s, Revision: %d, SyncID: %d, CursorStack: %s", i.getContents().toString(), i.getRevision(), i.getSyncId(), i.getCursorStack().toString()));
            } else if (packet instanceof ScreenHandlerSlotUpdateS2CPacket shsu) {
                if (!isPacketEnabled("ScreenHandlerSlotUpdateS2CPacket")) return;
                str.append(String.format("ScreenHandlerSlotUpdateS2CPacket: Stack: %s, Slot: %d, Revision: %d, SyncID: %d", shsu.getStack().toString(), shsu.getSlot(), shsu.getRevision(), shsu.getSyncId()));
            } else if (packet instanceof PlaySoundS2CPacket ps) {
                if (!isPacketEnabled("PlaySoundS2CPacket")) return;
                str.append(String.format("PlaySoundS2CPacket: X: %f, Y: %f, Z: %f, Sound: %s, Category: %s, Seed: %d, Volume: %f, Pitch: %f", ps.getX(), ps.getY(), ps.getZ(), ps.getSound().toString(), ps.getCategory().toString(), ps.getSeed(), ps.getVolume(), ps.getPitch()));
            } else if (packet instanceof PlayerActionResponseS2CPacket par) {
                if (!isPacketEnabled("PlayerActionResponseS2CPacket")) return;
                str.append(String.format("PlayerActionResponseS2CPacket: Sequence: %d", par.sequence()));
            } else if (packet instanceof EntitiesDestroyS2CPacket ed) {
                if (!isPacketEnabled("EntitiesDestroyS2CPacket")) return;
                str.append(String.format("EntitiesDestroyS2CPacket: EntityIds: %s", ed.getEntityIds().toString()));
            } else if (packet instanceof KeepAliveS2CPacket ka) {
                if (!isPacketEnabled("KeepAliveS2CPacket")) return;
                str.append(String.format("KeepAliveS2CPacket: ID: %d", ka.getId()));
            } else if (packet instanceof ChunkRenderDistanceCenterS2CPacket crdc) {
                if (!isPacketEnabled("ChunkRenderDistanceCenterS2CPacket")) return;
                str.append(String.format("ChunkRenderDistanceCenterS2CPacket: ChunkX: %d, ChunkZ: %d", crdc.getChunkX(), crdc.getChunkZ()));
            } else if (packet instanceof ChunkDataS2CPacket cd) {
                if (!isPacketEnabled("ChunkDataS2CPacket")) return;
                str.append(String.format("ChunkDataS2CPacket: ChunkX: %d, ChunkZ: %d ChunkData: HeightMap: %s, SectionsDataBuf: %s, LightData: BlockNibbles: %s, InitedBlock: %s, SkyNibbles: %s, UninitedBlock: %s, UninitedSky: %s", cd.getChunkX(), cd.getChunkZ(), cd.getChunkData().getHeightmap().toString(), cd.getChunkData().getSectionsDataBuf().toString(), cd.getLightData().getBlockNibbles().toString(), cd.getLightData().getInitedBlock().toString(), cd.getLightData().getSkyNibbles().toString(), cd.getLightData().getUninitedBlock().toString(), cd.getLightData().getUninitedSky().toString()));
            } else if (packet instanceof CommandTreeS2CPacket ct) {
                if (!isPacketEnabled("CommandTreeS2CPacket")) return;
                str.append(String.format("CommandTreeS2CPacket: CommandTree: %s", ct.getCommandTree((CommandRegistryAccess) MinecraftClient.getInstance().getServer())));
            } else if (packet instanceof EntityAttributesS2CPacket ea) {
                if (!isPacketEnabled("EntityAttributesS2CPacket")) return;
                str.append(String.format("EntityAttributesS2CPacket: Entity ID: %d, Entries: %s", ea.getEntityId(), ea.getEntries().toString()));
            } else if (packet instanceof PlayerListHeaderS2CPacket plh) {
                if (!isPacketEnabled("PlayerListHeaderS2CPacket")) return;
                str.append(String.format("PlayerListHeaderS2CPacket: Header: %s, Footer: %s", plh.getHeader().toString(), plh.getFooter().toString()));
            } else if (packet instanceof CommandSuggestionsS2CPacket cs) {
                if (!isPacketEnabled("CommandSuggestionsS2CPacket")) return;
                str.append(String.format("CommandSuggestionsS2CPacket: CompletionId: %d, Suggestions: %s", cs.getCompletionId(), cs.getSuggestions().toString()));
            } else if (packet instanceof OverlayMessageS2CPacket om) {
                if (!isPacketEnabled("OverlayMessageS2CPacket")) return;
                str.append(String.format("OverlayMessageS2CPacket: Message: %s", om.getMessage().toString()));
            } else if (packet instanceof UnlockRecipesS2CPacket ur) {
                if (!isPacketEnabled("UnlockRecipesS2CPacket")) return;
                str.append(String.format("UnlockRecipesS2CPacket: Action: %s, Options: %s, RecipeIdsToChange: %s", ur.getAction().toString(), ur.getOptions().toString(), ur.getRecipeIdsToChange().toString()));
            } else if (packet instanceof AdvancementUpdateS2CPacket au) {
                if (!isPacketEnabled("AdvancementUpdateS2CPacket")) return;
                str.append(String.format("AdvancementUpdateS2CPacket: AdvancementIdsToRemove: %s, AdvancementsToEarn: %s, AdvancementsToProgress: %s", au.getAdvancementIdsToRemove().toString(), au.getAdvancementsToEarn().toString(), au.getAdvancementsToProgress().toString()));
            } else if (packet instanceof BlockEventS2CPacket be) {
                if (!isPacketEnabled("BlockEventS2CPacket")) return;
                str.append(String.format("BlockEventS2CPacket: Block: %s, Pos: %s, Type: %d, Data: %d", be.getBlock().toString(), be.getPos().toString(), be.getType(), be.getData()));
            } else if (packet instanceof ChunkDeltaUpdateS2CPacket) {
                if (!isPacketEnabled("ChunkDeltaUpdateS2CPacket")) return;
                str.append("ChunkDeltaUpdateS2CPacket: (Я не разобрался в этом пакете)");
            } else if (packet instanceof EntityStatusEffectS2CPacket ese) {
                if (!isPacketEnabled("EntityStatusEffectS2CPacket")) return;
                str.append(String.format("EntityStatusEffectS2CPacket: Entity ID: %d, Effect ID: %s, Duration: %d, Amplifier: %d, FactorCalculationData: %s, Ambient: %b, ShowIcon: %b, ShowParticles: %b", ese.getEntityId(), ese.getEffectId().toString(), ese.getDuration(), ese.getAmplifier(), Objects.requireNonNull(ese.getFactorCalculationData()), ese.isAmbient(), ese.shouldShowIcon(), ese.shouldShowParticles()));
            } else if (packet instanceof HealthUpdateS2CPacket hu) {
                if (!isPacketEnabled("HealthUpdateS2CPacket")) return;
                str.append(String.format("HealthUpdateS2CPacket: Health: %f, Food: %d, Saturation: %f", hu.getHealth(), hu.getFood(), hu.getSaturation()));
            } else if (packet instanceof RemoveEntityStatusEffectS2CPacket rese) {
                if (!isPacketEnabled("RemoveEntityStatusEffectS2CPacket")) return;
                str.append(String.format("RemoveEntityStatusEffectS2CPacket: Entity: %s, effect type: %s", Objects.requireNonNull(rese.getEntity(MinecraftClient.getInstance().world)), Objects.requireNonNull(rese.getEffectType())));
            } else if (packet instanceof EntityDamageS2CPacket ed) {
                if (!isPacketEnabled("EntityDamageS2CPacket")) return;
                assert MinecraftClient.getInstance().world != null;
                str.append(String.format("EntityDamageS2CPacket: Entity ID: %d, Damage Source: %s, SourceCauseId: %d, SourceDirectId: %d, SourcePosition: %s SourceTypeId: %d", ed.entityId(), ed.createDamageSource(MinecraftClient.getInstance().world), ed.sourceCauseId(), ed.sourceDirectId(), ed.sourcePosition().toString(), ed.sourceTypeId()));
            } else if (packet instanceof ItemPickupAnimationS2CPacket ipa) {
                if (!isPacketEnabled("ItemPickupAnimationS2CPacket")) return;
                str.append(String.format("ItemPickupAnimationS2CPacket: Entity ID: %d, CollectorEntityId: %d, StackAmount: %d", ipa.getEntityId(), ipa.getCollectorEntityId(), ipa.getStackAmount()));
            } else if (packet instanceof PlayerPositionLookS2CPacket ppl) {
                if (!isPacketEnabled("PlayerPositionLookS2CPacket")) return;
                str.append(String.format("PlayerPositionLookS2CPacket: Pitch: %f, Yaw: %f, X: %f, Y: %f, Z: %f, Flags: %s, TeleportId: %d", ppl.getPitch(), ppl.getYaw(), ppl.getX(), ppl.getY(), ppl.getZ(), ppl.getFlags().toString(), ppl.getTeleportId()));
            } else if (packet instanceof EntityPassengersSetS2CPacket eps) {
                if (!isPacketEnabled("EntityPassengersSetS2CPacket")) return;
                str.append(String.format("EntityPassengersSetS2CPacket: Id: %d, PassengerIds: %s", eps.getId(), Arrays.toString(eps.getPassengerIds())));
            } else if (packet instanceof ScreenHandlerPropertyUpdateS2CPacket shpu) {
                if (!isPacketEnabled("ScreenHandlerPropertyUpdateS2CPacket")) return;
                str.append(String.format("ScreenHandlerPropertyUpdateS2CPacket: PropertyId: %d, SyncId: %d, Value: %d", shpu.getPropertyId(), shpu.getSyncId(), shpu.getValue()));
            } else if (packet instanceof EntityAnimationS2CPacket ea) {
                if (!isPacketEnabled("EntityAnimationS2CPacket")) return;
                str.append(String.format("EntityAnimationS2CPacket: AnimationId: %d, ID: %d", ea.getAnimationId(), ea.getId()));
            } else if (packet instanceof StartChunkSendS2CPacket scs) {
                if (!isPacketEnabled("StartChunkSendS2CPacket")) return;
                str.append(String.format("StartChunkSendS2CPacket: %s", scs));
            } else if (packet instanceof ChunkSentS2CPacket cs) {
                if (!isPacketEnabled("ChunkSentS2CPacket")) return;
                str.append(String.format("ChunkSentS2CPacket: BatchSize: %d", cs.batchSize()));
            } else if (packet instanceof PlaySoundFromEntityS2CPacket psfe) {
                if (!isPacketEnabled("PlaySoundFromEntityS2CPacket")) return;
                str.append(String.format("PlaySoundFromEntityS2CPacket: Sound: %s, Category: %s, Seed: %d, Volume: %f, Pitch: %f", psfe.getSound().toString(), psfe.getCategory().toString(), psfe.getSeed(), psfe.getVolume(), psfe.getPitch()));
            } else if (packet instanceof EntityAttachS2CPacket ea) {
                if (!isPacketEnabled("EntityAttachS2CPacket")) return;
                str.append(String.format("EntityAttachS2CPacket: AttachedEntityId: %d, getHoldingEntityId: %d", ea.getAttachedEntityId(), ea.getHoldingEntityId()));
            } else if (packet instanceof EntityEquipmentUpdateS2CPacket eeq) {
                if (!isPacketEnabled("EntityEquipmentUpdateS2CPacket")) return;
                str.append(String.format("EntityEquipmentUpdateS2CPacket: Id: %d, EquipmentList: %s", eeq.getId(), eeq.getEquipmentList().toString()));
            } else if (packet instanceof CustomPayloadS2CPacket cp) {
                if (!isPacketEnabled("CustomPayloadS2CPacket")) return;
                str.append(String.format("CustomPayloadS2CPacket: PayLoad: %s", cp.payload().toString()));
            } else if (packet instanceof EnterCombatS2CPacket ec) {
                if (!isPacketEnabled("EnterCombatS2CPacket")) return;
                str.append(String.format("EnterCombatS2CPacket: %s", ec));
            } else if (packet instanceof EndCombatS2CPacket ec) {
                if (!isPacketEnabled("EndCombatS2CPacket")) return;
                str.append(String.format("EndCombatS2CPacket: %s", ec));
            } else if (packet instanceof ExplosionS2CPacket e) {
                if (!isPacketEnabled("ExplosionS2CPacket")) return;
                if (MinecraftClient.getInstance().world == null) return;
                CreatePointParticle.afterExplosion(e.getAffectedBlocks(), e.getX(), e.getY(), e.getZ());
                CreatePointParticle.defaultParticle(e.getX(), e.getY(), e.getZ(), 0xa884f3, 0xeaaded, 1.5F, 300, 0);
                str.append(String.format("ExplosionS2CPacket: AffectedBlocks: %s, X: %f, Y: %f, Z: %f, DestructionType: %s, EmitterParticle: %s, Particle: %s, PlayerVelocityX: %f, PlayerVelocityY: %f, PlayerVelocityZ: %f, Radius: %f, SoundEvent: %s", e.getAffectedBlocks().toString(), e.getX(), e.getY(), e.getZ(), e.getDestructionType().toString(), e.getEmitterParticle().getType().toString(), e.getParticle().getType().toString(), e.getPlayerVelocityX(), e.getPlayerVelocityY(), e.getPlayerVelocityZ(), e.getRadius(), e.getSoundEvent().getId().toString()));
            } else if (packet instanceof SelectAdvancementTabS2CPacket sat) {
                if (!isPacketEnabled("SelectAdvancementTabS2CPacket")) return;
                str.append(String.format("SelectAdvancementTabS2CPacket: TabId: %s", Objects.requireNonNull(sat.getTabId())));
            } else if (packet instanceof CooldownUpdateS2CPacket cu) {
                if (!isPacketEnabled("CooldownUpdateS2CPacket")) return;
                str.append(String.format("CooldownUpdateS2CPacket: Cooldown: %d, Item: %s", cu.getCooldown(), cu.getItem().toString()));
            } else if (packet instanceof MapUpdateS2CPacket mu) {
                if (!isPacketEnabled("MapUpdateS2CPacket")) return;
                str.append(String.format("MapUpdateS2CPacket: Id: %d, Scale: %d, Locked: %b", mu.getId(), mu.getScale(), mu.isLocked()));
            } else if (packet instanceof BossBarS2CPacket bb) {
                if (!isPacketEnabled("BossBarS2CPacket")) return;
                str.append(String.format("BossBarS2CPacket: ", bb));
            } else if (packet instanceof BlockBreakingProgressS2CPacket bbp) {
                if (!isPacketEnabled("BlockBreakingProgressS2CPacket")) return;
                str.append(String.format("BlockBreakingProgressS2CPacket: Progress: %d, Id: %d, Pos: %s", bbp.getProgress(), bbp.getEntityId(), bbp.getPos().toString()));
            } else if (packet instanceof BlockEntityUpdateS2CPacket beu) {
                if (!isPacketEnabled("BlockEntityUpdateS2CPacket")) return;
                str.append(String.format("BlockEntityUpdateS2CPacket: BlockEntityType: %s, Pos: %s, Nbt: %s", beu.getBlockEntityType().toString(), beu.getPos().toString(), beu.getNbt()));
            } else if (packet instanceof ChunkLoadDistanceS2CPacket cld) {
                if (!isPacketEnabled("ChunkLoadDistanceS2CPacket")) return;
                str.append(String.format("ChunkLoadDistanceS2CPacket: Distance: %d", cld.getDistance()));
            } else if (packet instanceof ClearTitleS2CPacket ct) {
                if (!isPacketEnabled("ClearTitleS2CPacket")) return;
                str.append(String.format("ClearTitleS2CPacket: ShouldReset: %b", ct.shouldReset()));
            } else if (packet instanceof CloseScreenS2CPacket cs) {
                if (!isPacketEnabled("CloseScreenS2CPacket")) return;
                str.append(String.format("CloseScreenS2CPacket: SyncId: %d", cs.getSyncId()));
            } else if (packet instanceof CraftFailedResponseS2CPacket cfr) {
                if (!isPacketEnabled("CraftFailedResponseS2CPacket")) return;
                str.append(String.format("CraftFailedResponseS2CPacket: RecipeId: %s, SyncId: %d", cfr.getRecipeId().toString(), cfr.getSyncId()));
            } else if (packet instanceof DeathMessageS2CPacket dm) {
                if (!isPacketEnabled("DeathMessageS2CPacket")) return;
                str.append(String.format("DeathMessageS2CPacket: Message: %s, EntityId: %d, WritingErrorSkippable: %b", dm.getMessage().getString(), dm.getEntityId(), dm.isWritingErrorSkippable()));
            } else if (packet instanceof DifficultyS2CPacket d) {
                if (!isPacketEnabled("DifficultyS2CPacket")) return;
                str.append(String.format("DifficultyS2CPacket: Difficulty: %s, DifficultyLocked: %b", d.getDifficulty().toString(), d.isDifficultyLocked()));
            } else if (packet instanceof DisconnectS2CPacket d) {
                if (!isPacketEnabled("DisconnectS2CPacket")) return;
                str.append(String.format("DisconnectS2CPacket: Reason: %s", d.getReason().getString()));
            } else if (packet instanceof ExperienceOrbSpawnS2CPacket eos) {
                if (!isPacketEnabled("ExperienceOrbSpawnS2CPacket")) return;
                str.append(String.format("ExperienceOrbSpawnS2CPacket: Experience: %d, Id: %d, X: %f, Y: %f, Z: %f", eos.getExperience(), eos.getId(), eos.getX(), eos.getY(), eos.getZ()));
            } else if (packet instanceof GameJoinS2CPacket gj) {
                if (!isPacketEnabled("GameJoinS2CPacket")) return;
                str.append(String.format("GameJoinS2CPacket: CommonPlayerSpawnInfo: %s, DimensionIds: %s, MaxPlayers: $d, PlayerEntityId: %d, LimitedCrafting: %b, Hardcore: %b, ReducedDebugInfo: %b, ShowDeathScreen: %b, SimulationsDistance: %d, ViewDistance: %d", gj.commonPlayerSpawnInfo().toString(), gj.dimensionIds().toString(), gj.maxPlayers(), gj.playerEntityId(), gj.doLimitedCrafting(), gj.hardcore(), gj.reducedDebugInfo(), gj.showDeathScreen(), gj.simulationDistance(), gj.viewDistance()));
            } else if (packet instanceof LightUpdateS2CPacket lu) {
                if (!isPacketEnabled("LightUpdateS2CPacket")) return;
                str.append(String.format("LightUpdateS2CPacket: ChunkX: %d, ChunkZ: %d, Data: %s", lu.getChunkX(), lu.getChunkZ(), lu.getData().toString()));
            } else if (packet instanceof LookAtS2CPacket la) {
                if (!isPacketEnabled("LookAtS2CPacket")) return;
                str.append(String.format("LookAtS2CPacket: SelfAnchor: %s, TargetPosition: %s", la.getSelfAnchor().toString(), la.getTargetPosition(MinecraftClient.getInstance().world).toString()));
            } else if (packet instanceof NbtQueryResponseS2CPacket nqr) {
                if (!isPacketEnabled("NbtQueryResponseS2CPacket")) return;
                str.append(String.format("NbtQueryResponseS2CPacket: Nbt: %s, WritingErrorSkippable: %b, TransactionId: %d", nqr.getNbt().toString(), nqr.isWritingErrorSkippable(), nqr.getTransactionId()));
            } else if (packet instanceof OpenHorseScreenS2CPacket ohs) {
                if (!isPacketEnabled("OpenHorseScreenS2CPacket")) return;
                str.append(String.format("OpenHorseScreenS2CPacket: HorseId: %d, SyncId: %d, SlotCount: %d", ohs.getHorseId(), ohs.getSyncId(), ohs.getSlotCount()));
            } else if (packet instanceof OpenWrittenBookS2CPacket owb) {
                if (!isPacketEnabled("OpenWrittenBookS2CPacket")) return;
                str.append(String.format("OpenWrittenBookS2CPacket: Hand: %s", owb.getHand().toString()));
            } else if (packet instanceof PlayerRespawnS2CPacket pr) {
                if (!isPacketEnabled("PlayerRespawnS2CPacket")) return;
                str.append(String.format("PlayerRespawnS2CPacket: PlayerSpawnInfo: %s, Flag: %d, HasFlag: %b", pr.commonPlayerSpawnInfo().toString(), pr.flag(), pr.hasFlag(pr.flag())));
            } else if (packet instanceof PlayerSpawnPositionS2CPacket psp) {
                if (!isPacketEnabled("PlayerSpawnPositionS2CPacket")) return;
                str.append(String.format("PlayerSpawnPositionS2CPacket: Angle: %f, Pos: %s", psp.getAngle(), psp.getPos().toString()));
            } else if (packet instanceof ScoreboardDisplayS2CPacket sd) {
                if (!isPacketEnabled("ScoreboardDisplayS2CPacket")) return;
                str.append(String.format("ScoreboardDisplayS2CPacket: Slot: %s, Name: %s", sd.getSlot().toString(), sd.getName()));
            } else if (packet instanceof ScoreboardObjectiveUpdateS2CPacket sou) {
                if (!isPacketEnabled("ScoreboardObjectiveUpdateS2CPacket")) return;
                str.append(String.format("ScoreboardObjectiveUpdateS2CPacket: DisplayName: %s, Name: %s, Mode: %d, Type: %s, NumberFormat: %s", sou.getDisplayName().getString(), sou.getName(), sou.getMode(), sou.getType().toString(), sou.getNumberFormat().toString()));
            } else if (packet instanceof SetCameraEntityS2CPacket sce) {
                if (!isPacketEnabled("SetCameraEntityS2CPacket")) return;
                str.append(String.format("SetCameraEntityS2CPacket: Entity: %s", Objects.requireNonNull(sce.getEntity(MinecraftClient.getInstance().world))));
            } else if (packet instanceof SignEditorOpenS2CPacket seo) {
                if (!isPacketEnabled("SignEditorOpenS2CPacket")) return;
                str.append(String.format("SignEditorOpenS2CPacket: Pos: %s, Front: %b", seo.getPos().toString(), seo.isFront()));
            } else if (packet instanceof StatisticsS2CPacket s) {
                if (!isPacketEnabled("StatisticsS2CPacket")) return;
                str.append(String.format("StatisticsS2CPacket: Stats: %s", s.getStats().toString()));
            } else if (packet instanceof StopSoundS2CPacket ss) {
                if (!isPacketEnabled("StopSoundS2CPacket")) return;
                str.append(String.format("StopSoundS2CPacket: Category: %s, SoundId: %s", Objects.requireNonNull(ss.getCategory()).getName(), Objects.requireNonNull(ss.getSoundId())));
            } else if (packet instanceof SubtitleS2CPacket s) {
                if (!isPacketEnabled("SubtitleS2CPacket")) return;
                str.append(String.format("SubtitleS2CPacket: Subtitle: %s", s.getSubtitle().getString()));
            } else if (packet instanceof SynchronizeTagsS2CPacket st) {
                if (!isPacketEnabled("SynchronizeTagsS2CPacket")) return;
                str.append(String.format("SynchronizeTagsS2CPacket: Groups: %s", st.getGroups().toString()));
            } else if (packet instanceof TeamS2CPacket t) {
                if (!isPacketEnabled("TeamS2CPacket")) return;
                str.append(String.format("TeamS2CPacket: Team: %s, TeamName: %s, PlayerNames: %s, TeamOperation: %s", t.getTeam().toString(), t.getTeamName(), t.getPlayerNames().toString(), Objects.requireNonNull(t.getTeamOperation())));
            } else if (packet instanceof TitleFadeS2CPacket tf) {
                if (!isPacketEnabled("TitleFadeS2CPacket")) return;
                str.append(String.format("TitleFadeS2CPacket: FadeOutTicks: %d, StayTicks: %d, FadeInTicks: %d", tf.getFadeOutTicks(), tf.getStayTicks(), tf.getFadeInTicks()));
            } else if (packet instanceof UpdateSelectedSlotS2CPacket uss) {
                if (!isPacketEnabled("UpdateSelectedSlotS2CPacket")) return;
                str.append(String.format("UpdateSelectedSlotS2CPacket: Slot: %d", uss.getSlot()));
            } else if (packet instanceof VehicleMoveS2CPacket vm) {
                if (!isPacketEnabled("VehicleMoveS2CPacket")) return;
                str.append(String.format("VehicleMoveS2CPacket: Pitch: %f, Yaw: %f, X: %f, Y: %f, Z: %f", vm.getPitch(), vm.getYaw(), vm.getX(), vm.getY(), vm.getZ()));
            } else if (packet instanceof WorldBorderCenterChangedS2CPacket wbcc) {
                if (!isPacketEnabled("WorldBorderCenterChangedS2CPacket")) return;
                str.append(String.format("WorldBorderCenterChangedS2CPacket: CenterX: %f, CenterZ: %f", wbcc.getCenterX(), wbcc.getCenterZ()));
            } else if (packet instanceof WorldBorderInitializeS2CPacket wbi) {
                if (!isPacketEnabled("WorldBorderInitializeS2CPacket")) return;
                str.append(String.format("WorldBorderInitializeS2CPacket: SizeLerpTime: %d, Size: %f, SizeLerpTarget: %f, CenterX: %f, CenterZ: %f, MaxRadius: %d, WarningBlocks: %d, WarningTime: %d", wbi.getSizeLerpTime(), wbi.getSize(), wbi.getSizeLerpTarget(), wbi.getCenterX(), wbi.getCenterZ(), wbi.getMaxRadius(), wbi.getWarningBlocks(), wbi.getWarningTime()));
            } else if (packet instanceof WorldBorderInterpolateSizeS2CPacket wbis) {
                if (!isPacketEnabled("WorldBorderInterpolateSizeS2CPacket")) return;
                str.append(String.format("WorldBorderInterpolateSizeS2CPacket: SizeLerpTime: %d, SizeLerpTarget: %f, Size: %f", wbis.getSizeLerpTime(), wbis.getSizeLerpTarget(), wbis.getSize()));
            } else if (packet instanceof WorldBorderSizeChangedS2CPacket wbsc) {
                if (!isPacketEnabled("WorldBorderSizeChangedS2CPacket")) return;
                str.append(String.format("WorldBorderSizeChangedS2CPacket: SizeLerpTarget: %f", wbsc.getSizeLerpTarget()));
            } else if (packet instanceof WorldBorderWarningBlocksChangedS2CPacket wbwbc) {
                if (!isPacketEnabled("WorldBorderWarningBlocksChangedS2CPacket")) return;
                str.append(String.format("WorldBorderWarningBlocksChangedS2CPacket: WarningBlocks: %d", wbwbc.getWarningBlocks()));
            } else if (packet instanceof LoginCompressionS2CPacket lc) {
                if (!isPacketEnabled("LoginCompressionS2CPacket")) return;
                str.append(String.format("LoginCompressionS2CPacket: CompressionThreshold: %d", lc.getCompressionThreshold()));
            } else if (packet instanceof LoginDisconnectS2CPacket ld) {
                if (!isPacketEnabled("LoginDisconnectS2CPacket")) return;
                str.append(String.format("LoginDisconnectS2CPacket: Reason: %s", ld.getReason().getString()));
            } else if (packet instanceof LoginHelloS2CPacket lh) {
                if (!isPacketEnabled("LoginHelloS2CPacket")) return;
                str.append(String.format("LoginHelloS2CPacket: Nonce: %s, PublicKey: %s, ServerId: %s", Arrays.toString(lh.getNonce()), lh.getPublicKey().toString(), lh.getServerId()));
            } else if (packet instanceof LoginSuccessS2CPacket ls) {
                if (!isPacketEnabled("LoginSuccessS2CPacket")) return;
                str.append(String.format("LoginSuccessS2CPacket: NewNetworkState: %s, Profile: %s", ls.getNewNetworkState().toString(), ls.getProfile().toString()));
            } else if (packet instanceof FeaturesS2CPacket f) {
                if (!isPacketEnabled("FeaturesS2CPacket")) return;
                str.append(String.format("FeaturesS2CPacket: Features: %s", f.features().toString()));
            } else if (packet instanceof ReadyS2CPacket r) {
                if (!isPacketEnabled("ReadyS2CPacket")) return;
                str.append(String.format("ReadyS2CPacket: NewNetworkState: %s", r.getNewNetworkState().toString()));
            } else if (packet instanceof DynamicRegistriesS2CPacket dr) {
                if (!isPacketEnabled("DynamicRegistriesS2CPacket")) return;
                str.append(String.format("DynamicRegistriesS2CPacket: RegistryManager: %s", dr.registryManager().toString()));
            } else if (packet instanceof EnterConfigurationC2SPacket ec) {
                if (!isPacketEnabled("EnterConfigurationC2SPacket")) return;
                assert ec.getNewNetworkState() != null;
                str.append(String.format("EnterConfigurationC2SPacket: NewNetworkState: %s", ec.getNewNetworkState()));
            } else if (packet instanceof ReadyC2SPacket r) {
                if (!isPacketEnabled("ReadyC2SPacket")) return;
                assert r.getNewNetworkState() != null;
                str.append(String.format("ReadyC2SPacket: NewNetworkState: %s", r.getNewNetworkState()));
            } else if (packet instanceof CommonPingS2CPacket cp) {
                if (!isPacketEnabled("CommonPingS2CPacket")) return;
                str.append(String.format("CommonPingS2CPacket: Parameter: %s", cp.getParameter()));
            } else if (packet instanceof ServerMetadataS2CPacket sm) {
                if (!isPacketEnabled("ServerMetadataS2CPacket")) return;
                str.append(String.format("ServerMetadataS2CPacket: Description: %s, Favicon: %s, SecureChatEnforced: %b", sm.getDescription().getString(), sm.getFavicon().toString(), sm.isSecureChatEnforced()));
            } else if (packet instanceof UpdateTickRateS2CPacket utr) {
                if (!isPacketEnabled("UpdateTickRateS2CPacket")) return;
                str.append(String.format("UpdateTickRateS2CPacket: Frozen: %b, TickRate: %f", utr.isFrozen(), utr.tickRate()));
            } else if (packet instanceof TickStepS2CPacket ts) {
                if (!isPacketEnabled("TickStepS2CPacket")) return;
                str.append(String.format("TickStepS2CPacket: TickStep: %d", ts.tickSteps()));
            } else if (packet instanceof PingResultS2CPacket pr) {
                if (!isPacketEnabled("PingResultS2CPacket")) return;
                str.append(String.format("PingResultS2CPacket: StartTime: %d", pr.getStartTime()));
            } else if (packet instanceof QueryResponseS2CPacket qr) {
                if (!isPacketEnabled("QueryResponseS2CPacket")) return;
                str.append(String.format("QueryResponseS2CPacket: Metadata: %d", qr.metadata().toString()));
            }

            else {
                str.append("Unknown packet: ").append(packet.getClass().getName());
            }

            packets.add(str.toString());
        } catch (Exception e) {
            LOGGER.error("Error processing packet: {}", e.getMessage(), e);
        }
    }

    private static boolean isPacketEnabled(String packetName) {
        return OptionsScreen.packetOptions.stream()
                .filter(option -> option.name.equals(packetName))
                .findFirst()
                .map(option -> option.enabled)
                .orElse(false);
    }

    private static String getCurrentHourStamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }
}
