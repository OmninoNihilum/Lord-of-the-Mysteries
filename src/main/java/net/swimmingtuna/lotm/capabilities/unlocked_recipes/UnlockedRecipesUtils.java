package net.swimmingtuna.lotm.capabilities.unlocked_recipes;

import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Optional;

public class UnlockedRecipesUtils {

    public static Optional<IUnlockedRecipesDataCapability> getUnlockedRecipes(Player player) {
        return player.getCapability(UnlockedRecipesDataProvider.UNLOCKED_DATA).resolve();
    }

    public static List<String> getUnlockedRecipesNames(Player player) {
        var data = player.getCapability(UnlockedRecipesDataProvider.UNLOCKED_DATA).resolve();
        return data.map(iUnlockedRecipesDataCapability -> iUnlockedRecipesDataCapability.unlockedRecipes().stream().map(itemStack -> itemStack.getHoverName().getString()).toList()).orElseGet(List::of);
    }
}
