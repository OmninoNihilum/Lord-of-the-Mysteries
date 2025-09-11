package net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice;


import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.blocks.DimensionalSight.DimensionalSightTileEntity;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.EFunctions;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.EventManager;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.init.ParticleInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class GravityManipulation extends SimpleAbilityItem {

    public GravityManipulation(Properties properties) {
        super(properties, BeyonderClassInit.APPRENTICE, 1, 2000, 1000);
    }
    @Override
    public InteractionResult useAbility(Level level, LivingEntity player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        gravityManipulationTickEvent(player);
        useSpirituality(player);
        return InteractionResult.SUCCESS;
    }

    private void gravityManipulationTickEvent(LivingEntity player) {
        if (!player.level().isClientSide()) {
            DimensionalSightTileEntity dimensionalSightTileEntity = BeyonderUtil.findNearbyDimensionalSight(player);
            if (dimensionalSightTileEntity != null && dimensionalSightTileEntity.getScryTarget() != null) {
                dimensionalSightTileEntity.getScryTarget().getPersistentData().putInt("affectedByGravityManipulation", 500);
                EventManager.addToRegularLoop(dimensionalSightTileEntity.getScryTarget(), EFunctions.GRAVITY_MANIPULATION.get());
            } else {
                player.getPersistentData().putInt("keyOfStarsGravityManipulation", (int) (float) BeyonderUtil.getDamage(player).get(ItemInit.GRAVITY_MANIPULATION.get()));
            }
            EventManager.addToRegularLoop(player, EFunctions.GRAVITY_MANIPULATION.get());
        }
    }

    public static void gravityManipulationTickEvent(LivingEvent.LivingTickEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            LivingEntity livingEntity = event.getEntity();
            CompoundTag tag = livingEntity.getPersistentData();
            int gravityCounter = tag.getInt("keyOfStarsGravityManipulation");
            int affectedByGravityCounter = tag.getInt("affectedByGravityManipulation");
            if (gravityCounter >= 1) {
                Random random = new Random();
                int particleCount = 10;
                for (int i = 0; i < particleCount; i++) {
                    double angle = random.nextDouble() * 2 * Math.PI;
                    double radius = random.nextDouble() * 20.0;
                    double height = random.nextDouble() * 10.0 + 5.0;
                    double offsetX = Math.cos(angle) * radius;
                    double offsetZ = Math.sin(angle) * radius;
                    double spawnX = livingEntity.getX() + offsetX;
                    double spawnY = livingEntity.getY() + height;
                    double spawnZ = livingEntity.getZ() + offsetZ;
                    double velocityY = -1.0 - random.nextDouble() * 2.0;
                    double velocityX = (random.nextDouble() - 0.5) * 0.2;
                    double velocityZ = (random.nextDouble() - 0.5) * 0.2;
                    BeyonderUtil.sendParticles(livingEntity, ParticleInit.GRAVITY.get(), spawnX, spawnY, spawnZ, velocityX, velocityY, velocityZ);
                }
                tag.putInt("keyOfStarsGravityManipulation", gravityCounter - 1);
                if (gravityCounter % 5 == 0) {
                    for (LivingEntity entity : BeyonderUtil.getNonAlliesNearby(livingEntity, 100)) {
                        if (entity == livingEntity) {
                            continue;
                        }
                        if (BeyonderUtil.isImmuneToGravity(entity)) {
                            continue;
                        }
                        entity.getPersistentData().putInt("affectedByGravityManipulation", 20);
                    }
                    for (Projectile projectile : livingEntity.level().getEntitiesOfClass(Projectile.class, livingEntity.getBoundingBox().inflate(100))) {
                        if (projectile.getPersistentData().getInt("affectedByGravityManipulation") >= 1) {
                            projectile.getPersistentData().putInt("affectedByGravityManipulation", projectile.getPersistentData().getInt("affectedByGravityManipulation") - 1);
                            projectile.push(0, -10, 0);
                            Vec3 motion = projectile.getDeltaMovement();
                            if (projectile.tickCount % 10 == 0) {
                                projectile.setDeltaMovement(motion.x / 2, -2, motion.z / 2);
                                projectile.hurtMarked = true;
                            }
                        }
                    }
                }
            }
            if (affectedByGravityCounter >= 1) {
                tag.putInt("affectedByGravityManipulation", affectedByGravityCounter - 1);
                if (!BeyonderUtil.currentPathwayAndSequenceMatchesNoException(livingEntity, BeyonderClassInit.APPRENTICE.get(), 1)) {
                    livingEntity.push(0, -10, 0);
                    Vec3 motion = livingEntity.getDeltaMovement();
                    if (livingEntity.tickCount % 2 == 0) {
                        livingEntity.setDeltaMovement(motion.x, -2, motion.z);
                        livingEntity.hurtMarked = true;
                    } else {
                        BlockPos entityPos = livingEntity.blockPosition();
                        BlockPos oneBlockBelow = entityPos.below();
                        BlockPos twoBlocksBelow = entityPos.below(2);
                        boolean hasGroundBelow = !livingEntity.level().getBlockState(oneBlockBelow).isAir() || !livingEntity.level().getBlockState(twoBlocksBelow).isAir();
                        if (hasGroundBelow && motion.y < -1.0) {
                            livingEntity.hurt(livingEntity.damageSources().generic(), 10.0f);
                            livingEntity.invulnerableTime = 2;
                            livingEntity.hurtTime = 2;
                            livingEntity.hurtDuration = 2;
                        }
                    }
                }
                Random random = new Random();
                int particleCount = 10;
                for (int i = 0; i < particleCount; i++) {
                    double angle = random.nextDouble() * 2 * Math.PI;
                    double radius = random.nextDouble() * 20.0;
                    double height = random.nextDouble() * 10.0 + 5.0;
                    double offsetX = Math.cos(angle) * radius;
                    double offsetZ = Math.sin(angle) * radius;
                    double spawnX = livingEntity.getX() + offsetX;
                    double spawnY = livingEntity.getY() + height;
                    double spawnZ = livingEntity.getZ() + offsetZ;
                    double velocityY = -1.0 - random.nextDouble() * 2.0;
                    double velocityX = (random.nextDouble() - 0.5) * 0.2;
                    double velocityZ = (random.nextDouble() - 0.5) * 0.2;
                    BeyonderUtil.sendParticles(livingEntity, ParticleInit.GRAVITY.get(), spawnX, spawnY, spawnZ, velocityX, velocityY, velocityZ);
                }
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, use gravity to push all entities nearby into the ground for a while, in this state, they won't be able to go up and if they're on the ground, they'll be pressed into it and take damage."));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("2000").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("50 Seconds").withStyle(ChatFormatting.YELLOW)));
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
        if (target != null && !BeyonderUtil.isImmuneToGravity(target)) {
            return 100 - (BeyonderUtil.getSequence(target) * 10);
        }
        return 0;
    }
}
