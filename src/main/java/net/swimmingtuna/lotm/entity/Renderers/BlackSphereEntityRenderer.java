package net.swimmingtuna.lotm.entity.Renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.entity.BlackSphereEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BlackSphereEntityRenderer extends EntityRenderer<BlackSphereEntity> {
    public static final ResourceLocation BLACK = new ResourceLocation(LOTM.MOD_ID, "textures/block/black.png");

    public BlackSphereEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(BlackSphereEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        float animationDuration = 60.0f;
        float currentTick = entity.tickCount + partialTick;
        float progress = Math.min(currentTick / animationDuration, 1.0f);
        float startScale = 20.0f;
        float endScale = 12.0f;
        float scale = startScale + (endScale - startScale) * progress;
        poseStack.scale(scale, scale, scale);
        renderCore(poseStack, bufferSource, packedLight);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    private void renderCore(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        RenderType renderType = RenderType.entitySolid(BLACK);
        VertexConsumer consumer = bufferSource.getBuffer(renderType);
        Matrix4f matrix4f = poseStack.last().pose();
        Matrix3f matrix3f = poseStack.last().normal();
        renderSphere(consumer, matrix4f, matrix3f, packedLight);
    }

    private void renderSphere(VertexConsumer consumer, Matrix4f matrix4f, Matrix3f matrix3f, int packedLight) {
        int segments = 16;
        for (int i = 0; i < segments; i++) {
            float lat1 = (float) (Math.PI * (-0.5 + (double) (i) / segments));
            float lat2 = (float) (Math.PI * (-0.5 + (double) (i + 1) / segments));
            for (int j = 0; j < segments * 2; j++) {
                float lon1 = (float) (2 * Math.PI * (double) j / (segments * 2));
                float lon2 = (float) (2 * Math.PI * (double) (j + 1) / (segments * 2));
                float x1 = (float) ((float) 0.5 * Math.cos(lat1) * Math.cos(lon1));
                float y1 = (float) ((float) 0.5 * Math.sin(lat1));
                float z1 = (float) ((float) 0.5 * Math.cos(lat1) * Math.sin(lon1));
                float x2 = (float) ((float) 0.5 * Math.cos(lat1) * Math.cos(lon2));
                float y2 = (float) ((float) 0.5 * Math.sin(lat1));
                float z2 = (float) ((float) 0.5 * Math.cos(lat1) * Math.sin(lon2));
                float x3 = (float) ((float) 0.5 * Math.cos(lat2) * Math.cos(lon2));
                float y3 = (float) ((float) 0.5 * Math.sin(lat2));
                float z3 = (float) ((float) 0.5 * Math.cos(lat2) * Math.sin(lon2));
                float x4 = (float) ((float) 0.5 * Math.cos(lat2) * Math.cos(lon1));
                float y4 = (float) ((float) 0.5 * Math.sin(lat2));
                float z4 = (float) ((float) 0.5 * Math.cos(lat2) * Math.sin(lon1));
                addVertex(consumer, matrix4f, matrix3f, x1, y1, z1, (float) 0.0, (float) 0.0, (float) 0.0, (float) 0.8, 0, 0, packedLight);
                addVertex(consumer, matrix4f, matrix3f, x2, y2, z2, (float) 0.0, (float) 0.0, (float) 0.0, (float) 0.8, 1, 0, packedLight);
                addVertex(consumer, matrix4f, matrix3f, x3, y3, z3, (float) 0.0, (float) 0.0, (float) 0.0, (float) 0.8, 1, 1, packedLight);
                addVertex(consumer, matrix4f, matrix3f, x4, y4, z4, (float) 0.0, (float) 0.0, (float) 0.0, (float) 0.8, 0, 1, packedLight);
            }
        }
    }

    private void addVertex(VertexConsumer consumer, Matrix4f matrix4f, Matrix3f matrix3f, float x, float y, float z, float red, float green, float blue, float alpha, float u, float v, int packedLight) {
        consumer.vertex(matrix4f, x, y, z)
                .color(red, green, blue, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(matrix3f, 0, 1, 0)
                .endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(BlackSphereEntity entity) {
        return BLACK;
    }

    @Override
    public boolean shouldRender(BlackSphereEntity pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        return true;
    }
}