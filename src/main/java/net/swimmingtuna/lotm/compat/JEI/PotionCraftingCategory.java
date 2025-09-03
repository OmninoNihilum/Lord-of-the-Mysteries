package net.swimmingtuna.lotm.compat.JEI;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.init.BlockInit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PotionCraftingCategory implements IRecipeCategory<BeyonderJEIRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(LOTM.MOD_ID, "potion_crafting");
    public static final ResourceLocation TEXTURE = new ResourceLocation(LOTM.MOD_ID,
            "textures/gui/potion_cauldron_station.png");

    public static final RecipeType<BeyonderJEIRecipe> POTION_CRAFTING_TYPE = new RecipeType<>(UID, BeyonderJEIRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public PotionCraftingCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 176, 222);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockInit.POTION_CAULDRON.get()));
    }


    @Override
    public @NotNull RecipeType<BeyonderJEIRecipe> getRecipeType() {
        return POTION_CRAFTING_TYPE;
    }


    @Override
    public Component getTitle() {
        return Component.translatable("block.lotm.potion_cauldron");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @SuppressWarnings("removal")
    @Override
    public @Nullable IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BeyonderJEIRecipe recipe, IFocusGroup focuses) {
        List<ItemStack> ingredientsCombined = new ArrayList<>();
        ingredientsCombined.addAll(recipe.mainIngredients());
        ingredientsCombined.addAll(recipe.supplementaryIngredients());

        int[] xCoordinates = {80, 40, 120, 51, 109, 80};
        int[] yCoordinates = {42, 79, 79, 127, 127, 90};

        for (int i = 0; i < ingredientsCombined.size(); i++) {
            builder.addSlot(RecipeIngredientRole.INPUT, xCoordinates[i], yCoordinates[i])
                    .addItemStack(ingredientsCombined.get(i));
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, xCoordinates[xCoordinates.length - 1], yCoordinates[yCoordinates.length - 1])
                .addItemStack(recipe.result());
    }

}
