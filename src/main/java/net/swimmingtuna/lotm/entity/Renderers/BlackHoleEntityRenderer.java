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
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BlackHoleEntityRenderer extends EntityRenderer<BlackHoleEntity> {

    public boolean SHOULD_RENDER_GLOW = true;
    public static final ResourceLocation BLACK_HOLE_RING = new ResourceLocation(LOTM.MOD_ID, "textures/entity/black_hole_ring.png");

    public BlackHoleEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(BlackHoleEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        float scale = 12.0f;
        poseStack.scale(scale, scale, scale);
        float interpolatedY = entity.getRingRotationY() + (entity.getRingRotationSpeed() * partialTick);
        poseStack.mulPose(Axis.XP.rotationDegrees(entity.getRingRotationX()));
        poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedY));
        poseStack.mulPose(Axis.ZP.rotationDegrees(entity.getRingRotationZ()));

        renderCore(poseStack, bufferSource, packedLight);

        renderGlowingRing(poseStack, bufferSource, packedLight, partialTick);

        if (SHOULD_RENDER_GLOW) {
            renderOuterGlow(poseStack, bufferSource);
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    private void renderCore(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        RenderType renderType = RenderType.entitySolid(BLACK_HOLE_RING);
        VertexConsumer consumer = bufferSource.getBuffer(renderType);

        Matrix4f matrix4f = poseStack.last().pose();
        Matrix3f matrix3f = poseStack.last().normal();

        renderSphere(consumer, matrix4f, matrix3f, 0.5f, 0.0f, 0.0f, 0.0f, 0.8f, packedLight);
    }

    private void renderGlowingRing(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick) {
        RenderType renderType = createGlowingRenderType();
        VertexConsumer consumer = bufferSource.getBuffer(renderType);

        Matrix4f matrix4f = poseStack.last().pose();
        Matrix3f matrix3f = poseStack.last().normal();

        float[] ringRadii = {1.2f, 1.4f, 1.6f, 1.8f};
        float[] alphas = {1.0f, 0.9f, 0.8f, 0.7f};

        for (int i = 0; i < ringRadii.length; i++) {
            renderRing(consumer, matrix4f, matrix3f, ringRadii[i], alphas[i]);
        }
    }

    private void renderOuterGlow(PoseStack poseStack, MultiBufferSource bufferSource) {
        RenderType renderType = createGlowingSphereRenderType();
        VertexConsumer consumer = bufferSource.getBuffer(renderType);
        Matrix4f matrix4f = poseStack.last().pose();
        Matrix3f matrix3f = poseStack.last().normal();
        float outerGlowSize = 3.0f;
        renderSphere(consumer, matrix4f, matrix3f, outerGlowSize, 0.5f, 0.5f, 0.5f, 0.8f, 15728880);

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

    private void renderRing(VertexConsumer consumer, Matrix4f matrix4f, Matrix3f matrix3f, float radius, float alpha) {
        float thickness = 0.4f;
        int segments = 12;

        for (int i = 0; i < segments; i++) {
            float angle1 = (float) (2 * Math.PI * i / segments);
            float angle2 = (float) (2 * Math.PI * (i + 1) / segments);
            float x1Inner = (float) ((radius - thickness) * Math.cos(angle1));
            float z1Inner = (float) ((radius - thickness) * Math.sin(angle1));
            float x2Inner = (float) ((radius - thickness) * Math.cos(angle2));
            float z2Inner = (float) ((radius - thickness) * Math.sin(angle2));
            float x1Outer = (float) ((radius + thickness) * Math.cos(angle1));
            float z1Outer = (float) ((radius + thickness) * Math.sin(angle1));
            float x2Outer = (float) ((radius + thickness) * Math.cos(angle2));
            float z2Outer = (float) ((radius + thickness) * Math.sin(angle2));
            addVertex(consumer, matrix4f, matrix3f, x1Inner, 0, z1Inner, (float) 1.0, (float) 1.0, (float) 0, alpha, 0, 0, 15728880);
            addVertex(consumer, matrix4f, matrix3f, x1Outer, 0, z1Outer, (float) 1.0, (float) 1.0, (float) 0, alpha, 1, 0, 15728880);
            addVertex(consumer, matrix4f, matrix3f, x2Outer, 0, z2Outer, (float) 1.0, (float) 1.0, (float) 0, alpha, 1, 1, 15728880);
            addVertex(consumer, matrix4f, matrix3f, x2Inner, 0, z2Inner, (float) 1.0, (float) 1.0, (float) 0, alpha, 0, 1, 15728880);
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

    private static final RenderType GLOW_RENDER_TYPE = RenderType.create(
            "black_hole_glow",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            256,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEyesShader))
                    .setTextureState(new RenderStateShard.EmptyTextureStateShard(() -> {}, () -> {}))
                    .setTransparencyState(new RenderStateShard.TransparencyStateShard("additive_transparency",
                            () -> {
                                RenderSystem.enableBlend();
                                RenderSystem.blendFuncSeparate(
                                        GlStateManager.SourceFactor.SRC_ALPHA,
                                        GlStateManager.DestFactor.ONE,
                                        GlStateManager.SourceFactor.ONE,
                                        GlStateManager.DestFactor.ONE
                                );
                            },
                            () -> {
                                RenderSystem.disableBlend();
                                RenderSystem.defaultBlendFunc();
                            }))
                    .setCullState(new RenderStateShard.CullStateShard(false))
                    .setWriteMaskState(new RenderStateShard.WriteMaskStateShard(true, false))
                    .createCompositeState(false)
    );

    private static final RenderType GLOW_SPHERE_RENDER_TYPE = RenderType.create(
            "black_hole_glow",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            256,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEyesShader))
                    .setTextureState(new RenderStateShard.EmptyTextureStateShard(() -> {}, () -> {}))
                    .setTransparencyState(new RenderStateShard.TransparencyStateShard("additive_transparency",
                            () -> {
                                RenderSystem.enableBlend();
                                RenderSystem.blendFuncSeparate(
                                        GlStateManager.SourceFactor.SRC_ALPHA,
                                        GlStateManager.DestFactor.ONE,
                                        GlStateManager.SourceFactor.ONE,
                                        GlStateManager.DestFactor.ONE
                                );
                            },
                            () -> {
                                RenderSystem.disableBlend();
                                RenderSystem.defaultBlendFunc();
                            }))
                    .setCullState(new RenderStateShard.CullStateShard(false))
                    .setWriteMaskState(new RenderStateShard.WriteMaskStateShard(true, false))
                    .createCompositeState(false)
    );


    private RenderType createGlowingRenderType() {
        return GLOW_RENDER_TYPE;
    }

    private RenderType createGlowingSphereRenderType() {
        return GLOW_SPHERE_RENDER_TYPE;
    }

    @Override
    public ResourceLocation getTextureLocation(BlackHoleEntity entity) {
        return BLACK_HOLE_RING;
    }

    @Override
    public boolean shouldRender(BlackHoleEntity pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        return true;
    }
}