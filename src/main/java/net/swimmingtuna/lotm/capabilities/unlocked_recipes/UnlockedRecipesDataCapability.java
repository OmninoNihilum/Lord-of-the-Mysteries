package net.swimmingtuna.lotm.capabilities.unlocked_recipes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

public final class UnlockedRecipesDataCapability implements IUnlockedRecipesDataCapability, INBTSerializable<CompoundTag> {
    private List<ItemStack> unlockedRecipes = new ArrayList<>();
    private final String NBT_KEY = "unlocked_potion_recipes";

    public List<String> getUnlockedRecipesNames() {
        return unlockedRecipes.stream().map(itemStack -> itemStack.getHoverName().getString()).toList();
    }

    @Override
    public List<ItemStack> unlockedRecipes() {
        return unlockedRecipes;
    }

    @Override
    public void addRecipe(ItemStack stack) {
        if (getUnlockedRecipesNames().contains(stack.getDisplayName().getString())) return;
        unlockedRecipes.add(stack);
    }

    @Override
    public void addRecipes(List<ItemStack> stacks) {
        for (ItemStack stack : stacks) {
            if (getUnlockedRecipesNames().contains(stack.getDisplayName().getString())) continue;
            unlockedRecipes.add(stack);
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (ItemStack stack : unlockedRecipes) {
            list.add(stack.save(new CompoundTag()));
        }
        tag.put(NBT_KEY, list);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        unlockedRecipes.clear();
        if (nbt.contains(NBT_KEY, Tag.TAG_LIST)) {
            ListTag list = nbt.getList(NBT_KEY, Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                unlockedRecipes.add(ItemStack.of(list.getCompound(i)));
            }
        }
    }
    public void copyFrom(UnlockedRecipesDataCapability other) {
        this.unlockedRecipes = new ArrayList<>(other.unlockedRecipes);
    }
}
