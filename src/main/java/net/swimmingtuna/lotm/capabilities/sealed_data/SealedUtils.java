package net.swimmingtuna.lotm.capabilities.sealed_data;

import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SealedUtils {
    private static final Map<Integer, Integer> BREAK_SEAL_COST = new HashMap<>();

    static{
        BREAK_SEAL_COST.put(9, 100);
        BREAK_SEAL_COST.put(8, 150);
        BREAK_SEAL_COST.put(7, 300);
        BREAK_SEAL_COST.put(6, 400);
        BREAK_SEAL_COST.put(5, 700);
        BREAK_SEAL_COST.put(4, 1500);
        BREAK_SEAL_COST.put(3, 3000);
        BREAK_SEAL_COST.put(2, 6000);
        BREAK_SEAL_COST.put(1, 10000);
        BREAK_SEAL_COST.put(0, 20000);
        BREAK_SEAL_COST.put(-1, 30000);
    }

    public static Optional<ISealedDataCapability> getSealedData(LivingEntity entity) {
        return entity.getCapability(SealedDataProvider.SEALED_DATA).resolve();
    }

    public static boolean isSealed(LivingEntity entity){
        return getSealedData(entity).map(ISealedDataCapability::isSealed).orElse(false);
    }

    public static void setSealed(LivingEntity entity, Boolean isSealed){
        entity.getCapability(SealedDataProvider.SEALED_DATA).ifPresent(data -> {
            data.setSealed(isSealed);
        });
    }

    public static UUID sealCreator(LivingEntity entity){
        return getSealedData(entity).map(ISealedDataCapability::sealCreator).orElse(new UUID(0, 0));
    }

    public static void setCreator(LivingEntity entity, UUID creator){
        entity.getCapability(SealedDataProvider.SEALED_DATA).ifPresent(data -> {
            data.setCreator(creator);
        });
    }

    public static int sealSequence(LivingEntity entity){
        return getSealedData(entity).map(ISealedDataCapability::sealSequence).orElse(9);
    }

    public static void setSequence(LivingEntity entity, int sequence){
        entity.getCapability(SealedDataProvider.SEALED_DATA).ifPresent(data ->{
            data.setSequence(sequence);
        });
    }

    public static boolean sealedAbilities(LivingEntity entity){
        return getSealedData(entity).map(ISealedDataCapability::sealedAbilities).orElse(false);
    }

    public static void setSealedAbilities(LivingEntity entity, Boolean sealed){
        entity.getCapability(SealedDataProvider.SEALED_DATA).ifPresent(data -> {
            data.setSealedAbilities(sealed);
        });
    }

    public static int getBreakFreeCost(int sequence){
        return BREAK_SEAL_COST.get(sequence);
    }
}