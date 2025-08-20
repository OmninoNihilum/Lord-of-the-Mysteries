package net.swimmingtuna.lotm.compat.JEI;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record BeyonderJEIRecipe(ItemStack result, List<ItemStack> mainIngredients,
                                List<ItemStack> supplementaryIngredients) {
    public BeyonderJEIRecipe(ItemStack result, List<ItemStack> mainIngredients, List<ItemStack> supplementaryIngredients) {
        this.result = result;
        this.mainIngredients = new ArrayList<>(mainIngredients);
        this.supplementaryIngredients = new ArrayList<>(supplementaryIngredients);
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