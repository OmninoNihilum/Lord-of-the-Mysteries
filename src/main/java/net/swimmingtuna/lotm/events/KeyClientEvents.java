package net.swimmingtuna.lotm.events;

import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.util.ClientData.*;
import net.swimmingtuna.lotm.util.KeyBinding;
import net.swimmingtuna.lotm.util.effect.ModEffects;
import net.swimmingtuna.lotm.world.worldgen.dimension.DimensionInit;

public class KeyClientEvents {
    @Mod.EventBusSubscriber(modid = LOTM.MOD_ID, value = Dist.CLIENT)
    public static class ClientForgeEvents {

        @SubscribeEvent
        public static void onMovementInputUpdate(MovementInputUpdateEvent event) {
            Player player = Minecraft.getInstance().player;
            if (player != null && player.hasEffect(ModEffects.TUMBLE.get())) {
                event.getInput().forwardImpulse = 0;
                event.getInput().leftImpulse = 0;
                event.getInput().jumping = false;
                event.getInput().shiftKeyDown = false;
            }
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                ClientIgnoreShouldntRenderData.decrementAll();
                ClientShouldntRenderInvisibilityData.tick();
            }
        }


        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
           //if (KeyBinding.SPIRIT_VISION.consumeClick()) {
           //    LOTMNetworkHandler.sendToServer(new SpiritVisionC2S());
           //}
           //if (KeyBinding.SPIRIT_WORLD_TRAVERSAL.consumeClick()) {
           //    System.out.println("Worked");
           //    LOTMNetworkHandler.sendToServer(new SpiritWorldTraversalC2S());
           //}
            Player player = Minecraft.getInstance().player;
            if (player == null) return;


            // Check for left click
            if (KeyBinding.ABILITY_KEY_X.consumeClick()) {
                ClientAbilityKeyResetData.setAbilityResetTimer(100);
                byte[] keysClicked = ClientAbilityCombinationData.getKeysClicked();
                for (int i = 0; i < keysClicked.length; i++) {
                    if (keysClicked[i] == 0) {
                        ClientAbilityCombinationData.setKeyClicked(i, (byte) 1);
                        ClientAbilityCombinationData.handleClick();
                        int filledPositions = 0;
                        for (byte b : keysClicked) {
                            if (b != 0) filledPositions++;
                        }
                        if (filledPositions >= 5) {
                            ClientAbilityKeyResetData.setAbilityResetTimer(0);
                        }
                        break;
                    }
                }
            }

            // Handle right click
            if (KeyBinding.ABILITY_KEY_O.consumeClick()) {
                ClientAbilityKeyResetData.setAbilityResetTimer(100);
                byte[] keysClicked = ClientAbilityCombinationData.getKeysClicked();
                for (int i = 0; i < keysClicked.length; i++) {
                    if (keysClicked[i] == 0) {
                        ClientAbilityCombinationData.setKeyClicked(i, (byte) 2);
                        ClientAbilityCombinationData.handleClick();
                        int filledPositions = 0;
                        for (byte b : keysClicked) {
                            if (b != 0) filledPositions++;
                        }
                        if (filledPositions >= 5) {
                            ClientAbilityKeyResetData.setAbilityResetTimer(0);
                        }
                        break;
                    }
                }
            }
        }

        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void onFogDensityEvent(ViewportEvent.RenderFog event) {
            Player player = Minecraft.getInstance().player;
            if (ClientFogData.getFogTimer() >= 40) {
                event.setFarPlaneDistance(Math.max(7,100 - ClientFogData.getFogTimer() * 2));
                event.setNearPlaneDistance(Math.max(1,90 - ClientFogData.getFogTimer() * 2));
                event.setCanceled(true);
            } else if (player.level().dimension().equals(DimensionInit.SPIRIT_WORLD_LEVEL_KEY)) {
                event.setFogShape(FogShape.SPHERE);
                int currentSequence = ClientSequenceData.getCurrentSequence();
                if (currentSequence == 0) {
                    event.setFarPlaneDistance(999);
                    event.setNearPlaneDistance(999);
                } else if (currentSequence != -1) {
                    int far = 100 - Math.max(10, currentSequence * 10);
                    int near = 97 - Math.max(7, currentSequence * 10);
                    event.setFarPlaneDistance(far);
                    event.setNearPlaneDistance(near);
                } else {
                    event.setFarPlaneDistance(6);
                    event.setNearPlaneDistance(4);
                }
                event.setCanceled(true);
            } else if (player.level().dimension().equals(DimensionInit.EXILED_DIMENSION_LEVEL_KEY)) {

            }

        }
    }

    @Mod.EventBusSubscriber(modid = LOTM.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {
        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            //event.register(KeyBinding.SPIRIT_VISION);
            //event.register(KeyBinding.SPIRIT_WORLD_TRAVERSAL);
            event.register(KeyBinding.ABILITY_KEY_O);
            event.register(KeyBinding.ABILITY_KEY_X);
        }
    }
}
