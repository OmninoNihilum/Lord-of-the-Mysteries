package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.swimmingtuna.lotm.util.ClientData.ClientLookData;

import java.util.function.Supplier;

public class ForceLookPacketS2C {
    private final float yaw;
    private final float pitch;
    private final boolean smooth;

    public ForceLookPacketS2C(float yaw, float pitch, boolean smooth) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.smooth = smooth;
    }

    // Constructor for looking at a specific position
    public ForceLookPacketS2C(Vec3 playerPos, Vec3 targetPos, boolean smooth) {
        Vec3 lookDirection = targetPos.subtract(playerPos).normalize();
        this.yaw = (float) (Math.atan2(-lookDirection.x, lookDirection.z) * (180.0 / Math.PI));
        this.pitch = (float) (Math.asin(-lookDirection.y) * (180.0 / Math.PI));
        this.smooth = smooth;
    }

    // Encode to buffer
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeFloat(yaw);
        buffer.writeFloat(pitch);
        buffer.writeBoolean(smooth);
    }

    // Decode from buffer
    public static ForceLookPacketS2C decode(FriendlyByteBuf buffer) {
        float yaw = buffer.readFloat();
        float pitch = buffer.readFloat();
        boolean smooth = buffer.readBoolean();
        return new ForceLookPacketS2C(yaw, pitch, smooth);
    }

    // Client-side handler
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // This runs on the client thread
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                if (smooth) {
                    // Store target rotation for smooth interpolation in client-side storage
                    ClientLookData.setTargetRotation(yaw, pitch);
                    ClientLookData.setSmoothLooking(true);
                } else {
                    // Instant rotation
                    setPlayerRotation(mc.player, yaw, pitch);
                    ClientLookData.setSmoothLooking(false);
                }
            }
        });
        context.setPacketHandled(true);
    }

    // Helper method to set player rotation
    public static void setPlayerRotation(Player player, float yaw, float pitch) {
        // Clamp pitch to valid range
        pitch = Mth.clamp(pitch, -90.0f, 90.0f);

        // Set rotation
        player.setYRot(yaw);
        player.setXRot(pitch);
        player.yRotO = yaw;
        player.xRotO = pitch;
        player.yHeadRot = yaw;
        player.yHeadRotO = yaw;
    }
}

