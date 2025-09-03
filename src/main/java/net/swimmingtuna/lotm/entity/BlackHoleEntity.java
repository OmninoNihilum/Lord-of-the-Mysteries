package net.swimmingtuna.lotm.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ParticleInit;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;

import static net.swimmingtuna.lotm.util.BeyonderUtil.setAir;

public class BlackHoleEntity extends AbstractHurtingProjectile implements GeoEntity {

    // Custom ChatFormatting for ORANGE
    private static final ChatFormatting ORANGE = ChatFormatting.getByCode('6');

    private static final EntityDataAccessor<Float> RING_ROTATION_X = SynchedEntityData.defineId(BlackHoleEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> RING_ROTATION_Y = SynchedEntityData.defineId(BlackHoleEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> RING_ROTATION_Z = SynchedEntityData.defineId(BlackHoleEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> RING_ROTATION_SPEED = SynchedEntityData.defineId(BlackHoleEntity.class, EntityDataSerializers.FLOAT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final Map<Integer, List<BlockPos>> SPHERE_RELATIVE_OFFSETS = new HashMap<>();
    private List<BlockPos> activeRelativeOffsets = null;
    private int offsetIndex = 0;
    private int cachedRadius = -1;

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

    public float getRingRotationX() { return this.entityData.get(RING_ROTATION_X); }
    public float getRingRotationY() { return this.entityData.get(RING_ROTATION_Y); }
    public float getRingRotationZ() { return this.entityData.get(RING_ROTATION_Z); }
    public float getRingRotationSpeed() { return this.entityData.get(RING_ROTATION_SPEED); }

    public void setRingRotationX(float rotation) { this.entityData.set(RING_ROTATION_X, rotation); }
    public void setRingRotationY(float rotation) { this.entityData.set(RING_ROTATION_Y, rotation); }
    public void setRingRotationZ(float rotation) { this.entityData.set(RING_ROTATION_Z, rotation); }
    public void setRingRotationSpeed(float speed) { this.entityData.set(RING_ROTATION_SPEED, speed); }

    @Override
    protected float getInertia() {
        return 0.99f;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) { return true; }

    @Override
    public boolean shouldRender(double pX, double pY, double pZ) { return true; }

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
    protected void onHit(HitResult pResult) { return; }

    @Override
    protected void onHitEntity(EntityHitResult pResult) { return; }

    @Override
    protected void onHitBlock(BlockHitResult pResult) { return; }

    @Override
    public boolean isOnFire() { return false; }

    @Override
    public @NotNull ParticleOptions getTrailParticle() { return ParticleInit.NULL_PARTICLE.get(); }

    @Override
    public boolean isPickable() { return false; }

    @Override
    protected boolean shouldBurn() { return false; }

    private List<BlockPos> getOrCreateRelativeSphereOffsets(int radius) {
        return SPHERE_RELATIVE_OFFSETS.computeIfAbsent(radius, r -> {
            List<BlockPos> rel = new ArrayList<>();
            int rSq = r * r;
            for (int x = -r; x <= r; x++) {
                for (int y = -r; y <= r; y++) {
                    for (int z = -r; z <= r; z++) {
                        if (x * x + y * y + z * z <= rSq) {
                            rel.add(new BlockPos(x, y, z));
                        }
                    }
                }
            }
            rel.sort((a, b) -> {
                int cmpY = Integer.compare(a.getY(), b.getY());
                if (cmpY != 0) return cmpY;
                int distASq = a.getX()*a.getX() + a.getZ()*a.getZ();
                int distBSq = b.getX()*b.getX() + b.getZ()*b.getZ();
                return Integer.compare(distASq, distBSq);
            });
            return rel;
        });
    }

    public static void destroyBlocksInSphere(Entity entity, BlockPos hitPos, double radius) {
        for (BlockPos pos : BlockPos.betweenClosed(
                hitPos.offset((int) -radius, (int) -radius, (int) -radius),
                hitPos.offset((int) radius, (int) radius, (int) radius))) {
            if (pos.distSqr(hitPos) <= radius * radius) {
                setAir(entity, pos);
            }
        }
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
            if (this.tickCount == 1) {
                destroyBlocksInSphere(this, this.getOnPos(), 50);
            }
            float currentRotationY = getRingRotationY();
            float rotationSpeed = getRingRotationSpeed();
            setRingRotationY((currentRotationY + rotationSpeed) % 360.0f);
            float scale = BeyonderUtil.getScale(this);
            double centerX = this.getX();
            double centerY = this.getY();
            double centerZ = this.getZ();
            AABB attractionBox = this.getBoundingBox().inflate(scale * 15);
            List<Entity> entities = this.level().getEntitiesOfClass(Entity.class, attractionBox);
            for (Entity entity : entities) {
                if (entity == this) continue;
                double dx = centerX - entity.getX();
                double dy = centerY - entity.getY();
                double dz = centerZ - entity.getZ();
                double distSq = dx*dx + dy*dy + dz*dz;
                double distance = Math.sqrt(distSq);
                if (distance <= 0.001) continue;
                double nx = dx / distance;
                double ny = dy / distance;
                double nz = dz / distance;

                if (!(entity instanceof LivingEntity living)) {
                    double pullStrength = Math.min(2.0, (scale * 15.0) / Math.max(1.0, distance));
                    Vec3 currentVelocity = entity.getDeltaMovement();
                    entity.setDeltaMovement(
                            currentVelocity.x + nx * pullStrength * 0.3,
                            currentVelocity.y + ny * pullStrength * 0.3,
                            currentVelocity.z + nz * pullStrength * 0.3
                    );

                    if (distance <= 2.0) {
                        entity.discard();
                    }
                } else {
                    if (BeyonderUtil.isImmuneToGravity(living)) continue;
                    double pullStrength = Math.min(1.5, (scale * 10.0) / Math.max(1.0, distance));
                    Vec3 currentVelocity = living.getDeltaMovement();
                    living.setDeltaMovement(
                            currentVelocity.x + nx * pullStrength * 0.2,
                            currentVelocity.y + ny * pullStrength * 0.15,
                            currentVelocity.z + nz * pullStrength * 0.2
                    );

                    if (distance <= 30) {
                        if (this.getOwner() != null) {
                            living.hurt(BeyonderUtil.genericSource(this.getOwner(), living), (float) (45 - distance));
                        }
                        BeyonderUtil.setGray(living, 60);
                    }
                    if (distance <= 10) {
                        BeyonderUtil.applyStun(living, 20);
                    }
                    if (distance <= 5 * BeyonderUtil.getScale(this)) {
                        BeyonderUtil.setInvisible(living, true, 40);
                    }
                }
            }

            if (this.tickCount >= 30) {
                int radius = (int) (scale * 12);
                if (radius < 1) {
                    radius = 1;
                }

                if (radius != cachedRadius || activeRelativeOffsets == null) {
                    activeRelativeOffsets = getOrCreateRelativeSphereOffsets(radius);
                    offsetIndex = 0;
                    cachedRadius = radius;
                }

                int maxBlocksPerTick = Math.max(1, Math.min(200, (int) (scale * 20)));
                int processed = 0;
                BlockPos centerBlockPos = this.blockPosition();
                while (processed < maxBlocksPerTick && !activeRelativeOffsets.isEmpty()) {
                    if (offsetIndex >= activeRelativeOffsets.size()) {
                        offsetIndex = 0;
                    }
                    BlockPos rel = activeRelativeOffsets.get(offsetIndex++);
                    BlockPos abs = centerBlockPos.offset(rel);

                    BlockState blockState = this.level().getBlockState(abs);

                    if (!blockState.isAir()) {
                        int dx = rel.getX(), dy = rel.getY(), dz = rel.getZ();
                        if (dx * dx + dy * dy + dz * dz <= radius * radius) {
                            DustParticleOptions dustParticle = new DustParticleOptions(new Vector3f(0.0f, 0.0f, 0.0f), 1.0f);
                            double particleX = abs.getX() + 0.5;
                            double particleY = abs.getY() + 0.5;
                            double particleZ = abs.getZ() + 0.5;
                            double dirX = centerX - particleX;
                            double dirY = centerY - particleY;
                            double dirZ = centerZ - particleZ;
                            double distance = Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
                            if (distance > 0) {
                                dirX /= distance;
                                dirY /= distance;
                                dirZ /= distance;
                                double step = 0.5;
                                int steps = (int) (distance / step);
                                for (int i = 0; i <= steps; i++) {
                                    double px = particleX + dirX * (i * step);
                                    double py = particleY + dirY * (i * step);
                                    double pz = particleZ + dirZ * (i * step);
                                    BeyonderUtil.sendParticles(this, dustParticle, px, py, pz, 0, 0, 0);
                                }
                            }
                            setAir(this, abs);
                        }
                    }

                    processed++;
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
