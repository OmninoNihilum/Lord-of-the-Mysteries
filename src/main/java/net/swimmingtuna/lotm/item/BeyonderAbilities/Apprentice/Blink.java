package net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice;


import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.blocks.DimensionalSight.DimensionalSightTileEntity;
import net.swimmingtuna.lotm.entity.PlayerMobEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class Blink extends SimpleAbilityItem {

    public Blink(Properties properties) {
        super(properties, BeyonderClassInit.APPRENTICE, 5, 0, 5);
    }

    @Override
    public InteractionResult useAbility(Level level, LivingEntity player, InteractionHand hand) {
        int blinkDistance = player.getPersistentData().getInt("trickmasterBlinkDistance") * (int) (float) BeyonderUtil.getDamage(player).get(ItemInit.BLINK.get());
        if (!checkAll(player, BeyonderClassInit.APPRENTICE.get(), 5, blinkDistance, true)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player, this, 5);
        useSpirituality(player, blinkDistance);
        blink(player);
        return InteractionResult.SUCCESS;
    }

    public void blink(LivingEntity player) {
        if (!player.level().isClientSide()) {
            DimensionalSightTileEntity dimensionalSightTileEntity = BeyonderUtil.findNearbyDimensionalSight(player);
            if (dimensionalSightTileEntity != null && dimensionalSightTileEntity.getScryTarget() != null) {
                player.sendSystemMessage(Component.literal("You blinked to your Dimensional Sight Target").withStyle(ChatFormatting.AQUA));
                player.teleportTo(dimensionalSightTileEntity.getScryTarget().getX(), dimensionalSightTileEntity.getScryTarget().getY(), dimensionalSightTileEntity.getScryTarget().getZ());
            } else {
                int blinkDistance = player.getPersistentData().getInt("trickmasterBlinkDistance");
                if (player instanceof Mob mob && mob.getTarget() != null) {
                    blinkDistance = (int) mob.distanceTo(mob.getTarget());
                }
                Vec3 lookVector = player.getLookAngle();
                double startX = player.getX();
                double startY = player.getY();
                double startZ = player.getZ();
                double targetX = startX + blinkDistance * lookVector.x();
                double targetY = (startY + 1) + blinkDistance * lookVector.y();
                double targetZ = startZ + blinkDistance * lookVector.z();
                player.getPersistentData().putInt("secretsSorcererBlinkAfterimages", 5);
                player.getPersistentData().putDouble("blinkStartX", startX);
                player.getPersistentData().putDouble("blinkStartY", startY);
                player.getPersistentData().putDouble("blinkStartZ", startZ);
                player.getPersistentData().putDouble("blinkEndX", targetX);
                player.getPersistentData().putDouble("blinkEndY", targetY);
                player.getPersistentData().putDouble("blinkEndZ", targetZ);
                player.teleportTo(targetX, targetY, targetZ);
                double distanceTraveled = Math.sqrt(Math.pow(targetX - startX, 2) + Math.pow(targetY - startY, 2) + Math.pow(targetZ - startZ, 2));
                int afterimageCount = Math.max(1, (int) Math.floor(distanceTraveled / 8.0));
                player.getPersistentData().putInt("blinkAfterimageTimer", 0);
                player.getPersistentData().putInt("blinkAfterimageIndex", 0);
                player.getPersistentData().putInt("blinkTotalAfterimages", afterimageCount);
                BlockPos playerPos = new BlockPos((int) player.getX(), (int) player.getY(), (int) player.getZ());
                BlockPos playerPos1 = new BlockPos((int) player.getX() + 1, (int) player.getY() + 1, (int) player.getZ() + 1);
                if (BeyonderUtil.getSequence(player) <= 4) {
                    Level level = player.level();
                    for (int x = -2; x <= 2; x++) {
                        for (int y = -2; y <= 2; y++) {
                            for (int z = -2; z <= 2; z++) {
                                BlockPos targetPos = playerPos.offset(x, y, z);
                                BlockPos targetPos1 = playerPos.offset(x + 1, y + 1, z + 1);
                                BlockState blockState = level.getBlockState(targetPos);
                                if (blockState.is(Blocks.DIRT) || blockState.is(Blocks.STONE) || blockState.is(Blocks.IRON_ORE) || blockState.is(Blocks.COAL_ORE)
                                        || blockState.is(Blocks.NETHERRACK) || blockState.is(Blocks.SNOW_BLOCK) || blockState.is(Blocks.SNOW) || blockState.is(Blocks.END_STONE) ||
                                        blockState.is(Blocks.DEEPSLATE) || blockState.is(Blocks.COPPER_ORE) || blockState.is(Blocks.SOUL_SAND) || blockState.is(Blocks.SOUL_SOIL) || blockState.is(Blocks.DEEPSLATE_COPPER_ORE) || blockState.is(Blocks.DEEPSLATE_COAL_ORE)) {
                                    level.setBlockAndUpdate(targetPos, Blocks.AIR.defaultBlockState());
                                    level.destroyBlock(playerPos, false);
                                    level.destroyBlock(targetPos, false);
                                    level.destroyBlock(targetPos1, false);
                                    level.destroyBlock(playerPos1, false);
                                }
                            }
                        }
                    }
                }
            }
        }
    }



    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Blink forward in the direction you're looking"));
        tooltipComponents.add(Component.literal("Shift to increase blinking distance"));
        tooltipComponents.add(Component.literal("Left Click for Blink (Afterimage)"));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("Blink Distance").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("1/4 of a Second").withStyle(ChatFormatting.YELLOW)));
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
}
