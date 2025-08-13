package net.swimmingtuna.lotm.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.init.ParticleInit;
import net.swimmingtuna.lotm.util.BeyonderUtil;

public class StarfallEntity extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Integer> CYCLE_FREQUENCY = SynchedEntityData.defineId(StarfallEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MAX_LIFE = SynchedEntityData.defineId(StarfallEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> COLOR_MODE = SynchedEntityData.defineId(StarfallEntity.class, EntityDataSerializers.STRING);

    public StarfallEntity(EntityType<? extends AbstractHurtingProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected float getInertia() {
        return 1.0F;
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
        BeyonderUtil.destroyBlocksInSphere(this, this.getOnPos(), BeyonderUtil.getScale(this) * 4.0f, BeyonderUtil.getScale(this) * 6.0f);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(CYCLE_FREQUENCY, 0);
        this.entityData.define(MAX_LIFE, 100);
        this.entityData.define(COLOR_MODE, ColorMode.WHITE.name());
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return true;
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

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            this.getPersistentData().putInt("matterAccelerationEntitiesTimer", 200);
            if (this.getCycleFrequency() != 0 && this.tickCount % this.getCycleFrequency() == 0) {
                this.cycleColorMode();
            }
            BeyonderUtil.sendAlwaysVisibleParticle(ParticleInit.FLASH_PARTICLE.get(), this.getX(), this.getY(), this.getZ());
            BeyonderUtil.updateLocationClientSide(this);
        }
        for (Entity living : this.level().getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(BeyonderUtil.getScale(this) * 5))) {
            if (this.getOwner() != null && this.getOwner() instanceof LivingEntity owner) {
                if (!BeyonderUtil.isEntityAlly(owner, living)) {
                    BeyonderUtil.destroyBlocksInSphere(this, this.getOnPos(), BeyonderUtil.getScale(this) * 4.0f, BeyonderUtil.getScale(this) * 6.0f);
                    this.discard();
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
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putString("colorMode", this.getColorMode());
        compound.putInt("cycleFrequency", this.getCycleFrequency());
        compound.putInt("maxLife", this.getMaxLife());
    }
}