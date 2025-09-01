package net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.blocks.DimensionalSight.DimensionalSightTileEntity;
import net.swimmingtuna.lotm.entity.ApprenticeDoorEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class DoorLayering extends SimpleAbilityItem {

    public DoorLayering(Properties properties) {
        super(properties, BeyonderClassInit.APPRENTICE, 0, 10000, 1200, 100, 100);
    }

    @Override
    public InteractionResult useAbility(Level level, LivingEntity player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        useSpirituality(player);
        addCooldown(player);
        doorLayering(player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useAbilityOnEntity(ItemStack stack, LivingEntity player, LivingEntity interactionTarget, InteractionHand hand) {
        if (!player.level().isClientSide()) {
            if (!checkAll(player)) {
                return InteractionResult.FAIL;
            }
            addCooldown(player);
            useSpirituality(player);
            doorLayering(player, interactionTarget);
        }
        return InteractionResult.SUCCESS;
    }


    public static void doorLayering(LivingEntity livingEntity) {
        Level level = livingEntity.level();
        if (!level.isClientSide()) {
            int doorLayeringDistance = livingEntity.getPersistentData().getInt("doorLayering");
            DimensionalSightTileEntity dimensionalSightTileEntity = BeyonderUtil.findNearbyDimensionalSight(livingEntity);
            if (dimensionalSightTileEntity != null && dimensionalSightTileEntity.getScryTarget() != null) {
                livingEntity.getPersistentData().putInt("doorLayeringCounter", 100);
                livingEntity.getPersistentData().putInt("doorLayeringX", (int) dimensionalSightTileEntity.getScryTarget().getX());
                livingEntity.getPersistentData().putInt("doorLayeringY", (int) dimensionalSightTileEntity.getScryTarget().getY());
                livingEntity.getPersistentData().putInt("doorLayeringZ", (int) dimensionalSightTileEntity.getScryTarget().getZ());
                livingEntity.getPersistentData().putInt("doorLayeringDamage", (int) (float) BeyonderUtil.getDamage(livingEntity).get(ItemInit.DOOR_LAYERING.get()));
            } else {
                Vec3 scale = livingEntity.getLookAngle().scale(doorLayeringDistance);
                Vec3 playerPos = livingEntity.position();
                BlockPos pos = new BlockPos((int) (playerPos.x + scale.x()), (int) (playerPos.y + scale.y()), (int) (playerPos.z + scale.z()));
                livingEntity.getPersistentData().putInt("doorLayeringCounter", 100);
                livingEntity.getPersistentData().putInt("doorLayeringX", pos.getX());
                livingEntity.getPersistentData().putInt("doorLayeringY", pos.getY());
                livingEntity.getPersistentData().putInt("doorLayeringZ", pos.getZ());
                livingEntity.getPersistentData().putInt("doorLayeringDamage", (int) (float) BeyonderUtil.getDamage(livingEntity).get(ItemInit.DOOR_LAYERING.get()));
            }
        }
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
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 100, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 100, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }


    public static void doorLayering(LivingEntity livingEntity, LivingEntity living) {
        Level level = livingEntity.level();
        if (!level.isClientSide()) {
            livingEntity.getPersistentData().putInt("doorLayeringCounter", 100);
            livingEntity.getPersistentData().putInt("doorLayeringX", (int) living.getX());
            livingEntity.getPersistentData().putInt("doorLayeringY", (int) living.getY());
            livingEntity.getPersistentData().putInt("doorLayeringZ", (int) living.getZ());
            livingEntity.getPersistentData().putInt("doorLayeringDamage", (int) (float) BeyonderUtil.getDamage(livingEntity).get(ItemInit.DOOR_LAYERING.get()));
        }
    }

    public static void doorLayeringTick(LivingEvent.LivingTickEvent event) {
        LivingEntity living = event.getEntity();
        CompoundTag tag = living.getPersistentData();
        int timer = tag.getInt("doorLayeringCounter");
        float originalScale = tag.getFloat("originalBlackholeScale");
        int gravityResetTimer = tag.getInt("resetBlackholeScale");
        int doorX = tag.getInt("doorLayeringX");
        int doorY = tag.getInt("doorLayeringY");
        int doorZ = tag.getInt("doorLayeringZ");
        int damage = tag.getInt("doorLayeringDamage");
        if (gravityResetTimer >= 1) {
            tag.putInt("resetBlackholeScale", gravityResetTimer - 1);
            if (gravityResetTimer == 1) {
                BeyonderUtil.setScale(living, originalScale);
                tag.putInt("resetBlackholeScale", 0);
            }
        }
        if (timer >= 1) {
            tag.putInt("doorLayeringCounter", timer - 1);
            BlockPos pos = new BlockPos(doorX, doorY, doorZ);
            ApprenticeDoorEntity door = new ApprenticeDoorEntity(living.level(), living, 0, 400 - timer, 0, pos.getX(), pos.getY(), pos.getZ(), living.level(), ApprenticeDoorEntity.DoorAnimationKind.FADE_IN);
            door.setYaw(BeyonderUtil.getPositiveRandomInRange(360));
            door.setPitch(BeyonderUtil.getPositiveRandomInRange(360));
            door.teleportTo(pos.getX(), pos.getY(), pos.getZ());
            BeyonderUtil.setScale(door, (int) BeyonderUtil.getRandomInRange(Math.max(4, 7)));
            living.level().addFreshEntity(door);
        }
        if (timer == 30) {
            BlockPos pos = new BlockPos(doorX, doorY, doorZ);
            tag.putInt("doorLayeringCounter", 0);
            ApprenticeDoorEntity blackHoleDoor = new ApprenticeDoorEntity(living, 120, living.level(), 0, 0);
            blackHoleDoor.getPersistentData().putInt("doorLayeringDamage", damage);
            blackHoleDoor.setYaw(BeyonderUtil.getPositiveRandomInRange(360));
            blackHoleDoor.setPitch(BeyonderUtil.getPositiveRandomInRange(360));
            blackHoleDoor.teleportTo(pos.getX(), pos.getY(), pos.getZ());
            BeyonderUtil.setScale(blackHoleDoor, (int) BeyonderUtil.getRandomInRange(Math.max(4, 7)));
            living.level().addFreshEntity(blackHoleDoor);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, layer doors in front of you over and over, causing the gravity around them to be so strong it creates a black hole."));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("10000").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("1 Minute").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    @Override
    public Rarity getRarity(ItemStack pStack) {
        return Rarity.create("APPRENTICE_ABILITY", ChatFormatting.AQUA);
    }

    @Override
    public int getPriority(LivingEntity livingEntity, LivingEntity target) {
        if (target != null) {
            return 90;
        }
        return 0;
    }
}