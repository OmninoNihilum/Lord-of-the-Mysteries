package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.swimmingtuna.lotm.compat.JEI.JEILordOfTheMysteries;
import net.swimmingtuna.lotm.world.worlddata.BeyonderRecipeData;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public record ClientRemoveRecipeJEISyncS2C(Map<ItemStack, BeyonderRecipeData.RecipeIngredients> beyonderRecipes) {

    public ClientRemoveRecipeJEISyncS2C(FriendlyByteBuf buf) {
        this(buf.readMap(
                FriendlyByteBuf::readItem, // key: ItemStack
                BeyonderRecipeData.RecipeIngredients::fromNetwork // value: RecipeIngredients
        ));
    }

    public static void encode(ClientRemoveRecipeJEISyncS2C packet, FriendlyByteBuf buf) {
        buf.writeMap(
                packet.beyonderRecipes(),
                FriendlyByteBuf::writeItem, // key: ItemStack
                (buffer, ingredients) -> ingredients.toNetwork(buffer) // value: RecipeIngredients
        );
    }

    public static ClientRemoveRecipeJEISyncS2C decode(FriendlyByteBuf buf) {
        Map<ItemStack, BeyonderRecipeData.RecipeIngredients> recipes = buf.readMap(
                FriendlyByteBuf::readItem, // key: ItemStack
                BeyonderRecipeData.RecipeIngredients::fromNetwork // value: RecipeIngredients
        );
        return new ClientRemoveRecipeJEISyncS2C(recipes);
    }
    public static void handle(ClientRemoveRecipeJEISyncS2C packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            JEILordOfTheMysteries.removeIngredient(packet.beyonderRecipes);
        });
        ctx.get().setPacketHandled(true);
    }
}
