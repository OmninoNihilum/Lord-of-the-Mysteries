package net.swimmingtuna.lotm.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ParticleInit;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.networking.packet.NonVisibleS2C;
import org.jetbrains.annotations.NotNull;

public class BlackHoleEntity extends AbstractHurtingProjectile {

    // Entity data accessors for ring rotation
    private static final EntityDataAccessor<Float> RING_ROTATION_X = SynchedEntityData.defineId(BlackHoleEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> RING_ROTATION_Y = SynchedEntityData.defineId(BlackHoleEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> RING_ROTATION_Z = SynchedEntityData.defineId(BlackHoleEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> RING_ROTATION_SPEED = SynchedEntityData.defineId(BlackHoleEntity.class, EntityDataSerializers.FLOAT);

    public BlackHoleEntity(EntityType<? extends BlackHoleEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BlackHoleEntity(Level level, LivingEntity shooter, double offsetX, double offsetY, double offsetZ) {
        super(EntityInit.BLACK_HOLE_ENTITY.get(), shooter, offsetX, offsetY, offsetZ, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        // Initialize ring rotation values - starting horizontal (0 degrees on X-axis)
        this.entityData.define(RING_ROTATION_X, 0.0f);
        this.entityData.define(RING_ROTATION_Y, 0.0f);
        this.entityData.define(RING_ROTATION_Z, 0.0f);
        this.entityData.define(RING_ROTATION_SPEED, 2.0f); // Default rotation speed
    }

    // Getter methods for ring rotation
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

    // Setter methods for ring rotation
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

        // Continuously rotate the ring around the Y-axis (horizontal spinning)
        if (!this.level().isClientSide) {
            float currentRotationY = getRingRotationY();
            float rotationSpeed = getRingRotationSpeed();
            setRingRotationY((currentRotationY + rotationSpeed) % 360.0f);
        }
    }
}