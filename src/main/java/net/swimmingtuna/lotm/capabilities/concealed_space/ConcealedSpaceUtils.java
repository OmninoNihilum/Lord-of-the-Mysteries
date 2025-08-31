package net.swimmingtuna.lotm.capabilities.concealed_space;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.capabilities.concealed_data.CONCEALMENT_TYPES;
import net.swimmingtuna.lotm.capabilities.concealed_data.ConcealedUtils;
import net.swimmingtuna.lotm.world.worlddata.ConcealedSpaceSavedData;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class ConcealedSpaceUtils {

    public static Optional<IConcealedSpaceCapability> getConcealedData(LivingEntity entity) {
        return entity.getCapability(ConcealedSpaceProvider.CONCEALED_SPACE).resolve();
    }

    public static Optional<ConcealedSpaceData> getConcealedSpace(LivingEntity entity){
        if (entity.level().isClientSide()) return Optional.empty();
        return ConcealedSpaceData.getConcealedSpace(entity.getUUID(), entity.getServer().overworld());
    }

    public static boolean hasConcealedSpace(LivingEntity entity){
        return getConcealedSpace(entity).isPresent();
    }

    public static void createConcealedSpace(LivingEntity entity, BlockPos center){
        ConcealedSpaceData.createConcealedSpace(entity, center);
    }

    public static int getConcealedSpaceSequence(LivingEntity entity){
        return getConcealedSpace(entity).map(ConcealedSpaceData::getSequence).orElse(10);
    }

    public static void setSequence(LivingEntity entity, int sequence){
        getConcealedSpace(entity).ifPresent(space -> space.setSequence(sequence, entity.getServer().overworld()));
    }

    public static BlockPos getConcealedSpaceCenter(LivingEntity entity){
        return getConcealedSpace(entity).map(ConcealedSpaceData::getCenter).orElse(BlockPos.ZERO);
    }

    public static BlockPos getConcealedSpaceSpawn(LivingEntity entity){
        return getConcealedSpace(entity).map(ConcealedSpaceData::getSpawn).orElse(BlockPos.ZERO);
    }

    public static void setConcealedSpaceSpawn(LivingEntity entity, BlockPos spawn){
        getConcealedSpace(entity).ifPresent(space -> space.setSpawn(spawn, entity.getServer().overworld()));
    }

    public static BlockPos getConcealedSpaceExit(LivingEntity entity){
        return getConcealedData(entity).map(IConcealedSpaceCapability::exitConcealedSpace).orElse(BlockPos.ZERO);
    }

    public static void setConcealedSpaceExit(LivingEntity entity, BlockPos exit){
        entity.getCapability(ConcealedSpaceProvider.CONCEALED_SPACE).ifPresent(data ->{
            data.setExitConcealedSpace(exit);
        });
    }

    public static Level getConcealedSpaceExitDimension(LivingEntity entity) {
        MinecraftServer server = entity.getServer();
        if (server == null) return null;
        return getConcealedData(entity).map(IConcealedSpaceCapability::exitDimensionConcealedSpace).map(server::getLevel).orElse(null);
    }

    public static void setConcealedSpaceExitDimension(LivingEntity entity, Level exit){
        entity.getCapability(ConcealedSpaceProvider.CONCEALED_SPACE).ifPresent(data ->{
            data.setExitDimensionConcealedSpace(exit.dimension());
        });
    }

    public static boolean insideConcealedSpace(LivingEntity entity){
        boolean inside = false;
        for(UUID concealment : ConcealedUtils.getAllConcealments(entity)){
            if(ConcealedUtils.getConcealmentType(entity, concealment).equals(CONCEALMENT_TYPES.CONCEALED_SPACE)){
                inside = true;
                break;
            }
        }
        return inside;
    }

    public static UUID getSpaceUUID(LivingEntity entity){
        return getConcealedData(entity).map(IConcealedSpaceCapability::concealmentUUID).orElse(new UUID(0, 0));
    }

    public static void setSpaceUUID(LivingEntity entity, UUID uuid){
        entity.getCapability(ConcealedSpaceProvider.CONCEALED_SPACE).ifPresent(data -> data.setConcealmentUUID(uuid));
    }
}