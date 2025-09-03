package net.swimmingtuna.lotm.capabilities.unlocked_recipes;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UnlockedRecipesDataProvider  implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<IUnlockedRecipesDataCapability> UNLOCKED_DATA = CapabilityManager.get(new CapabilityToken<>() {});

    private UnlockedRecipesDataCapability unlockedRecipesDataCapability = null;
    private final LazyOptional<IUnlockedRecipesDataCapability> optional = LazyOptional.of(this::createUnlockedRecipesData);

    private UnlockedRecipesDataCapability createUnlockedRecipesData() {
        if (this.unlockedRecipesDataCapability == null) {
            this.unlockedRecipesDataCapability = new UnlockedRecipesDataCapability();
        }
        return this.unlockedRecipesDataCapability;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == UNLOCKED_DATA){
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return createUnlockedRecipesData().serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createUnlockedRecipesData().deserializeNBT(nbt);
    }
}
