package net.swimmingtuna.lotm.compat.JEI;

import com.mojang.logging.LogUtils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.screen.PotionCauldronScreen;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JEILordOfTheMysteries implements IModPlugin {

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
            ingredientManager.removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, List.of(new ItemStack(item)));
        }
    }

    public static void registerRecipesToJei(List<BeyonderJEIRecipe> jeiRecipes, List<String> unlockedRecipes) {
        List<BeyonderJEIRecipe> toRegister = new ArrayList<>();
        for (BeyonderJEIRecipe jeiRecipe : jeiRecipes) {
            if (unlockedRecipes.contains(jeiRecipe.result().getHoverName().getString())) {
                toRegister.add(jeiRecipe);
            }
        }
        if (jeiRuntime != null) {
            jeiRuntime.getRecipeManager().addRecipes(PotionCraftingCategory.POTION_CRAFTING_TYPE, toRegister);
            jeiRuntime.getIngredientManager().addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, toRegister.stream().map(BeyonderJEIRecipe::result).toList());
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
