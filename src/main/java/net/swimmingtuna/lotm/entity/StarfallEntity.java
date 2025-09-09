package net.swimmingtuna.lotm.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.init.ParticleInit;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.networking.packet.ClientShouldntRenderS2C;
import net.swimmingtuna.lotm.util.BeyonderUtil;

import java.util.ArrayList;
import java.util.List;

public class StarfallEntity extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Integer> CYCLE_FREQUENCY = SynchedEntityData.defineId(StarfallEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MAX_LIFE = SynchedEntityData.defineId(StarfallEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ROTATE_YAW = SynchedEntityData.defineId(StarfallEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ROTATE_PITCH = SynchedEntityData.defineId(StarfallEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> COLOR_MODE = SynchedEntityData.defineId(StarfallEntity.class, EntityDataSerializers.STRING);

    private final List<TrailPoint> trailPoints = new ArrayList<>();
    private static final int MAX_TRAIL_LENGTH = 20;
    private Vec3 lastPosition = null;

    private float currentYaw = 0.0f;
    private float currentPitch = 0.0f;

    public StarfallEntity(EntityType<? extends AbstractHurtingProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected float getInertia() {
        return 1.0F;
    }

    public static class TrailPoint {
        public final Vec3 position;
        public int age;
        public final int maxAge;

        public TrailPoint(Vec3 position, int maxAge) {
            this.position = position;
            this.age = 0;
            this.maxAge = maxAge;
        }

        public float getAlpha() {
            return 1.0f - ((float) age / maxAge);
        }

        public boolean isExpired() {
            return age >= maxAge;
        }

        public void tick() {
            age++;
        }
    }

    public enum ColorMode {
        WHITE(1.0f, 1.0f, 1.0f),
        AQUA(0.2f, 1.0f, 1.0f),
        GRAY(0.5f, 0.5f, 0.5f),
        BLACK(0.1f, 0.1f, 0.1f),
        RED(1.0f, 0.2f, 0.2f),
        BLUE(0.2f, 0.4f, 1.0f),
        YELLOW(1.0f, 1.0f, 0.2f),
        PURPLE(0.8f, 0.2f, 1.0f),
        GREEN(0.2f, 1.0f, 0.2f);

        public final float r;
        public final float g;
        public final float b;

        ColorMode(float r, float g, float b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        removeTrail();
        if (BeyonderUtil.getScale(this) < 7) {
            if (Math.random() > 0.5) {
                BeyonderUtil.destroyBlocksInSphereNotHittingOwner(this, this.getOnPos(), BeyonderUtil.getScale(this) * 4.0f, BeyonderUtil.getScale(this) * 6.0f);
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(CYCLE_FREQUENCY, 0);
        this.entityData.define(ROTATE_PITCH, 0);
        this.entityData.define(ROTATE_YAW, 0);
        this.entityData.define(MAX_LIFE, 100);
        this.entityData.define(COLOR_MODE, ColorMode.WHITE.name());
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return true;
    }

    public List<TrailPoint> getTrailPoints() {
        return trailPoints;
    }

    private void updateTrail() {
        Vec3 currentPos = this.position();
        if (lastPosition == null || lastPosition.distanceTo(currentPos) > 0.1) {
            trailPoints.add(0, new TrailPoint(currentPos, 40));
            lastPosition = currentPos;
        }
        trailPoints.removeIf(point -> {
            point.tick();
            return point.isExpired();
        });
        while (trailPoints.size() > MAX_TRAIL_LENGTH) {
            trailPoints.remove(trailPoints.size() - 1);
        }
    }

    public void removeTrail() {
        trailPoints.clear();
        lastPosition = null;
    }

    public int getCycleFrequency() {
        return this.entityData.get(CYCLE_FREQUENCY);
    }

    public void setCycleFrequency(int cycleFrequency) {
        this.entityData.set(CYCLE_FREQUENCY, cycleFrequency);
    }

    public String getColorMode() {
        return this.entityData.get(COLOR_MODE);
    }

    public void setColorMode(ColorMode colorMode) {
        this.entityData.set(COLOR_MODE, colorMode.name());
    }

    public void setRandomColor() {
        ColorMode[] modes = ColorMode.values();
        int randomIndex = (int) (Math.random() * modes.length);
        setColorMode(modes[randomIndex]);
    }

    public void cycleColorMode() {
        ColorMode[] modes = ColorMode.values();
        ColorMode currentMode;
        try {
            currentMode = ColorMode.valueOf(getColorMode());
        } catch (IllegalArgumentException e) {
            currentMode = ColorMode.YELLOW;
        }

        int currentIndex = currentMode.ordinal();
        int nextIndex = (currentIndex + 1) % modes.length;
        setColorMode(modes[nextIndex]);
    }

    public void setColorMode(String colorMode) {
        try {
            ColorMode mode = ColorMode.valueOf(colorMode.toUpperCase());
            this.entityData.set(COLOR_MODE, mode.name());
        } catch (IllegalArgumentException e) {
            this.entityData.set(COLOR_MODE, ColorMode.WHITE.name());
        }
    }

    public ColorMode getColorModeEnum() {
        try {
            return ColorMode.valueOf(getColorMode());
        } catch (IllegalArgumentException e) {
            return ColorMode.WHITE;
        }
    }

    public int getMaxLife() {
        return this.entityData.get(MAX_LIFE);
    }

    public void setMaxLife(int maxLife) {
        this.entityData.set(MAX_LIFE, maxLife);
    }

    public int getRotateYaw() {
        return this.entityData.get(ROTATE_YAW);
    }

    public void setRotateYaw(int rotateYaw) {
        this.entityData.set(ROTATE_YAW, rotateYaw);
    }

    public int getRotatePitch() {
        return this.entityData.get(ROTATE_PITCH);
    }

    public void setRotatePitch(int rotatePitch) {
        this.entityData.set(ROTATE_PITCH, rotatePitch);
    }

    public float getCurrentYaw() {
        return currentYaw;
    }

    public float getCurrentPitch() {
        return currentPitch;
    }

    @Override
    public void tick() {
        super.tick();
        updateTrail();

        currentYaw += getRotateYaw();
        currentPitch += getRotatePitch();

        if (!this.level().isClientSide()) {
            this.getPersistentData().putInt("ignoreShouldntRender", 4);
            LOTMNetworkHandler.sendToAllPlayers(new ClientShouldntRenderS2C(this.uuid, 3));
            this.getPersistentData().putInt("matterAccelerationEntitiesTimer", 200);
            if (this.getCycleFrequency() != 0 && this.tickCount % this.getCycleFrequency() == 0) {
                this.cycleColorMode();
            }
            BeyonderUtil.sendAlwaysVisibleParticle(ParticleInit.FLASH_PARTICLE.get(), this.getX(), this.getY(), this.getZ());
            BeyonderUtil.updateLocationClientSide(this);
        }
        if (this.tickCount >= this.getMaxLife()) {
            this.discard();
        }
        if (this.getOwner() != null) {
            for (Entity living : this.level().getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(BeyonderUtil.getScale(this) * 4))) {
                if (this.getOwner() != null && this.getOwner() instanceof LivingEntity owner && living instanceof LivingEntity livingEntity) {
                    if (!BeyonderUtil.isEntityAlly(owner, living) && living != this.getOwner()) {
                        removeTrail();
                        livingEntity.invulnerableTime = 0;
                        livingEntity.hurtTime = 0;
                        livingEntity.hurtDuration = 0;
                        BeyonderUtil.destroyBlocksInSphereNotHittingOwner(this, this.getOnPos(), BeyonderUtil.getScale(this) * 4.0f, BeyonderUtil.getScale(this) * 30.0f);
                        this.discard();
                    }
                }
            }
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("cycleFrequency")) {
            this.setCycleFrequency(compound.getInt("cycleFrequency"));
        }
        if (compound.contains("colorMode")) {
            this.setColorMode(compound.getString("colorMode"));
        }
        if (compound.contains("maxLife")) {
            this.setMaxLife(compound.getInt("maxLife"));
        }
        if (compound.contains("rotateYaw")) {
            this.setRotateYaw(compound.getInt("rotateYaw"));
        }
        if (compound.contains("rotatePitch")) {
            this.setRotatePitch(compound.getInt("rotatePitch"));
        }
        if (compound.contains("currentYaw")) {
            this.currentYaw = compound.getFloat("currentYaw");
        }
        if (compound.contains("currentPitch")) {
            this.currentPitch = compound.getFloat("currentPitch");
        }
        if (compound.contains("trailPoints")) {
            ListTag trailList = compound.getList("trailPoints", Tag.TAG_COMPOUND);
            trailPoints.clear();
            for (int i = 0; i < trailList.size(); i++) {
                CompoundTag pointTag = trailList.getCompound(i);
                Vec3 pos = new Vec3(
                        pointTag.getDouble("x"),
                        pointTag.getDouble("y"),
                        pointTag.getDouble("z")
                );
                TrailPoint point = new TrailPoint(pos, pointTag.getInt("maxAge"));
                point.age = pointTag.getInt("age");
                trailPoints.add(point);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putString("colorMode", this.getColorMode());
        compound.putInt("cycleFrequency", this.getCycleFrequency());
        compound.putInt("maxLife", this.getMaxLife());
        compound.putInt("rotateYaw", this.getRotateYaw());
        compound.putInt("rotatePitch", this.getRotatePitch());
        compound.putFloat("currentYaw", this.currentYaw);
        compound.putFloat("currentPitch", this.currentPitch);

        ListTag trailList = new ListTag();
        for (TrailPoint point : trailPoints) {
            CompoundTag pointTag = new CompoundTag();
            pointTag.putDouble("x", point.position.x);
            pointTag.putDouble("y", point.position.y);
            pointTag.putDouble("z", point.position.z);
            pointTag.putInt("age", point.age);
            pointTag.putInt("maxAge", point.maxAge);
            trailList.add(pointTag);
        }
        compound.put("trailPoints", trailList);
    }
}