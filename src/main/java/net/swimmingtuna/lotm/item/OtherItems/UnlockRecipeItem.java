package net.swimmingtuna.lotm.item.OtherItems;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.capabilities.unlocked_recipes.UnlockedRecipesUtils;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.networking.packet.BeyonderRecipeDataAndScreenRenderRequestC2S;

import java.util.Collections;

public class UnlockRecipeItem extends Item {

    private final ItemStack potionLearned;

    public UnlockRecipeItem(Properties pProperties, ItemStack potionLearned) {
        super(pProperties);
        this.potionLearned = potionLearned;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        UnlockedRecipesUtils.getUnlockedRecipes(pPlayer).get().addRecipe(potionLearned);
        LOTMNetworkHandler.sendToServer(new BeyonderRecipeDataAndScreenRenderRequestC2S(Collections.singletonList(potionLearned)));
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public Rarity getRarity(ItemStack pStack) {
        return Rarity.EPIC;
    }
}
