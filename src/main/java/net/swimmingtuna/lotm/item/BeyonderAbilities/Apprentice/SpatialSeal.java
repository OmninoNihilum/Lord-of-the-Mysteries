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
import net.swimmingtuna.lotm.entity.KeyOfStarsProtectiveSealEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.networking.packet.UpdateItemInHandC2S;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.LeftClickHandler.LeftClickHandlerSkillP;
import net.swimmingtuna.lotm.util.LeftClickHandler.LeftClickType;
import org.jetbrains.annotations.NotNull;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpatialSeal extends LeftClickHandlerSkillP {

    public SpatialSeal(Properties properties) {
        super(properties, BeyonderClassInit.APPRENTICE, 1, 0, 2400);
    }

    @Override
    public InteractionResult useAbility(Level level, LivingEntity player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        useSpirituality(player);
        createSpatialSeal(player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof Player player && !player.isSpectator()) {
            if (player.isShiftKeyDown()) {
                int sealSize = player.getPersistentData().getInt("keyOfStarsSealScale");
                if (player.getMainHandItem().getItem() instanceof SpatialSeal && BeyonderUtil.currentPathwayAndSequenceMatches(player, BeyonderClassInit.APPRENTICE.get(), 1)) {
                    if (player.tickCount % 2 == 0) {
                        player.getPersistentData().putInt("keyOfStarsSealScale", Math.max(5, sealSize + 1));
                    }
                    player.displayClientMessage(Component.literal("Protective Seal Size is " + sealSize).withStyle(BeyonderUtil.getStyle(player)), true);
                    if (sealSize >= 26) {
                        player.displayClientMessage(Component.literal("Protective Seal Size is 5").withStyle(BeyonderUtil.getStyle(player)), true);
                        player.getPersistentData().putInt("keyOfStarsSealScale", 5);
                    }
                }
            }
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }

    public void createSpatialSeal(LivingEntity living) {
        if (!living.level().isClientSide()) {
            int amount = 0;
            for (KeyOfStarsProtectiveSealEntity sealEntity : living.level().getEntitiesOfClass(KeyOfStarsProtectiveSealEntity.class, living.getBoundingBox().inflate(50))) {
                amount++;
                if (living.isShiftKeyDown()) {
                    if (sealEntity.getOwnerUUID().isPresent()) {
                        Optional<UUID> uuid = sealEntity.getOwnerUUID();
                        UUID newUUID = uuid.get();
                        if (newUUID == living.getUUID()) {
                            sealEntity.discard();
                        }
                    }
                }
            }
            if (amount == 0) {
                int damage = (int) (float) BeyonderUtil.getDamage(living).get(ItemInit.SPATIAL_SEAL.get());
                KeyOfStarsProtectiveSealEntity spatialSealEntity = new KeyOfStarsProtectiveSealEntity(EntityInit.PROTECTIVE_SEAL_ENTITY.get(), living.level());
                ScaleData scaleData = ScaleTypes.BASE.getScaleData(spatialSealEntity);
                int x = living.getPersistentData().getInt("keyOfStarsSealScale");
                scaleData.setTargetScale(x);
                spatialSealEntity.setOwnerUUID(living.getUUID());
                spatialSealEntity.teleportTo(living.getX(), living.getY(), living.getZ());
                spatialSealEntity.setMaxHealth(damage * 150);
                living.level().addFreshEntity(spatialSealEntity);
                addCooldown(living);
            } else if (living instanceof Player player) {
                player.displayClientMessage(Component.literal("You need to be 50 blocks away from a protective seal to create one."), true);
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Use in order to create a spatial seal that only you and your allies can freely enter. Any non-ally or projectile owned by a non-ally that tries to enter will be teleported away and hurt the barrier corresponding to their strength. However, the barrier will regenerate on it's own with starlight."));
        tooltipComponents.add(Component.literal("Shift while using this ability to remove any protective seals you own that you're in or near the center of."));
        tooltipComponents.add(Component.literal("Shift while holding this item to increase/decrease max size of the seal."));
        tooltipComponents.add(Component.literal("Left click for Spatial Authority: Fragmentation."));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("50 + Amount of damage").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("2 Minutes").withStyle(ChatFormatting.YELLOW)));
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
        return 0;
    }

    @Override
    public <T> LeftClickType getleftClickEmpty(T item) {
        return new UpdateItemInHandC2S((Integer) item, new ItemStack(ItemInit.SPACE_FRAGMENTATION.get()));
    }
}