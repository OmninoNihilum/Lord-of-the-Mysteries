package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.util.BeyonderUtil;

import java.util.UUID;
import java.util.function.Supplier;

public class ClientShouldntRenderS2C {
    private final UUID uuid;
    private final int ignoreShouldntRender;

    public ClientShouldntRenderS2C(UUID livingUUID, int ignoreShouldntRender) {
        this.uuid = livingUUID;
        this.ignoreShouldntRender = ignoreShouldntRender;
    }

    public ClientShouldntRenderS2C(FriendlyByteBuf buf) {
        this.uuid = buf.readUUID();
        this.ignoreShouldntRender = buf.readInt();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(this.uuid);
        buf.writeInt(this.ignoreShouldntRender);
    }

    public static void encode(ClientShouldntRenderS2C packet, FriendlyByteBuf buf) {
        packet.write(buf);
    }

    public static ClientShouldntRenderS2C decode(FriendlyByteBuf buf) {
        return new ClientShouldntRenderS2C(buf);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Level level = Minecraft.getInstance().level;
            if (level != null) {
                LOTM.LOGGER.info("LEVEL NOT NULL");
                LivingEntity living = BeyonderUtil.getClientLivingEntityFromUUID(level, this.uuid);
                if (living != null) {
                    System.out.println("PACKET HANDLER SEND WITH VALUE OF " + this.ignoreShouldntRender);
                    living.getPersistentData().putInt("ignoreShouldntRender", this.ignoreShouldntRender);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
