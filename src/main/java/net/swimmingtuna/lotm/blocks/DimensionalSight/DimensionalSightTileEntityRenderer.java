package net.swimmingtuna.lotm.blocks.DimensionalSight;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.util.BeyonderUtil;

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
        LivingEntity scryTarget = BeyonderUtil.getClientLivingEntityFromUUID(tileEntity.getLevel(), tileEntity.scryUniqueID);
        poseStack.pushPose();
        try {
            Vec3 entityDisplayPos = tileEntity.getEntityDisplayPos();
            Vec3 displayCenter = tileEntity.getDisplayCenter();
            Vec3 relativePos = entityDisplayPos.subtract(displayCenter);
            Vec3 scaledPos = new Vec3(relativePos.x / DimensionalSightTileEntity.RENDER_SCALE, relativePos.y / DimensionalSightTileEntity.RENDER_SCALE, relativePos.z / DimensionalSightTileEntity.RENDER_SCALE);
            poseStack.translate(scaledPos.x, scaledPos.y, scaledPos.z);
            long gameTime = tileEntity.getLevel() != null ? tileEntity.getLevel().getGameTime() : 0;
            float glowIntensity = (float) (0.7 + 0.3 * Math.sin((gameTime + partialTicks) * 0.05));
            int magicalLight = Math.max(combinedLight, (int) (240 * glowIntensity));

            // Store original values
            float originalYaw = scryTarget.getYRot();
            float originalPitch = scryTarget.getXRot();
            float originalHeadYaw = scryTarget.yHeadRot;
            float originalBodyYaw = scryTarget.yBodyRot;
            Vec3 originalPos = scryTarget.position();
            Vec3 originalDelta = scryTarget.getDeltaMovement();
            float originalSwingProgress = scryTarget.attackAnim;
            boolean originalOnGround = scryTarget.onGround();
            float originalFallDistance = scryTarget.fallDistance;

            try {
                // Check if target is a player and has packet render data
                if (scryTarget instanceof Player && scryTarget.getPersistentData().contains("dimensionalSightRenderData")) {
                    LOTM.LOGGER.info("USING NEW THING");
                    CompoundTag renderData = scryTarget.getPersistentData().getCompound("dimensionalSightRenderData");

                    // Use packet data for rendering
                    scryTarget.setYRot(renderData.getFloat("displaYaw"));
                    scryTarget.setXRot(renderData.getFloat("displayPitch"));
                    scryTarget.yHeadRot = renderData.getFloat("displayHeadYaw");
                    scryTarget.yBodyRot = renderData.getFloat("displayRenderYaw");
                    scryTarget.setOldPosAndRot();
                    scryTarget.setDeltaMovement(
                            renderData.getDouble("displayVelX"),
                            renderData.getDouble("displayVelY"),
                            renderData.getDouble("displayVelZ")
                    );
                    scryTarget.attackAnim = renderData.getFloat("displaySwingProgress");
                    scryTarget.setOnGround(renderData.getBoolean("displayOnGround"));
                    scryTarget.fallDistance = renderData.getFloat("displayFallDistance");

                    // Optionally adjust position based on packet data
                    Vec3 packetDisplayCenter = new Vec3(
                            renderData.getDouble("displayCenterX"),
                            renderData.getDouble("displayCenterY"),
                            renderData.getDouble("displayCenterZ")
                    );
                    Vec3 packetEntityPos = new Vec3(
                            renderData.getDouble("displayEntityDisplayPosX"),
                            renderData.getDouble("displayEntityDisplayPosY"),
                            renderData.getDouble("displayEntityDisplayPosZ")
                    );

                     Vec3 packetRelativePos = packetEntityPos.subtract(packetDisplayCenter);
                     Vec3 packetScaledPos = new Vec3(packetRelativePos.x / DimensionalSightTileEntity.RENDER_SCALE, packetRelativePos.y / DimensionalSightTileEntity.RENDER_SCALE, packetRelativePos.z / DimensionalSightTileEntity.RENDER_SCALE);
                     poseStack.translate(packetScaledPos.x - scaledPos.x, packetScaledPos.y - scaledPos.y, packetScaledPos.z - scaledPos.z);

                } else {
                    scryTarget.setYRot(tileEntity.getYaw());
                    scryTarget.setXRot(tileEntity.getPitch());
                    scryTarget.yHeadRot = tileEntity.getHeadYaw();
                    scryTarget.yBodyRot = tileEntity.getRenderYaw();
                    scryTarget.setOldPosAndRot();
                    scryTarget.setDeltaMovement(tileEntity.getVelX(), tileEntity.getVelY(), tileEntity.getVelZ());
                    scryTarget.attackAnim = tileEntity.swingProgress;
                    scryTarget.setOnGround(true);
                    scryTarget.fallDistance = 0.0f;
                    if (!(scryTarget instanceof Player)) {
                        LOTM.LOGGER.info("SCRY TARGET NOT PLAYER");
                    } else if (scryTarget instanceof  Player && !scryTarget.getPersistentData().contains("dimensionalSightRenderData")) {
                        LOTM.LOGGER.info("DATA NOT FOUND");
                    }
                }

                EntityRenderer<? super LivingEntity> renderer = this.entityRenderer.getRenderer(scryTarget);
                renderer.render(scryTarget, 0.0F, partialTicks, poseStack, bufferSource, magicalLight);

            } catch (Exception ignored) {
            } finally {
                // Restore original values
                scryTarget.setYRot(originalYaw);
                scryTarget.setXRot(originalPitch);
                scryTarget.yHeadRot = originalHeadYaw;
                scryTarget.yBodyRot = originalBodyYaw;
                scryTarget.setPos(originalPos.x, originalPos.y, originalPos.z);
                scryTarget.setDeltaMovement(originalDelta);
                scryTarget.attackAnim = originalSwingProgress;
                scryTarget.setOnGround(originalOnGround);
                scryTarget.fallDistance = originalFallDistance;
            }
        } catch (Exception ignored) {
        } finally {
            poseStack.popPose();
        }
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