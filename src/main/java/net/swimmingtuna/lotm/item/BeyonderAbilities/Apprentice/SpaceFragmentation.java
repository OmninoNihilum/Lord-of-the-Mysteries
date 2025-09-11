package net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.blocks.DimensionalSight.DimensionalSightTileEntity;
import net.swimmingtuna.lotm.entity.SpaceFragmentationEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.networking.packet.UpdateItemInHandC2S;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.LeftClickHandler.LeftClickHandlerSkillP;
import net.swimmingtuna.lotm.util.LeftClickHandler.LeftClickType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class SpaceFragmentation extends LeftClickHandlerSkillP {

    public SpaceFragmentation(Properties properties) {
        super(properties, BeyonderClassInit.APPRENTICE, 1, 5000, 1200);
    }

    @Override
    public InteractionResult useAbility(Level level, LivingEntity player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        useSpirituality(player);
        addCooldown(player);
        tearSpace(player);
        return InteractionResult.SUCCESS;
    }

    public static void tearSpace(LivingEntity livingEntity){
        Level level = livingEntity.level();
        if(level.isClientSide) return;
        int damage = (int) (float) BeyonderUtil.getDamage(livingEntity).get(ItemInit.SPACE_FRAGMENTATION.get());
        DimensionalSightTileEntity dimensionalSightTileEntity = BeyonderUtil.findNearbyDimensionalSight(livingEntity);
        if (dimensionalSightTileEntity != null && dimensionalSightTileEntity.getScryTarget() != null) {
            livingEntity.sendSystemMessage(Component.literal("You fragmented space around your dimensional sight target").withStyle(ChatFormatting.AQUA));
            SpaceFragmentationEntity fragment = new SpaceFragmentationEntity(EntityInit.SPACE_FRAGMENTATION_ENTITY.get(), livingEntity.level());
            fragment.setOwner(livingEntity);
            fragment.setArea(damage);
            LivingEntity scryEntity = dimensionalSightTileEntity.getScryTarget();
            fragment.teleportTo(scryEntity.getX(), scryEntity.getY(), scryEntity.getZ());
            level.addFreshEntity(fragment);
        } else {
            int x = livingEntity.getPersistentData().getInt("keyOfStarsFragmentation");
            SpaceFragmentationEntity fragment = new SpaceFragmentationEntity(EntityInit.SPACE_FRAGMENTATION_ENTITY.get(), livingEntity.level());
            fragment.setOwner(livingEntity);
            fragment.setArea(damage);
            Vec3 scale = livingEntity.getLookAngle().scale(x);
            fragment.teleportTo(livingEntity.getX() + scale.x(), livingEntity.getY() + scale.y(), livingEntity.getZ() + scale.z());
            level.addFreshEntity(fragment);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof Player livingEntity && !livingEntity.isSpectator()) {
            if (livingEntity.isShiftKeyDown()) {
                int fragmentationDistance = livingEntity.getPersistentData().getInt("keyOfStarsFragmentation");
                if (livingEntity.getMainHandItem().getItem() instanceof SpaceFragmentation && BeyonderUtil.currentPathwayAndSequenceMatches(livingEntity, BeyonderClassInit.APPRENTICE.get(), 1)) {
                    livingEntity.getPersistentData().putInt("keyOfStarsFragmentation", fragmentationDistance + 2);
                    livingEntity.displayClientMessage(Component.literal("Spatial Authority: Fragmentation Spawn Distance is " + fragmentationDistance).withStyle(BeyonderUtil.getStyle(livingEntity)), true);
                    if (fragmentationDistance >= 151) {
                        livingEntity.displayClientMessage(Component.literal("Spatial Authority: Fragmentation Spawn Distance is 0").withStyle(BeyonderUtil.getStyle(livingEntity)), true);
                        livingEntity.getPersistentData().putInt("keyOfStarsFragmentation", 0);
                    }
                }
            }
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, create a gash in space in front of you. Entities near this gash will be stuck in space and time, unable to move or act until the gash disappears. You can walk into this to split yourself temporarily into 7 distinct entities."));
        tooltipComponents.add(Component.literal("Shift to increase spawn distance."));
        tooltipComponents.add(Component.literal("Left click for Spatial Authority: Protective Seal."));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("5000").withStyle(ChatFormatting.YELLOW)));
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
            return 65;
        }
        return 0;
    }

    @Override
    public <T> LeftClickType getleftClickEmpty(T item) {
        return new UpdateItemInHandC2S((Integer) item, new ItemStack(ItemInit.SPATIAL_SEAL.get()));
    }
}