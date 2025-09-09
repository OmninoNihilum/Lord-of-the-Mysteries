package net.swimmingtuna.lotm.entity.Renderers;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.entity.StarfallEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

public class StarfallEntityRenderer extends EntityRenderer<StarfallEntity> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(LOTM.MOD_ID, "textures/entity/colored_box.png");

    public StarfallEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
    final static int[] size = {2, 2};

    @Override
    public void render(StarfallEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        StarfallEntity.ColorMode mode = entity.getColorModeEnum();
        renderTrail(entity, poseStack, buffer, packedLight, partialTicks, mode);
        renderSphere(poseStack, buffer.getBuffer(RENDER_TYPE), packedLight, size[0], size[1], mode);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void renderTrail(StarfallEntity entity, PoseStack poseStack, MultiBufferSource buffer, int packedLight, float partialTicks, StarfallEntity.ColorMode colorMode) {
        List<StarfallEntity.TrailPoint> trailPoints = entity.getTrailPoints();
        if (trailPoints.size() < 2) return;
        VertexConsumer consumer = buffer.getBuffer(TRAIL_RENDER_TYPE);
        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normalMatrix = poseStack.last().normal();
        Vec3 entityPos = entity.position().add(0, entity.getBbHeight() / 2, 0);
        for (int i = 0; i < trailPoints.size() - 1; i++) {
            StarfallEntity.TrailPoint current = trailPoints.get(i);
            StarfallEntity.TrailPoint next = trailPoints.get(i + 1);
            float currentAlpha = current.getAlpha() * 0.7f;
            float nextAlpha = next.getAlpha() * 0.7f;
            float currentWidth = (float) (0.8f * (1.0f - (double)i / trailPoints.size()));
            float nextWidth = (float) (0.8f * (1.0f - (double)(i + 1) / trailPoints.size()));
            Vec3 currentRelPos = current.position.subtract(entityPos);
            Vec3 nextRelPos = next.position.subtract(entityPos);
            renderTrailSegment(consumer, matrix, normalMatrix, currentRelPos, nextRelPos, currentWidth, nextWidth, currentAlpha, nextAlpha, colorMode, packedLight);
        }
    }

    private void renderTrailSegment(VertexConsumer consumer, Matrix4f matrix, Matrix3f normalMatrix, Vec3 pos1, Vec3 pos2, float width1, float width2, float alpha1, float alpha2, StarfallEntity.ColorMode colorMode, int packedLight) {
        Vec3 direction = pos2.subtract(pos1).normalize();
        Vec3 up = new Vec3(0, 1, 0);
        Vec3 right = direction.cross(up).normalize();
        if (right.lengthSqr() < 0.01) {
            right = direction.cross(new Vec3(0, 0, 1)).normalize();
        }
        Vec3 p1Left = pos1.add(right.scale(width1));
        Vec3 p1Right = pos1.subtract(right.scale(width1));
        Vec3 p2Left = pos2.add(right.scale(width2));
        Vec3 p2Right = pos2.subtract(right.scale(width2));
        addTrailVertex(consumer, matrix, normalMatrix, p1Left, colorMode, alpha1, 0.0f, 0.0f, packedLight);
        addTrailVertex(consumer, matrix, normalMatrix, p1Right, colorMode, alpha1, 1.0f, 0.0f, packedLight);
        addTrailVertex(consumer, matrix, normalMatrix, p2Right, colorMode, alpha2, 1.0f, 1.0f, packedLight);
        addTrailVertex(consumer, matrix, normalMatrix, p2Left, colorMode, alpha2, 0.0f, 1.0f, packedLight);
    }

    private void addTrailVertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normalMatrix, Vec3 pos, StarfallEntity.ColorMode color, float alpha, float u, float v, int packedLight) {
        Vector3f normal = new Vector3f(0, 0, 1);
        consumer.vertex(matrix, (float)pos.x, (float)pos.y, (float)pos.z)
                .color(color.r, color.g, color.b, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normalMatrix, normal.x, normal.y, normal.z)
                .endVertex();
    }

    private void renderSphere(PoseStack poseStack, VertexConsumer consumer, int packedLight, int width, int height, StarfallEntity.ColorMode colorMode) {
        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normalMatrix = poseStack.last().normal();

        int slices = 16;
        int stacks = 16;
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
                addVertex(consumer, matrix, normalMatrix, width * x0 * zr0, height * y0 * zr0, width * z0, packedLight, colorMode, 0.0f, 1.0f);
                addVertex(consumer, matrix, normalMatrix, width * x1 * zr0, height * y1 * zr0, width * z0, packedLight, colorMode, 1.0f, 1.0f);
                addVertex(consumer, matrix, normalMatrix, width * x1 * zr1, height * y1 * zr1, width * z1, packedLight, colorMode, 1.0f, 0.0f);
                addVertex(consumer, matrix, normalMatrix, width * x0 * zr1, height * y0 * zr1, width * z1, packedLight, colorMode, 0.0f, 0.0f);
            }
        }
    }

    private void addVertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normalMatrix, float x, float y, float z, int packedLight, StarfallEntity.ColorMode color, float u, float v) {
        float length = (float) Math.sqrt(x * x + y * y + z * z);
        float nx = x / length;
        float ny = y / length;
        float nz = z / length;
        consumer.vertex(matrix, x, y, z)
                .color(color.r, color.g, color.b, 1.0f)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normalMatrix, nx, ny, nz)
                .endVertex();
    }

    private static final RenderType RENDER_TYPE = RenderType.create(
            "starfall_entity",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            256,
            true,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_SOLID_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(TEXTURE, false, false))
                    .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                    .createCompositeState(true)
    );

    private static final RenderType TRAIL_RENDER_TYPE = RenderType.create(
            "starfall_trail",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            256,
            true,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(TEXTURE, false, false))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .createCompositeState(true)
    );

    @Override
    public ResourceLocation getTextureLocation(StarfallEntity entity) {
        return TEXTURE;
    }
}