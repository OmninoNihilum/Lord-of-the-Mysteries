package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.swimmingtuna.lotm.compat.JEI.BeyonderJEIRecipe;
import net.swimmingtuna.lotm.screen.PotionRecipeScreen;

import java.util.List;
import java.util.function.Supplier;

public record ClientRecipeUnlockScreenRenderS2C(List<BeyonderJEIRecipe> recipes) {

    public ClientRecipeUnlockScreenRenderS2C(FriendlyByteBuf buf) {
        this(buf.readList(BeyonderJEIRecipe::fromNetwork));
    }

    public static void encode(ClientRecipeUnlockScreenRenderS2C msg, FriendlyByteBuf buf) {
        buf.writeCollection(msg.recipes(), (b, recipe) -> recipe.toNetwork(b));
    }

    public static ClientRecipeUnlockScreenRenderS2C decode(FriendlyByteBuf buf) {
        return new ClientRecipeUnlockScreenRenderS2C(buf);
    }

    public static void handle(ClientRecipeUnlockScreenRenderS2C msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (!msg.recipes().isEmpty()) {
                PotionRecipeScreen.open(msg.recipes());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
