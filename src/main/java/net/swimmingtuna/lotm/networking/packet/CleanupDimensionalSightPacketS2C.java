package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.blocks.DimensionalSight.DimensionalSightTileEntity;

import java.util.function.Supplier;


public class CleanupDimensionalSightPacketS2C {
    private final BlockPos tileEntityPos;
    private final int entityId;

    public CleanupDimensionalSightPacketS2C(BlockPos pos, int entityId) {
        this.tileEntityPos = pos;
        this.entityId = entityId;
    }

    // Decoder constructor - reads from PacketBuffer
    public CleanupDimensionalSightPacketS2C(FriendlyByteBuf buffer) {
        this.tileEntityPos = buffer.readBlockPos();
        this.entityId = buffer.readInt();
    }

    // Static decode method for registration
    public static CleanupDimensionalSightPacketS2C decode(FriendlyByteBuf buffer) {
        return new CleanupDimensionalSightPacketS2C(buffer);
    }

    // Static encode method for registration
    public static void encode(CleanupDimensionalSightPacketS2C packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.tileEntityPos);
        buffer.writeInt(packet.entityId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level level = Minecraft.getInstance().level;
            if (level != null) {
                BlockEntity blockEntity = level.getBlockEntity(tileEntityPos);
                if (blockEntity instanceof DimensionalSightTileEntity dimensionalSight) {
                    dimensionalSight.removeThis();
                }
                Entity entity = level.getEntity(entityId);
                if (Minecraft.getInstance().level != null && entity != null) {
                    try {
                        Minecraft.getInstance().level.removeEntity(entityId, Entity.RemovalReason.DISCARDED);
                        entity.remove(Entity.RemovalReason.DISCARDED);
                    } catch (Exception ignored) {
                        LOTM.LOGGER.info("PROBLEM WITH DIMENSIONAL SIGHT CLEANUP PACKET!");
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
