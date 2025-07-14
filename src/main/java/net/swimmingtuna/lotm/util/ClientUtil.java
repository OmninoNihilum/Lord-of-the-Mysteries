package net.swimmingtuna.lotm.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.Blocks;
import net.swimmingtuna.lotm.blocks.DimensionalSight.DimensionalTileEntity;
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

    public static void removeDimensionalSight(DimensionalTileEntity dimensionalTileEntity) {
        if (dimensionalTileEntity.getLevel() != null) {
            dimensionalTileEntity.getLevel().setBlock(dimensionalTileEntity.getBlockPos(), Blocks.AIR.defaultBlockState(), 3);
        }
    }
}
