package net.swimmingtuna.lotm.mixin;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.swimmingtuna.lotm.blocks.DimensionalSight.DimensionalSightTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void onShouldRender(Entity entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        CompoundTag tag = entity.getPersistentData();
        if (tag.getInt("ignoreShouldntRender") >= 1) {
            System.out.println("SHOULDNT RENDER MIXIN WORKING WITH VALUE OF " + tag.getInt("ignoreShouldntRender") + " FOR " + entity.getName().getString());
            cir.setReturnValue(true);
        }
    }
}
