package net.swimmingtuna.lotm.entity;


import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.phys.*;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ParticleInit;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.networking.packet.ClientShouldntRenderS2C;
import net.swimmingtuna.lotm.networking.packet.NonVisibleS2C;
import org.jetbrains.annotations.NotNull;

public class BlackSphereEntity extends AbstractHurtingProjectile {

    private static final EntityDataAccessor<Integer> TARGETX = SynchedEntityData.defineId(BlackSphereEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TARGETY = SynchedEntityData.defineId(BlackSphereEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TARGETZ = SynchedEntityData.defineId(BlackSphereEntity.class, EntityDataSerializers.INT);

    public BlackSphereEntity(EntityType<? extends BlackSphereEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BlackSphereEntity(Level level, LivingEntity shooter, double offsetX, double offsetY, double offsetZ) {
        super(EntityInit.BLACK_SPHERE_ENTITY.get(), shooter, offsetX, offsetY, offsetZ, level);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(TARGETX, 0);
        this.entityData.define(TARGETY, 0);
        this.entityData.define(TARGETZ, 0);
    }

    public int getTargetX() {
        return this.entityData.get(TARGETX);
    }

    public int getTargetY() {
        return this.entityData.get(TARGETY);
    }

    public int getTargetZ() {
        return this.entityData.get(TARGETZ);
    }

    public void setTargetX(int x) {
        this.entityData.set(TARGETX, x);
    }

    public void setTargetY(int y) {
        this.entityData.set(TARGETY, y);
    }

    public void setTargetZ(int z) {
        this.entityData.set(TARGETZ, z);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("targetX", this.entityData.get(TARGETX));
        tag.putInt("targetY", this.entityData.get(TARGETY));
        tag.putInt("targetZ", this.entityData.get(TARGETZ));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("targetX")) {
            this.entityData.set(TARGETX, tag.getInt("targetX"));
        }
        if (tag.contains("targetY")) {
            this.entityData.set(TARGETY, tag.getInt("targetY"));
        }
        if (tag.contains("targetZ")) {
            this.entityData.set(TARGETZ, tag.getInt("targetZ"));
        }
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
    public boolean shouldRenderAtSqrDistance(double pDistance) { return true; }

    @Override
    public boolean shouldRender(double pX, double pY, double pZ) { return true; }


    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public @NotNull ParticleOptions getTrailParticle() {
        return ParticleInit.NULL_PARTICLE.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        return;
    }

    @Override
    protected void onHit(HitResult pResult) {
        return;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {

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
        if (this.tickCount == 80) {
            shootDragonBreathAtTarget(30, 150, 4);
        }
        if (this.tickCount >= 260) {
            this.getPersistentData().putInt("ignoreShouldntRender", 0);
            this.discard();
        } else {
            LOTMNetworkHandler.sendToAllPlayers(new ClientShouldntRenderS2C(this.uuid, 400));
            this.getPersistentData().putInt("ignoreShouldntRender", 10);
        }
    }

    private void shootDragonBreathAtTarget(float damage, float range, float size) {
        if (this.getOwner() instanceof LivingEntity owner) {
            Vec3 targetPos = new Vec3(getTargetX(), getTargetY(), getTargetZ());
            Vec3 currentPos = this.position();
            Vec3 direction = targetPos.subtract(currentPos).normalize();
            DragonBreathEntity dragonBreath = new DragonBreathEntity(EntityInit.DRAGON_BREATH_ENTITY.get(), owner.level());
            dragonBreath.setDamage((damage));
            dragonBreath.endPos = targetPos;
            dragonBreath.setOwner(owner);
            dragonBreath.setRange((int) range);
            dragonBreath.setSize((int) size);
            dragonBreath.setGammaRay(true);
            dragonBreath.setIsDragonbreath(false);
            dragonBreath.setDestroyBlocks(false);
            dragonBreath.setDuration(50);
            dragonBreath.setCharge(20);
            dragonBreath.teleportTo(this.getX(), this.getY(), this.getZ());
            double deltaX = targetPos.x - currentPos.x;
            double deltaY = targetPos.y - currentPos.y;
            double deltaZ = targetPos.z - currentPos.z;
            double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
            float yaw = (float) Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0f;
            float pitch = (float) -Math.toDegrees(Math.atan2(deltaY, horizontalDistance));
            dragonBreath.setYaw((float) Math.toRadians(yaw));
            dragonBreath.setPitch((float) Math.toRadians(pitch));
            this.level().addFreshEntity(dragonBreath);
        }
    }
}
