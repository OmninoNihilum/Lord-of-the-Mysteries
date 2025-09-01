package net.swimmingtuna.lotm.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ParticleInit;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static net.swimmingtuna.lotm.util.BeyonderUtil.findSurfaceY;

public class BlackHoleEntity extends AbstractHurtingProjectile implements GeoEntity {

    // Custom ChatFormatting for ORANGE
    private static final ChatFormatting ORANGE = ChatFormatting.getByCode('6');

    private static final EntityDataAccessor<Float> RING_ROTATION_X = SynchedEntityData.defineId(BlackHoleEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> RING_ROTATION_Y = SynchedEntityData.defineId(BlackHoleEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> RING_ROTATION_Z = SynchedEntityData.defineId(BlackHoleEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> RING_ROTATION_SPEED = SynchedEntityData.defineId(BlackHoleEntity.class, EntityDataSerializers.FLOAT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public BlackHoleEntity(EntityType<? extends BlackHoleEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BlackHoleEntity(Level level, LivingEntity shooter, double offsetX, double offsetY, double offsetZ) {
        super(EntityInit.BLACK_HOLE_ENTITY.get(), shooter, offsetX, offsetY, offsetZ, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RING_ROTATION_X, 0.0f);
        this.entityData.define(RING_ROTATION_Y, 0.0f);
        this.entityData.define(RING_ROTATION_Z, 0.0f);
        this.entityData.define(RING_ROTATION_SPEED, 250.0f);
    }

    public float getRingRotationX() {
        return this.entityData.get(RING_ROTATION_X);
    }

    public float getRingRotationY() {
        return this.entityData.get(RING_ROTATION_Y);
    }

    public float getRingRotationZ() {
        return this.entityData.get(RING_ROTATION_Z);
    }

    public float getRingRotationSpeed() {
        return this.entityData.get(RING_ROTATION_SPEED);
    }

    public void setRingRotationX(float rotation) {
        this.entityData.set(RING_ROTATION_X, rotation);
    }

    public void setRingRotationY(float rotation) {
        this.entityData.set(RING_ROTATION_Y, rotation);
    }

    public void setRingRotationZ(float rotation) {
        this.entityData.set(RING_ROTATION_Z, rotation);
    }

    public void setRingRotationSpeed(float speed) {
        this.entityData.set(RING_ROTATION_SPEED, speed);
    }

    @Override
    protected float getInertia() {
        return 0.99f;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return true;
    }

    @Override
    public boolean shouldRender(double pX, double pY, double pZ) {
        return true;
    }

    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
        return new AABB(
                this.getX() - 1000,
                this.getY() - 1000,
                this.getZ() - 1000,
                this.getX() + 1000,
                this.getY() + 1000,
                this.getZ() + 1000
        );
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
    public @NotNull ParticleOptions getTrailParticle() {
        return ParticleInit.NULL_PARTICLE.get();
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        this.setGlowingTag(true);
        if (this.level() instanceof ServerLevel serverLevel) {
            Scoreboard scoreboard = serverLevel.getScoreboard();
            PlayerTeam team = scoreboard.getPlayerTeam("black_hole_glow");
            if (team == null) {
                team = scoreboard.addPlayerTeam("black_hole_glow");
                team.setColor(Objects.requireNonNullElse(ORANGE, ChatFormatting.YELLOW));
            }

            PlayerTeam currentTeam = scoreboard.getPlayersTeam(this.getStringUUID());
            if (currentTeam == null || !currentTeam.equals(team)) {
                scoreboard.addPlayerToTeam(this.getStringUUID(), team);
            }
        }

        if (!this.level().isClientSide) {
            float currentRotationY = getRingRotationY();
            float rotationSpeed = getRingRotationSpeed();
            setRingRotationY((currentRotationY + rotationSpeed) % 360.0f);
            float scale = BeyonderUtil.getScale(this);
            for (Entity entity : this.level().getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(scale * 10))) {
                if (entity == this) {
                    continue;
                }
                double distanceToBlackHole = entity.distanceTo(this);
                double deltaX = this.getX() - entity.getX();
                double deltaY = this.getY() - entity.getY();
                double deltaZ = this.getZ() - entity.getZ();
                double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                if (distance > 0) {
                    deltaX /= distance;
                    deltaY /= distance;
                    deltaZ /= distance;
                }
                if (!(entity instanceof LivingEntity)) {
                    double pullStrength = Math.min(2.0, (scale * 15.0) / Math.max(1.0, distanceToBlackHole));
                    entity.setDeltaMovement(entity.getDeltaMovement().x + deltaX * pullStrength * 0.3, entity.getDeltaMovement().y + deltaY * pullStrength * 0.3, entity.getDeltaMovement().z + deltaZ * pullStrength * 0.3);
                    if (distanceToBlackHole <= 2.0) {
                        entity.discard();
                    }


                } else if (entity instanceof LivingEntity living) {
                    if (BeyonderUtil.isImmuneToGravity(living)) {
                        continue;
                    }
                    double pullStrength = Math.min(1.5, (scale * 10.0) / Math.max(1.0, distanceToBlackHole));
                    living.setDeltaMovement(living.getDeltaMovement().x + deltaX * pullStrength * 0.2, living.getDeltaMovement().y + deltaY * pullStrength * 0.15, living.getDeltaMovement().z + deltaZ * pullStrength * 0.2);
                    if (living.distanceTo(this) <= 30) {
                        if (this.getOwner() != null) {
                            living.hurt(BeyonderUtil.genericSource(this.getOwner(), living), 45 - living.distanceTo(this));
                        }
                        BeyonderUtil.setGray(living, 60);
                    }
                    if (living.distanceTo(this) <= 10) {
                        BeyonderUtil.applyStun(living, 20);
                    }

                    living.getPersistentData().putInt("resetBlackholeScale", 10);
                    living.getPersistentData().putFloat("originalBlackholeScale", BeyonderUtil.getScale(living));
                    if (living.getPersistentData().getInt("resetBlackholeScale") >= 1) {
                        BeyonderUtil.setScale(living, BeyonderUtil.getRandomInRange(6));
                    }
                }
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<BlackHoleEntity> animationState) {
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}