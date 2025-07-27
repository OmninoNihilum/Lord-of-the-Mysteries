package net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
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
import net.swimmingtuna.lotm.blocks.DimensionalSight.DimensionalSightTileEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.EmptyLeftClick.EmptyLeftClickHandlerSkill;
import net.swimmingtuna.lotm.item.BeyonderAbilities.EmptyLeftClick.EmptyLeftClickType;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.networking.packet.SealingLeftClickC2S;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class Sealing extends EmptyLeftClickHandlerSkill {
    public Sealing(Properties properties) {
        super(properties, BeyonderClassInit.APPRENTICE, 2, 3000, 1500);
    }

    @Override
    public InteractionResult useAbility(Level level, LivingEntity player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        DimensionalSightTileEntity dimensionalSightTileEntity = BeyonderUtil.findNearbyDimensionalSight(player);
        if (dimensionalSightTileEntity != null && dimensionalSightTileEntity.getScryTarget() != null) {
            if (!checkAll(player, BeyonderClassInit.APPRENTICE.get(), 2, 4000 - (BeyonderUtil.getSequence(dimensionalSightTileEntity.getScryTarget()) * 250), true)) {
                return InteractionResult.FAIL;
            }
            addCooldown(player, this, 1200 - (BeyonderUtil.getSequence(dimensionalSightTileEntity.getScryTarget()) * 100));
            useSpirituality(player, 4000 - (BeyonderUtil.getSequence(dimensionalSightTileEntity.getScryTarget()) * 250));
            sealAbilities(player, dimensionalSightTileEntity.getScryTarget());
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useAbilityOnEntity(ItemStack stack, LivingEntity livingEntity, LivingEntity interactionTarget, InteractionHand hand) {
        if (!livingEntity.level().isClientSide && !interactionTarget.level().isClientSide) {
            if (!checkAll(livingEntity, BeyonderClassInit.APPRENTICE.get(), 2, 4000 - (BeyonderUtil.getSequence(interactionTarget) * 250), true)) {
                return InteractionResult.FAIL;
            }
            addCooldown(livingEntity, this, 1200 - (BeyonderUtil.getSequence(interactionTarget) * 100));
            useSpirituality(livingEntity, 4000 - (BeyonderUtil.getSequence(interactionTarget) * 250));
            sealAbilities(livingEntity, interactionTarget);
        }
        return InteractionResult.SUCCESS;
    }

    public static void sealAbilities(LivingEntity livingEntity, LivingEntity target) {
        if (!livingEntity.level().isClientSide() && !target.level().isClientSide()) {
            int sealingChoice = livingEntity.getPersistentData().getInt("planeswalkerSealingChoice");
            int damage = (int) (float) BeyonderUtil.getDamage(livingEntity).get(ItemInit.SEALING.get());
            target.getPersistentData().putInt("abilitySealed" + sealingChoice, damage);
        }
    }

    public static void sealingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity living = event.getEntity();
        if (!living.level().isClientSide()) {
            for (int i = 0; i <= 9; i++) {
                if (living.getPersistentData().getInt("abilitySealed" + i) >= 1) {
                    living.getPersistentData().putInt("abilitySealed" + i, living.getPersistentData().getInt("abilitySealed" + i) - 1);
                }
            }
        }
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof Player player) {
            if (player.tickCount % 2 == 0 && !level.isClientSide()) {
                if (player.getMainHandItem().getItem() instanceof Sealing) {
                    player.displayClientMessage(Component.literal("Current Ability Seal Choice is: " + player.getPersistentData().getInt("planeswalkerSealingChoice")).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
                }
            }
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.literal("Upon use on a target, seal their abilities depending on the sequence you choose for some time."));
        tooltipComponents.add(Component.literal("Left click in order to switch which sequence abilities you can seal, with the lowest being your own."));
        tooltipComponents.add(Component.literal("Cooldown and spirituality will vary depending on strength of target."));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("~3000").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("~40 Seconds").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, isAdvanced);
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
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 15, AttributeModifier.Operation.ADDITION)); // adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 15, AttributeModifier.Operation.ADDITION)); // adds a 12 block reach for interacting with blocks, pretty much useless for this item
        return attributeBuilder.build();
    }

    @Override
    public Rarity getRarity(ItemStack pStack) {
        return Rarity.create("APPRENTICE_ABILITY", ChatFormatting.AQUA);
    }

    @Override
    public int getPriority(LivingEntity livingEntity, LivingEntity target) {
        if (target != null) {
            return 60;
        }
        return 0;
    }

    @Override
    public EmptyLeftClickType getleftClickEmpty() {
        return new SealingLeftClickC2S();
    }
}