package net.swimmingtuna.lotm.world.worlddata;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.networking.packet.ClientRecipesJEISyncS2C;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeyonderRecipeData extends SavedData {
    private static final String DATA_NAME = "beyonder_recipe_data";
    private static final String RECIPES_KEY = "BeyonderRecipes";
    private final Map<ItemStack, RecipeIngredients> beyonderRecipes = new HashMap<>();


    public record RecipeIngredients(
            List<ItemStack> mainIngredients,
            List<ItemStack> supplementaryIngredients
    ) {

        public void toNetwork(FriendlyByteBuf buf) {
            buf.writeInt(mainIngredients.size());
            for (ItemStack stack : mainIngredients) {
                buf.writeItem(stack);
            }
            buf.writeInt(supplementaryIngredients.size());
            for (ItemStack stack : supplementaryIngredients) {
                buf.writeItem(stack);
            }
        }

        public static RecipeIngredients fromNetwork(FriendlyByteBuf buf) {
            int mainSize = buf.readInt();
            List<ItemStack> main = new ArrayList<>();
            for (int i = 0; i < mainSize; i++) {
                main.add(buf.readItem());
            }

            int suppSize = buf.readInt();
            List<ItemStack> supp = new ArrayList<>();
            for (int i = 0; i < suppSize; i++) {
                supp.add(buf.readItem());
            }

            return new RecipeIngredients(main, supp);
        }

        @Override
        public List<ItemStack> mainIngredients() {
            return new ArrayList<>(mainIngredients);
        }

        @Override
        public List<ItemStack> supplementaryIngredients() {
            return new ArrayList<>(supplementaryIngredients);
        }
    }


    private BeyonderRecipeData() {
        super();
    }

    private BeyonderRecipeData(CompoundTag tag) {
        super();
        load(tag);
    }

    public static BeyonderRecipeData getInstance(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                BeyonderRecipeData::new,
                BeyonderRecipeData::new,
                DATA_NAME
        );
    }

    public boolean setRecipe(ItemStack potion, List<ItemStack> mainIngredients, List<ItemStack> supplementaryIngredients) {
        // Check if recipe already exists
        ItemStack existingKey = findExistingRecipe(potion);
        if (existingKey != null) {
            return false;
        }

        // Create defensive copies to avoid external modification
        List<ItemStack> mainCopy = new ArrayList<>();
        for (ItemStack item : mainIngredients) {
            mainCopy.add(item.copy());
        }

        List<ItemStack> suppCopy = new ArrayList<>();
        for (ItemStack item : supplementaryIngredients) {
            suppCopy.add(item.copy());
        }

        beyonderRecipes.put(potion.copy(), new RecipeIngredients(mainCopy, suppCopy));
        setDirty();
        LOTMNetworkHandler.sendToAllPlayers(new ClientRecipesJEISyncS2C(Map.of(potion.copy(), new RecipeIngredients(mainCopy, suppCopy))));
        return true;
    }

    public boolean removeRecipe(ItemStack potion) {
        ItemStack existingKey = findExistingRecipe(potion);
        if (existingKey != null) {
            beyonderRecipes.remove(existingKey);
            setDirty();
            return true;
        }
        return false;
    }

    private ItemStack findExistingRecipe(ItemStack potion) {
        for (ItemStack existingPotion : beyonderRecipes.keySet()) {
            if (ItemStack.isSameItemSameTags(existingPotion, potion)) {
                return existingPotion;
            }
        }
        return null;
    }

    public void clearRecipes() {
        beyonderRecipes.clear();
        setDirty();
    }


    public Map<ItemStack, RecipeIngredients> getBeyonderRecipes() {
        return new HashMap<>(beyonderRecipes);
    }

    public void sendPlayerRecipeValues(Player player) {
        if (beyonderRecipes.isEmpty()) {
            player.sendSystemMessage(Component.literal("No Beyonder recipes found.").withStyle(ChatFormatting.RED));
            return;
        }
        for (Map.Entry<ItemStack, RecipeIngredients> entry : beyonderRecipes.entrySet()) {
            StringBuilder recipeMessage = new StringBuilder("Potion: ").append(entry.getKey().getHoverName().getString()).append(" - Main Ingredients: ");

            List<ItemStack> mainIngredients = entry.getValue().mainIngredients();
            if (!mainIngredients.isEmpty()) {
                for (int i = 0; i < mainIngredients.size(); i++) {
                    recipeMessage.append(mainIngredients.get(i).getHoverName().getString());
                    if (i < mainIngredients.size() - 1) {
                        recipeMessage.append(", ");
                    }
                }
            }

            recipeMessage.append(" - Supplementary Ingredients: ");
            List<ItemStack> suppIngredients = entry.getValue().supplementaryIngredients();
            if (!suppIngredients.isEmpty()) {
                for (int i = 0; i < suppIngredients.size(); i++) {
                    recipeMessage.append(suppIngredients.get(i).getHoverName().getString());
                    if (i < suppIngredients.size() - 1) {
                        recipeMessage.append(", ");
                    }
                }
            }

            player.sendSystemMessage(Component.literal(recipeMessage.toString()).withStyle(ChatFormatting.WHITE).withStyle(ChatFormatting.BOLD));
        }
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        ListTag recipeList = new ListTag();

        for (Map.Entry<ItemStack, RecipeIngredients> entry : beyonderRecipes.entrySet()) {
            CompoundTag recipeTag = new CompoundTag();

            // Save potion
            CompoundTag potionTag = new CompoundTag();
            entry.getKey().save(potionTag);
            recipeTag.put("beyonderPotion", potionTag);

            // Save main ingredients
            ListTag mainIngredientsTag = new ListTag();
            for (ItemStack ingredient : entry.getValue().mainIngredients()) {
                CompoundTag ingredientTag = new CompoundTag();
                ingredient.save(ingredientTag);
                mainIngredientsTag.add(ingredientTag);
            }
            recipeTag.put("mainIngredients", mainIngredientsTag);

            // Save supplementary ingredients
            ListTag supplementaryIngredientsTag = new ListTag();
            for (ItemStack ingredient : entry.getValue().supplementaryIngredients()) {
                CompoundTag ingredientTag = new CompoundTag();
                ingredient.save(ingredientTag);
                supplementaryIngredientsTag.add(ingredientTag);
            }
            recipeTag.put("supplementaryIngredients", supplementaryIngredientsTag);

            recipeList.add(recipeTag);
        }

        compoundTag.put(RECIPES_KEY, recipeList);
        return compoundTag;
    }

    private void load(CompoundTag compoundTag) {
        beyonderRecipes.clear();

        if (compoundTag.contains(RECIPES_KEY, Tag.TAG_LIST)) {
            ListTag recipeList = compoundTag.getList(RECIPES_KEY, Tag.TAG_COMPOUND);

            for (int i = 0; i < recipeList.size(); i++) {
                CompoundTag recipeTag = recipeList.getCompound(i);

                // Load potion
                if (!recipeTag.contains("beyonderPotion", Tag.TAG_COMPOUND)) {
                    continue; // Skip malformed entries
                }
                ItemStack potion = ItemStack.of(recipeTag.getCompound("beyonderPotion"));

                // Load main ingredients
                List<ItemStack> mainIngredients = new ArrayList<>();
                if (recipeTag.contains("mainIngredients", Tag.TAG_LIST)) {
                    ListTag mainIngredientsTag = recipeTag.getList("mainIngredients", Tag.TAG_COMPOUND);
                    for (int j = 0; j < mainIngredientsTag.size(); j++) {
                        ItemStack ingredient = ItemStack.of(mainIngredientsTag.getCompound(j));
                        if (!ingredient.isEmpty()) {
                            mainIngredients.add(ingredient);
                        }
                    }
                }

                // Load supplementary ingredients
                List<ItemStack> supplementaryIngredients = new ArrayList<>();
                if (recipeTag.contains("supplementaryIngredients", Tag.TAG_LIST)) {
                    ListTag supplementaryIngredientsTag = recipeTag.getList("supplementaryIngredients", Tag.TAG_COMPOUND);
                    for (int j = 0; j < supplementaryIngredientsTag.size(); j++) {
                        ItemStack ingredient = ItemStack.of(supplementaryIngredientsTag.getCompound(j));
                        if (!ingredient.isEmpty()) {
                            supplementaryIngredients.add(ingredient);
                        }
                    }
                }

                // Only add recipe if potion and at least one main ingredient exist
                if (!potion.isEmpty() && !mainIngredients.isEmpty()) {
                    beyonderRecipes.put(potion, new RecipeIngredients(mainIngredients, supplementaryIngredients));
                }
            }
        }
    }

    public static BeyonderRecipeData create() {
        return new BeyonderRecipeData();
    }
}