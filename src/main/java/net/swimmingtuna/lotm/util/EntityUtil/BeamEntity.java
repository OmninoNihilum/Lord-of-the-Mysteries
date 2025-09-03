package net.swimmingtuna.lotm.util.EntityUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;
import net.swimmingtuna.lotm.entity.DragonBreathEntity;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.networking.packet.UpdateDragonBreathS2C;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.RotationUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BeamEntity extends LOTMProjectile {
    public double endPosX;
    public double endPosY;
    public double endPosZ;
    public Vec3 endPos;
    public double collidePosX;
    public double collidePosY;
    public double collidePosZ;
    public Vec3 collidePos;
    public double prevCollidePosX;
    public double prevCollidePosY;
    public double prevCollidePosZ;
    public Vec3 prevCollidePos;
    public float renderYaw;
    public float renderPitch;

    public boolean on = true;

    public @Nullable Direction side = null;

    // Add fixed direction storage for non-living owners
    private Vec3 fixedDirection = null;
    private Vec3 fixedStartPos = null;

    private static final EntityDataAccessor<Boolean> GAMMA_RAY = SynchedEntityData.defineId(BeamEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> TWILIGHT = SynchedEntityData.defineId(BeamEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LIVINGOWNER = SynchedEntityData.defineId(BeamEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DESTROY_BLOCKS = SynchedEntityData.defineId(BeamEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DRAGON_BREATH = SynchedEntityData.defineId(BeamEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(BeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> SIZE = SynchedEntityData.defineId(BeamEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FRENZY_TIME = SynchedEntityData.defineId(BeamEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_YAW = SynchedEntityData.defineId(BeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_PITCH = SynchedEntityData.defineId(BeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> RANGE = SynchedEntityData.defineId(BeamEntity.class, EntityDataSerializers.INT);

    public float prevYaw;
    public float prevPitch;

    public int animation;

    protected BeamEntity(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);

        this.noCulling = true;

        this.update();
    }

    protected BeamEntity(EntityType<? extends Projectile> entityType, LivingEntity owner, float power) {
        this(entityType, owner.level());

        this.setOwner(owner);
        this.setPower(power);
    }

    // Add method to set fixed direction for non-living owners
    public void setFixedDirection(Vec3 direction) {
        this.fixedDirection = direction.normalize();
        this.fixedStartPos = this.position();

        // Calculate yaw and pitch from direction for rendering
        double horizontalDistance = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        float yaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float pitch = (float) -Math.toDegrees(Math.atan2(direction.y, horizontalDistance));

        this.renderYaw = yaw;
        this.renderPitch = pitch;
        this.setYaw((float) Math.toRadians(yaw));
        this.setPitch((float) Math.toRadians(pitch));
    }

    public abstract int getFrames();

    public double getRange() {
        return this.entityData.get(RANGE);
    }

    public void setRange(int range) {
        this.entityData.set(RANGE, range);
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    public int getFrenzyTime() {
        return this.entityData.get(FRENZY_TIME);
    }

    public void setFrenzyTime(int frenzyTime) {
        this.entityData.set(FRENZY_TIME, frenzyTime);
    }

    public boolean getIsDragonBreath() {
        return this.entityData.get(DRAGON_BREATH);
    }

    public void setIsDragonbreath(boolean isDragonBreath) {
        this.entityData.set(DRAGON_BREATH, isDragonBreath);
    }

    public boolean getIsTwilight() {
        return this.entityData.get(TWILIGHT);
    }

    public void setIsTwilight(boolean isTwilight) {
        this.entityData.set(TWILIGHT, isTwilight);
    }

    public boolean getDestroyBlocks() {
        return this.entityData.get(DESTROY_BLOCKS);
    }

    public void setDestroyBlocks(boolean destroyBlocks) {
        this.entityData.set(DESTROY_BLOCKS, destroyBlocks);
    }

    public abstract int getDuration();

    public abstract int getCharge();

    protected boolean causesFire() {
        return false;
    }

    protected boolean breaksBlocks() {
        return true;
    }

    protected boolean isStill() {
        return false;
    }

    protected Vec3 calculateSpawnPos(LivingEntity owner) {
        return new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F) + 0.5, owner.getZ()).add(RotationUtil.getTargetAdjustedLookAngle(owner));
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        this.update();
        this.calculateEndPos();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            this.setPos(this.getX(), this.getY(), this.getZ());
            this.hasImpulse = true;
        }
        this.prevCollidePosX = this.collidePosX;
        this.prevCollidePosY = this.collidePosY;
        this.prevCollidePosZ = this.collidePosZ;
        this.prevCollidePos = this.collidePos;
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        if (!this.isStill()) {
            this.update();
        }

        // Modified owner check to handle non-living owners
        Entity owner = this.getOwner();
        if (owner != null || !this.getIsLivingOwner()) {
            if (!this.on && this.animation == 0) {
                this.discard();
            }

            if (this.getFrames() > 0) {
                if (this.on) {
                    if (this.animation < this.getFrames()) {
                        this.animation++;
                    }
                } else {
                    if (this.animation > 0) {
                        this.animation--;
                    }
                }
            }

            if (this.getTime() >= this.getCharge()) {
                if (!this.level().isClientSide) {
                    LOTMNetworkHandler.sendToAllPlayers(new UpdateDragonBreathS2C(
                            this.getX(), this.getY(), this.getZ(),  // start positions
                            this.endPosX, this.endPosY, this.endPosZ,  // end positions
                            this.getId(),  // entity ID
                            this.prevYaw, this.renderYaw,  // yaw data
                            this.prevPitch, this.renderPitch,  // pitch data
                            this.getTime(),  // current time
                            this.getCharge(),  // charge time
                            this.getDuration(),  // duration
                            (float) this.animation,  // animation progress
                            (float) this.getSize(),  // size
                            this.causesFire(),  // causes fire flag
                            this.prevCollidePosX, this.prevCollidePosY, this.prevCollidePosZ,  // previous collision pos
                            this.collidePosX, this.collidePosY, this.collidePosZ  // current collision pos
                    ));
                }

                if (!this.isStill()) {
                    this.calculateEndPos();
                }
                List<Entity> entities = this.checkCollisions(
                        new Vec3(this.getX(), this.getY(), this.getZ()),
                        new Vec3(this.endPosX, this.endPosY, this.endPosZ)
                );
                for (Entity entity : entities) {
                    if (entity == owner) continue;
                    if (getIsDragonBreath() && this.getOwner() != null && this.getOwner() instanceof LivingEntity livingOwner && entity instanceof LivingEntity livingEntity && !BeyonderUtil.areAllies(livingOwner, livingEntity)) {
                        BeyonderUtil.applyMentalDamage(livingOwner, livingEntity, this.getDamage());
                    } else if (getIsDragonBreath() && this.getOwner() == null && entity instanceof LivingEntity livingEntity) {
                        livingEntity.hurt(BeyonderUtil.magicSource(this, livingEntity), getDamage());
                    }
                    if (getIsTwilight() && entity instanceof LivingEntity livingEntity && this.getOwner() instanceof LivingEntity pOwner && !BeyonderUtil.areAllies(pOwner, livingEntity)) {
                        int age = livingEntity.getPersistentData().getInt("age");
                        livingEntity.hurt(BeyonderUtil.genericSource(owner, livingEntity), 10);
                        int ageDivisibleAmount = 2;
                        if (pOwner instanceof Mob) {
                            ageDivisibleAmount = 3;
                        }
                        if (this.tickCount % 2 == 0) {
                            if (livingEntity instanceof Player player) {
                                player.displayClientMessage(Component.literal("You are getting rapidly aged").withStyle(BeyonderUtil.ageStyle(livingEntity)).withStyle(ChatFormatting.BOLD), true);
                            }
                            if (BeyonderUtil.getSequence(pOwner) != 0) {
                                livingEntity.getPersistentData().putUUID("ageUUID", pOwner.getUUID());
                                livingEntity.getPersistentData().putInt("age", ((age + (30 - BeyonderUtil.getSequence(pOwner))) * 9 / ageDivisibleAmount));
                            } else {
                                livingEntity.getPersistentData().putUUID("ageUUID", pOwner.getUUID());
                                livingEntity.getPersistentData().putInt("age", (age + (50 / ageDivisibleAmount)));
                            }
                        }
                    }
                    if (isGammaRay() && this.getOwner() != null && this.getOwner() instanceof LivingEntity livingOwner && entity instanceof LivingEntity livingEntity) {
                        int noRegeneration = livingEntity.getPersistentData().getInt("LOTMNoRegeneration");
                        BeyonderUtil.applyNoRegeneration(livingEntity, noRegeneration + 5);
                        livingEntity.hurt(BeyonderUtil.magicSource(livingOwner, livingEntity), this.getDamage());
                    } else if (isGammaRay() && this.getOwner() == null && entity instanceof LivingEntity livingEntity) {
                        livingEntity.hurt(BeyonderUtil.magicSource(this, livingEntity), getDamage());
                        int noRegeneration = livingEntity.getPersistentData().getInt("LOTMNoRegeneration");
                        BeyonderUtil.applyNoRegeneration(livingEntity, noRegeneration + 5);
                    }

                    if (entity instanceof LivingEntity livingEntity && getIsDragonBreath() && this.getOwner() != null && this.getOwner() instanceof LivingEntity livingOwner && !BeyonderUtil.areAllies(livingOwner, livingEntity) && this.tickCount >= this.getCharge() + this.getDuration() - 20) {
                        BeyonderUtil.applyFrenzy(livingEntity, this.getFrenzyTime());
                    }

                    if (this.causesFire()) {
                        entity.setSecondsOnFire(5);
                    }
                }

                if (!this.level().isClientSide && this.getIsLivingOwner() && owner instanceof LivingEntity livingOwner) {
                    this.handleBlockDestruction(livingOwner);
                } else if (!this.level().isClientSide && !this.getIsLivingOwner()) {
                    this.handleBlockDestructionNonLiving();
                }
            }

            if (this.getTime() - this.getCharge() >= this.getDuration()) {
                this.on = false;
            }
        }
    }

    private void handleBlockDestructionNonLiving() {
        double radius = this.getSize();
        Vec3 from = new Vec3(this.getX(), this.getY(), this.getZ());
        Vec3 to = new Vec3(this.collidePosX, this.collidePosY, this.collidePosZ);
        Vec3 dir = to.subtract(from).normalize();

        AABB bounds = new AABB(
                Math.min(from.x, this.collidePosX) - radius,
                Math.min(from.y, this.collidePosY) - radius,
                Math.min(from.z, this.collidePosZ) - radius,
                Math.max(from.x, this.collidePosX) + radius,
                Math.max(from.y, this.collidePosY) + radius,
                Math.max(from.z, this.collidePosZ) + radius
        );

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int x = (int) Math.floor(bounds.minX); x <= Math.ceil(bounds.maxX); x++) {
            for (int y = (int) Math.floor(bounds.minY); y <= Math.ceil(bounds.maxY); y++) {
                for (int z = (int) Math.floor(bounds.minZ); z <= Math.ceil(bounds.maxZ); z++) {
                    mutablePos.set(x, y, z);
                    Vec3 point = new Vec3(x + 0.5, y + 0.5, z + 0.5);
                    Vec3 fromToPoint = point.subtract(from);
                    double dot = fromToPoint.dot(dir);
                    Vec3 projection = dir.scale(dot);
                    Vec3 distanceVec = fromToPoint.subtract(projection);
                    double distance = distanceVec.length();

                    if (distance <= radius) {
                        if (getDestroyBlocks()) {
                            if (this.breaksBlocks() && !EXCLUDED_BLOCKS.contains(this.level().getBlockState(mutablePos).getBlock())) {
                                this.level().destroyBlock(mutablePos, false);
                            }
                        }

                        if (this.tickCount % 5 == 0 && getIsTwilight() &&
                                this.level().getBlockState(mutablePos) != Blocks.BEDROCK.defaultBlockState() &&
                                this.level().getBlockState(mutablePos) != Blocks.WATER.defaultBlockState()) {
                            if (this.level().getBlockState(mutablePos) != Blocks.DIRT.defaultBlockState() && this.level().getBlockState(mutablePos) != Blocks.AIR.defaultBlockState()) {
                                BeyonderUtil.setAsBlock(this, mutablePos, Blocks.DIRT);
                            } else {
                                this.level().destroyBlock(mutablePos, false);
                            }
                        }

                        if (this.causesFire()) {
                            if (this.random.nextInt(3) == 0 &&
                                    this.level().getBlockState(mutablePos).isAir() &&
                                    this.level().getBlockState(mutablePos.below()).isSolidRender(this.level(), mutablePos.below())) {
                                BeyonderUtil.setAsBlock(this, mutablePos, Blocks.FIRE);
                            }
                        }
                    }
                }
            }
        }
    }
    private void handleBlockDestruction(LivingEntity owner) {
        double radius = this.getSize();
        Vec3 from = new Vec3(this.getX(), this.getY(), this.getZ());
        Vec3 to = new Vec3(this.collidePosX, this.collidePosY, this.collidePosZ);
        Vec3 dir = to.subtract(from).normalize();

        AABB bounds = new AABB(
                Math.min(from.x, this.collidePosX) - radius,
                Math.min(from.y, this.collidePosY) - radius,
                Math.min(from.z, this.collidePosZ) - radius,
                Math.max(from.x, this.collidePosX) + radius,
                Math.max(from.y, this.collidePosY) + radius,
                Math.max(from.z, this.collidePosZ) + radius
        );
        boolean isLookingDown = false;
        if (owner != null) {
            float pitch = owner.getXRot();
            isLookingDown = pitch > 70.0f;
        }

        double ownerGroundY = owner != null ? owner.getY() : Double.MAX_VALUE;
        double ownerRadius = owner != null ? owner.getBbWidth() / 2.0 : 1.0;

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int x = (int) Math.floor(bounds.minX); x <= Math.ceil(bounds.maxX); x++) {
            for (int y = (int) Math.floor(bounds.minY); y <= Math.ceil(bounds.maxY); y++) {
                for (int z = (int) Math.floor(bounds.minZ); z <= Math.ceil(bounds.maxZ); z++) {
                    mutablePos.set(x, y, z);
                    Vec3 point = new Vec3(x + 0.5, y + 0.5, z + 0.5);
                    Vec3 fromToPoint = point.subtract(from);
                    double dot = fromToPoint.dot(dir);
                    Vec3 projection = dir.scale(dot);
                    Vec3 distanceVec = fromToPoint.subtract(projection);
                    double distance = distanceVec.length();

                    if (distance <= radius) {
                        boolean isNearOwnerFeet = false;
                        double blockDistanceFromOwner = Math.sqrt(Math.pow(x + 0.5 - owner.getX(), 2) + Math.pow(z + 0.5 - owner.getZ(), 2));
                        isNearOwnerFeet = blockDistanceFromOwner <= (ownerRadius + 0.5) && y <= ownerGroundY + 1;
                        boolean shouldDestroy = getDestroyBlocks() && (!isNearOwnerFeet || isLookingDown);

                        if (shouldDestroy) {
                            if (this.breaksBlocks() && !EXCLUDED_BLOCKS.contains(this.level().getBlockState(mutablePos).getBlock())) {
                                this.level().destroyBlock(mutablePos, false);
                            }
                        } else if (this.tickCount % 5 == 0 && getIsTwilight() &&
                                this.level().getBlockState(mutablePos) != Blocks.BEDROCK.defaultBlockState() &&
                                this.level().getBlockState(mutablePos) != Blocks.WATER.defaultBlockState() &&
                                (!isNearOwnerFeet || isLookingDown)) { // Same logic for twilight effect
                            if (this.level().getBlockState(mutablePos) != Blocks.DIRT.defaultBlockState() && this.level().getBlockState(mutablePos) != Blocks.AIR.defaultBlockState()) {
                                BeyonderUtil.setAsBlock(this, mutablePos, Blocks.DIRT);
                            } else {
                                this.level().destroyBlock(mutablePos, false);
                            }
                        }

                        if (this.causesFire()) {
                            if (this.random.nextInt(3) == 0 &&
                                    this.level().getBlockState(mutablePos).isAir() &&
                                    this.level().getBlockState(mutablePos.below()).isSolidRender(this.level(), mutablePos.below())) {
                                BeyonderUtil.setAsBlock(this, mutablePos, Blocks.FIRE);
                            }
                        }
                    }
                }
            }
        }
    }

    private static final List<Block> EXCLUDED_BLOCKS = List.of(Blocks.BEDROCK, Blocks.OBSIDIAN);

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(GAMMA_RAY, false);
        this.entityData.define(DAMAGE, 20.0F);
        this.entityData.define(DATA_YAW, 0.0F);
        this.entityData.define(DATA_PITCH, 0.0F);
        this.entityData.define(DRAGON_BREATH, false);
        this.entityData.define(LIVINGOWNER, true);
        this.entityData.define(FRENZY_TIME, 1);
        this.entityData.define(SIZE, 1);
        this.entityData.define(DESTROY_BLOCKS, true);
        this.entityData.define(TWILIGHT, false);
        this.entityData.define(RANGE, 64);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("damage")) {
            this.setDamage(compound.getFloat("damage"));
        }
        if (compound.contains("gammaRay")) {
            this.setGammaRay(compound.getBoolean("gammaRay"));
        }
        if (compound.contains("range")) {
            this.setRange(compound.getInt("range"));
        }
        if (compound.contains("data_yaw")) {
            this.setYaw(compound.getFloat("data_yaw"));
        }
        if (compound.contains("data_pitch")) {
            this.setPitch(compound.getFloat("data_pitch"));
        }
        if (compound.contains("dragon_breath")) {
            this.setIsDragonbreath(compound.getBoolean("dragon_breath"));
        }
        if (compound.contains("livingOwner")) {
            this.setIsLivingOwner(compound.getBoolean("livingOwner"));
        }
        if (compound.contains("frenzy_time")) {
            this.setFrenzyTime(compound.getInt("frenzy_time"));
        }
        if (compound.contains("size")) {
            this.setSize(compound.getInt("size"));
        }
        if (compound.contains("destroy_blocks")) {
            this.setDestroyBlocks(compound.getBoolean("destroy_blocks")); // Fixed bug here
        }
        if (compound.contains("twilight")) {
            this.setIsTwilight(compound.getBoolean("twilight")); // Fixed bug here
        }

        // Add serialization for fixed direction
        if (compound.contains("fixedDirectionX")) {
            this.fixedDirection = new Vec3(
                    compound.getDouble("fixedDirectionX"),
                    compound.getDouble("fixedDirectionY"),
                    compound.getDouble("fixedDirectionZ")
            );
        }
        if (compound.contains("fixedStartPosX")) {
            this.fixedStartPos = new Vec3(
                    compound.getDouble("fixedStartPosX"),
                    compound.getDouble("fixedStartPosY"),
                    compound.getDouble("fixedStartPosZ")
            );
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("damage", this.getDamage());
        compound.putBoolean("gammaRay", this.isGammaRay());
        compound.putFloat("data_yaw", this.getYaw());
        compound.putFloat("data_pitch", this.getPitch());
        compound.putBoolean("livingOwner", this.getIsLivingOwner());
        compound.putBoolean("dragon_breath", this.getIsDragonBreath());
        compound.putInt("frenzy_time", this.getFrenzyTime());
        compound.putInt("size", this.getSize());
        compound.putBoolean("destroy_blocks", this.getDestroyBlocks());
        compound.putBoolean("twilight", this.getIsTwilight());
        compound.putInt("range", (int) this.getRange());

        // Add serialization for fixed direction
        if (this.fixedDirection != null) {
            compound.putDouble("fixedDirectionX", this.fixedDirection.x);
            compound.putDouble("fixedDirectionY", this.fixedDirection.y);
            compound.putDouble("fixedDirectionZ", this.fixedDirection.z);
        }
        if (this.fixedStartPos != null) {
            compound.putDouble("fixedStartPosX", this.fixedStartPos.x);
            compound.putDouble("fixedStartPosY", this.fixedStartPos.y);
            compound.putDouble("fixedStartPosZ", this.fixedStartPos.z);
        }
    }

    public int getSize() {
        return this.entityData.get(SIZE);
    }

    public void setSize(int size) {
        this.entityData.set(SIZE, size);
    }

    public boolean getIsLivingOwner() {
        return this.entityData.get(LIVINGOWNER);
    }

    public void setIsLivingOwner(boolean isLivingOwner) {
        this.entityData.set(LIVINGOWNER, isLivingOwner);
    }

    public float getYaw() {
        return this.entityData.get(DATA_YAW);
    }

    public void setYaw(float yaw) {
        this.entityData.set(DATA_YAW, yaw);
    }

    public boolean isGammaRay() {
        return this.entityData.get(GAMMA_RAY);
    }

    public void setGammaRay(boolean gammaRay) {
        this.entityData.set(GAMMA_RAY, gammaRay);
    }

    public float getPitch() {
        return this.entityData.get(DATA_PITCH);
    }

    public void setPitch(float pitch) {
        this.entityData.set(DATA_PITCH, pitch);
    }

    public List<Entity> checkCollisions(Vec3 from, Vec3 to) {
        Entity owner = this.getOwner(); // Changed to Entity instead of casting to LivingEntity
        BlockHitResult result = this.level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (result.getType() != HitResult.Type.MISS) {
            Vec3 pos = result.getLocation();
            this.collidePosX = pos.x;
            this.collidePosY = pos.y;
            this.collidePosZ = pos.z;
            this.side = result.getDirection();
        } else {
            this.collidePosX = to.x;
            this.collidePosY = to.y;
            this.collidePosZ = to.z;
            this.side = null;
        }
        Vec3 dir = to.subtract(from).normalize();
        double radius = this.getSize();
        AABB bounds = new AABB(
                Math.min(from.x, this.collidePosX) - radius,
                Math.min(from.y, this.collidePosY) - radius,
                Math.min(from.z, this.collidePosZ) - radius,
                Math.max(from.x, this.collidePosX) + radius,
                Math.max(from.y, this.collidePosY) + radius,
                Math.max(from.z, this.collidePosZ) + radius
        );

        double entityDetectionRadius = radius * 1.5;

        AABB entityBounds = new AABB(
                Math.min(from.x, this.collidePosX) - radius * 0.8,
                Math.min(from.y, this.collidePosY) - radius * 0.8,
                Math.min(from.z, this.collidePosZ) - radius * 0.8,
                Math.max(from.x, this.collidePosX) + radius * 0.8,
                Math.max(from.y, this.collidePosY) + radius,
                Math.max(from.z, this.collidePosZ) + radius * 0.8
        );
        List<Entity> entities = new ArrayList<>();
        for (Entity entity : this.level().getEntitiesOfClass(Entity.class, entityBounds)) {
            if (entity == this.getOwner() || entity == this) continue;
            AABB entityBox = entity.getBoundingBox();
            if (rayIntersectsBox(from, to, entityBox)) {
                entities.add(entity);
                continue;
            }
            Vec3[] checkPoints = {
                    new Vec3(entity.getX(), entity.getY(), entity.getZ()),
                    new Vec3(entity.getX(), entity.getY() + entity.getBbHeight() * 0.3, entity.getZ()),
                    new Vec3(entity.getX(), entity.getY() + entity.getBbHeight() * 0.6, entity.getZ()),
                    new Vec3(entity.getX(), entity.getY() + entity.getBbHeight() * 0.45, entity.getZ())
            };

            for (Vec3 point : checkPoints) {
                Vec3 nearestPoint = getNearestPointOnLine(from, to, point);
                double distance = point.distanceTo(nearestPoint);

                if (distance <= entityDetectionRadius) {
                    entities.add(entity);
                    break;
                }
            }
        }
        return entities;
    }

    private boolean rayIntersectsBox(Vec3 rayStart, Vec3 rayEnd, AABB box) {
        Vec3 rayDir = rayEnd.subtract(rayStart).normalize();

        // Calculate intersection with each face of the box
        double tMin = (box.minX - rayStart.x) / (rayDir.x == 0 ? 0.00001 : rayDir.x);
        double tMax = (box.maxX - rayStart.x) / (rayDir.x == 0 ? 0.00001 : rayDir.x);

        if (tMin > tMax) {
            double temp = tMin;
            tMin = tMax;
            tMax = temp;
        }

        double tyMin = (box.minY - rayStart.y) / (rayDir.y == 0 ? 0.00001 : rayDir.y);
        double tyMax = (box.maxY - rayStart.y) / (rayDir.y == 0 ? 0.00001 : rayDir.y);

        if (tyMin > tyMax) {
            double temp = tyMin;
            tyMin = tyMax;
            tyMax = temp;
        }

        if ((tMin > tyMax) || (tyMin > tMax)) {
            return false;
        }

        if (tyMin > tMin) {
            tMin = tyMin;
        }

        if (tyMax < tMax) {
            tMax = tyMax;
        }

        double tzMin = (box.minZ - rayStart.z) / (rayDir.z == 0 ? 0.00001 : rayDir.z);
        double tzMax = (box.maxZ - rayStart.z) / (rayDir.z == 0 ? 0.00001 : rayDir.z);

        if (tzMin > tzMax) {
            double temp = tzMin;
            tzMin = tzMax;
            tzMax = temp;
        }

        if ((tMin > tzMax) || (tzMin > tMax)) {
            return false;
        }

        // Check if intersection is within ray length
        double maxDistance = rayStart.distanceTo(rayEnd);
        return tMin >= 0 && tMin <= maxDistance;
    }

    private Vec3 getNearestPointOnLine(Vec3 lineStart, Vec3 lineEnd, Vec3 point) {
        Vec3 line = lineEnd.subtract(lineStart);
        double len = line.length();
        line = line.normalize();

        Vec3 v = point.subtract(lineStart);
        double d = v.dot(line);
        d = Math.max(0, Math.min(len, d));

        return lineStart.add(line.scale(d));
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    private void update() {
        if (!this.getIsLivingOwner()) {
            this.prevYaw = this.renderYaw;
            this.prevPitch = this.renderPitch;
            if (this.fixedStartPos != null) {
                this.setPos(this.fixedStartPos.x, this.fixedStartPos.y, this.fixedStartPos.z);
            }
            this.setYaw((float) Math.toRadians(this.renderYaw));
            this.setPitch((float) Math.toRadians(this.renderPitch));

        } else if (this.getOwner() instanceof LivingEntity owner) {
            float yaw = owner.getYRot();
            float pitch = owner.getXRot();
            this.renderYaw = yaw;
            this.renderPitch = pitch;
            this.setYaw((float) Math.toRadians(yaw));
            this.setPitch((float) Math.toRadians(pitch));
            Vec3 spawn = this.calculateSpawnPos(owner);
            double yOffset = (this.getFrames() <= this.getCharge()) ? 0.5 : 0.0;
            this.setPos(spawn.x, spawn.y + yOffset, spawn.z);
        }
    }

    private void calculateEndPos() {
        Vec3 direction;
        Vec3 startPos;

        if (this.getIsLivingOwner()) {
            if (this.getOwner() instanceof LivingEntity owner) {
                float scale = 1.0f;
                try {
                    scale = BeyonderUtil.getScale(owner);
                } catch (Exception ignored) {
                }
                direction = owner.getLookAngle();
                startPos = owner.getEyePosition();
                Vec3 endPos = performRaycast(startPos, direction, scale);
                this.endPosX = endPos.x;
                this.endPosY = endPos.y;
                this.endPosZ = endPos.z;
                this.endPos = endPos;
            } else {
                if (this.level().isClientSide) {
                    direction = new Vec3(
                            -Math.sin(Math.toRadians(this.renderYaw)) * Math.cos(Math.toRadians(this.renderPitch)),
                            -Math.sin(Math.toRadians(this.renderPitch)),
                            Math.cos(Math.toRadians(this.renderYaw)) * Math.cos(Math.toRadians(this.renderPitch))
                    ).normalize();
                } else {
                    direction = new Vec3(
                            -Math.sin(this.getYaw()) * Math.cos(this.getPitch()),
                            -Math.sin(this.getPitch()),
                            Math.cos(this.getYaw()) * Math.cos(this.getPitch())
                    ).normalize();
                }
                startPos = new Vec3(this.getX(), this.getY(), this.getZ());
                Vec3 end = startPos.add(direction.scale(this.getRange()));
                this.endPosX = end.x;
                this.endPosY = end.y;
                this.endPosZ = end.z;
                this.endPos = end;
            }
        } else {
            if (this.fixedDirection != null && this.fixedStartPos != null) {
                direction = this.fixedDirection;
                startPos = this.fixedStartPos;
                Vec3 endPos = performRaycast(startPos, direction, 1.0f);
                this.endPosX = endPos.x;
                this.endPosY = endPos.y;
                this.endPosZ = endPos.z;
                this.endPos = endPos;
            } else {
                direction = new Vec3(
                        -Math.sin(Math.toRadians(this.renderYaw)) * Math.cos(Math.toRadians(this.renderPitch)),
                        -Math.sin(Math.toRadians(this.renderPitch)),
                        Math.cos(Math.toRadians(this.renderYaw)) * Math.cos(Math.toRadians(this.renderPitch))
                ).normalize();
                startPos = new Vec3(this.getX(), this.getY(), this.getZ());
                Vec3 endPos = performRaycast(startPos, direction, 1.0f);
                this.endPosX = endPos.x;
                this.endPosY = endPos.y;
                this.endPosZ = endPos.z;
                this.endPos = endPos;
            }
        }
    }

    private Vec3 performRaycast(Vec3 startPos, Vec3 direction, float scale) {
        double maxDistance = this.getRange();
        maxDistance *= Math.max(1.0, scale * 0.5);
        Vec3 endPos = startPos.add(direction.scale(maxDistance));
        BlockHitResult blockHit = this.level().clip(new ClipContext(
                startPos,
                endPos,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.ANY,
                this
        ));
        EntityHitResult entityHit = getEntityHitResult(startPos, direction, maxDistance);
        Vec3 finalEndPos;
        if (entityHit != null && blockHit.getType() != HitResult.Type.MISS) {
            double entityDistance = startPos.distanceTo(entityHit.getLocation());
            double blockDistance = startPos.distanceTo(blockHit.getLocation());

            if (entityDistance < blockDistance) {
                finalEndPos = entityHit.getLocation();
            } else {
                finalEndPos = blockHit.getLocation();
            }
        } else if (entityHit != null) {
            finalEndPos = entityHit.getLocation();
        } else if (blockHit.getType() != HitResult.Type.MISS) {
            finalEndPos = blockHit.getLocation();
        } else {
            finalEndPos = startPos.add(direction.scale(maxDistance));
        }

        return finalEndPos;
    }


    private EntityHitResult getEntityHitResult(Vec3 startPos, Vec3 direction, double maxDistance) {
        Vec3 endPos = startPos.add(direction.scale(maxDistance));
        AABB searchBox = new AABB(startPos, endPos).inflate(1.0);
        Entity closestEntity = null;
        Vec3 closestHitPos = null;
        double closestDistance = maxDistance;
        for (Entity entity : this.level().getEntities(this.getOwner(), searchBox)) {
            if (entity == this.getOwner() || entity == this) {
                continue;
            }

            AABB entityBox = entity.getBoundingBox();
            Optional<Vec3> hitResult = entityBox.clip(startPos, endPos);
            if (hitResult.isPresent()) {
                Vec3 hitPos = hitResult.get();
                double distance = startPos.distanceTo(hitPos);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestEntity = entity;
                    closestHitPos = hitPos;
                }
            }
        }

        if (closestEntity != null) {
            return new EntityHitResult(closestEntity, closestHitPos);
        }

        return null;
    }

    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
        return new AABB(
                this.getX() - 3000,
                this.getY() - 3000,
                this.getZ() - 3000,
                this.getX() + 3000,
                this.getY() + 3000,
                this.getZ() + 3000
        );
    }
}