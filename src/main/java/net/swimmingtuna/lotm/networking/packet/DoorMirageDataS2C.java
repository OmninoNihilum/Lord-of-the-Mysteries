package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DoorMirageDataS2C {
    private final int entityId;
    private final CompoundTag nbt;

    public DoorMirageDataS2C(int entityId, CompoundTag nbt) {
        this.entityId = entityId;
        this.nbt = nbt;
    }

    public DoorMirageDataS2C(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.nbt = buf.readNbt();
    }

    public void toByte(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeNbt(nbt);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            ClientLevel level = mc.level;
            if (level != null) {
                Entity entity = level.getEntity(entityId);
                if (entity instanceof LivingEntity living) {
                    CompoundTag tag = this.nbt;
                    if (tag.contains("doorMirageIsActive")) {
                        living.getPersistentData().putBoolean("doorMirageIsActive", tag.getBoolean("doorMirageIsActive"));
                    }
                    if (tag.contains("doorMirageDodgeCounter")) {
                        living.getPersistentData().putInt("doorMirageDodgeCounter", tag.getInt("doorMirageDodgeCounter"));
                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
