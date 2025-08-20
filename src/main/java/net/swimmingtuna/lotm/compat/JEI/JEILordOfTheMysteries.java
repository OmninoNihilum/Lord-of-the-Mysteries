package net.swimmingtuna.lotm.compat.JEI;

import com.mojang.logging.LogUtils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.screen.PotionCauldronScreen;
import net.swimmingtuna.lotm.world.worlddata.BeyonderRecipeData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@JeiPlugin
public class JEILordOfTheMysteries implements IModPlugin {
    private static final Pattern potionPattern = Pattern.compile(".*_\\d+_potion.*");

    private static IJeiRuntime jeiRuntime;

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(LOTM.MOD_ID, "jei_lotm");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        jeiRuntime = runtime;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new PotionCraftingCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        IIngredientManager ingredientManager = registration.getIngredientManager();
        for (Item item : ItemInit.ITEMS.getEntries().stream().map(RegistryObject::get).toList()){
            var id = ForgeRegistries.ITEMS.getKey(item);

            if (!potionPattern.matcher(Objects.requireNonNull(id).getPath()).matches()) {
                ingredientManager.removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, List.of(new ItemStack(item)));
            }
        }
    }

    private static List<BeyonderJEIRecipe> recipesToJEIRecipe(Map<ItemStack, BeyonderRecipeData.RecipeIngredients> recipes) {
        List<BeyonderJEIRecipe> jeiRecipes = new ArrayList<>();
        for (Map.Entry<ItemStack, BeyonderRecipeData.RecipeIngredients> entry : recipes.entrySet()) {
            jeiRecipes.add(new BeyonderJEIRecipe(
                    entry.getKey(),
                    entry.getValue().mainIngredients(),
                    entry.getValue().supplementaryIngredients()
            ));
        }
        return jeiRecipes;
    }

    public static void registerRecipesToJei(Map<ItemStack, BeyonderRecipeData.RecipeIngredients> recipes) {
        if (jeiRuntime != null) {
            jeiRuntime.getRecipeManager().addRecipes(PotionCraftingCategory.POTION_CRAFTING_TYPE, recipesToJEIRecipe(recipes));
            LogUtils.getLogger().debug("JEI: Registered Potions for LOTM recipes");
        } else {
            LogUtils.getLogger().warn("JEI runtime not ready to register LOTM potions — Problem");
        }
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(PotionCauldronScreen.class, 65, 65, 20, 30,
                PotionCraftingCategory.POTION_CRAFTING_TYPE);
    }
}