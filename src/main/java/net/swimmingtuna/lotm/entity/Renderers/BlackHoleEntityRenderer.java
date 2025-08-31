package net.swimmingtuna.lotm.entity.Renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.entity.BlackHoleEntity;
import net.swimmingtuna.lotm.util.LOTMRenderTypes;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BlackHoleEntityRenderer extends EntityRenderer<BlackHoleEntity> {
    public BlackHoleEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }
    public static final ResourceLocation BLACK_HOLE_RING = new ResourceLocation(LOTM.MOD_ID, "textures/entity/black_hole_ring.png");

    @Override
    public void render(BlackHoleEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        renderSphere(poseStack, buffer.getBuffer(LOTMRenderTypes.NO_CULL_SOLID), packedLight, 5, 5);
        poseStack.pushPose();
        float interpolatedY = entity.getRingRotationY() + (entity.getRingRotationSpeed() * partialTicks);
        poseStack.mulPose(Axis.XP.rotationDegrees(entity.getRingRotationX()));
        poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedY));
        poseStack.mulPose(Axis.ZP.rotationDegrees(entity.getRingRotationZ()));

        // Increased radial thickness from 1 to 4, making the ring extend from radius 5 to radius 9
        // Added vertical thickness parameter (0.5f) to give the ring some depth
        renderOrangeSphere(poseStack, buffer.getBuffer(LOTMRenderTypes.BLACK_HOLE_RING), packedLight, 9, 4, 0.75f, 12);
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

    public static void renderOrangeSphere(PoseStack poseStack, VertexConsumer buffer, int packedLight, float radius, float radialThickness, float verticalThickness, int sides) {
        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        float angleStep = (float) (2 * Math.PI / sides);

        // Render the ring as a series of quads with vertical thickness
        for (int i = 0; i < sides; i++) {
            float angle1 = i * angleStep;
            float angle2 = (i + 1) * angleStep;

            // Outer vertices
            float x1Outer = (float) Math.cos(angle1) * radius;
            float z1Outer = (float) Math.sin(angle1) * radius;
            float x2Outer = (float) Math.cos(angle2) * radius;
            float z2Outer = (float) Math.sin(angle2) * radius;

            // Inner vertices
            float x1Inner = (float) Math.cos(angle1) * (radius - radialThickness);
            float z1Inner = (float) Math.sin(angle1) * (radius - radialThickness);
            float x2Inner = (float) Math.cos(angle2) * (radius - radialThickness);
            float z2Inner = (float) Math.sin(angle2) * (radius - radialThickness);

            float yTop = verticalThickness / 2.0f;
            float yBottom = -verticalThickness / 2.0f;

            // Top face of the ring
            buffer.vertex(matrix, x1Outer, yTop, z1Outer)
                    .color(255, 165, 0, 255) // Orange color
                    .uv(0.0f, 0.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight)
                    .normal(normal, 0.0f, 1.0f, 0.0f)
                    .endVertex();

            buffer.vertex(matrix, x1Inner, yTop, z1Inner)
                    .color(255, 165, 0, 255)
                    .uv(1.0f, 0.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight)
                    .normal(normal, 0.0f, 1.0f, 0.0f)
                    .endVertex();

            buffer.vertex(matrix, x2Inner, yTop, z2Inner)
                    .color(255, 165, 0, 255)
                    .uv(1.0f, 1.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight)
                    .normal(normal, 0.0f, 1.0f, 0.0f)
                    .endVertex();

            buffer.vertex(matrix, x2Outer, yTop, z2Outer)
                    .color(255, 165, 0, 255)
                    .uv(0.0f, 1.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight)
                    .normal(normal, 0.0f, 1.0f, 0.0f)
                    .endVertex();

            // Bottom face of the ring
            buffer.vertex(matrix, x2Outer, yBottom, z2Outer)
                    .color(255, 165, 0, 255)
                    .uv(0.0f, 1.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight)
                    .normal(normal, 0.0f, -1.0f, 0.0f)
                    .endVertex();

            buffer.vertex(matrix, x2Inner, yBottom, z2Inner)
                    .color(255, 165, 0, 255)
                    .uv(1.0f, 1.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight)
                    .normal(normal, 0.0f, -1.0f, 0.0f)
                    .endVertex();

            buffer.vertex(matrix, x1Inner, yBottom, z1Inner)
                    .color(255, 165, 0, 255)
                    .uv(1.0f, 0.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight)
                    .normal(normal, 0.0f, -1.0f, 0.0f)
                    .endVertex();

            buffer.vertex(matrix, x1Outer, yBottom, z1Outer)
                    .color(255, 165, 0, 255)
                    .uv(0.0f, 0.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight)
                    .normal(normal, 0.0f, -1.0f, 0.0f)
                    .endVertex();

            // Outer edge of the ring
            buffer.vertex(matrix, x1Outer, yTop, z1Outer)
                    .color(255, 165, 0, 255)
                    .uv(0.0f, 0.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight)
                    .normal(normal, x1Outer / radius, 0.0f, z1Outer / radius)
                    .endVertex();

            buffer.vertex(matrix, x2Outer, yTop, z2Outer)
                    .color(255, 165, 0, 255)
                    .uv(1.0f, 0.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight)
                    .normal(normal, x2Outer / radius, 0.0f, z2Outer / radius)
                    .endVertex();

            buffer.vertex(matrix, x2Outer, yBottom, z2Outer)
                    .color(255, 165, 0, 255)
                    .uv(1.0f, 1.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight)
                    .normal(normal, x2Outer / radius, 0.0f, z2Outer / radius)
                    .endVertex();

            buffer.vertex(matrix, x1Outer, yBottom, z1Outer)
                    .color(255, 165, 0, 255)
                    .uv(0.0f, 1.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight)
                    .normal(normal, x1Outer / radius, 0.0f, z1Outer / radius)
                    .endVertex();

            // Inner edge of the ring
            float innerRadius = radius - radialThickness;
            buffer.vertex(matrix, x1Inner, yBottom, z1Inner)
                    .color(255, 165, 0, 255)
                    .uv(0.0f, 1.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight)
                    .normal(normal, -x1Inner / innerRadius, 0.0f, -z1Inner / innerRadius)
                    .endVertex();

            buffer.vertex(matrix, x2Inner, yBottom, z2Inner)
                    .color(255, 165, 0, 255)
                    .uv(1.0f, 1.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight)
                    .normal(normal, -x2Inner / innerRadius, 0.0f, -z2Inner / innerRadius)
                    .endVertex();

            buffer.vertex(matrix, x2Inner, yTop, z2Inner)
                    .color(255, 165, 0, 255)
                    .uv(1.0f, 0.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight)
                    .normal(normal, -x2Inner / innerRadius, 0.0f, -z2Inner / innerRadius)
                    .endVertex();

            buffer.vertex(matrix, x1Inner, yTop, z1Inner)
                    .color(255, 165, 0, 255)
                    .uv(0.0f, 0.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight)
                    .normal(normal, -x1Inner / innerRadius, 0.0f, -z1Inner / innerRadius)
                    .endVertex();
        }
    }

    private void addVertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normalMatrix, float x, float y, float z, int packedLight) {
        consumer.vertex(matrix, x, y, z)
                .color(0, 0, 0, 255)
                .uv(0, 0)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normalMatrix, 0, 1, 0)
                .endVertex();
    }

    private void addOrangeVertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normalMatrix, float x, float y, float z, int packedLight) {
        consumer.vertex(matrix, x, y, z)
                .color(255, 165, 0, 200)
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

    @Override
    public boolean shouldRender(BlackHoleEntity pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        return true;
    }
}