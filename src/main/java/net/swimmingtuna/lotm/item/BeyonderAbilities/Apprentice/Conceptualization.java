package net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice;


import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.blocks.DimensionalSight.DimensionalSightTileEntity;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.EFunctions;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.EventManager;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.init.ParticleInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.networking.packet.UpdateItemInHandC2S;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.LeftClickHandler.LeftClickHandlerSkillP;
import net.swimmingtuna.lotm.util.LeftClickHandler.LeftClickType;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class Conceptualization extends SimpleAbilityItem {

    public Conceptualization(Properties properties) {
        super(properties, BeyonderClassInit.APPRENTICE, 0, 0, 20, 30,30);
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
            conceptualizeTarget(player, interactionTarget);
            useSpirituality(player, 10000 / sequence);
            addCooldown(player, this, 2400 / sequence);

        }
        return InteractionResult.SUCCESS;
    }

    public void conceptualizeTarget(LivingEntity user,LivingEntity target) {
        int sequence = BeyonderUtil.getSequence(target);
        if (sequence == -1) {
            sequence = 9;
        }
        if (sequence == 0) {
            sequence = 1;
        }
        if (user instanceof Mob mob) {
            float health = mob.getHealth();
            float targetHealth = mob.getHealth();
            if (targetHealth < health || mob.getPersistentData().getBoolean("doorConceptualization")) {
                EventManager.addToRegularLoop(target, EFunctions.CONCEPTUALIZATION_TICK.get());
                conceptualize(mob);
            } else {
                EventManager.addToRegularLoop(target, EFunctions.CONCEPTUALIZATION_TICK.get());
                target.getPersistentData().putInt("doorConceptualizationPassive", 6000 / (10 - sequence));
            }
        }
    }

    public void conceptualize(LivingEntity player) {
        if (!player.level().isClientSide()) {
            DimensionalSightTileEntity dimensionalSightTileEntity = BeyonderUtil.findNearbyDimensionalSight(player);
            if (dimensionalSightTileEntity != null && dimensionalSightTileEntity.getScryTarget() != null) {
                LivingEntity scryTarget = dimensionalSightTileEntity.getScryTarget();
                EventManager.addToRegularLoop(scryTarget, EFunctions.CONCEPTUALIZATION_TICK.get());
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
                EventManager.addToRegularLoop(player, EFunctions.CONCEPTUALIZATION_TICK.get());
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
                        float scale = BeyonderUtil.getScale(living);
                        float random = BeyonderUtil.getRandomInRange(scale) * 5;
                        float random2 = BeyonderUtil.getRandomInRange(scale) * 5;
                        float random3 = BeyonderUtil.getRandomInRange(scale) * 5;
                        float random4 = BeyonderUtil.getRandomInRange(scale) * 5;
                        serverLevel.sendParticles(ParticleInit.YELLOW_FLASH_PARTICLE.get(), living.getX() + random, living.getY() + random4, living.getZ() + random2, 0, 0, 0, 0, 0);
                        serverLevel.sendParticles(ParticleInit.AQUA_FLASH_PARTICLE.get(), living.getX() + random2, living.getY() + random3, living.getZ() + random3, 0, 0, 0, 0, 0);
                        serverLevel.sendParticles(ParticleInit.PURPLE_FLASH_PARTICLE.get(), living.getX() + random3, living.getY() + random2, living.getZ() + random4, 0, 0, 0, 0, 0);
                        serverLevel.sendParticles(ParticleInit.WHITE_FLASH_PARTICLE.get(), living.getX() + random4, living.getY() + random, living.getZ() + random, 0, 0, 0, 0, 0);
                    }
                }
                if (living.tickCount % 20 == 0) {
                    BeyonderUtil.setInvisible(living, true, 30);
                }
            } else {
                if (conceptualized == 0) {
                    EventManager.removeFromRegularLoop(living, EFunctions.CONCEPTUALIZATION_TICK.get());
                }
            }
            if (conceptualized >= 1) {
                living.getActiveEffectsMap().entrySet().removeIf(entry -> {
                    MobEffect effect = entry.getKey();
                    return !effect.isBeneficial();
                });
                BeyonderUtil.startFlying(living, 0.18f, 20);
                int damage = (int) (float) BeyonderUtil.getDamage(living).get(ItemInit.CONCEPTUALIZATION.get());
                if (BeyonderUtil.getSpirituality(living) < damage) {
                    BeyonderUtil.setInvisible(living, false, 0);
                    tag.putBoolean("doorConceptualization", false);
                    living.sendSystemMessage(Component.literal("Your Conceptualization was turned off due to a lack of spirituality").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                } else {
                    BeyonderUtil.useSpirituality(living, damage);
                    if (living.level() instanceof ServerLevel serverLevel) {
                        for (Player player : serverLevel.players()) {
                            if (player != living) {
                                for (int i = 0; i <= 2; i++) {
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
                            } else if (BeyonderUtil.getRandomInRange(10) > 9) {
                                for (int i = 0; i <= 1; i++) {
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
                            }
                        }
                    }
                    if (living.tickCount % 20 == 0) {
                        BeyonderUtil.setInvisible(living, true, 30);
                    }
                }
            } else {
                if (passive == 0) {
                    EventManager.removeFromRegularLoop(living, EFunctions.CONCEPTUALIZATION_TICK.get());
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
        tooltipComponents.add(Component.literal("Less particles will be shown for you"));
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
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 30, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 30, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }

    @Override
    public int getPriority(LivingEntity livingEntity, LivingEntity target) {
        CompoundTag tag = livingEntity.getPersistentData();
        boolean conceptualization = tag.getBoolean("doorConceptualization");
        if (target == null && conceptualization) {
            return 100;
        } else if (target != null && conceptualization && BeyonderUtil.getSpirituality(livingEntity) < BeyonderUtil.getMaxSpirituality(livingEntity) * 0.3) {
            return 70;
        } else if (target != null && !conceptualization) {
            return (int) (BeyonderUtil.getSpirituality(livingEntity) / (BeyonderUtil.getMaxSpirituality(livingEntity) * 0.6f)) * 100;
        }
        return 0;
    }
}
