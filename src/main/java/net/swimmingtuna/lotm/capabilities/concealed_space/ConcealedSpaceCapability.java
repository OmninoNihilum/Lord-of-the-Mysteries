package net.swimmingtuna.lotm.capabilities.concealed_space;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

public class ConcealedSpaceCapability implements IConcealedSpaceCapability, INBTSerializable<CompoundTag> {
    private BlockPos exitConcealedSpace = BlockPos.ZERO;
    private ResourceKey<Level> exitDimensionConcealedSpace = Level.OVERWORLD;
    private UUID concealmentUUID = new UUID(0, 0);

    @Override
    public BlockPos exitConcealedSpace() {
        return exitConcealedSpace;
    }

    @Override
    public ResourceKey<Level> exitDimensionConcealedSpace() {
        return exitDimensionConcealedSpace;
    }

    @Override
    public UUID concealmentUUID() {
        return this.concealmentUUID;
    }

    @Override
    public void setExitConcealedSpace(BlockPos exitConcealedSpace) {
        this.exitConcealedSpace = exitConcealedSpace;
    }

    @Override
    public void setExitDimensionConcealedSpace(ResourceKey<Level> exitDimensionConcealedSpace) {
        this.exitDimensionConcealedSpace = exitDimensionConcealedSpace;
    }

    @Override
    public void setConcealmentUUID(UUID concealmentUUID) {
        this.concealmentUUID = concealmentUUID;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putLong("exitConcealedSpace", exitConcealedSpace.asLong());
        tag.putString("exitDimensionConcealedSpace", exitDimensionConcealedSpace.location().toString());
        tag.putUUID("concealmentUUID", this.concealmentUUID);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        exitConcealedSpace = BlockPos.of(tag.getLong("exitConcealedSpace"));

        String dimString = tag.getString("exitDimensionConcealedSpace");
        exitDimensionConcealedSpace = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dimString));

        concealmentUUID = tag.getUUID("concealmentUUID");
    }

    public void copyFrom(ConcealedSpaceCapability other) {
        this.exitConcealedSpace = other.exitConcealedSpace;
        this.exitDimensionConcealedSpace = other.exitDimensionConcealedSpace;
        this.concealmentUUID = other.concealmentUUID;
    }
}