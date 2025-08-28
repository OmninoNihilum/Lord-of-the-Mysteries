package net.swimmingtuna.lotm.world.worlddata;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class SealStrengthenData extends SavedData {
    private static final String DATA_NAME = "seal_strengthen";
    private int calamityEnhancement = 1;

    public static SealStrengthenData create() {
        return new SealStrengthenData();
    }

    public static SealStrengthenData load(CompoundTag nbt) {
        SealStrengthenData data = new SealStrengthenData();
        data.calamityEnhancement = nbt.getInt("sealStrengthen");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.putInt("sealStrengthen", calamityEnhancement);
        return nbt;
    }

    public int getSealStrengthen() {
        return calamityEnhancement;
    }

    public void setSealStrengthen(int value) {
        this.calamityEnhancement = value;
        setDirty();
    }

    public static SealStrengthenData getInstance(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                SealStrengthenData::load,
                SealStrengthenData::create,
                DATA_NAME
        );
    }
}
