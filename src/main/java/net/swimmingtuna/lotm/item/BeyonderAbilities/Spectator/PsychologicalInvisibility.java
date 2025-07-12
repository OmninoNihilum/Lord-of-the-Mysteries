package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator;


import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.networking.packet.SyncShouldntRenderInvisibilityPacketS2C;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.ClientData.ClientShouldntRenderInvisibilityData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;


public class PsychologicalInvisibility extends SimpleAbilityItem {

    public PsychologicalInvisibility(Properties properties) {
        super(properties, BeyonderClassInit.SPECTATOR, 6, 0, 240);
    }

    @Override
    public InteractionResult useAbility(Level level, LivingEntity player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        psychologicalInvisibilityAbility(player);
        if (ClientShouldntRenderInvisibilityData.getShouldntRender(player.getUUID())) {
            addCooldown(player);
        }
        return InteractionResult.SUCCESS;
    }

    private static void psychologicalInvisibilityAbility(LivingEntity player) {
        if (!player.level().isClientSide()) {
            CompoundTag tag = player.getPersistentData();
            boolean newState = !tag.getBoolean("psychologicalInvisibility");

            if (newState) {
                for (Mob mob : player.level().getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(50))) {
                    if (mob.getTarget() == player) {
                        mob.setTarget(null);
                    }
                }
                if (player instanceof Player pPlayer) {
                    pPlayer.displayClientMessage(Component.literal("You are now invisible").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GREEN), true);
                }
            } else {
                if (player instanceof Player pPlayer) {
                    pPlayer.displayClientMessage(Component.literal("You are now visible").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.RED), true);
                }
            }
            tag.putBoolean("psychologicalInvisibility", newState);

            UUID playerId = player.getUUID();
            LOTMNetworkHandler.sendToAllPlayers(new SyncShouldntRenderInvisibilityPacketS2C(true, playerId, 20));
        }
    }

    public static void removePsychologicalInvisibilityEffect(LivingEntity living) {
        if (living.level().isClientSide()) {
            return;
        }
        CompoundTag tag = living.getPersistentData();
        if (tag.getBoolean("psychologicalInvisibility")) {
            tag.putBoolean("psychologicalInvisibility", false);
            tag.putInt("psychologicalInvisibilityHurt", 0);
            LOTMNetworkHandler.sendToAllPlayers(new SyncShouldntRenderInvisibilityPacketS2C(false, living.getUUID(), 0));
        }
        living.removeEffect(MobEffects.INVISIBILITY);
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, hypnotize all entities around you to hide your presence. If you get hit enough times in a close period of time, you will be turned visible again."));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("2% of your max spirituality per second (Max of 20/s)").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("12 Seconds").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    @Override
    public @NotNull Rarity getRarity(ItemStack pStack) {
        return Rarity.create("SPECTATOR_ABILITY", ChatFormatting.AQUA);
    }

    public static void psychologicalInvisibility(LivingEvent.LivingTickEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity.tickCount % 10 == 0) {
            CompoundTag tag = livingEntity.getPersistentData();
            int x = tag.getInt("psychologicalInvisibilityHurt");
            if (x >= 1) {
                tag.putInt("psychologicalInvisibilityHurt", x - 1);
            }
            boolean currentState = tag.getBoolean("psychologicalInvisibility");
            if (currentState) {
                if (x >= 400) {
                    removePsychologicalInvisibilityEffect(livingEntity);
                    if (livingEntity instanceof Player player) {
                        player.displayClientMessage(Component.literal("You got hit too many times and are no longer invisible!").withStyle(ChatFormatting.RED), true);
                    }
                    SimpleAbilityItem.addCooldown(livingEntity, ItemInit.PSYCHOLOGICAL_INVISIBILITY.get(), 240);
                }
                Collection<MobEffectInstance> effects = livingEntity.getActiveEffects();
                effects.forEach(effect -> {
                    if (effect.isAmbient() || effect.isVisible()) {
                        MobEffectInstance newEffect = new MobEffectInstance(effect.getEffect(), effect.getDuration(), effect.getAmplifier(), false, false, false);
                        effect.update(newEffect);
                    }
                });
                for (Mob mob : livingEntity.level().getEntitiesOfClass(Mob.class, livingEntity.getBoundingBox().inflate(40))) {
                    if (mob.getTarget() == livingEntity && mob.getLastAttacker() != livingEntity) {
                        mob.setTarget(null);
                    }
                }
                livingEntity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 11, 1, false, false));
                BeyonderUtil.useSpirituality(livingEntity, Math.min(10, BeyonderUtil.getMaxSpirituality(livingEntity) / 100));
                UUID playerId = livingEntity.getUUID();
                LOTMNetworkHandler.sendToAllPlayers(new SyncShouldntRenderInvisibilityPacketS2C(true, playerId, 20));
            }
        }
    }


    public static void psychologicalInvisibilityAttack(LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide()) {
            if (entity.getPersistentData().getBoolean("psychologicalInvisibility")) {
                CompoundTag tag = entity.getPersistentData();
                int x = tag.getInt("psychologicalInvisibilityHurt");
                tag.putInt("psychologicalInvisibilityHurt", x + 100);
            }
        }
    }

    @Override
    public int getPriority(LivingEntity livingEntity, LivingEntity target) {
        if (livingEntity.getPersistentData().getBoolean("psychologicalInvisibility") && target == null) {
            return 100;
        } else if (!livingEntity.getPersistentData().getBoolean("psychologicalInvisibility") && target != null) {
            return 100;
        }
        return 0;
    }
}