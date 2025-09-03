package net.swimmingtuna.lotm.beyonder;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.swimmingtuna.lotm.attributes.PathwayAttributes.ApprenticeAttributes;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;
import net.swimmingtuna.lotm.capabilities.concealed_data.ConcealedUtils;
import net.swimmingtuna.lotm.capabilities.replicated_entity.ReplicatedEntityUtils;
import net.swimmingtuna.lotm.capabilities.scribed_abilities.ScribedUtils;
import net.swimmingtuna.lotm.capabilities.sealed_data.SealedUtils;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice.Conceptualization;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice.DoorConcealment;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.networking.packet.ClientShouldntRenderS2C;
import net.swimmingtuna.lotm.networking.packet.ClientWormOfStarDataS2C;
import net.swimmingtuna.lotm.networking.packet.SyncShouldntRenderHandPacketS2C;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.ClientData.ClientIgnoreShouldntRenderData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ApprenticeClass implements BeyonderClass {
    @Override
    public List<String> sequenceNames() {
        return List.of(
                "Door",
                "Key of Stars",
                "Planeswalker",
                "Wanderer",
                "Secrets Sorcerer",
                "Traveler",
                "Scribe",
                "Astrologer",
                "Trickmaster",
                "Apprentice"
        );
    }


    @Override
    public List<Integer> antiDivination() {
        return List.of(50, 40, 35, 25, 20, 10, 7, 5, 1, 1);
    }

    @Override
    public List<Integer> divination() {
        return List.of(50, 40, 35, 25, 20, 10, 7, 5, 1, 1);
    }

    @Override
    public List<Integer> spiritualityLevels() {
        return List.of(30000, 12000, 7000, 3500, 2300, 900, 550, 400, 225, 150);
    }


    @Override
    public List<Integer> mentalStrength() {
        return List.of(560, 380, 285, 220, 180, 140, 100, 80, 65, 40);
    }

    @Override
    public List<Integer> spiritualityRegen() {
        return List.of(34, 22, 16, 12, 10, 8, 6, 5, 3, 2);
    }

    @Override
    public void applyAllModifiers(LivingEntity entity, int seq) {
        ApprenticeAttributes.applyAll(entity, seq);
    }


    @Override
    public void tick(LivingEntity player, int sequenceLevel) {
        if (player.level().getGameTime() % 50 == 0) {
            CompoundTag tag = player.getPersistentData();
            int waitOnWormLogic = tag.getInt("waitOnWormLogic");
            int maxWormCount = 0;
            int wormRegenAmount = 0;
            switch (sequenceLevel) {
                case 6:
                    tag.putInt("maxScribedAbilities", 20);
                    break;
                case 5:
                    tag.putInt("maxScribedAbilities", 25);
                    break;
                case 4:
                    maxWormCount = 200;
                    wormRegenAmount = 1;
                    tag.putInt("wormOfStar", Math.min(maxWormCount, tag.getInt("wormOfStar") + wormRegenAmount));
                    if (player instanceof ServerPlayer serverPlayer) {
                        LOTMNetworkHandler.sendToPlayer(new ClientWormOfStarDataS2C(tag.getInt("wormOfStar")), serverPlayer);
                    }
                    tag.putInt("maxScribedAbilities", 30);
                    break;
                case 3:
                    maxWormCount = 800;
                    wormRegenAmount = 3;
                    tag.putInt("wormOfStar", Math.min(maxWormCount, tag.getInt("wormOfStar") + wormRegenAmount));

                    if (player instanceof ServerPlayer serverPlayer) {
                        LOTMNetworkHandler.sendToPlayer(new ClientWormOfStarDataS2C(tag.getInt("wormOfStar")), serverPlayer);
                    }

                    tag.putInt("maxScribedAbilities", 35);
                    break;
                case 2:
                    maxWormCount = 4000;
                    wormRegenAmount = 10;
                    tag.putInt("wormOfStar", Math.min(maxWormCount, tag.getInt("wormOfStar") + wormRegenAmount));

                    if (player instanceof ServerPlayer serverPlayer) {
                        LOTMNetworkHandler.sendToPlayer(new ClientWormOfStarDataS2C(tag.getInt("wormOfStar")), serverPlayer);
                    }

                    tag.putInt("maxScribedAbilities", 40);
                    if (player instanceof Player pPlayer) {
                        ReplicatedEntityUtils.setMaxEntities(pPlayer, 5);
                        ReplicatedEntityUtils.setMaxAbilitiesUse(pPlayer, 1);
                    }
                    ScribedUtils.seq2FixCount(player);
                    break;
                case 1:
                    maxWormCount = 16000;
                    wormRegenAmount = 25;
                    tag.putInt("wormOfStar", Math.min(maxWormCount, tag.getInt("wormOfStar") + wormRegenAmount));

                    if (player instanceof ServerPlayer serverPlayer) {
                        LOTMNetworkHandler.sendToPlayer(new ClientWormOfStarDataS2C(tag.getInt("wormOfStar")), serverPlayer);
                    }

                    tag.putInt("maxScribedAbilities", 45);
                    if (player instanceof Player pPlayer) {
                        ReplicatedEntityUtils.setMaxEntities(pPlayer, 10);
                        ReplicatedEntityUtils.setMaxAbilitiesUse(pPlayer, 4);
                    }
                    ScribedUtils.seq2FixCount(player);
                    break;
                case 0:
                    maxWormCount = 80000;
                    wormRegenAmount = 100;
                    tag.putInt("wormOfStar", Math.min(maxWormCount, tag.getInt("wormOfStar") + wormRegenAmount));

                    if (player instanceof ServerPlayer serverPlayer) {
                        LOTMNetworkHandler.sendToPlayer(new ClientWormOfStarDataS2C(tag.getInt("wormOfStar")), serverPlayer);
                    }

                    tag.putInt("maxScribedAbilities", 50);
                    if (player instanceof Player pPlayer) {
                        ReplicatedEntityUtils.setMaxEntities(pPlayer, 45);
                        ReplicatedEntityUtils.setMaxAbilitiesUse(pPlayer, 10000000);
                    }
                    ScribedUtils.seq2FixCount(player);
                    break;
            }
            if (sequenceLevel <= 4) {
                if (waitOnWormLogic == 0) {
                    if (tag.getInt("wormOfStar") < maxWormCount * 0.1) {
                        player.sendSystemMessage(Component.literal("Died due to a lack of Worms of Star").withStyle(ChatFormatting.DARK_RED).withStyle(ChatFormatting.BOLD));
                        player.kill();
                    } else if (tag.getInt("wormOfStar") < maxWormCount * 0.25) {
                        player.sendSystemMessage(Component.literal("You can't handle the low amount of Worms of Star and will soon die").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        player.hurt(player.damageSources().magic(), player.getMaxHealth() / 7);
                        BeyonderUtil.applyMobEffect(player, MobEffects.BLINDNESS, 100, 1, true, true);
                    } else if (tag.getInt("wormOfStar") < maxWormCount * 0.5) {
                        player.sendSystemMessage(Component.literal("You are dangerously low on Worms of Star and are taking damage because of it").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        player.hurt(player.damageSources().magic(), player.getMaxHealth() / 10);
                        BeyonderUtil.applyMobEffect(player, MobEffects.DARKNESS, 100, 1, true, true);
                    } else if (tag.getInt("wormOfStar") < maxWormCount * 0.75) {
                        player.hurt(player.damageSources().magic(), player.getMaxHealth() / 20);
                        player.sendSystemMessage(Component.literal("You are over exerting yourself, and shouldn't separate any more Worms of Stars").withStyle(ChatFormatting.YELLOW).withStyle(ChatFormatting.BOLD));
                    }
                }
            }
            if (waitOnWormLogic >= 1) {
                tag.putInt("waitOnWormLogic", waitOnWormLogic - 1);
            }
        }
    }


    @Override
    public Multimap<Integer, Item> getItems() {
        HashMultimap<Integer, Item> items = HashMultimap.create();
        items.put(9, ItemInit.ALLY_MAKER.get());
        items.put(9, ItemInit.CREATEDOOR.get());

        items.put(8, ItemInit.TRICKBURNING.get());
        items.put(8, ItemInit.TRICKFREEZING.get());
        items.put(8, ItemInit.TRICKTUMBLE.get());
        items.put(8, ItemInit.TRICKWIND.get());
        items.put(8, ItemInit.TRICKFOG.get());
        items.put(8, ItemInit.TRICKELECTRICSHOCK.get());
        items.put(8, ItemInit.TRICKTELEKENISIS.get());
        items.put(8, ItemInit.TRICKESCAPETRICK.get());
        items.put(8, ItemInit.TRICKFLASH.get());
        items.put(8, ItemInit.TRICKLOUDNOISE.get());
        items.put(8, ItemInit.TRICKBLACKCURTAIN.get());

        //items.put(7, ItemInit.ASTROLOGER_SPIRIT_VISION.get());

        //items.put(6, ItemInit.RECORDSCRIBE.get());
        items.put(6, ItemInit.SCRIBEABILITIES.get());

        items.put(5, ItemInit.TRAVELERSDOOR.get());
        items.put(5, ItemInit.TRAVELERSDOORHOME.get());
        items.put(5, ItemInit.INVISIBLEHAND.get());
        items.put(5, ItemInit.BLINK.get());
        items.put(5, ItemInit.BLINKAFTERIMAGE.get());

        items.put(4, ItemInit.BLINK_STATE.get());
        items.put(4, ItemInit.EXILE.get());
        items.put(4, ItemInit.DOOR_MIRAGE.get());
        items.put(4, ItemInit.CREATE_CONCEALED_BUNDLE.get());
        items.put(4, ItemInit.CREATE_CONCEALED_SPACE.get());
        items.put(4, ItemInit.SEPARATE_WORM_OF_STAR.get());

        items.put(3, ItemInit.SPATIAL_CAGE.get());
        items.put(3, ItemInit.SPATIAL_TEARING.get());

        items.put(2, ItemInit.SYMBOLIZATION.get());
        items.put(2, ItemInit.DIMENSIONAL_SIGHT.get());
        items.put(2, ItemInit.MINIATURIZE.get());
        items.put(2, ItemInit.REPLICATE.get());
        items.put(2, ItemInit.SEALING.get());
        items.put(2, ItemInit.TELEPORTATION.get());
        //items.put(2, ItemInit.UNSEAL.get());

        items.put(1, ItemInit.SPACE_FRAGMENTATION.get());
        items.put(1, ItemInit.SPATIAL_SEAL.get());
        items.put(1, ItemInit.STARFALL.get());
        items.put(1, ItemInit.GRAVITY_MANIPULATION.get());
        items.put(1, ItemInit.SPATIAL_MAZE.get());

        items.put(0, ItemInit.SECRET_KEEPING.get());
        items.put(0, ItemInit.DOOR_LAYERING.get());
        items.put(0, ItemInit.DOOR_CONCEALMENT.get());
        items.put(0, ItemInit.DOOR_SEAL_STRENGTHENING.get());
        items.put(0, ItemInit.DOOR_GAMMA_RAY_BURST.get());
        items.put(0, ItemInit.CONCEPTUALIZATION.get());

        return items;
    }

    @Override
    public ChatFormatting getColorFormatting() {
        return ChatFormatting.BLUE;
    }

    public void scribeRecordedAbilitiesMenu(ServerPlayer player, ItemStack... item) {
        SimpleContainer menu = new SimpleContainer(45);

    }

    public SimpleContainer getRegisteredAbilityItemsContainer(int sequenceLevel) {
        SimpleContainer container = new SimpleContainer(45);
        for (int i = 9; i >= sequenceLevel; i--) {
            getItems().get(i)
                    .stream()
                    .map(Item::getDefaultInstance)
                    .forEach(container::addItem);
        }
        return container;
    }

    public static void doorRightClick(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        BlockPos pos = event.getPos();
        Level level = event.getLevel();
        BlockState state = level.getBlockState(pos);
        if (!level.isClientSide) {
            if (event.getHand() != event.getEntity().getUsedItemHand()) {
                return;
            }
            if (BeyonderUtil.currentPathwayMatchesNoException(player, BeyonderClassInit.APPRENTICE.get())) {
                if (state.getBlock() instanceof DoorBlock) {
                    if (state.getBlock().getStateDefinition().getProperty("open") instanceof BooleanProperty open) {
                        boolean isCurrentlyOpen = state.getValue(open);
                        BlockState newState = state.setValue(open, !isCurrentlyOpen);
                        player.swing(InteractionHand.MAIN_HAND, true);
                        if (!player.isShiftKeyDown()) {
                            level.setBlock(pos, newState, 3);
                            level.playSound(null, pos,
                                    isCurrentlyOpen ? SoundEvents.IRON_DOOR_CLOSE : SoundEvents.IRON_DOOR_OPEN,
                                    SoundSource.BLOCKS,
                                    0.9F, 0.9F
                            );
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }

    public static final Map<UUID, Boolean> lastSentHandStates = new HashMap<>();

    public static void apprenticeTick(LivingEvent.LivingTickEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (!livingEntity.level().isClientSide()) {
            DoorConcealment.concealmentTick(event);
            if (livingEntity.getPersistentData().getInt("spaceFragmentationCopies") >= 1) {
                livingEntity.getPersistentData().putInt("spaceFragmentationCopies", livingEntity.getPersistentData().getInt("spaceFragmentationCopies") - 1);
            }
            if (livingEntity.getPersistentData().getInt("mazeTrap") >= 1) {
                CompoundTag tag = livingEntity.getPersistentData();
                int timer = tag.getInt("mazeTrap");
                int x = tag.getInt("mazeTrapX");
                int y = tag.getInt("mazeTrapY");
                int z = tag.getInt("mazeTrapZ");
                if (timer >= 1 ) {
                    if (livingEntity.isAlive()) {
                        livingEntity.getPersistentData().putInt("ignoreShouldntRender", 10);
                        LOTMNetworkHandler.sendToAllPlayers(new ClientShouldntRenderS2C(livingEntity.getUUID(), 20));
                    }
                    tag.putInt("mazeTrap", timer - 1);
                    if (timer == 1) {
                        BeyonderUtil.trueTeleportEntity(livingEntity, livingEntity.level(), x, y, z);
                        tag.putInt("mazeTrap", 0);
                    } else {
                        BeyonderUtil.trueTeleportEntity(livingEntity, livingEntity.level(), x, y + 130, z);
                    }
                }
            }
        }
        if (!livingEntity.level().isClientSide() && livingEntity.tickCount % 40 == 0) {
            if (livingEntity instanceof ServerPlayer serverPlayer) {
                LOTMNetworkHandler.sendToPlayer(new ClientWormOfStarDataS2C(livingEntity.getPersistentData().getInt("wormOfStar")), serverPlayer);
            }
            CompoundTag tag = livingEntity.getPersistentData();
            boolean currentState = tag.getBoolean("shouldntRenderSecretsSorcererHand");
            if (BeyonderUtil.currentPathwayAndSequenceMatchesNoException(livingEntity, BeyonderClassInit.APPRENTICE.get(), 4)) {
                tag.putBoolean("shouldntRenderSecretsSorcererHand", true);
            } else if (currentState) {
                tag.putBoolean("shouldntRenderSecretsSorcererHand", false);
            }
            if (livingEntity.tickCount % 120 == 0) {
                UUID playerId = livingEntity.getUUID();
                Boolean lastState = lastSentHandStates.get(playerId);
                if (lastState == null || lastState != currentState) {
                    LOTMNetworkHandler.sendToAllPlayers(new SyncShouldntRenderHandPacketS2C(currentState, playerId));
                    lastSentHandStates.put(playerId, currentState);
                }
            }
        }
    }


    public static void apprenticeAttackEvent(LivingAttackEvent event) {
        LivingEntity attacked = event.getEntity();
        Conceptualization.conceptualizationAttack(event);
        if (!attacked.level().isClientSide() && BeyonderUtil.currentPathwayAndSequenceMatchesNoException(attacked, BeyonderClassInit.APPRENTICE.get(), 3)) {
            if (event.getSource().is(DamageTypes.FALL)) {
                event.setCanceled(true);
            } else if (event.getSource().is(DamageTypes.ON_FIRE)) {
                event.setCanceled(true);
            } else if (event.getSource().is(DamageTypes.IN_FIRE)) {
                event.setCanceled(true);
            } else if (event.getSource().is(DamageTypes.FELL_OUT_OF_WORLD)) {
                event.setCanceled(true);
            }
        }
    }

    public static void enableWaterWalking(LivingEvent.LivingTickEvent event) {
        LivingEntity livingEntity = event.getEntity();
        Level level = livingEntity.level();
        if (level.isClientSide()) {
            return;
        }
        if (BeyonderUtil.currentPathwayAndSequenceMatchesNoException(livingEntity, BeyonderClassInit.APPRENTICE.get(), 3)) {
            BlockPos pos = livingEntity.blockPosition();
            BlockPos belowPos = pos.below();
            BlockState blockBelow = level.getBlockState(belowPos);
            BlockState currentBlock = level.getBlockState(pos);
            if (blockBelow.getBlock() instanceof LiquidBlock || currentBlock.getBlock() instanceof LiquidBlock) {
                if (!livingEntity.isShiftKeyDown()) {
                    if (livingEntity.getDeltaMovement().y < 0) {
                        Vec3 lookVec = livingEntity.getLookAngle().scale(2);
                        livingEntity.setDeltaMovement(lookVec.x(), 0.01, lookVec.z());
                        livingEntity.hurtMarked = true;
                    }
                    double waterSurfaceY = belowPos.getY() + 1.0;
                    if (livingEntity.getY() < waterSurfaceY) {
                        livingEntity.setPos(livingEntity.getX(), waterSurfaceY, livingEntity.getZ());
                    }
                    livingEntity.fallDistance = 0.0f;
                    livingEntity.setOnGround(true);
                }
            }
        }
    }
}
