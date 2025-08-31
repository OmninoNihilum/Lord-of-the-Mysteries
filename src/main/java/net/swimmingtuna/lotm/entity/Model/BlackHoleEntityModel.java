package net.swimmingtuna.lotm.entity.Model;

import net.minecraft.resources.ResourceLocation;
import net.swimmingtuna.lotm.entity.BlackHoleEntity;
import software.bernie.geckolib.model.GeoModel;

public class BlackHoleEntityModel extends GeoModel<BlackHoleEntity> {

    @SuppressWarnings("removal")
    @Override
    public ResourceLocation getModelResource(BlackHoleEntity animatable) {
        // Return null since we're using custom math-based rendering
        return null;
    }
    @SuppressWarnings("removal")
    @Override
    public ResourceLocation getTextureResource(BlackHoleEntity animatable) {
        // Return null since we don't need textures for the math-based geometry
        return null;
    }
    @Override
    public ResourceLocation getAnimationResource(BlackHoleEntity animatable) {
        // Return null since there are no animations
        return null;
    }
}
