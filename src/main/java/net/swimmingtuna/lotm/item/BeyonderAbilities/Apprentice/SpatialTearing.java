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
import net.swimmingtuna.lotm.entity.SpaceRiftEntity;
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

public class SpatialTearing extends LeftClickHandlerSkillP {

    public SpatialTearing(Properties properties) {
        super(properties, BeyonderClassInit.APPRENTICE, 3, 2500, 900);
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


    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof Player livingEntity && !livingEntity.isSpectator()) {
            if (livingEntity.isShiftKeyDown()) {
                int tearingDistance = livingEntity.getPersistentData().getInt("wandererTearing");
                if (livingEntity.getMainHandItem().getItem() instanceof SpatialTearing && BeyonderUtil.currentPathwayAndSequenceMatches(livingEntity, BeyonderClassInit.APPRENTICE.get(), 3)) {
                    livingEntity.getPersistentData().putInt("wandererTearing", tearingDistance + 2);
                    livingEntity.displayClientMessage(Component.literal("Spatial Authority: Tear Spawn Distance is " + tearingDistance).withStyle(BeyonderUtil.getStyle(livingEntity)), true);
                    if (tearingDistance >= 101) {
                        livingEntity.displayClientMessage(Component.literal("Spatial Authority: Tear Spawn Distance is 0").withStyle(BeyonderUtil.getStyle(livingEntity)), true);
                        livingEntity.getPersistentData().putInt("wandererTearing", 0);
                    }
                }
            }
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }

    public static void tearSpace(LivingEntity livingEntity){
        Level level = livingEntity.level();
        if(level.isClientSide) return;
        DimensionalSightTileEntity dimensionalSightTileEntity = BeyonderUtil.findNearbyDimensionalSight(livingEntity);
        if (dimensionalSightTileEntity != null && dimensionalSightTileEntity.getScryTarget() != null) {
            livingEntity.sendSystemMessage(Component.literal("You tore the space around your Dimensional Sight Target").withStyle(ChatFormatting.AQUA));
            SpaceRiftEntity rift = new SpaceRiftEntity(EntityInit.SPACE_RIFT_ENTITY.get(), livingEntity.level());
            rift.setOwner(livingEntity);
            LivingEntity scryEntity = dimensionalSightTileEntity.getScryTarget();
            rift.teleportTo(scryEntity.getX(), scryEntity.getY(), scryEntity.getZ());
            BeyonderUtil.setScale(rift, 6 - BeyonderUtil.getSequence(livingEntity));
            rift.setMaxLife((int) (float) BeyonderUtil.getDamage(livingEntity).get(ItemInit.SPATIAL_TEARING.get()));
            level.addFreshEntity(rift);
        } else {
            int x = livingEntity.getPersistentData().getInt("wandererTearing");
            SpaceRiftEntity rift = new SpaceRiftEntity(EntityInit.SPACE_RIFT_ENTITY.get(), livingEntity.level());
            rift.setOwner(livingEntity);
            Vec3 scale = livingEntity.getLookAngle().scale(x);
            rift.teleportTo(livingEntity.getX() + scale.x(), livingEntity.getY() + scale.y(), livingEntity.getZ() + scale.z());
            BeyonderUtil.setScale(rift, 6 - BeyonderUtil.getSequence(livingEntity));
            rift.setMaxLife((int) (float) BeyonderUtil.getDamage(livingEntity).get(ItemInit.SPATIAL_TEARING.get()));
            level.addFreshEntity(rift);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, create a tear in space 20 blocks in front of you, sucking in all entities and dealing massive damage to anything caught"));
        tooltipComponents.add(Component.literal("Shift to increase spawn distance."));
        tooltipComponents.add(Component.literal("Left click for Spatial Authority: Cage."));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("2500").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("45 Seconds").withStyle(ChatFormatting.YELLOW)));
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
        return new UpdateItemInHandC2S((Integer) item, new ItemStack(ItemInit.SPATIAL_CAGE.get()));
    }
}