package net.swimmingtuna.lotm.capabilities.concealed_space;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.world.worlddata.ConcealedSpaceSavedData;

import java.util.Optional;
import java.util.UUID;

public class ConcealedSpaceData {
    private final UUID creator;
    private int sequence;
    private final BlockPos center;
    private BlockPos spawn;

    public ConcealedSpaceData(UUID creator,int sequence, BlockPos center) {
        this.creator = creator;
        this.sequence = sequence;
        this.center = center;
        this.spawn = center;
    }

    public ConcealedSpaceData(UUID creator, int sequence, BlockPos center, BlockPos spawn) {
        this.creator = creator;
        this.sequence = sequence;
        this.center = center;
        this.spawn = spawn;
    }

    private void updateSavedData(ServerLevel level) {
        ServerLevel overworld = level.getServer().overworld();
        ConcealedSpaceSavedData data = ConcealedSpaceSavedData.get(overworld);
        data.putSpace(this.creator, this);
    }

    public UUID getCreator() {
        return creator;
    }

    public int getSequence(){
        return this.sequence;
    }

    public BlockPos getCenter(){
        return this.center;
    }

    public BlockPos getSpawn(){
        return this.spawn;
    }

    public void setSequence(int sequence, ServerLevel level){
        this.sequence = sequence;
        updateSavedData(level);
    }

    public void setSpawn(BlockPos spawn, ServerLevel level){
        this.spawn = spawn;
        updateSavedData(level);
    }

    public boolean isCreator(UUID uuid){
        return this.creator.equals(uuid);
    }

    public static void createConcealedSpace(LivingEntity entity, BlockPos center){
        ConcealedSpaceData concealedSpace = new ConcealedSpaceData(entity.getUUID(), BeyonderUtil.getSequence(entity), center);
        concealedSpace.updateSavedData(entity.getServer().overworld());
    }

    public static Optional<ConcealedSpaceData> getConcealedSpace(UUID creator, ServerLevel level) {
        ServerLevel overworld = level.getServer().overworld();
        ConcealedSpaceSavedData data = ConcealedSpaceSavedData.get(overworld);
        return Optional.ofNullable(data.getSpace(creator));
    }

    public CompoundTag serialize(){
        CompoundTag tag = new CompoundTag();

        tag.putUUID("creator", this.creator);
        tag.putInt("sequence", this.sequence);
        tag.putLong("center", this.center.asLong());
        tag.putLong("spawn", this.spawn.asLong());

        return tag;
    }

    public static ConcealedSpaceData deserialize(CompoundTag tag){
        UUID creator = tag.getUUID("creator");
        int sequence = tag.getInt("sequence");
        BlockPos center = BlockPos.of(tag.getLong("center"));
        BlockPos spawn = BlockPos.of(tag.getLong("spawn"));

        return new ConcealedSpaceData(creator, sequence, center, spawn);
    }
}