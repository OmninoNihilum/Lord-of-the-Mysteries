package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.swimmingtuna.lotm.compat.JEI.JEILordOfTheMysteries;
import net.swimmingtuna.lotm.world.worlddata.BeyonderRecipeData;

import java.util.Map;
import java.util.function.Supplier;

public record ClientRecipesJEISyncS2C(Map<ItemStack, BeyonderRecipeData.RecipeIngredients> beyonderRecipes) {

    public ClientRecipesJEISyncS2C(FriendlyByteBuf buf) {
        // key: ItemStack
        // value: RecipeIngredients
        this(buf.readMap(
                FriendlyByteBuf::readItem, // key: ItemStack
                BeyonderRecipeData.RecipeIngredients::fromNetwork // value: RecipeIngredients
        ));
    }

    public static void encode(ClientRecipesJEISyncS2C packet, FriendlyByteBuf buf) {
        buf.writeMap(
                packet.beyonderRecipes(),
                FriendlyByteBuf::writeItem, // key: ItemStack
                (buffer, ingredients) -> ingredients.toNetwork(buffer) // value: RecipeIngredients
        );
    }

    public static void handle(ClientRecipesJEISyncS2C packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            JEILordOfTheMysteries.registerRecipesToJei(packet.beyonderRecipes());
        });
        ctx.get().setPacketHandled(true);
    }

    public static ClientRecipesJEISyncS2C decode(FriendlyByteBuf buf) {
        Map<ItemStack, BeyonderRecipeData.RecipeIngredients> recipes = buf.readMap(
                FriendlyByteBuf::readItem, // key: ItemStack
                BeyonderRecipeData.RecipeIngredients::fromNetwork // value: RecipeIngredients
        );
        return new ClientRecipesJEISyncS2C(recipes);
    }
}