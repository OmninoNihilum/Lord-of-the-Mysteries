package net.swimmingtuna.lotm.capabilities.sealed_data;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

public class SealedDataCapability implements ISealedDataCapability, INBTSerializable<CompoundTag> {
    private boolean isSealed = false;
    private UUID sealCreator = new UUID(0, 0);
    private int sealSequence = 9;
    private boolean sealedAbilities = false;

    @Override
    public boolean isSealed() {
        return isSealed;
    }

    @Override
    public UUID sealCreator() {
        return sealCreator;
    }

    @Override
    public int sealSequence() {
        return sealSequence;
    }

    @Override
    public boolean sealedAbilities() {
        return sealedAbilities;
    }

    @Override
    public void setSealed(boolean sealed) {
        this.isSealed = sealed;
    }

    @Override
    public void setCreator(UUID creator) {
        this.sealCreator = creator;
    }

    @Override
    public void setSequence(int sequence) {
        this.sealSequence = sequence;
    }

    @Override
    public void setSealedAbilities(boolean sealed) {
        this.sealedAbilities = sealed;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putBoolean("isSealed", isSealed);
        tag.putUUID("sealCreator", sealCreator);
        tag.putInt("sealSequence", sealSequence);
        tag.putBoolean("sealedAbilities", sealedAbilities);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        isSealed = tag.getBoolean("isSealed");
        sealCreator = tag.getUUID("sealCreator");
        sealSequence = tag.getInt("sealSequence");
        sealedAbilities = tag.getBoolean("sealedAbilities");
    }

    public void copyFrom(SealedDataCapability other){
        isSealed = other.isSealed;
        sealCreator = other.sealCreator;
        sealSequence = other.sealSequence;
        sealedAbilities = other.sealedAbilities;
    }
}