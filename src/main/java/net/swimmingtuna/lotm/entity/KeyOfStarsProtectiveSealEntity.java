package net.swimmingtuna.lotm.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import org.jetbrains.annotations.NotNull;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class KeyOfStarsProtectiveSealEntity extends Entity {
    private static final EntityDataAccessor<Integer> MAX_HEALTH = SynchedEntityData.defineId(KeyOfStarsProtectiveSealEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> REGENERATION = SynchedEntityData.defineId(KeyOfStarsProtectiveSealEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DAMAGE = SynchedEntityData.defineId(KeyOfStarsProtectiveSealEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MAX_SIZE = SynchedEntityData.defineId(KeyOfStarsProtectiveSealEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(KeyOfStarsProtectiveSealEntity.class, EntityDataSerializers.OPTIONAL_UUID);


    public KeyOfStarsProtectiveSealEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.noCulling = true;
    }

    public KeyOfStarsProtectiveSealEntity(Level level, double x, double y, double z, float maxRadius) {
        this(EntityInit.GUARDIAN_BOX_ENTITY.get(), level);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(MAX_HEALTH, 100);
        this.entityData.define(DAMAGE, 0);
        this.entityData.define(MAX_SIZE, 10);
        this.entityData.define(OWNER_UUID, Optional.empty());
        this.entityData.define(REGENERATION, 0);
    }


    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return true;
    }

    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
        return new AABB(this.getX() - 600, this.getY() - 600, this.getZ() - 600, this.getX() + 600, this.getY() + 600, this.getZ() + 600);
    }

    public int getMaxHealth() {
        return this.entityData.get(MAX_HEALTH);
    }

    public void setMaxHealth(int maxHealth) {
        this.entityData.set(MAX_HEALTH, maxHealth);
    }

    public int getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public void setDamage(int damage) {
        this.entityData.set(DAMAGE, damage);
    }

    public Optional<UUID> getOwnerUUID() {
        return this.entityData.get(OWNER_UUID);
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.entityData.set(OWNER_UUID, Optional.ofNullable(ownerUUID));
    }

    public int getMaxSize() {
        return this.entityData.get(MAX_SIZE);
    }

    public void setMaxSize(int maxSize) {
        this.entityData.set(MAX_SIZE, maxSize);
    }

    public int getRegeneration() {
        return this.entityData.get(REGENERATION);
    }

    public void setRegeneration(int regeneration) {
        this.entityData.set(REGENERATION, regeneration);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("maxHealth")) {
            this.setMaxHealth(compound.getInt("maxHealth"));
        }
        if (compound.contains("maxSize")) {
            this.setMaxSize(compound.getInt("maxSize"));
        }
        if (compound.contains("damage")) {
            this.setDamage(compound.getInt("damage"));
        }
        if (compound.contains("ownerUUID")) {
            this.setOwnerUUID(compound.getUUID("ownerUUID"));
        }
        if (compound.contains("regeneration")) {
            this.setRegeneration(compound.getInt("regeneration"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("regeneration", this.getRegeneration());
        compound.putInt("maxHealth", this.getMaxHealth());
        compound.putInt("damage", this.getDamage());
        compound.putInt("maxSize", this.getMaxSize());
        Optional<UUID> ownerUUID = this.getOwnerUUID();
        if (ownerUUID.isPresent()) {
            compound.putUUID("ownerUUID", ownerUUID.get());
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            float damage = this.getDamage();
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(this);
            this.setMaxSize((int) scaleData.getScale());
            UUID ownerUUID = this.getOwnerUUID().orElse(null);
            if (ownerUUID != null) {
                LivingEntity owner = BeyonderUtil.getLivingEntityFromUUID(this.level(), ownerUUID);
                if (owner != null && owner.distanceTo(this) < this.getMaxSize() * 3.1) {
                    if (this.tickCount % 50 == 0) {
                        if (owner instanceof Player player) {
                            player.displayClientMessage(Component.literal(damage + " / " + this.getMaxHealth()).withStyle(ChatFormatting.AQUA), true);

                        }
                        this.setRegeneration(Math.max(1,10 - (BeyonderUtil.getSequence(owner) * 2)));
                    }
                }

                for (Entity entity : this.level().getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate((this.getMaxSize() * 3.1) + 2))) {
                    if (!BeyonderUtil.isEntityAlly(owner, entity) && entity != this && entity != owner && !(entity instanceof ItemEntity)) {
                        int sequence = 9;
                        if (entity instanceof Projectile projectile && projectile.getOwner() != null && projectile.getOwner() instanceof LivingEntity living) {
                            if (BeyonderUtil.getSequence(living) != -1 && BeyonderUtil.getSequence(living) != 10) {
                                sequence = BeyonderUtil.getSequence(living);
                            }
                        } else if (entity instanceof LivingEntity living) {
                            if (BeyonderUtil.getSequence(living) != -1 && BeyonderUtil.getSequence(living) != 10) {
                                sequence = BeyonderUtil.getSequence(living);
                            }
                        }

                        RandomSource random = this.level().getRandom();
                        int maxSize = this.getMaxSize();
                        double angle = random.nextDouble() * 2 * Math.PI;
                        double minDistance = (maxSize * 4) + 5;
                        double maxDistance = maxSize * 7;
                        double distance = minDistance + random.nextDouble() * (maxDistance - minDistance);
                        int randomX = (int) (this.getX() + Math.cos(angle) * distance);
                        int randomZ = (int) (this.getZ() + Math.sin(angle) * distance);
                        int surfaceY = entity.level().getHeight(Heightmap.Types.WORLD_SURFACE, randomX, randomZ) + 20;
                        entity.teleportTo(randomX, surfaceY, randomZ);
                        int projectileMultiplier = 1;
                        if (entity instanceof Projectile projectile) {
                            projectileMultiplier = (int) Math.max(1, projectile.getBbHeight() * projectile.getBbWidth());
                        }
                        this.setDamage(this.getDamage() + (50 - (sequence * 5)) * projectileMultiplier);
                        if (entity instanceof Player player) {
                            player.displayClientMessage(Component.literal("You are not allowed in this protective seal").withStyle(ChatFormatting.RED), true);
                        }
                    }
                }
            }
            if (this.getDamage() >= this.getMaxHealth()) {
                this.discard();
            } else if (this.getDamage() > 0) {
                this.setDamage(Math.max(0, this.getDamage() - this.getRegeneration() * 5));
            }
        }
    }
}