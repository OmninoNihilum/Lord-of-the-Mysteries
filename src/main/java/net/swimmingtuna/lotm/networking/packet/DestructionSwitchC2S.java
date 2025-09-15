package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;
import net.swimmingtuna.lotm.util.BeyonderUtil;

import java.util.function.Supplier;

public class DestructionSwitchC2S {
    public DestructionSwitchC2S() {
    }

    public DestructionSwitchC2S(FriendlyByteBuf friendlyByteBuf) {
    }

    public static void encode(DestructionSwitchC2S msg, FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        ServerPlayer player = context.getSender();

        context.enqueueWork(() -> {
            if (player == null) return;
            BeyonderUtil.switchDestruction(player);

            boolean value = BeyonderUtil.getDestruction(player);

            player.displayClientMessage(Component.literal(
                                    "Destruction mode: " + (value ? "ON" : "OFF")).
                            withStyle((value ? ChatFormatting.RED : ChatFormatting.GREEN))
                    , true);
        });
    }
}