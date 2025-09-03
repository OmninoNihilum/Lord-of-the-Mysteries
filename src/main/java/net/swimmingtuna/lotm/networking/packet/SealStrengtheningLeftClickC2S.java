package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.util.LeftClickHandler.LeftClickType;

import java.util.function.Supplier;

public class SealStrengtheningLeftClickC2S implements LeftClickType {
    public SealStrengtheningLeftClickC2S() {

    }

    public SealStrengtheningLeftClickC2S(FriendlyByteBuf buf) {

    }

    public void toByte(FriendlyByteBuf buf) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        ServerPlayer player = context.getSender();
        context.enqueueWork(() -> {
            if (player == null) return;
            if (player.isShiftKeyDown()) {
                CompoundTag tag = player.getPersistentData();
                int x = tag.getInt("sealStrengtheningItemValue");
                if (x <= 9) {
                    tag.putInt("sealStrengtheningItemValue", x + 1);
                } else {
                    tag.putInt("sealStrengtheningItemValue", 1);
                }
            } else {
                player.setItemInHand(InteractionHand.MAIN_HAND, ItemInit.DOOR_LAYERING.get().getDefaultInstance());
            }
            });
        return true;
    }
}
