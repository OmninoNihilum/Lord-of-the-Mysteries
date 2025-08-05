package net.swimmingtuna.lotm.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class SpaceFragmentationEntity extends Projectile implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public SpaceFragmentationEntity(EntityType<? extends SpaceFragmentationEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void onHit(HitResult pResult) {
        return;
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        return;
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        return;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public void setRemainingFireTicks(int pRemainingFireTicks) {
        return;
    }

    @Override
    public void tick(){
        super.tick();
        if(!this.level().isClientSide){
            /*
            this.setGlowingTag(true);

            if (this.level() instanceof ServerLevel serverLevel) {
                Scoreboard scoreboard = serverLevel.getScoreboard();
                PlayerTeam team = scoreboard.addPlayerTeam("space_fragmentation_glow");
                team.setColor(ChatFormatting.BLACK);
                scoreboard.addPlayerToTeam(this.getStringUUID(), team);
            }

             */
            if (this.tickCount >= 210) {
                this.discard();
            }
            int coloredBoxes = 0;
            if (this.getOwner() != null) {
                for (ColoredBoxEntity coloredBoxEntity : this.level().getEntitiesOfClass(ColoredBoxEntity.class, this.getBoundingBox().inflate(20))) {
                    if (coloredBoxEntity.getPersistentData().contains("createdBySpaceFragmentation")) {
                        UUID uuid = coloredBoxEntity.getPersistentData().getUUID("createdBySpaceFragmentation");
                        if (uuid.equals(this.getUUID())) {
                            coloredBoxes++;
                            coloredBoxEntity.teleportTo(this.getX(), this.getY() + 5, this.getZ());
                            if (this.tickCount == 30) {
                                coloredBoxEntity.setColorMode(ColoredBoxEntity.ColorMode.BLUE);
                            } else if (this.tickCount == 60) {
                                coloredBoxEntity.setColorMode(ColoredBoxEntity.ColorMode.PURPLE);
                            } else if (this.tickCount == 90) {
                                coloredBoxEntity.setColorMode(ColoredBoxEntity.ColorMode.YELLOW);
                            } else if (this.tickCount == 120) {
                                coloredBoxEntity.setColorMode(ColoredBoxEntity.ColorMode.GRAY);
                            } else if (this.tickCount == 150) {
                                coloredBoxEntity.setColorMode(ColoredBoxEntity.ColorMode.BLUE);
                            } else if (this.tickCount == 180) {
                                coloredBoxEntity.setColorMode(ColoredBoxEntity.ColorMode.GREEN);
                            } else if (this.tickCount >= 209) {
                                coloredBoxEntity.discard();
                            }
                        }
                    }
                }
            }
            if (this.getOwner() != null && this.getOwner() instanceof LivingEntity owner) {
                if (coloredBoxes == 0) {
                    int damage = (int) (float) BeyonderUtil.getDamage(owner).get(ItemInit.SPACE_FRAGMENTATION.get());
                    ColoredBoxEntity coloredBoxEntity = new ColoredBoxEntity(EntityInit.COLORED_BOX_ENTITY.get(), this.level());
                    coloredBoxEntity.teleportTo(this.getX(), this.getY() + 5, this.getZ());
                    coloredBoxEntity.setMaxHealth(209);
                    BeyonderUtil.setScale(coloredBoxEntity, (float) damage / 10);
                    coloredBoxEntity.getPersistentData().putUUID("createdBySpaceFragmentation", this.getUUID());
                    this.level().addFreshEntity(coloredBoxEntity);
                }
                for (Entity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(BeyonderUtil.getDamage(owner).get(ItemInit.SPACE_FRAGMENTATION.get())))) {
                    if (entity instanceof LivingEntity livingTarget && livingTarget != owner) {
                        livingTarget.getPersistentData().putInt("cancelTick", 10);
                        BeyonderUtil.applyStun(livingTarget, 10);
                        BeyonderUtil.setGray(livingTarget, 10);
                        livingTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 5, false, false));
                    } else if (!(entity instanceof LivingEntity) && !(entity instanceof ColoredBoxEntity)) {
                        if (entity.distanceTo(this) <= 10) {
                            entity.discard();
                        }
                    }
                    if (entity instanceof LivingEntity living && living == owner && living.distanceTo(this) <= 5 && living.getPersistentData().getInt("spaceFragmentationCopies") == 0) {
                        living.getPersistentData().putInt("spaceFragmentationCopies", 200);

                        // Create all the colored copies
                        PlayerMobEntity playerMobEntityBlack = PlayerMobEntity.playerCopy(living);
                        playerMobEntityBlack.setColorMode(PlayerMobEntity.ColorMode.BLACK);

                        PlayerMobEntity playerMobEntityGray = PlayerMobEntity.playerCopy(living);
                        playerMobEntityGray.setColorMode(PlayerMobEntity.ColorMode.GRAY);

                        PlayerMobEntity playerMobEntityRed = PlayerMobEntity.playerCopy(living);
                        playerMobEntityRed.setColorMode(PlayerMobEntity.ColorMode.RED);

                        PlayerMobEntity playerMobEntityBlue = PlayerMobEntity.playerCopy(living);
                        playerMobEntityBlue.setColorMode(PlayerMobEntity.ColorMode.BLUE);

                        PlayerMobEntity playerMobEntityYellow = PlayerMobEntity.playerCopy(living);
                        playerMobEntityYellow.setColorMode(PlayerMobEntity.ColorMode.YELLOW);

                        PlayerMobEntity playerMobEntityPurple = PlayerMobEntity.playerCopy(living);
                        playerMobEntityPurple.setColorMode(PlayerMobEntity.ColorMode.PURPLE);

                        PlayerMobEntity playerMobEntityGreen = PlayerMobEntity.playerCopy(living);
                        playerMobEntityGreen.setColorMode(PlayerMobEntity.ColorMode.GREEN);

                        PlayerMobEntity[] coloredCopies = {
                                playerMobEntityBlack,
                                playerMobEntityGray,
                                playerMobEntityRed,
                                playerMobEntityBlue,
                                playerMobEntityYellow,
                                playerMobEntityPurple,
                                playerMobEntityGreen
                        };
                        double radius = 20.0;
                        double playerX = living.getX();
                        double playerY = living.getY();
                        double playerZ = living.getZ();
                        int totalCopies = coloredCopies.length;
                        for (int i = 0; i < totalCopies; i++) {
                            double angle = (2 * Math.PI * i) / totalCopies;
                            double offsetX = Math.cos(angle) * radius;
                            double offsetZ = Math.sin(angle) * radius;
                            double newX = playerX + offsetX;
                            double newZ = playerZ + offsetZ;
                            coloredCopies[i].teleportTo(newX, playerY, newZ);
                            owner.level().addFreshEntity(coloredCopies[i]);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<SpaceFragmentationEntity> animationState) {
        AnimationController<SpaceFragmentationEntity> controller = animationState.getController();

        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
