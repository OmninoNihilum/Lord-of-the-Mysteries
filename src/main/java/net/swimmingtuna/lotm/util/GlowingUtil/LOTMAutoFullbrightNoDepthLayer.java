package net.swimmingtuna.lotm.util.GlowingUtil;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class LOTMAutoFullbrightNoDepthLayer<T extends GeoAnimatable> extends AutoGlowingGeoLayer<T> {
    public LOTMAutoFullbrightNoDepthLayer(GeoRenderer<T> renderer) {
        super(renderer);
    }

    protected RenderType getRenderType(T animatable) {
        return LOTMAutoFullbrightNoDepthTexture.getRenderType(this.getTextureResource(animatable));
    }

    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        RenderType fullbrightRenderType = this.getRenderType(animatable);
        this.getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, fullbrightRenderType, bufferSource.getBuffer(fullbrightRenderType), partialTick, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
