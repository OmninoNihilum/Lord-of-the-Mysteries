package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.swimmingtuna.lotm.compat.JEI.BeyonderJEIRecipe;
import net.swimmingtuna.lotm.compat.JEI.JEILordOfTheMysteries;

import java.util.List;
import java.util.function.Supplier;

public record ClientRecipesJEISyncS2C(List<BeyonderJEIRecipe> beyonderRecipes, List<String> unlockedRecipes) {

    public ClientRecipesJEISyncS2C(FriendlyByteBuf buf) {
        this(buf.readList(BeyonderJEIRecipe::fromNetwork),
                buf.readList(FriendlyByteBuf::readUtf));
    }

    public static void encode(ClientRecipesJEISyncS2C packet, FriendlyByteBuf buf) {
        buf.writeCollection(packet.beyonderRecipes(), (buffer, recipe) -> recipe.toNetwork(buffer));
        buf.writeCollection(packet.unlockedRecipes(), FriendlyByteBuf::writeUtf);
    }

    public static void handle(ClientRecipesJEISyncS2C packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            JEILordOfTheMysteries.registerRecipesToJei(packet.beyonderRecipes(), packet.unlockedRecipes);
        });
        ctx.get().setPacketHandled(true);
    }

    public static ClientRecipesJEISyncS2C decode(FriendlyByteBuf buf) {
        return new ClientRecipesJEISyncS2C(buf);
    }
}
