package net.swimmingtuna.lotm.compat.JEI;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record BeyonderJEIRecipe(ItemStack result, List<ItemStack> mainIngredients,
                                List<ItemStack> supplementaryIngredients) {

    public BeyonderJEIRecipe(ItemStack result, List<ItemStack> mainIngredients, List<ItemStack> supplementaryIngredients) {
        this.result = result;
        this.mainIngredients = mainIngredients;
        this.supplementaryIngredients = supplementaryIngredients;
    }

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeItem(result);
        buf.writeCollection(mainIngredients, FriendlyByteBuf::writeItem);
        buf.writeCollection(supplementaryIngredients, FriendlyByteBuf::writeItem);
    }

    public static BeyonderJEIRecipe fromNetwork(FriendlyByteBuf buf) {
        ItemStack result = buf.readItem();

        List<ItemStack> mainIngredients = buf.readCollection(ArrayList::new, FriendlyByteBuf::readItem);
        List<ItemStack> supplementaryIngredients = buf.readCollection(ArrayList::new, FriendlyByteBuf::readItem);
        return new BeyonderJEIRecipe(result, mainIngredients, supplementaryIngredients);
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BeyonderJEIRecipe that = (BeyonderJEIRecipe) o;
        return Objects.equals(result, that.result) && Objects.equals(mainIngredients, that.mainIngredients) && Objects.equals(supplementaryIngredients, that.supplementaryIngredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, mainIngredients, supplementaryIngredients);
    }
}
