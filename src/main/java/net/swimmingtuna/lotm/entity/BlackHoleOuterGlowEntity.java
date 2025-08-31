package net.swimmingtuna.lotm.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ParticleInit;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Objects;
import java.util.UUID;

public class BlackHoleOuterGlowEntity extends AbstractHurtingProjectile implements GeoEntity {


    private static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(BlackHoleOuterGlowEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> RING_ROTATION_X = SynchedEntityData.defineId(BlackHoleOuterGlowEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> RING_ROTATION_Y = SynchedEntityData.defineId(BlackHoleOuterGlowEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> RING_ROTATION_Z = SynchedEntityData.defineId(BlackHoleOuterGlowEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> RING_ROTATION_SPEED = SynchedEntityData.defineId(BlackHoleOuterGlowEntity.class, EntityDataSerializers.FLOAT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public BlackHoleOuterGlowEntity(EntityType<? extends BlackHoleOuterGlowEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BlackHoleOuterGlowEntity(Level level, LivingEntity shooter, double offsetX, double offsetY, double offsetZ) {
        super(EntityInit.BLACK_HOLE_GLOW_ENTITY.get(), shooter, offsetX, offsetY, offsetZ, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SIZE, 0.0f);
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
        if (!this.level().isClientSide) {
            float currentRotationY = getRingRotationY();
            float rotationSpeed = getRingRotationSpeed();
            setRingRotationY((currentRotationY + rotationSpeed) % 360.0f);
            int blackHoleFound = 0;
            boolean uuidMatches = false;
            for (BlackHoleEntity blackHoleEntity : this.level().getEntitiesOfClass(BlackHoleEntity.class, this.getBoundingBox().inflate(40))) {
                blackHoleFound++;
                if (this.getPersistentData().contains("blackHoleGlowOwner")) {
                    UUID uuid = this.getPersistentData().getUUID("blackHoleGlowOwner");
                    if (uuid == blackHoleEntity.getUUID()) {
                        uuidMatches = true;
                    }
                }
            }
            if (this.tickCount >= 20) {
                if (blackHoleFound == 0) {
                    //this.discard();
                } else if (!uuidMatches) {
                    //this.discard();
                }
            }

        }
    }



    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<BlackHoleOuterGlowEntity> animationState) {
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}