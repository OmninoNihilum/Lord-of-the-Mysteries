package net.swimmingtuna.lotm.entity.Renderers;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.entity.BlackHoleEntity;
import net.swimmingtuna.lotm.entity.BlackHoleOuterGlowEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BlackHoleEntityGlowRenderer extends EntityRenderer<BlackHoleOuterGlowEntity> {

    public static final ResourceLocation BLACK = new ResourceLocation(LOTM.MOD_ID, "textures/block/black.png");

    public BlackHoleEntityGlowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(BlackHoleOuterGlowEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        float scale = 12.0f;
        poseStack.scale(scale, scale, scale);
        float interpolatedY = entity.getRingRotationY() + (entity.getRingRotationSpeed() * partialTick);
        poseStack.mulPose(Axis.XP.rotationDegrees(entity.getRingRotationX()));
        poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedY));
        poseStack.mulPose(Axis.ZP.rotationDegrees(entity.getRingRotationZ()));

        renderOuterGlow(poseStack, bufferSource);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    private void renderOuterGlow(PoseStack poseStack, MultiBufferSource bufferSource) {
        RenderType renderType = RenderType.entityTranslucent(BLACK);
        VertexConsumer consumer = bufferSource.getBuffer(renderType);
        Matrix4f matrix4f = poseStack.last().pose();
        Matrix3f matrix3f = poseStack.last().normal();
        float outerGlowRadius = 3.0f;
        float red = 0.5f;
        float green = 0.5f;
        float blue = 0.5f;
        float alpha = 0.3f;
        int packedLight = 15728880;
        renderSphere(consumer, matrix4f, matrix3f, outerGlowRadius, red, green, blue, alpha, packedLight);
    }

    private void renderSphere(VertexConsumer consumer, Matrix4f matrix4f, Matrix3f matrix3f, float radius, float red, float green, float blue, float alpha, int packedLight) {

        int segments = 16;
        for (int i = 0; i < segments; i++) {
            float lat1 = (float) (Math.PI * (-0.5 + (double) (i) / segments));
            float lat2 = (float) (Math.PI * (-0.5 + (double) (i + 1) / segments));
            for (int j = 0; j < segments * 2; j++) {
                float lon1 = (float) (2 * Math.PI * (double) j / (segments * 2));
                float lon2 = (float) (2 * Math.PI * (double) (j + 1) / (segments * 2));
                float x1 = (float) (radius * Math.cos(lat1) * Math.cos(lon1));
                float y1 = (float) (radius * Math.sin(lat1));
                float z1 = (float) (radius * Math.cos(lat1) * Math.sin(lon1));
                float x2 = (float) (radius * Math.cos(lat1) * Math.cos(lon2));
                float y2 = (float) (radius * Math.sin(lat1));
                float z2 = (float) (radius * Math.cos(lat1) * Math.sin(lon2));
                float x3 = (float) (radius * Math.cos(lat2) * Math.cos(lon2));
                float y3 = (float) (radius * Math.sin(lat2));
                float z3 = (float) (radius * Math.cos(lat2) * Math.sin(lon2));
                float x4 = (float) (radius * Math.cos(lat2) * Math.cos(lon1));
                float y4 = (float) (radius * Math.sin(lat2));
                float z4 = (float) (radius * Math.cos(lat2) * Math.sin(lon1));
                addVertex(consumer, matrix4f, matrix3f, x1, y1, z1, red, green, blue, alpha, 0, 0, packedLight);
                addVertex(consumer, matrix4f, matrix3f, x2, y2, z2, red, green, blue, alpha, 1, 0, packedLight);
                addVertex(consumer, matrix4f, matrix3f, x3, y3, z3, red, green, blue, alpha, 1, 1, packedLight);
                addVertex(consumer, matrix4f, matrix3f, x4, y4, z4, red, green, blue, alpha, 0, 1, packedLight);
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
    public ResourceLocation getTextureLocation(BlackHoleOuterGlowEntity entity) {
        return BLACK;
    }

    @Override
    public boolean shouldRender(BlackHoleOuterGlowEntity pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        return true;
    }
}