package net.swimmingtuna.lotm.blocks.DimensionalSight;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.util.BeyonderUtil;

import java.util.List;
import java.util.UUID;

public class DimensionalSightTileEntityRenderer implements BlockEntityRenderer<DimensionalSightTileEntity> {

    private final BlockRenderDispatcher blockRenderer;
    private final EntityRenderDispatcher entityRenderer;

    public DimensionalSightTileEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = Minecraft.getInstance().getBlockRenderer();
        this.entityRenderer = Minecraft.getInstance().getEntityRenderDispatcher();
    }

    @Override
    public void render(DimensionalSightTileEntity tileEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        poseStack.pushPose();
        try {
            Vec3 displayCenter = tileEntity.getDisplayCenter();
            Vec3 tilePos = new Vec3(tileEntity.getBlockPos().getX() + 0.5, tileEntity.getBlockPos().getY(), tileEntity.getBlockPos().getZ() + 0.5);
            Vec3 offset = displayCenter.subtract(tilePos);
            poseStack.translate(offset.x, offset.y, offset.z);
            poseStack.scale(DimensionalSightTileEntity.RENDER_SCALE, DimensionalSightTileEntity.RENDER_SCALE, DimensionalSightTileEntity.RENDER_SCALE);
            renderScryBlocks(tileEntity, poseStack, bufferSource, combinedLight, combinedOverlay);
            renderScryEntity(tileEntity, partialTicks, poseStack, bufferSource, combinedLight);
        } catch (Exception ignored) {
        } finally {
            poseStack.popPose();
        }
    }

    private void renderScryBlocks(DimensionalSightTileEntity tileEntity, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {

        for (DimensionalSightTileEntity.BlockPosInfo blockInfo : tileEntity.getScryBlocks()) {
            if (blockInfo == null) {
                continue;
            }
            if (blockInfo.state == null || blockInfo.state.getBlock() == Blocks.AIR) {
                continue;
            }
            poseStack.pushPose();
            try {
                poseStack.translate(blockInfo.relativeX, blockInfo.relativeY, blockInfo.relativeZ);
                try {
                    this.blockRenderer.renderSingleBlock(blockInfo.state, poseStack, bufferSource, combinedLight, combinedOverlay, ModelData.EMPTY, RenderType.translucent());
                } catch (Exception ignored) {
                }
            } catch (Exception ignored) {
            } finally {
                poseStack.popPose();
            }
        }
    }

    private void renderScryEntity(DimensionalSightTileEntity tileEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight) {
        LivingEntity scryTarget = getClientLivingEntityFromUUID(tileEntity.getLevel(), tileEntity.scryUniqueID);
        poseStack.pushPose();
        if (scryTarget == null || !scryTarget.isAlive() || scryTarget.isRemoved()) {
            return;
        }
        try {
            Vec3 entityDisplayPos = tileEntity.getEntityDisplayPos();
            Vec3 displayCenter = tileEntity.getDisplayCenter();
            Vec3 relativePos = entityDisplayPos.subtract(displayCenter);
            Vec3 scaledPos = new Vec3(relativePos.x / DimensionalSightTileEntity.RENDER_SCALE, relativePos.y / DimensionalSightTileEntity.RENDER_SCALE, relativePos.z / DimensionalSightTileEntity.RENDER_SCALE);
            poseStack.translate(scaledPos.x, scaledPos.y, scaledPos.z);
            long gameTime = tileEntity.getLevel() != null ? tileEntity.getLevel().getGameTime() : 0;
            float glowIntensity = (float) (0.7 + 0.3 * Math.sin((gameTime + partialTicks) * 0.05));
            int magicalLight = Math.max(combinedLight, (int) (240 * glowIntensity));
            try {
                if (scryTarget instanceof Player && scryTarget.getPersistentData().contains("dimensionalSightRenderData")) {
                    EntityType<?> entityType = scryTarget.getType();
                    Entity newEntity = entityType.create(scryTarget.level());
                    CompoundTag renderData = scryTarget.getPersistentData().getCompound("dimensionalSightRenderData");
                    if (newEntity instanceof LivingEntity livingRenderTarget) {
                        livingRenderTarget.setYRot(renderData.getFloat("displayYaw"));
                        livingRenderTarget.setXRot(renderData.getFloat("displayPitch"));
                        livingRenderTarget.setYHeadRot(renderData.getFloat("displayHeadYaw"));
                        livingRenderTarget.setYBodyRot(renderData.getFloat("displayHeadYaw"));
                        livingRenderTarget.setOldPosAndRot();
                        livingRenderTarget.setDeltaMovement(renderData.getDouble("displayVelX"), renderData.getDouble("displayVelY"), renderData.getDouble("displayVelZ"));
                        livingRenderTarget.attackAnim = renderData.getFloat("displaySwingProgress");
                        livingRenderTarget.setOnGround(renderData.getBoolean("displayOnGround"));
                        livingRenderTarget.fallDistance = renderData.getFloat("displayFallDistance");
                    }
                    Vec3 packetDisplayCenter = new Vec3(renderData.getDouble("displayCenterX"), renderData.getDouble("displayCenterY"), renderData.getDouble("displayCenterZ"));
                    Vec3 packetEntityPos = new Vec3(renderData.getDouble("displayEntityDisplayPosX"), renderData.getDouble("displayEntityDisplayPosY"), renderData.getDouble("displayEntityDisplayPosZ"));
                    Vec3 packetRelativePos = packetEntityPos.subtract(packetDisplayCenter);
                    Vec3 packetScaledPos = new Vec3(packetRelativePos.x / DimensionalSightTileEntity.RENDER_SCALE, packetRelativePos.y / DimensionalSightTileEntity.RENDER_SCALE, packetRelativePos.z / DimensionalSightTileEntity.RENDER_SCALE);
                    poseStack.translate(packetScaledPos.x - scaledPos.x, packetScaledPos.y - scaledPos.y, packetScaledPos.z - scaledPos.z);
                } else {
                    EntityType<?> entityType = scryTarget.getType();
                    Entity newEntity = entityType.create(scryTarget.level());
                    if (newEntity instanceof LivingEntity living) {
                        living.setYRot(tileEntity.getYaw());
                        living.setXRot(tileEntity.getPitch());
                        living.yHeadRot = tileEntity.getHeadYaw();
                        living.yBodyRot = tileEntity.getRenderYaw();
                        living.setPos(0, 0, 0);
                        living.xo = 0; living.yo = 0; living.zo = 0;
                        living.setOldPosAndRot();
                        living.yRotO = living.getYRot();
                        living.xRotO = living.getXRot();
                        living.yHeadRotO = living.yHeadRot;
                        living.yBodyRotO = living.yBodyRot;
                        living.walkAnimation.setSpeed(scryTarget.walkAnimation.speed());
                        living.walkAnimation.position = scryTarget.walkAnimation.position();
                        living.attackAnim = tileEntity.swingProgress;
                        living.oAttackAnim = scryTarget.oAttackAnim;
                        living.setDeltaMovement(tileEntity.getVelX(), tileEntity.getVelY(), tileEntity.getVelZ());
                        living.tickCount = scryTarget.tickCount;
                        living.setOnGround(true);
                        living.fallDistance = 0.0f;
                        living.hurtTime = scryTarget.hurtTime;
                        living.hurtDuration = scryTarget.hurtDuration;
                        living.deathTime = scryTarget.deathTime;
                        EntityRenderer<? super LivingEntity> renderer = this.entityRenderer.getRenderer(scryTarget);
                        renderer.render(scryTarget, 0.0F, partialTicks, poseStack, bufferSource, magicalLight);
                    }
                }
            } catch (Exception ignored) {
            }
        } catch (Exception ignored) {
        } finally {
            poseStack.popPose();
        }
    }

    public static LivingEntity getClientLivingEntityFromUUID(Level level, UUID uuid) {
        if (level instanceof ClientLevel clientLevel) {
            for (Entity entity : clientLevel.entitiesForRendering()) {
                if (entity instanceof LivingEntity livingEntity && uuid.equals(entity.getUUID())) {
                    return livingEntity;
                }
            }
            if (level.isClientSide() && Minecraft.getInstance().player != null) {
                Player player = Minecraft.getInstance().player;
                int renderDistance = Minecraft.getInstance().options.renderDistance().get() * 16;
                AABB searchArea = new AABB(player.getX() - renderDistance, player.getY() - 128, player.getZ() - renderDistance, player.getX() + renderDistance, player.getY() + 128, player.getZ() + renderDistance);
                List<Entity> nearbyEntities = level.getEntities((Entity) null, searchArea, entity -> true);
                for (Entity entity : nearbyEntities) {
                    if (entity instanceof LivingEntity livingEntity && uuid.equals(entity.getUUID())) {
                        return livingEntity;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public boolean shouldRenderOffScreen(DimensionalSightTileEntity blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 502;
    }
}