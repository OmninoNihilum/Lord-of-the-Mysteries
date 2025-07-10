package net.swimmingtuna.lotm.util;

import net.minecraft.client.Minecraft;
import net.swimmingtuna.lotm.networking.packet.ForceLookPacketS2C;
import net.swimmingtuna.lotm.util.ClientData.ClientLookData;

public class ClientUtil {

    public static void handleForceLook(ForceLookPacketS2C packet) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            if (packet.isSmooth()) {
                ClientLookData.setTargetRotation(packet.getYaw(), packet.getPitch());
                ClientLookData.setSmoothLooking(true);
            } else {
                ForceLookPacketS2C.setPlayerRotation(mc.player, packet.getYaw(), packet.getPitch());
                ClientLookData.setSmoothLooking(false);
            }
        }
    }
}
