package net.swimmingtuna.lotm.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.CustomEntityDataSerializers;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ApprenticeDoorEntity extends Entity implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public enum DoorMode{
        TELEPORT_ONLY
    }

    public enum DoorAnimationKind{
        BEHIND,
        BELLOW,
        FADE_IN
    }


    private static final EntityDataAccessor<DoorMode> DOOR_MODE = SynchedEntityData.defineId(ApprenticeDoorEntity.class, CustomEntityDataSerializers.DOOR_MODE);
    private static final EntityDataAccessor<DoorAnimationKind> DOOR_ANIMATION_KIND = SynchedEntityData.defineId(ApprenticeDoorEntity.class, CustomEntityDataSerializers.DOOR_ANIMATION_KIND);
    private static final EntityDataAccessor<Boolean> HAS_PLAYED_ANIMATION = SynchedEntityData.defineId(ApprenticeDoorEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_DYING = SynchedEntityData.defineId(ApprenticeDoorEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FREE_TO_USE = SynchedEntityData.defineId(ApprenticeDoorEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> SEQUENCE = SynchedEntityData.defineId(ApprenticeDoorEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LIFE = SynchedEntityData.defineId(ApprenticeDoorEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FULL_LIFE = SynchedEntityData.defineId(ApprenticeDoorEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> YAW = SynchedEntityData.defineId(ApprenticeDoorEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> X = SynchedEntityData.defineId(ApprenticeDoorEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> Y = SynchedEntityData.defineId(ApprenticeDoorEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> Z = SynchedEntityData.defineId(ApprenticeDoorEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<String> DIMENSION_ID = SynchedEntityData.defineId(ApprenticeDoorEntity.class, EntityDataSerializers.STRING);


    private DoorMode doorMode;
    private DoorAnimationKind animationKind;
    private Level dimensionDestination;
    private LivingEntity creator;

    public ApprenticeDoorEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ApprenticeDoorEntity(Level level, LivingEntity creator, int sequence, int life, float yaw, float x, float y, float z, Level dimensionDestination, DoorAnimationKind animationKind){
        this(EntityInit.APPRENTICE_DOOR_ENTITY.get(), level);
        this.entityData.set(DOOR_MODE, DoorMode.TELEPORT_ONLY);
        this.entityData.set(DOOR_ANIMATION_KIND, animationKind);
        this.entityData.set(SEQUENCE, sequence);
        this.entityData.set(LIFE, life);
        this.entityData.set(FULL_LIFE, life);
        this.entityData.set(YAW, yaw);
        this.entityData.set(X, x);
        this.entityData.set(Y, y);
        this.entityData.set(Z, z);

        this.dimensionDestination = dimensionDestination;
        if (dimensionDestination != null) {
            ResourceKey<Level> dimKey = ((ServerLevel)dimensionDestination).dimension();
            this.entityData.set(DIMENSION_ID, dimKey.location().toString());
        }
        this.creator = creator;
    }

    @Override
    public void tick(){
        super.tick();

        if(getDoorMode() == DoorMode.TELEPORT_ONLY){
            if(!this.level().isClientSide){
                handleLife();
                if(BeyonderUtil.isEntityColliding(this, this.level(), 0.5)){
                    Entity entity = BeyonderUtil.checkEntityCollision(this, this.level(), 0.5);
                    if(entity instanceof Player player){
                        if(player.isShiftKeyDown()) teleport(player);
                    }else if(entity instanceof LivingEntity living){
                        teleport(living);
                    }
                }
            }
        }
    }

    public DoorMode getDoorMode(){
        return this.entityData.get(DOOR_MODE);
    }

    public DoorAnimationKind getAnimationKind(){
        return this.entityData.get(DOOR_ANIMATION_KIND);
    }

    public boolean isFreeToUse(){
        return this.entityData.get(FREE_TO_USE);
    }

    public int getLife(){
        return this.entityData.get(LIFE);
    }

    public int getFullLife(){
        return this.entityData.get(FULL_LIFE);
    }

    public LivingEntity getCreator(){
        return this.creator;
    }

    public int getSequence(){
        return this.entityData.get(SEQUENCE);
    }

    public float getTeleportX(){
        return this.entityData.get(X);
    }

    public float getTeleportY(){
        return this.entityData.get(Y);
    }

    public float getTeleportZ(){
        return this.entityData.get(Z);
    }

    public Level getDimensionDestination(){
        return this.dimensionDestination;
    }

    public float getYaw() {
        return this.entityData.get(YAW);
    }

    public void delete(){
        this.remove(RemovalReason.DISCARDED);
    }

    private void handleLife(){
        int life = getLife();

        if(getDoorMode() == DoorMode.TELEPORT_ONLY) {
            if (life < getFullLife() - 30) {
                this.entityData.set(HAS_PLAYED_ANIMATION, true);
                this.entityData.set(FREE_TO_USE, true);
            }else{
                this.entityData.set(HAS_PLAYED_ANIMATION, false);
                this.entityData.set(FREE_TO_USE, false);
            }

            if (life > 0) {
                if (life <= 30) {
                    this.entityData.set(IS_DYING, true);
                    this.entityData.set(FREE_TO_USE, false);
                }
                this.entityData.set(LIFE, life - 1);
            } else {
                delete();
            }
        }
    }

    private void teleport(LivingEntity entity) {
        if (getDoorMode() == DoorMode.TELEPORT_ONLY) {
            if (isFreeToUse()) {
                if (getSequence() > 7) {
                    if (entity != null && entity == getCreator()) {
                        teleportToDestination(entity);
                    }
                } else {
                    if (entity != null) {
                        teleportToDestination(entity);
                    }
                }
            }
        }
    }
    private void teleportToDestination(LivingEntity entity) {
        if (dimensionDestination == null && !this.entityData.get(DIMENSION_ID).isEmpty()) {
            if (level().getServer() != null) {
                ResourceKey<Level> dimKey = ResourceKey.create(Registries.DIMENSION,
                        new ResourceLocation(this.entityData.get(DIMENSION_ID)));
                ServerLevel targetLevel = level().getServer().getLevel(dimKey);
                if (targetLevel != null) {
                    dimensionDestination = targetLevel;
                }
            }
        }
        if (dimensionDestination == null) {
            return;
        }
        float x = getTeleportX();
        float y = getTeleportY();
        float z = getTeleportZ();
        if (dimensionDestination.equals(entity.level())) {
            entity.teleportTo(x, y, z);
        } else {
            BeyonderUtil.teleportEntityTroughDimensions(entity, dimensionDestination, x, y, z);
        }
    }

    private String getOpenAnimation(){
        if(getAnimationKind() == DoorAnimationKind.BEHIND) return "open_behind";
        if(getAnimationKind() == DoorAnimationKind.BELLOW) return "open_below";
        if(getAnimationKind() == DoorAnimationKind.FADE_IN) return "open_fade_in";
        return "";
    }

    private String getCloseAnimation(){
        if(getAnimationKind() == DoorAnimationKind.BEHIND) return "close_behind";
        if(getAnimationKind() == DoorAnimationKind.BELLOW) return "close_below";
        if(getAnimationKind() == DoorAnimationKind.FADE_IN) return "close_fade_in";
        return "";
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DOOR_MODE, DoorMode.TELEPORT_ONLY);
        this.entityData.define(DOOR_ANIMATION_KIND, DoorAnimationKind.BEHIND);
        this.entityData.define(HAS_PLAYED_ANIMATION, false);
        this.entityData.define(IS_DYING, false);
        this.entityData.define(FREE_TO_USE, false);
        this.entityData.define(SEQUENCE, 9);
        this.entityData.define(LIFE, 20);
        this.entityData.define(FULL_LIFE, 20);
        this.entityData.define(YAW, 0F);
        this.entityData.define(X, 0F);
        this.entityData.define(Y, 0F);
        this.entityData.define(Z, 0F);
        this.entityData.define(DIMENSION_ID, "");
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("doorMode")) {
            try {
                DoorMode mode = DoorMode.valueOf(tag.getString("doorMode"));
                this.entityData.set(DOOR_MODE, mode);
            } catch (IllegalArgumentException ignored) {
                delete();
            }
        }
        if(tag.contains("dimensionId")) {
            this.entityData.set(DIMENSION_ID, tag.getString("dimensionId"));
            if (level().getServer() != null) {
                ResourceKey<Level> dimKey = ResourceKey.create(Registries.DIMENSION,
                        new ResourceLocation(tag.getString("dimensionId")));
                ServerLevel targetLevel = level().getServer().getLevel(dimKey);
                if (targetLevel != null) {
                    this.dimensionDestination = targetLevel;
                }
            }
        }
        if (tag.contains("doorAnimationKind")) {
            try {
                DoorAnimationKind kind = DoorAnimationKind.valueOf(tag.getString("doorAnimationKind"));
                this.entityData.set(DOOR_ANIMATION_KIND, kind);
            } catch (IllegalArgumentException ignored) {
                delete();
            }
        }
        if(tag.contains("hasPlayedAnimation")){
            this.entityData.set(HAS_PLAYED_ANIMATION, tag.getBoolean("hasPlayedAnimation"));
        }
        if(tag.contains("isDying")){
            this.entityData.set(IS_DYING, tag.getBoolean("isDying"));
        }
        if(tag.contains("freeToUse")){
            this.entityData.set(FREE_TO_USE, tag.getBoolean("freeToUse"));
        }
        if(tag.contains("sequence")){
            this.entityData.set(SEQUENCE, tag.getInt("sequence"));
        }
        if(tag.contains("life")){
            this.entityData.set(LIFE, tag.getInt("life"));
        }
        if(tag.contains("fullLife")){
            this.entityData.set(FULL_LIFE, tag.getInt("fullLife"));
        }
        if(tag.contains("yaw")){
            this.entityData.set(YAW, tag.getFloat("yaw"));
        }
        if(tag.contains("x")){
            this.entityData.set(X, tag.getFloat("x"));
        }
        if(tag.contains("y")){
            this.entityData.set(Y, tag.getFloat("y"));
        }
        if(tag.contains("z")){
            this.entityData.set(Z, tag.getFloat("z"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putString("doorMode", this.entityData.get(DOOR_MODE).name());
        tag.putString("doorAnimationKind", this.entityData.get(DOOR_ANIMATION_KIND).name());
        tag.putBoolean("hasPlayedAnimation", this.entityData.get(HAS_PLAYED_ANIMATION));
        tag.putBoolean("isDying", this.entityData.get(IS_DYING));
        tag.putBoolean("freeToUse", this.entityData.get(FREE_TO_USE));
        tag.putInt("sequence", this.entityData.get(SEQUENCE));
        tag.putInt("life", this.entityData.get(LIFE));
        tag.putInt("fullLife", this.entityData.get(FULL_LIFE));
        tag.putFloat("yaw", this.entityData.get(YAW));
        tag.putFloat("x", this.entityData.get(X));
        tag.putFloat("y", this.entityData.get(Y));
        tag.putFloat("z", this.entityData.get(Z));
        String dimId = this.entityData.get(DIMENSION_ID);
        if (!dimId.isEmpty()) {
            tag.putString("dimensionId", dimId);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<ApprenticeDoorEntity> animationState) {
        AnimationController<ApprenticeDoorEntity> controller = animationState.getController();

        if (!this.entityData.get(HAS_PLAYED_ANIMATION)) {
            controller.setAnimation(RawAnimation.begin().then(getOpenAnimation(), Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }

        if (this.entityData.get(IS_DYING)) {
            controller.setAnimation(RawAnimation.begin().then(getCloseAnimation(), Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }

        controller.setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
