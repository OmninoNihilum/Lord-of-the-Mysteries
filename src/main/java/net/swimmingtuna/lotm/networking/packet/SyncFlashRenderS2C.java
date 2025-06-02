package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.swimmingtuna.lotm.util.ClientData.ClientShouldntRenderFlashData;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncFlashRenderS2C {
    private final UUID entityUUID;
    private final boolean shouldBeInvisible;

    public SyncFlashRenderS2C(UUID entityUUID, boolean shouldBeInvisible) {
        this.entityUUID = entityUUID;
        this.shouldBeInvisible = shouldBeInvisible;
    }

    public SyncFlashRenderS2C(FriendlyByteBuf buf) {
        this.entityUUID = buf.readUUID();
        this.shouldBeInvisible = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(entityUUID);
        buf.writeBoolean(shouldBeInvisible);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (shouldBeInvisible) {
                ClientShouldntRenderFlashData.setShouldntRender(1, entityUUID);
            } else {
                ClientShouldntRenderFlashData.setShouldntRender(0, entityUUID);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
