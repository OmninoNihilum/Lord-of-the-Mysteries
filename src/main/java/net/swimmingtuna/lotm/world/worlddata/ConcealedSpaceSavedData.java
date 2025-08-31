package net.swimmingtuna.lotm.world.worlddata;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.swimmingtuna.lotm.capabilities.concealed_space.ConcealedSpaceData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConcealedSpaceSavedData extends SavedData {
    private final Map<UUID, ConcealedSpaceData> spaces = new HashMap<>();
    private static final String DATA_NAME = "concealed_space_data";

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();

        for (Map.Entry<UUID, ConcealedSpaceData> entry : spaces.entrySet()) {
            CompoundTag spaceTag = entry.getValue().serialize();
            spaceTag.putUUID("creator", entry.getKey());
            list.add(spaceTag);
        }

        tag.put("spaces", list);
        return tag;
    }

    public static ConcealedSpaceSavedData load(CompoundTag tag) {
        ConcealedSpaceSavedData data = new ConcealedSpaceSavedData();

        ListTag list = tag.getList("spaces", CompoundTag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag spaceTag = list.getCompound(i);
            ConcealedSpaceData space = ConcealedSpaceData.deserialize(spaceTag);
            data.spaces.put(space.getCreator(), space);
        }

        return data;
    }

    public void putSpace(UUID creator, ConcealedSpaceData space) {
        spaces.put(creator, space);
        setDirty();
    }

    public ConcealedSpaceData getSpace(UUID creator) {
        return spaces.get(creator);
    }

    public boolean hasSpace(UUID creator) {
        return spaces.containsKey(creator);
    }

    public void removeSpace(UUID creator) {
        spaces.remove(creator);
        setDirty();
    }

    public Map<UUID, ConcealedSpaceData> getAllSpaces() {
        return spaces;
    }

    public static ConcealedSpaceSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                ConcealedSpaceSavedData::load,
                ConcealedSpaceSavedData::new,
                DATA_NAME
        );
    }
}