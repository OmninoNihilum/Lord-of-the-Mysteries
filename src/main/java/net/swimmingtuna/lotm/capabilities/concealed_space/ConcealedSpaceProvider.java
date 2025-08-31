package net.swimmingtuna.lotm.capabilities.concealed_space;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ConcealedSpaceProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<IConcealedSpaceCapability> CONCEALED_SPACE = CapabilityManager.get(new CapabilityToken<IConcealedSpaceCapability>() {});

    private ConcealedSpaceCapability concealedSpace = null;
    private final LazyOptional<IConcealedSpaceCapability> optional = LazyOptional.of(this::createConcealedSpace);

    private ConcealedSpaceCapability createConcealedSpace() {
        if (this.concealedSpace == null) {
            this.concealedSpace = new ConcealedSpaceCapability();
        }
        return this.concealedSpace;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CONCEALED_SPACE) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return createConcealedSpace().serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createConcealedSpace().deserializeNBT(nbt);
    }
}