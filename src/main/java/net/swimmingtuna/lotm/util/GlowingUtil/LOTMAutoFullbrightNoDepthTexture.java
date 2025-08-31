package net.swimmingtuna.lotm.util.GlowingUtil;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.texture.AutoGlowingTexture;

public class LOTMAutoFullbrightNoDepthTexture extends AutoGlowingTexture {
    private static final RenderStateShard.ShaderStateShard SHADER_STATE = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEyesShader);
    private static final RenderStateShard.TransparencyStateShard TRANSPARENCY_STATE = new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    private static final RenderStateShard.WriteMaskStateShard WRITE_MASK = new RenderStateShard.WriteMaskStateShard(true, false);
    protected static final RenderStateShard.CullStateShard CULL = new RenderStateShard.CullStateShard(true);
    private static final Function<ResourceLocation, RenderType> RENDER_TYPE_FUNCTION = Util.memoize((texture) -> {
        RenderStateShard.TextureStateShard textureState = new RenderStateShard.TextureStateShard(texture, false, false);
        return RenderType.create("arma_fullbright_no_depth", DefaultVertexFormat.NEW_ENTITY, Mode.QUADS, 256, false, true, CompositeState.builder().setShaderState(SHADER_STATE).setCullState(CULL).setTextureState(textureState).setTransparencyState(TRANSPARENCY_STATE).setWriteMaskState(WRITE_MASK).createCompositeState(false));
    });

    public LOTMAutoFullbrightNoDepthTexture(ResourceLocation originalLocation, ResourceLocation location) {
        super(originalLocation, location);
    }

    public static RenderType getRenderType(ResourceLocation texture) {
        return RENDER_TYPE_FUNCTION.apply(getEmissiveResource(texture));
    }

    private static ResourceLocation getEmissiveResource(ResourceLocation baseResource) {
        ResourceLocation path = appendToPath(baseResource, "_fbnd");
        generateTexture(path, (textureManager) -> {
            textureManager.register(path, new AutoGlowingTexture(baseResource, path));
        });
        return path;
    }
}

