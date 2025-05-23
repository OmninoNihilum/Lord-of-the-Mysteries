package net.swimmingtuna.lotm.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.swimmingtuna.lotm.util.ClientData.ClientShouldntRenderHandData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    private void onRenderItem(LivingEntity pEntity, ItemStack pItemStack, ItemDisplayContext pDisplayContext,
                              boolean pLeftHand, PoseStack pPoseStack, MultiBufferSource pBuffer, int pSeed,
                              CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if (pEntity != null && pEntity != minecraft.player &&
                ClientShouldntRenderHandData.getShouldntRender(pEntity.getUUID())) {
            ci.cancel(); // Cancel the rendering if hand should be hidden for other players
        }
    }

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void onRenderArmWithItem(AbstractClientPlayer pPlayer, float pPartialTicks, float pPitch,
                                     InteractionHand pHand, float pSwingProgress, ItemStack pStack,
                                     float pEquippedProgress, PoseStack pPoseStack, MultiBufferSource pBuffer,
                                     int pCombinedLight, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        // Only hide if the player is NOT the current player (so first person still sees their items)
        if (pPlayer != null && pPlayer != minecraft.player &&
                ClientShouldntRenderHandData.getShouldntRender(pPlayer.getUUID())) {
            ci.cancel(); // Cancel the rendering if hand should be hidden for other players
        }
    }

    @Inject(method = "renderPlayerArm", at = @At("HEAD"), cancellable = true)
    private void onRenderPlayerArm(PoseStack pPoseStack, MultiBufferSource pBuffer, int pCombinedLight,
                                   float pEquippedProgress, float pSwingProgress, HumanoidArm pSide,
                                   CallbackInfo ci) {
        // This method is only called for first-person rendering, so we don't need to cancel it
        // The first-person player should always see their own arms
    }

    @Inject(method = "renderMapHand", at = @At("HEAD"), cancellable = true)
    private void onRenderMapHand(PoseStack pPoseStack, MultiBufferSource pBuffer, int pCombinedLight,
                                 HumanoidArm pSide, CallbackInfo ci) {
        // This method is only called for first-person rendering, so we don't need to cancel it
        // The first-person player should always see their own hands when holding maps
    }
}
