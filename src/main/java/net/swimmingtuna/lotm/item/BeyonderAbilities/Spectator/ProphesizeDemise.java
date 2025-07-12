package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.networking.packet.SyncSequencePacketS2C;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

import static net.swimmingtuna.lotm.util.BeyonderUtil.isLivingEntityMoving;
import static net.swimmingtuna.lotm.util.BeyonderUtil.updatePositions;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ProphesizeDemise extends SimpleAbilityItem {

    public ProphesizeDemise(Properties properties) {
        super(properties, BeyonderClassInit.SPECTATOR, 1, 1000, 3000, 400, 400);
    }

    @Override
    public InteractionResult useAbilityOnEntity(ItemStack stack, LivingEntity player, LivingEntity interactionTarget, InteractionHand hand) {
        if (!player.level().isClientSide()) {
            if (!checkAll(player)) {
                return InteractionResult.FAIL;
            }
            int dir = BeyonderUtil.getDreamIntoReality(player);
            addCooldown(player, stack.getItem(), 3000 / dir);
            useSpirituality(player);
            prophesizeDemise(player, interactionTarget);
        }
        return InteractionResult.SUCCESS;
    }

    private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = Lazy.of(this::createAttributeMap);

    @SuppressWarnings("deprecation")
    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            return this.lazyAttributeMap.get();
        }
        return super.getDefaultAttributeModifiers(slot);
    }

    private Multimap<Attribute, AttributeModifier> createAttributeMap() {

        ImmutableMultimap.Builder<Attribute, AttributeModifier> attributeBuilder = ImmutableMultimap.builder();
        attributeBuilder.putAll(super.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND));
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 400, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 400, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use on an entity, prophesizes their demise, making it so they have to stay still for 10 seconds out of 30 seconds, otherwise they won't evade this prophecy and die"));
        tooltipComponents.add(Component.literal("Left Click for Prophesize Teleport (Block)"));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("1000").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("2.5 Minutes").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    public void prophesizeDemise(LivingEntity player, LivingEntity interactionTarget) {
        if (!interactionTarget.level().isClientSide() && !player.level().isClientSide()) {
            if (interactionTarget instanceof Player) {
                if (BeyonderUtil.getDreamIntoReality(player) > 1) {
                    interactionTarget.addEffect(new MobEffectInstance(ModEffects.FRENZY.get(), 40, 1, false, false));
                }
                interactionTarget.addEffect(new MobEffectInstance(ModEffects.SPECTATORDEMISE.get(), 600, 1, false, false));
            } else {
                interactionTarget.addEffect(new MobEffectInstance(MobEffects.WITHER, 300, 6, false, false));
            }
        }
    }

    public static void demiseTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        CompoundTag tag = entity.getPersistentData();
        if (!entity.level().isClientSide()) {
            double prevX = tag.getDouble("prevX");
            double prevY = tag.getDouble("prevY");
            double prevZ = tag.getDouble("prevZ");
            double currentX = tag.getDouble("currentX");
            double currentY = tag.getDouble("currentY");
            double currentZ = tag.getDouble("currentZ");
            boolean hasSpectatorDemise = entity.hasEffect(ModEffects.SPECTATORDEMISE.get());
            if (!hasSpectatorDemise) {
                tag.putInt("EntityDemise", 0);
                tag.putInt("NonDemise", 0);
            }

            if (hasSpectatorDemise) {
                MobEffectInstance demiseEffect = entity.getEffect(ModEffects.SPECTATORDEMISE.get());
                if (demiseEffect != null) {
                    int effectDuration = demiseEffect.getDuration();
                    int effectDurationSeconds;
                    if (effectDuration < 20) {
                        effectDurationSeconds = 1;
                    } else {
                        effectDurationSeconds = (effectDuration + 19) / 20;
                    }
                    int demise = tag.getInt("EntityDemise");
                    int nonDemise = tag.getInt("NonDemise");
                    if (isLivingEntityMoving(entity)) {
                        tag.putInt("EntityDemise", demise + 1);
                    } else {
                        tag.putInt("NonDemise", nonDemise + 1);
                    }
                    if (demise == 400) {
                        entity.kill();
                        tag.putInt("NonDemise", 0);
                    }
                    if (nonDemise > 200) {
                        tag.putInt("EntityDemise", 0);
                        tag.putInt("NonDemise", 0);
                        entity.removeEffect(ModEffects.SPECTATORDEMISE.get());
                        tag.putInt("NonDemise", 0);
                    }
                    if (nonDemise == 200) {
                        tag.putInt("EntityDemise", 0);
                        tag.putInt("NonDemise", 0);
                        entity.removeEffect(ModEffects.SPECTATORDEMISE.get());
                        entity.sendSystemMessage(Component.literal("You survived your fate").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
                    }
                    if (demise == 20) {
                        tag.putInt("EntityDemise", 21);
                        entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 19 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (demise == 40) {
                        tag.putInt("EntityDemise", 41);
                        entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 18 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (demise == 60) {
                        tag.putInt("EntityDemise", 61);
                        entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 17 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (demise == 80) {
                        tag.putInt("EntityDemise", 81);
                        entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 16 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (demise == 100) {
                        tag.putInt("EntityDemise", 101);
                        entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 15 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (demise == 120) {
                        tag.putInt("EntityDemise", 121);
                        entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 14 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (demise == 140) {
                        tag.putInt("EntityDemise", 141);
                        entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 13 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (demise == 160) {
                        tag.putInt("EntityDemise", 161);
                        entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 12 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (demise == 180) {
                        tag.putInt("EntityDemise", 181);
                        entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 11 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (demise == 200) {
                        tag.putInt("EntityDemise", 201);
                        entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 10 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (demise == 220) {
                        tag.putInt("EntityDemise", 221);
                        entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 9 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (demise == 240) {
                        tag.putInt("EntityDemise", 241);
                        entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 8 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (demise == 260) {
                        tag.putInt("EntityDemise", 261);
                        entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 7 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (demise == 280) {
                        tag.putInt("EntityDemise", 281);
                        entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 6 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (demise == 300) {
                        tag.putInt("EntityDemise", 301);
                        entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 5 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (demise == 320) {
                        tag.putInt("EntityDemise", 321);
                        entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 4 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (demise == 340) {
                        tag.putInt("EntityDemise", 341);
                        entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 3 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (demise == 360) {
                        tag.putInt("EntityDemise", 361);
                        entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 2 seconds, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (demise == 380) {
                        tag.putInt("EntityDemise", 381);
                        entity.sendSystemMessage(Component.literal("You need to stand still or you will die in 1 second, remaining time left on Death Prophecy is " + effectDurationSeconds).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                    }
                    if (nonDemise >= 20 && nonDemise <= 180 && nonDemise % 20 == 0) {
                        int standStillSecondsLeft = (200 - nonDemise) / 20;
                        entity.sendSystemMessage(Component.literal("You need to stand still for " + standStillSecondsLeft + " more seconds").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void handlePlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
            LOTMNetworkHandler.sendToPlayer(new SyncSequencePacketS2C(holder.getSequence()), (ServerPlayer) player);
            CompoundTag persistentData = player.getPersistentData();

            if (persistentData.contains("DemiseCounter")) {
                int demiseCounter = persistentData.getInt("DemiseCounter");

                if (!persistentData.contains("EntityDemise") || persistentData.getInt("EntityDemise") == 0) {
                    player.getPersistentData().putInt("EntityDemise", demiseCounter);
                }
            } else {
                if (!persistentData.contains("EntityDemise") || persistentData.getInt("EntityDemise") == 0) {

                    player.getPersistentData().putInt("EntityDemise", 0);
                }
            }
        }
    }
    @Override
    public @NotNull Rarity getRarity(ItemStack pStack) {
        return Rarity.create("SPECTATOR_ABILITY", ChatFormatting.AQUA);
    }

    @Override
    public int getPriority(LivingEntity livingEntity, LivingEntity target) {
        if (target != null) {
            return (int) (100 - livingEntity.getHealth());
        }
        return 0;
    }
}