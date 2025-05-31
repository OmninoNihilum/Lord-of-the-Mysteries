package net.swimmingtuna.lotm.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.capabilities.concealed_data.ConcealedDataCapability;
import net.swimmingtuna.lotm.capabilities.concealed_data.ConcealedDataProvider;
import net.swimmingtuna.lotm.capabilities.concealed_data.IConcealedDataCapability;
import net.swimmingtuna.lotm.capabilities.is_concealed_data.IIsConcealedCapability;
import net.swimmingtuna.lotm.capabilities.is_concealed_data.IsConcealedCapability;
import net.swimmingtuna.lotm.capabilities.is_concealed_data.IsConcealedProvider;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID)
public class CapabilityInit {
    private static final String MOD_ID = LOTM.MOD_ID;

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IConcealedDataCapability.class);
        event.register(IIsConcealedCapability.class);
    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof LivingEntity entity)) {
            return;
        }

        if (!entity.getCapability(ConcealedDataProvider.CONCEALED_DATA).isPresent()) {
            event.addCapability(
                    new ResourceLocation(MOD_ID, "concealed_data"),
                    new ConcealedDataProvider()
            );
        }

        if (!entity.getCapability(IsConcealedProvider.IS_CONCEALED).isPresent()) {
            event.addCapability(
                    new ResourceLocation(MOD_ID, "is_concealed"),
                    new IsConcealedProvider()
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        Player clone = event.getEntity();

        original.getCapability(ConcealedDataProvider.CONCEALED_DATA).ifPresent(oldData -> {
            clone.getCapability(ConcealedDataProvider.CONCEALED_DATA).ifPresent(newData -> {
                ((ConcealedDataCapability) newData).copyFrom((ConcealedDataCapability) oldData);
            });
        });

        original.getCapability(IsConcealedProvider.IS_CONCEALED).ifPresent(oldData -> {
            clone.getCapability(IsConcealedProvider.IS_CONCEALED).ifPresent(newData -> {
                ((IsConcealedCapability) newData).copyFrom((IsConcealedCapability) oldData);
            });
        });
    }
}