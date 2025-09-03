package net.swimmingtuna.lotm.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.swimmingtuna.lotm.compat.JEI.BeyonderJEIRecipe;

import java.util.Collections;
import java.util.List;

public class PotionRecipeScreen extends Screen {
    private static final ResourceLocation BOOK_TEXTURE = new ResourceLocation("minecraft", "textures/gui/book.png");

    private final List<BeyonderJEIRecipe> recipes;
    private int currentPage = 0;

    private Button nextButton;
    private Button prevButton;

    public PotionRecipeScreen(List<BeyonderJEIRecipe> recipes) {
        super(recipes.size() == 1 ? Component.literal(recipes.get(0).result().getHoverName().getString() + " recipe") : Component.literal("Unlocked Recipes"));
        this.recipes = recipes.isEmpty() ? Collections.emptyList() : recipes;
    }

    public PotionRecipeScreen(BeyonderJEIRecipe recipe) {
        this(Collections.singletonList(recipe));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int yBottom = this.height - 30;

        this.addRenderableWidget(Button.builder(Component.literal("Close"), b -> this.onClose())
                .bounds(centerX - 40, yBottom, 80, 20)
                .build());

        this.prevButton = this.addRenderableWidget(Button.builder(Component.literal("< Prev"), b -> {
            if (currentPage > 0) currentPage--;
        }).bounds(centerX - 100, yBottom, 60, 20).build());

        this.nextButton = this.addRenderableWidget(Button.builder(Component.literal("Next >"), b -> {
            if (currentPage < recipes.size() - 1) currentPage++;
        }).bounds(centerX + 40, yBottom, 60, 20).build());

        updateButtons();
    }

    private void updateButtons() {
        if (prevButton != null) prevButton.active = currentPage > 0;
        if (nextButton != null) nextButton.active = currentPage < recipes.size() - 1;
    }

    public static void open(BeyonderJEIRecipe recipe) {
        Minecraft.getInstance().setScreen(new PotionRecipeScreen(recipe));
    }

    public static void open(List<BeyonderJEIRecipe> recipes) {
        Minecraft.getInstance().setScreen(new PotionRecipeScreen(recipes));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (Minecraft.getInstance().level == null) return;
        this.renderBackground(guiGraphics);

        int texWidth = 192;
        int texHeight = 192;
        int x = (this.width - texWidth) / 2;
        int y = (this.height - texHeight) / 2;
        guiGraphics.blit(BOOK_TEXTURE, x, y, 0, 0, texWidth, texHeight);

        Font font = this.font;
        int centerX = this.width / 2;
        int textY = y + 20;

        if (recipes.isEmpty()) {
            guiGraphics.drawCenteredString(font, Component.literal("No recipes unlocked"), centerX, textY, 0xFF0000);
        } else {
            BeyonderJEIRecipe recipe = recipes.get(currentPage);

            // Title in blue
            guiGraphics.drawCenteredString(font,
                    Component.literal(recipe.result().getHoverName().getString()), centerX, textY, 0x149fe5);
            textY += 20;

            // Main Ingredients in red
            guiGraphics.drawCenteredString(font, Component.literal("Main Ingredients"), centerX, textY, 0xe8334b);
            textY += 15;

            int maxWidth = texWidth - 60;

            for (ItemStack stack : recipe.mainIngredients()) {
                String line = "- " + stack.getHoverName().getString();
                for (FormattedCharSequence wrapped : font.split(Component.literal(line), maxWidth)) {
                    int lineWidth = font.width(wrapped);
                    guiGraphics.drawString(font, wrapped, centerX - (lineWidth / 2), textY, 0x000000, false);
                    textY += 12;
                }
            }

            textY += 10;

            // Supplementary in green
            guiGraphics.drawCenteredString(font, Component.literal("Supplementary"), centerX, textY, 0x23d317);
            textY += 15;

            for (ItemStack stack : recipe.supplementaryIngredients()) {
                String line = "- " + stack.getHoverName().getString();
                for (FormattedCharSequence wrapped : font.split(Component.literal(line), maxWidth)) {
                    int lineWidth = font.width(wrapped);
                    guiGraphics.drawString(font, wrapped, centerX - (lineWidth / 2), textY, 0x000000, false);
                    textY += 12;
                }
            }

            // Page number if more than one recipe
            if (recipes.size() > 1) {
                String pageLabel = (currentPage + 1) + " / " + recipes.size();
                guiGraphics.drawCenteredString(font, Component.literal(pageLabel), centerX, y + texHeight - 15, 0x555555);
            }
        }

        updateButtons();
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
