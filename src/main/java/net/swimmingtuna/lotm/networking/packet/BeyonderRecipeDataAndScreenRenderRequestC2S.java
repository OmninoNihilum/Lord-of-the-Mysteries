package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.swimmingtuna.lotm.capabilities.unlocked_recipes.UnlockedRecipesUtils;
import net.swimmingtuna.lotm.compat.JEI.BeyonderJEIRecipe;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.world.worlddata.BeyonderRecipeData;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public record BeyonderRecipeDataAndScreenRenderRequestC2S(List<ItemStack> potions) {

    public BeyonderRecipeDataAndScreenRenderRequestC2S(FriendlyByteBuf buf) {
        this(buf.readList(FriendlyByteBuf::readItem));
    }

    public static void encode(BeyonderRecipeDataAndScreenRenderRequestC2S packet, FriendlyByteBuf buf) {
        buf.writeCollection(packet.potions(), FriendlyByteBuf::writeItem);
    }

    public static BeyonderRecipeDataAndScreenRenderRequestC2S decode(FriendlyByteBuf buf) {
        return new BeyonderRecipeDataAndScreenRenderRequestC2S(buf);
    }

    public static void handle(BeyonderRecipeDataAndScreenRenderRequestC2S packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                BeyonderRecipeData recipeData = BeyonderRecipeData.getInstance(
                        Objects.requireNonNull(player.serverLevel())
                );
                List<BeyonderJEIRecipe> recipes = recipeData.getRecipeAsJEIRecipeFormatByPotions(packet.potions());
                LOTMNetworkHandler.sendToPlayer(
                        new ClientRecipesJEISyncS2C(recipes, UnlockedRecipesUtils.getUnlockedRecipesNames(player)),
                        player
                );
                LOTMNetworkHandler.sendToPlayer(new ClientRecipeUnlockScreenRenderS2C(recipes), player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
