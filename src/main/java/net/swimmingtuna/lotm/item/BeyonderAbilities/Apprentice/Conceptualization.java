package net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice;


import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.blocks.DimensionalSight.DimensionalSightTileEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.init.ParticleInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.networking.packet.UpdateItemInHandC2S;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.LeftClickHandler.LeftClickHandlerSkillP;
import net.swimmingtuna.lotm.util.LeftClickHandler.LeftClickType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class Conceptualization extends LeftClickHandlerSkillP {

    public Conceptualization(Properties properties) {
        super(properties, BeyonderClassInit.APPRENTICE, 0, 0, 20);
    }

    boolean shouldUseSpirituality = true;

    @Override
    public InteractionResult useAbility(Level level, LivingEntity player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        conceptualize(player);
        if (shouldUseSpirituality) {
            addCooldown(player);
            useSpirituality(player);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useAbilityOnEntity(ItemStack stack, LivingEntity player, LivingEntity interactionTarget, InteractionHand hand) {
        if (!player.level().isClientSide()) {
            int sequence = BeyonderUtil.getSequence(interactionTarget);
            if (sequence == -1) {
                sequence = 9;
            }
            if (sequence == 0) {
                sequence = 1;
            }
            if (!checkAll(player, BeyonderClassInit.APPRENTICE.get(), 0,10000 / sequence,true)) {
                return InteractionResult.FAIL;
            }
            conceptualizeTarget(interactionTarget);
            useSpirituality(player, 10000 / sequence);
            addCooldown(player, this, 2400 / sequence);

        }
        return InteractionResult.SUCCESS;
    }

    public void conceptualizeTarget(LivingEntity target) {
        int sequence = BeyonderUtil.getSequence(target);
        if (sequence == -1) {
            sequence = 9;
        }
        if (sequence == 0) {
            sequence = 1;
        }
        target.getPersistentData().putInt("doorConceptualizationPassive", 6000 / (10 - sequence));
    }

    public void conceptualize(LivingEntity player) {
        if (!player.level().isClientSide()) {
            DimensionalSightTileEntity dimensionalSightTileEntity = BeyonderUtil.findNearbyDimensionalSight(player);
            if (dimensionalSightTileEntity != null && dimensionalSightTileEntity.getScryTarget() != null) {
                LivingEntity scryTarget = dimensionalSightTileEntity.getScryTarget();
                int sequence = BeyonderUtil.getSequence(scryTarget);
                if (sequence == -1) {
                    sequence = 9;
                }
                if (sequence == 0) {
                    sequence = 1;
                }
                if (BeyonderUtil.getSpirituality(player) <= 10000 / sequence) {
                    shouldUseSpirituality = false;
                    player.sendSystemMessage(Component.literal("You don't have enough spirituality to conceptualize your Dimensional Sight Target").withStyle(ChatFormatting.RED));
                }
                player.sendSystemMessage(Component.literal("You conceptualized your Dimensional Sight Target").withStyle(ChatFormatting.AQUA));
                dimensionalSightTileEntity.getScryTarget().getPersistentData().putInt("doorConceptualizationPassive", 6000 / (10 - sequence));
            } else {
                CompoundTag tag = player.getPersistentData();
                boolean conceptualization = tag.getBoolean("doorConceptualization");
                tag.putBoolean("doorConceptualization", !conceptualization);
                if (player instanceof Player pPlayer) {
                    pPlayer.displayClientMessage(Component.literal("Conceptualization Turned " + (conceptualization ? "Off" : "On")).withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE), true);
                }
            }
        }
    }

    public static void conceptualizationTick(LivingEvent.LivingTickEvent event) {
        LivingEntity living = event.getEntity();
        if (!living.level().isClientSide()) {
            CompoundTag tag = living.getPersistentData();
            int passive = tag.getInt("doorConceptualizationPassive");
            int conceptualized = tag.getInt("doorConceptualization");
            if (passive >= 1) {
                living.getActiveEffectsMap().entrySet().removeIf(entry -> {
                    MobEffect effect = entry.getKey();
                    return !effect.isBeneficial();
                });
                tag.putInt("doorConceptualizationPassive", passive - 1);
                BeyonderUtil.applyStun(living, 10);
                if (living.level() instanceof ServerLevel serverLevel) {
                    for (int i = 0; i <= 1; i++) {
                        //WHITE FLASH //AQUA FLASH //PURPLE FLASH //YELLOW FLASH
                        float scale = BeyonderUtil.getScale(living);
                        float random = BeyonderUtil.getRandomInRange(scale) * 5;
                        float random2 = BeyonderUtil.getRandomInRange(scale) * 5;
                        float random3 = BeyonderUtil.getRandomInRange(scale) * 5;
                        float random4 = BeyonderUtil.getRandomInRange(scale) * 5;
                        //WHITE FLASH //AQUA FLASH //PURPLE FLASH //YELLOW FLASH
                        serverLevel.sendParticles(ParticleInit.YELLOW_FLASH_PARTICLE.get(), living.getX() + random, living.getY() + random4, living.getZ() + random2, 0, 0, 0, 0, 0);
                        serverLevel.sendParticles(ParticleInit.AQUA_FLASH_PARTICLE.get(), living.getX() + random2, living.getY() + random3, living.getZ() + random3, 0, 0, 0, 0, 0);
                        serverLevel.sendParticles(ParticleInit.PURPLE_FLASH_PARTICLE.get(), living.getX() + random3, living.getY() + random2, living.getZ() + random4, 0, 0, 0, 0, 0);
                        serverLevel.sendParticles(ParticleInit.WHITE_FLASH_PARTICLE.get(), living.getX() + random4, living.getY() + random, living.getZ() + random, 0, 0, 0, 0, 0);
                    }
                }
                if (living.tickCount % 20 == 0) {
                    BeyonderUtil.setInvisible(living, true, 30);
                }
            }
            if (conceptualized >= 1) {
                living.getActiveEffectsMap().entrySet().removeIf(entry -> {
                    MobEffect effect = entry.getKey();
                    return !effect.isBeneficial();
                });
                BeyonderUtil.startFlying(living, 0.18f, 20);
                if (BeyonderUtil.getSpirituality(living) < 15) {
                    BeyonderUtil.setInvisible(living, false, 0);
                    tag.putBoolean("doorConceptualization", false);
                } else {
                    BeyonderUtil.useSpirituality(living, 20);
                    if (living.level() instanceof ServerLevel serverLevel) {
                        for (Player player : serverLevel.players()) {
                            //if (player != living) {
                                for (int i = 0; i <= 1; i++) {
                                    //WHITE FLASH //AQUA FLASH //PURPLE FLASH //YELLOW FLASH
                                    float scale = BeyonderUtil.getScale(living);
                                    float random = BeyonderUtil.getRandomInRange(scale) * 5;
                                    float random2 = BeyonderUtil.getRandomInRange(scale) * 5;
                                    float random3 = BeyonderUtil.getRandomInRange(scale) * 5;
                                    float random4 = BeyonderUtil.getRandomInRange(scale) * 5;
                                    BeyonderUtil.sendPlayerParticle(player, ParticleInit.YELLOW_FLASH_PARTICLE.get(), living.getX() + random, living.getY() + random4, living.getZ() + random2, 0,0,0);
                                    BeyonderUtil.sendPlayerParticle(player, ParticleInit.AQUA_FLASH_PARTICLE.get(), living.getX() + random2, living.getY() + random3, living.getZ() + random, 0,0,0);
                                    BeyonderUtil.sendPlayerParticle(player, ParticleInit.PURPLE_FLASH_PARTICLE.get(), living.getX() + random3, living.getY() + random2, living.getZ() + random4, 0,0,0);
                                    BeyonderUtil.sendPlayerParticle(player, ParticleInit.WHITE_FLASH_PARTICLE.get(), living.getX() + random4, living.getY() + random, living.getZ() + random3, 0,0,0);
                                }
                            //}
                        }
                        /*
                        for (int i = 0; i <= 1; i++) {
                            //WHITE FLASH //AQUA FLASH //PURPLE FLASH //YELLOW FLASH
                            float scale = BeyonderUtil.getScale(living);
                            float random = BeyonderUtil.getRandomInRange(scale) * 5;
                            float random2 = BeyonderUtil.getRandomInRange(scale) * 5;
                            float random3 = BeyonderUtil.getRandomInRange(scale) * 5;
                            float random4 = BeyonderUtil.getRandomInRange(scale) * 5;
                            //WHITE FLASH //AQUA FLASH //PURPLE FLASH //YELLOW FLASH
                            serverLevel.sendParticles(ParticleInit.YELLOW_FLASH_PARTICLE.get(), living.getX() + random, living.getY() + random4, living.getZ() + random2, 0, 0, 0, 0, 0);
                            serverLevel.sendParticles(ParticleInit.AQUA_FLASH_PARTICLE.get(), living.getX() + random2, living.getY() + random3, living.getZ() + random3, 0, 0, 0, 0, 0);
                            serverLevel.sendParticles(ParticleInit.PURPLE_FLASH_PARTICLE.get(), living.getX() + random3, living.getY() + random2, living.getZ() + random4, 0, 0, 0, 0, 0);
                            serverLevel.sendParticles(ParticleInit.WHITE_FLASH_PARTICLE.get(), living.getX() + random4, living.getY() + random, living.getZ() + random, 0, 0, 0, 0, 0);
                        }

                         */
                    }
                    if (living.tickCount % 20 == 0) {
                        BeyonderUtil.setInvisible(living, true, 30);
                    }
                }
            }
        }
    }

    public static void conceptualizationAttack(LivingAttackEvent event) {
        LivingEntity attacked = event.getEntity();
        if (!attacked.level().isClientSide()) {
            if (isConceptualized(attacked)) {
                event.setCanceled(true);
            }
        }
    }

    public static boolean isConceptualized(LivingEntity living) {
        return (living.getPersistentData().getInt("doorConceptualizationPassive") >= 1 || living.getPersistentData().getBoolean("doorConceptualization"));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Use to enable/disable conceptualization on yourself, or conceptualize a target. If used on yourself, you will be immune to any damage or negative effects entirely. If used on a target, they will have the same effect, but also be unable to do anything for some time. The weaker the entity, the longer they will be conceptualized."));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("300 per second if used on self. Depends on strength of target if used on one.").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("1 Second").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    @Override
    public @NotNull Rarity getRarity(ItemStack pStack) {
        return Rarity.create("APPRENTICE_ABILITY", ChatFormatting.AQUA);
    }

    @Override
    public int getPriority(LivingEntity livingEntity, LivingEntity target) {
        if (target != null && target.getHealth() <= livingEntity.getHealth()) {
            livingEntity.getPersistentData().putInt("trickmasterBlinkDistance", (int) target.distanceTo(livingEntity));
            return (int) (100 - (target.getHealth()));
        } else if (livingEntity.getHealth() <= 20) {
            livingEntity.getPersistentData().putInt("trickmasterBlinkDistance", 100);
            return 80;
        }
        if (livingEntity.getPersistentData().getInt("trickmasterBlinkDistance") == 0 && target == null) {
            livingEntity.getPersistentData().putInt("trickmasterBlinkDistance", 5);
        }
        return 0;
    }
    @Override
    public <T> LeftClickType getleftClickEmpty(T item) {
        return new UpdateItemInHandC2S((Integer) item, new ItemStack(ItemInit.BLINKAFTERIMAGE.get()));
    }
}
