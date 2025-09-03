package net.swimmingtuna.lotm.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.swimmingtuna.lotm.capabilities.unlocked_recipes.UnlockedRecipesUtils;
import net.swimmingtuna.lotm.compat.JEI.BeyonderJEIRecipe;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.networking.packet.ClientRecipeUnlockScreenRenderS2C;
import net.swimmingtuna.lotm.networking.packet.ClientRecipesJEISyncS2C;
import net.swimmingtuna.lotm.world.worlddata.BeyonderRecipeData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static net.swimmingtuna.lotm.util.BeyonderUtil.registerAllRecipes;

public class BeyonderRecipeCommand {
    private static final List<String> validPathwayNames = List.of(
            "apothecary",
            "apprentice",
            "arbiter",
            "assassin",
            "bard",
            "corpse_collector",
            "criminal",
            "hunter",
            "lawyer",
            "marauder",
            "monster",
            "mystery_pryer",
            "planter",
            "prisoner",
            "reader",
            "sailor",
            "savant",
            "secrets_suppliant",
            "seer",
            "sleepless",
            "spectator",
            "warrior"
    );

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(
                literal("beyonderrecipe")
                        .requires(source -> source.hasPermission(2))
                        .then(literal("add")
                                .then(argument("craftedItem", ItemArgument.item(buildContext))
                                        .then(literal("ingredients")
                                                .then(argument("mainCount", IntegerArgumentType.integer(1, 5))
                                                        .then(argument("ingredient1", ItemArgument.item(buildContext))
                                                                .executes(ctx -> addRecipeWithFlexibleIngredients(ctx, 1))
                                                                .then(argument("ingredient2", ItemArgument.item(buildContext))
                                                                        .executes(ctx -> addRecipeWithFlexibleIngredients(ctx, 2))
                                                                        .then(argument("ingredient3", ItemArgument.item(buildContext))
                                                                                .executes(ctx -> addRecipeWithFlexibleIngredients(ctx, 3))
                                                                                .then(argument("ingredient4", ItemArgument.item(buildContext))
                                                                                        .executes(ctx -> addRecipeWithFlexibleIngredients(ctx, 4))
                                                                                        .then(argument("ingredient5", ItemArgument.item(buildContext))
                                                                                                .executes(ctx -> addRecipeWithFlexibleIngredients(ctx, 5))
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(literal("remove")
                                .then(argument("craftedItem", ItemArgument.item(buildContext))
                                        .executes(BeyonderRecipeCommand::removeRecipe)
                                )
                                .then(literal("all")
                                        .executes(BeyonderRecipeCommand::removeAllRecipes)
                                )
                        )
                        .then(literal("load")
                                .executes(BeyonderRecipeCommand::loadModpackRecipes)
                        )
                        .then(literal("unlock")
                                .then(literal("pathway")
                                        .then(argument("pathway_name", StringArgumentType.string())
                                                .suggests(((context, builder) -> SharedSuggestionProvider.suggest(
                                                        validPathwayNames,
                                                        builder
                                                )))
                                                .executes(BeyonderRecipeCommand::unlockPathwayRecipes)
                                        )
                                )
                                .then(literal("single")
                                        .then(argument("potion", ItemArgument.item(buildContext))
                                                .executes(BeyonderRecipeCommand::unlockRecipe))
                                )
                                .then(literal("all")
                                        .executes(BeyonderRecipeCommand::unlockAllRecipes)
                                )
                        )
                        .then(literal("unlocked")
                                .executes(BeyonderRecipeCommand::showUnlockedRecipes))
        );
    }

    private static final Pattern potionPattern = Pattern.compile(".*_\\d+_potion.*");

    public static int showUnlockedRecipes(CommandContext<CommandSourceStack> ctx) {
        try{
            ServerPlayer player = ctx.getSource().getPlayerOrException();

            BeyonderRecipeData recipeData = BeyonderRecipeData.getInstance(
                    Objects.requireNonNull(player.serverLevel())
            );
            List<ItemStack> stack = UnlockedRecipesUtils.getUnlockedRecipes(player).get().unlockedRecipes();
            List<BeyonderJEIRecipe> recipes = recipeData.getRecipeAsJEIRecipeFormatByPotions(stack);
            if (!recipes.isEmpty()) {
                LOTMNetworkHandler.sendToPlayer(new ClientRecipeUnlockScreenRenderS2C(recipes), player);
            }

            return 1;
        } catch (CommandSyntaxException e){
            ctx.getSource().sendFailure(Component.literal("Something went wrong displaying your Recipes"));
            return 0;
        }
    }

    private static int unlockPathwayRecipes(CommandContext<CommandSourceStack> ctx) {
        String pathway = StringArgumentType.getString(ctx, "pathway_name").toLowerCase();

        if (!validPathwayNames.contains(pathway)) {
            ctx.getSource().sendFailure(Component.literal("Invalid pathway name!"));
            return 0;
        }

        try {
            ServerPlayer player = ctx.getSource().getPlayerOrException();
            BeyonderRecipeData recipeData = BeyonderRecipeData.getInstance(ctx.getSource().getLevel());
            Pattern pathwayPattern = Pattern.compile(pathway + "_\\d+_potion.*");
            List<BeyonderJEIRecipe> recipes = new ArrayList<>();

            for (Item item : ItemInit.POTION_ITEMS.stream().map(RegistryObject::get).toList()) {
                var id = ForgeRegistries.ITEMS.getKey(item);

                if (pathwayPattern.matcher(Objects.requireNonNull(id).getPath()).matches()) {
                    recipes.addAll(recipeData.getRecipeAsJEIRecipeFormatByPotion(item.getDefaultInstance()));
                }
            }
            UnlockedRecipesUtils.getUnlockedRecipes(player).get().addRecipes(recipes.stream().map(BeyonderJEIRecipe::result).toList());
            LOTMNetworkHandler.sendToPlayer(new ClientRecipesJEISyncS2C(recipes, UnlockedRecipesUtils.getUnlockedRecipesNames(player)), player);
            givePlayerRecipesInBook(recipes, player);
            ctx.getSource().sendSuccess(() -> Component.literal("Successfully unlocked recipes for " + pathway), false);
            return 1;
        } catch (CommandSyntaxException e) {
            ctx.getSource().sendFailure(Component.literal("A problem occurred while unlocking the recipes for the pathway"));
            return 0;
        }
    }

    private static int unlockAllRecipes(CommandContext<CommandSourceStack> ctx) {
        try {
            ServerPlayer player = ctx.getSource().getPlayerOrException();
            BeyonderRecipeData recipeData = BeyonderRecipeData.getInstance(ctx.getSource().getLevel());
            List<BeyonderJEIRecipe> recipes = new ArrayList<>();
            for (Item item : ItemInit.POTION_ITEMS.stream().map(RegistryObject::get).toList()) {
                recipes.addAll(recipeData.getRecipeAsJEIRecipeFormatByPotion(item.getDefaultInstance()));
            }
            UnlockedRecipesUtils.getUnlockedRecipes(player).get().addRecipes(recipes.stream().map(BeyonderJEIRecipe::result).toList());
            LOTMNetworkHandler.sendToPlayer(new ClientRecipesJEISyncS2C(recipes, UnlockedRecipesUtils.getUnlockedRecipesNames(player)), player);
            givePlayerRecipesInBook(recipes, player);
            ctx.getSource().sendSuccess(() -> Component.literal("Successfully unlocked all recipes"), true);
            return 1;
        } catch (CommandSyntaxException e) {
            ctx.getSource().sendFailure(Component.literal("There was a problem with unlocking all recipes"));
            return 0;
        }
    }

    private static int unlockRecipe(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = context.getSource().getPlayerOrException();
            ItemStack unlockItem = ItemArgument.getItem(context, "potion").createItemStack(1, false);

            var id = ForgeRegistries.ITEMS.getKey(unlockItem.getItem());
            if (!potionPattern.matcher(Objects.requireNonNull(id).getPath()).matches()) {
                context.getSource().sendFailure(Component.literal("The given Item was no potion of our mod"));
                return 0;
            }
            BeyonderRecipeData recipeData = BeyonderRecipeData.getInstance(context.getSource().getLevel());
            List<BeyonderJEIRecipe> recipes = recipeData.getRecipeAsJEIRecipeFormatByPotion(unlockItem);
            if (recipes.isEmpty()) {
                context.getSource().sendFailure(Component.literal("There is no recipe registered for this potion"));
                return 0;
            }
            UnlockedRecipesUtils.getUnlockedRecipes(player).get().addRecipe(unlockItem);
            LOTMNetworkHandler.sendToPlayer(new ClientRecipesJEISyncS2C(recipes, UnlockedRecipesUtils.getUnlockedRecipesNames(player)), player);
            givePlayerRecipesInBook(recipes, player);
            context.getSource().sendSuccess(() -> Component.literal("Successfully learned recipe for " + unlockItem.getHoverName().getString()), true);
            return 1;
        } catch (CommandSyntaxException e) {
            context.getSource().sendFailure(Component.literal("Could not find the given potion or this command was not performed by an player"));
            return 0;
        }
    }

    private static void givePlayerRecipesInBook(List<BeyonderJEIRecipe> recipes, Player player) {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);

        CompoundTag tag = book.getOrCreateTag();
        tag.putString("title", "Unlocked Recipes");
        tag.putString("author", "0-08");

        ListTag pages = new ListTag();
        int maxPageLength = 256;

        for (BeyonderJEIRecipe recipe : recipes) {
            StringBuilder pageText = new StringBuilder();

            pageText.append("§9").append("§o").append("§n").append("§l").append(Component.literal(recipe.result().getHoverName().getString())
                    .getString()).append("\n\n");

            pageText.append("§r").append("§c").append("§n").append("§l").append(Component.literal("Main Ingredients")
                    .getString()).append("\n\n");

            for (ItemStack stack : recipe.mainIngredients()) {
                pageText.append("§r").append("§0").append(Component.literal("- " + stack.getHoverName().getString())
                        .getString()).append("\n");
            }

            pageText.append("\n");

            pageText.append("§a").append("§n").append("§l").append(Component.literal("Supplementary Ingredients")
                    .getString()).append("\n\n");

            for (ItemStack stack : recipe.supplementaryIngredients()) {
                pageText.append("§r").append("§0").append(Component.literal("- " + stack.getHoverName().getString())
                        .getString()).append("\n");
            }

            int start = 0;
            while (start < pageText.length()) {
                int end = Math.min(start + maxPageLength, pageText.length());
                String slice = pageText.substring(start, end);

                pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(slice))));
                start = end;
            }
        }

        tag.put("pages", pages);
        book.setTag(tag);

        if (!player.getInventory().add(book)) {
            player.drop(book, false);
        }
    }



    private static int removeRecipe(CommandContext<CommandSourceStack> context) {
        try {
            ItemStack craftedItem = ItemArgument.getItem(context, "craftedItem").createItemStack(1, false);
            ServerLevel level = context.getSource().getLevel();
            BeyonderRecipeData recipeData = BeyonderRecipeData.getInstance(level);

            boolean recipeRemoved = recipeData.removeRecipe(craftedItem);
            if (recipeRemoved) {
                context.getSource().sendSuccess(() -> Component.literal("Successfully removed recipe for " +
                        craftedItem.getHoverName().getString()).withStyle(ChatFormatting.GREEN), true);
                return 1;
            } else {
                context.getSource().sendFailure(Component.literal("No recipe found for this item!").withStyle(ChatFormatting.YELLOW));
                return 0;
            }
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Error removing recipe: " + e.getMessage()).withStyle(ChatFormatting.DARK_RED));
            return 0;
        }
    }

    private static int removeAllRecipes(CommandContext<CommandSourceStack> context) {
        try {
            ServerLevel level = context.getSource().getLevel();
            BeyonderRecipeData recipeData = BeyonderRecipeData.getInstance(level);
            recipeData.clearRecipes();
            context.getSource().sendSystemMessage(Component.literal("Cleared all recipes").withStyle(ChatFormatting.GREEN));
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Error removing recipe: " + e.getMessage()).withStyle(ChatFormatting.DARK_RED));
            return 0;
        }
    }


    private static int addRecipeWithFlexibleIngredients(CommandContext<CommandSourceStack> context, int ingredientCount) {
        try {
            // Get the crafted item
            ItemStack craftedItem = ItemArgument.getItem(context, "craftedItem").createItemStack(1, false);

            // Get the specified main ingredient count
            int mainIngredientCount = context.getArgument("mainCount", Integer.class);

            // Prepare lists for ingredients
            List<ItemStack> mainIngredients = new ArrayList<>();
            List<ItemStack> supplementaryIngredients = new ArrayList<>();

            // Validate main ingredient count
            if (mainIngredientCount > ingredientCount) {
                context.getSource().sendFailure(Component.literal("Main ingredient count cannot exceed total ingredient count.")
                        .withStyle(ChatFormatting.RED));
                return 0;
            }

            // Collect ingredients
            for (int i = 1; i <= ingredientCount; i++) {
                ItemStack ingredient = ItemArgument.getItem(context, "ingredient" + i).createItemStack(1, false);

                // Categorize ingredients
                if (mainIngredients.size() < mainIngredientCount) {
                    mainIngredients.add(ingredient);
                } else {
                    supplementaryIngredients.add(ingredient);
                }
            }

            // Ensure at least one main ingredient
            if (mainIngredients.isEmpty()) {
                context.getSource().sendFailure(Component.literal("At least one main ingredient is required!")
                        .withStyle(ChatFormatting.RED));
                return 0;
            }

            // Get recipe data and add recipe
            ServerLevel level = context.getSource().getLevel();
            BeyonderRecipeData recipeData = BeyonderRecipeData.getInstance(level);
            boolean recipeAdded = recipeData.setRecipe(craftedItem, mainIngredients, supplementaryIngredients, level);

            if (recipeAdded) {
                // Construct success message
                StringBuilder message = new StringBuilder("Successfully added recipe for ")
                        .append(craftedItem.getHoverName().getString())
                        .append(" - Main Ingredients: ");

                for (ItemStack ingredient : mainIngredients) {
                    message.append(ingredient.getHoverName().getString()).append(", ");
                }

                message.append(" - Supplementary Ingredients: ");
                for (ItemStack ingredient : supplementaryIngredients) {
                    message.append(ingredient.getHoverName().getString()).append(", ");
                }

                context.getSource().sendSuccess(() -> Component.literal(message.toString())
                        .withStyle(ChatFormatting.GREEN), true);
                return 1;
            } else {
                context.getSource().sendFailure(Component.literal("A recipe for this item already exists!")
                        .withStyle(ChatFormatting.YELLOW));
                return 0;
            }
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Error adding recipe: " + e.getMessage())
                    .withStyle(ChatFormatting.DARK_RED));
            return 0;
        }
    }


    private static int loadModpackRecipes(CommandContext<CommandSourceStack> context) {
        try {
            ServerLevel level = context.getSource().getLevel();
            BeyonderRecipeData recipeData = BeyonderRecipeData.getInstance(level);
            recipeData.clearRecipes();
            registerAllRecipes(context);
            context.getSource().sendSuccess(() -> Component.literal("Successfully loaded all modpack recipes!")
                    .withStyle(ChatFormatting.GREEN), true);
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Error loading modpack recipes: " + e.getMessage())
                    .withStyle(ChatFormatting.DARK_RED));
            return 0;
        }
    }


    public static void executeRecipeCommand(CommandContext<CommandSourceStack> context, String command) {
        try {
            context.getSource().getServer().getCommands().performPrefixedCommand(
                    context.getSource(), command.substring(1));
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Failed to execute recipe: " + command)
                    .withStyle(ChatFormatting.RED));
        }
    }

}

