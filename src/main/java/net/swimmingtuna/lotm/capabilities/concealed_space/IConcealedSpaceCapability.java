package net.swimmingtuna.lotm.capabilities.concealed_space;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.UUID;

public interface IConcealedSpaceCapability {
    BlockPos exitConcealedSpace();
    ResourceKey<Level> exitDimensionConcealedSpace();
    UUID concealmentUUID();

    void setExitConcealedSpace(BlockPos exitConcealedSpace);
    void setExitDimensionConcealedSpace(ResourceKey<Level> exitDimensionConcealedSpace);
    void setConcealmentUUID(UUID uuid);
}