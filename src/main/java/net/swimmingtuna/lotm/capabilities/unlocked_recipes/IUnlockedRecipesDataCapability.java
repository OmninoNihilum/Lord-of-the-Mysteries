package net.swimmingtuna.lotm.capabilities.unlocked_recipes;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IUnlockedRecipesDataCapability {
     List<ItemStack> unlockedRecipes();

    void addRecipe(ItemStack stack);
    void addRecipes(List<ItemStack> stacks);
}
