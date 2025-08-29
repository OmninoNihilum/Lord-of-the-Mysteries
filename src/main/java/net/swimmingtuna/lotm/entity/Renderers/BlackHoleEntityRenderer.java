package net.swimmingtuna.lotm.entity.Renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.swimmingtuna.lotm.entity.BlackHoleEntity;
import net.swimmingtuna.lotm.util.LOTMRenderTypes;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BlackHoleEntityRenderer extends EntityRenderer<BlackHoleEntity> {
    public BlackHoleEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(BlackHoleEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        // Render main black hole sphere (keep original black color)
        renderSphere(poseStack, buffer.getBuffer(LOTMRenderTypes.NO_CULL_SOLID), packedLight, 5, 5);

        // Render orange ring offset from the main body - now horizontal and using entity data
        poseStack.pushPose();

        // Apply rotations from entity data
        poseStack.mulPose(Axis.XP.rotationDegrees(entity.getRingRotationX()));
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getRingRotationY()));
        poseStack.mulPose(Axis.ZP.rotationDegrees(entity.getRingRotationZ()));

        renderOrangeSphere(poseStack, buffer.getBuffer(LOTMRenderTypes.END_PORTAL_NO_CULL), packedLight, 9, 1); // Larger radius (9) and thicker (1) for space and thickness
        poseStack.popPose();

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void renderSphere(PoseStack poseStack, VertexConsumer consumer, int packedLight, int width, int height) {
        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normalMatrix = poseStack.last().normal();

        int slices = 32;
        int stacks = 32;

        for (int i = 0; i < stacks; i++) {
            float lat0 = (float) Math.PI * (-0.5f + (float) i / stacks);
            float z0 = (float) Math.sin(lat0);
            float zr0 = (float) Math.cos(lat0);

            float lat1 = (float) Math.PI * (-0.5f + (float) (i + 1) / stacks);
            float z1 = (float) Math.sin(lat1);
            float zr1 = (float) Math.cos(lat1);

            for (int j = 0; j < slices; j++) {
                float lng0 = (float) (2 * Math.PI * j / slices);
                float x0 = (float) Math.cos(lng0);
                float y0 = (float) Math.sin(lng0);

                float lng1 = (float) (2 * Math.PI * (j + 1) / slices);
                float x1 = (float) Math.cos(lng1);
                float y1 = (float) Math.sin(lng1);

                addVertex(consumer, matrix, normalMatrix, width * x0 * zr0, height * y0 * zr0, width * z0, packedLight);
                addVertex(consumer, matrix, normalMatrix, width * x1 * zr0, height * y1 * zr0, width * z0, packedLight);
                addVertex(consumer, matrix, normalMatrix, width * x1 * zr1, height * y1 * zr1, width * z1, packedLight);
                addVertex(consumer, matrix, normalMatrix, width * x0 * zr1, height * y0 * zr1, width * z1, packedLight);
            }
        }
    }

    private void renderOrangeSphere(PoseStack poseStack, VertexConsumer consumer, int packedLight, int width, int height) {
        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normalMatrix = poseStack.last().normal();

        int slices = 32;
        int stacks = 32;

        for (int i = 0; i < stacks; i++) {
            float lat0 = (float) Math.PI * (-0.5f + (float) i / stacks);
            float z0 = (float) Math.sin(lat0);
            float zr0 = (float) Math.cos(lat0);

            float lat1 = (float) Math.PI * (-0.5f + (float) (i + 1) / stacks);
            float z1 = (float) Math.sin(lat1);
            float zr1 = (float) Math.cos(lat1);

            for (int j = 0; j < slices; j++) {
                float lng0 = (float) (2 * Math.PI * j / slices);
                float x0 = (float) Math.cos(lng0);
                float y0 = (float) Math.sin(lng0);

                float lng1 = (float) (2 * Math.PI * (j + 1) / slices);
                float x1 = (float) Math.cos(lng1);
                float y1 = (float) Math.sin(lng1);

                addOrangeVertex(consumer, matrix, normalMatrix, width * x0 * zr0, height * y0 * zr0, width * z0, packedLight);
                addOrangeVertex(consumer, matrix, normalMatrix, width * x1 * zr0, height * y1 * zr0, width * z0, packedLight);
                addOrangeVertex(consumer, matrix, normalMatrix, width * x1 * zr1, height * y1 * zr1, width * z1, packedLight);
                addOrangeVertex(consumer, matrix, normalMatrix, width * x0 * zr1, height * y0 * zr1, width * z1, packedLight);
            }
        }
    }

    private void addVertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normalMatrix,
                           float x, float y, float z, int packedLight) {
        consumer.vertex(matrix, x, y, z)
                .color(0, 0, 0, 255)
                .uv(0, 0)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normalMatrix, 0, 1, 0)
                .endVertex();
    }

    private void addOrangeVertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normalMatrix,
                                 float x, float y, float z, int packedLight) {
        consumer.vertex(matrix, x, y, z)
                .color(255, 165, 0, 200) // Orange color with some transparency
                .uv(0, 0)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normalMatrix, 0, 1, 0)
                .endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(BlackHoleEntity blackHoleEntity) {
        return null;
    }
}