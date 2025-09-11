package net.swimmingtuna.lotm.capabilities.sealed_data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.item.Item;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;
import net.swimmingtuna.lotm.entity.SpatialCageEntity;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.EFunctions;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.EventManager;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice.SpatialMaze;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.util.BeyonderUtil;

import javax.annotation.Nullable;
import java.util.*;

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
        return getSealedData(entity).map(data -> !data.sealsCreators().isEmpty()).orElse(false);
    }

    public static boolean hasSpecificSeal(LivingEntity entity, UUID sealUUID){
        return getSealedData(entity).map(data -> data.sealsCreators().containsKey(sealUUID)).orElse(false);
    }

    public static HashSet<UUID> getAllSeals(LivingEntity entity){
        HashSet<UUID> seals = new HashSet<>();
        entity.getCapability(SealedDataProvider.SEALED_DATA).ifPresent(data -> {
            seals.addAll(data.sealsCreators().keySet());
        });
        return seals;
    }

    public static int getHowManySeals(LivingEntity entity) {
        return getSealedData(entity).map(data -> data.sealsCreators().size()).orElse(0);
    }

    public static UUID getCreator(LivingEntity entity, UUID sealUUID) {
        return getSealedData(entity).map(data -> data.sealsCreators().get(sealUUID)).orElse(null);
    }

    public static String getCreatorName(LivingEntity entity, UUID sealUUID){
        return getSealedData(entity).map(data -> data.sealsCreatorsNames().get(sealUUID)).orElse("");
    }

    public static int getSealSequence(LivingEntity entity, UUID sealUUID){
        return getSealedData(entity).map(data -> data.sealsSequences().get(sealUUID)).orElse(10);
    }

    public static boolean hasTimer(LivingEntity entity, UUID sealUUID){
        return getSealedData(entity).map(data -> data.sealsHasTimers().get(sealUUID)).orElse(false);
    }

    public static int getTimer(LivingEntity entity, UUID sealUUID){
        return getSealedData(entity).map(data -> data.sealsTimers().get(sealUUID)).orElse(0);
    }

    public static HashSet<UUID> getAllSealsWithTimers(LivingEntity entity){
        return getSealedData(entity).map(ISealedDataCapability::sealsWithTimers).orElse(new HashSet<>());
    }

    public static boolean hasSealWithTimer(LivingEntity entity){
        return !getAllSealsWithTimers(entity).isEmpty();
    }

    public static boolean hasAbilitiesSealed(LivingEntity entity){
        boolean isSealed = false;
        for(ABILITIES_SEAL_TYPES type : getSealedAbilitiesTypes(entity)){
            if(type != ABILITIES_SEAL_TYPES.NONE){
                isSealed = true;
                break;
            }
        }
        return isSealed;
    }

    public static ABILITIES_SEAL_TYPES getSealedAbilitiesTypeSingleSeal(LivingEntity entity, UUID sealUUID){
        return getSealedData(entity).map(data -> data.sealedAbilitiesType().get(sealUUID)).orElse(ABILITIES_SEAL_TYPES.NONE);
    }

    public static HashSet<ABILITIES_SEAL_TYPES> getSealedAbilitiesTypes(LivingEntity entity){
        HashSet<ABILITIES_SEAL_TYPES> sets = new HashSet<>();
        entity.getCapability(SealedDataProvider.SEALED_DATA).ifPresent(data -> {
            sets.addAll(data.sealedAbilitiesType().values());
        });
        return sets;
    }

    public static HashSet<SimpleAbilityItem> getSealedAbilitiesSingleSealListType(LivingEntity entity, UUID sealUUID){
        return getSealedData(entity).map(data -> data.sealedAbilitiesList().get(sealUUID)).orElse(new HashSet<>());
    }

    public static HashSet<SimpleAbilityItem> getAllSealedAbilitiesListType(LivingEntity entity){
        HashSet<SimpleAbilityItem> abilities = new HashSet<>();
        entity.getCapability(SealedDataProvider.SEALED_DATA).ifPresent(data -> {
            for (Map.Entry<UUID, HashSet<SimpleAbilityItem>> abilitiesSet : data.sealedAbilitiesList().entrySet()){
                abilities.addAll(abilitiesSet.getValue());
            }
        });
        return abilities;
    }

    public static HashSet<Integer> getSealedAbilitiesSingleSealSequenceType(LivingEntity entity, UUID sealUUID){
        return getSealedData(entity).map(data -> data.sealedAbilitiesSequences().get(sealUUID)).orElse(new HashSet<>());
    }

    public static HashSet<Integer> getAllSealedAbilitiesSequenceType(LivingEntity entity){
        HashSet<Integer> sequences = new HashSet<>();
        entity.getCapability(SealedDataProvider.SEALED_DATA).ifPresent(data -> {
            for (Map.Entry<UUID, HashSet<Integer>> sequencesSet : data.sealedAbilitiesSequences().entrySet()){
                sequences.addAll(sequencesSet.getValue());
            }
        });
        return sequences;
    }

    public static SEAL_TYPES getSealType(LivingEntity entity, UUID sealUUID){
        return getSealedData(entity).map(data -> data.sealsTypes().get(sealUUID)).orElse(SEAL_TYPES.NONE);
    }

    public static void setCreator(LivingEntity entity, UUID sealUUID, UUID creatorUUID){
        entity.getCapability(SealedDataProvider.SEALED_DATA).ifPresent(data -> {
            data.setCreator(sealUUID, creatorUUID);
        });
    }

    public static void setCreatorName(LivingEntity entity, UUID sealUUID, String creatorName){
        entity.getCapability(SealedDataProvider.SEALED_DATA).ifPresent(data -> {
            data.setCreatorName(sealUUID, creatorName);
        });
    }

    public static void setSequence(LivingEntity entity, UUID sealUUID, int sequence){
        entity.getCapability(SealedDataProvider.SEALED_DATA).ifPresent(data -> {
            data.setSequence(sealUUID, sequence);
        });
    }

    public static void toggleTimer(LivingEntity entity, UUID sealUUID){
        entity.getCapability(SealedDataProvider.SEALED_DATA).ifPresent(data -> {
            data.toggleTimer(sealUUID);
        });
    }

    public static void setTimer(LivingEntity entity, UUID sealUUID, int timer){
        entity.getCapability(SealedDataProvider.SEALED_DATA).ifPresent(data -> {
            data.setTimer(sealUUID, timer * BeyonderUtil.getSealStrength(entity));
        });
    }

    public static void setSealedAbilitiesType(LivingEntity entity, UUID sealUUID, ABILITIES_SEAL_TYPES type){
        entity.getCapability(SealedDataProvider.SEALED_DATA).ifPresent(data -> {
            data.setSealedAbilitiesType(sealUUID, type);
        });
        if(type != ABILITIES_SEAL_TYPES.NONE) BeyonderUtil.removeTags(entity);
    }

    public static void setSealedAbilitiesList(LivingEntity entity, UUID sealUUID, HashSet<SimpleAbilityItem> abilities){
        entity.getCapability(SealedDataProvider.SEALED_DATA).ifPresent(data -> {
            data.setSealedAbilityList(sealUUID, abilities);
        });
    }

    public static void setSealedAbilitiesSequences(LivingEntity entity, UUID sealUUID, HashSet<Integer> sequences){
        entity.getCapability(SealedDataProvider.SEALED_DATA).ifPresent(data -> {
            data.setSealedAbilitiesSequence(sealUUID, sequences);
        });
    }

    public static void setSealType(LivingEntity entity, UUID sealUUID, SEAL_TYPES type){
        entity.getCapability(SealedDataProvider.SEALED_DATA).ifPresent(data -> {
            data.setSealType(sealUUID, type);
        });
    }

    public static boolean isValidUUID(LivingEntity entity, UUID sealUUID){
        return !getAllSeals(entity).contains(sealUUID);
    }

    private static UUID generateValidUUID(LivingEntity entity) {
        UUID sealUUID;
        do {
            sealUUID = UUID.randomUUID();
        } while (!isValidUUID(entity, sealUUID));
        return sealUUID;
    }

    public static HashSet<SimpleAbilityItem> getBlacklistedAbilities(HashSet<SimpleAbilityItem> abilities){
        HashSet<Item> allAbilitiesItem = new HashSet<>(BeyonderUtil.getAbilities());
        HashSet<SimpleAbilityItem> allAbilities = new HashSet<>();
        for(Item abilityItem : allAbilitiesItem){
            if (abilityItem instanceof SimpleAbilityItem ability) allAbilities.add(ability);
        }
        allAbilities.removeAll(abilities);
        return allAbilities;
    }

    public static UUID seal(LivingEntity entity, UUID creator, String creatorName, int sequence, SEAL_TYPES type){
        UUID sealUUID = generateValidUUID(entity);
        setCreator(entity, sealUUID, creator);
        setCreatorName(entity, sealUUID, creatorName);
        setSequence(entity, sealUUID, sequence);
        setSealType(entity, sealUUID, type);
        EventManager.addToRegularLoop(entity, EFunctions.SEAL.get());
        return sealUUID;
    }

    public static UUID seal(LivingEntity entity, UUID creator, String creatorName, int sequence, int timer, SEAL_TYPES type){
        UUID sealUUID = generateValidUUID(entity);
        setCreator(entity, sealUUID, creator);
        setCreatorName(entity, sealUUID, creatorName);
        setSequence(entity, sealUUID, sequence);
        toggleTimer(entity, sealUUID);
        setTimer(entity, sealUUID, timer);
        setSealType(entity, sealUUID, type);
        EventManager.addToRegularLoop(entity, EFunctions.SEAL.get());
        return sealUUID;
    }

    public static UUID seal(LivingEntity entity, UUID creator, String creatorName, int sequence, int timer, ABILITIES_SEAL_TYPES sealType, @Nullable HashSet<SimpleAbilityItem> abilities, boolean isBlacklist, @Nullable HashSet<Integer> sequences, SEAL_TYPES type){
        int wormCount = entity.getPersistentData().getInt("wormOfStar");
        UUID sealUUID = generateValidUUID(entity);
        setCreator(entity, sealUUID, creator);
        setCreatorName(entity, sealUUID, creatorName);
        setSequence(entity, sealUUID, sequence);
        toggleTimer(entity, sealUUID);
        setTimer(entity, sealUUID, timer);
        setSealedAbilitiesType(entity, sealUUID, sealType);
        if(abilities != null){
            if(!isBlacklist) setSealedAbilitiesList(entity, sealUUID, abilities);
            else setSealedAbilitiesList(entity, sealUUID, getBlacklistedAbilities(abilities));
        }
        if(sequences != null) setSealedAbilitiesSequences(entity, sealUUID, sequences);
        setSealType(entity, sealUUID, type);
        entity.getPersistentData().putInt("wormOfStar", wormCount);
        EventManager.addToRegularLoop(entity, EFunctions.SEAL.get());
        return sealUUID;
    }

    public static UUID seal(LivingEntity entity, UUID creator, String creatorName, int sequence, ABILITIES_SEAL_TYPES sealType, @Nullable HashSet<SimpleAbilityItem> abilities, boolean isBlacklist, @Nullable HashSet<Integer> sequences, SEAL_TYPES type){
        UUID sealUUID = generateValidUUID(entity);
        setCreator(entity, sealUUID, creator);
        setCreatorName(entity, sealUUID, creatorName);
        setSequence(entity, sealUUID, sequence);
        setSealedAbilitiesType(entity, sealUUID, sealType);
        if(abilities != null){
            if(!isBlacklist) setSealedAbilitiesList(entity, sealUUID, abilities);
            else setSealedAbilitiesList(entity, sealUUID, getBlacklistedAbilities(abilities));
        }
        if(sequences != null) setSealedAbilitiesSequences(entity, sealUUID, sequences);
        setSealType(entity, sealUUID, type);
        EventManager.addToRegularLoop(entity, EFunctions.SEAL.get());
        return sealUUID;
    }

    public static void removeSeal(LivingEntity entity, UUID sealUUID){
        SEAL_TYPES type = getSealType(entity, sealUUID);
        entity.getCapability(SealedDataProvider.SEALED_DATA).ifPresent(data -> {
            data.removeSeal(sealUUID);
        });
        doWhenRemoved(entity, type);
    }

    public static void removeAllSeals(LivingEntity entity){
        for(UUID seal : getAllSeals(entity)){
            removeSeal(entity, seal);
        }
    }

    public static void timerTick(LivingEntity entity){
        entity.getCapability(SealedDataProvider.SEALED_DATA).ifPresent(data -> {
            HashSet<UUID> sealsWithTimers = data.sealsWithTimers();
            if (sealsWithTimers.isEmpty()) {
                return;
            }
            List<UUID> sealsToRemove = new ArrayList<>();
            for (UUID seal : sealsWithTimers) {
                int currentTime = data.sealsTimers().getOrDefault(seal, 0);
                int newTime = currentTime - 1;

                if (newTime <= 0) {
                    sealsToRemove.add(seal);
                } else {
                    data.setTimer(seal, newTime);
                }
            }
            for (UUID sealToRemove : sealsToRemove) {
                removeSeal(entity, sealToRemove);
            }
        });
    }

    public static void doWhenRemoved(LivingEntity entity, SEAL_TYPES type){
        if(type.equals(SEAL_TYPES.SPATIAL_CAGE)){
            SpatialCageEntity.unsetSealed(entity);
        } else if(type.equals(SEAL_TYPES.SPATIAL_MAZE)) {
            SpatialMaze.removeSeal(entity);
        }
        if (getAllSeals(entity).isEmpty()) {
            EventManager.removeFromRegularLoop(entity, EFunctions.SEAL.get());
        }
    }

    public static int getBreakFreeCost(LivingEntity breaker, LivingEntity sealed, UUID sealUUID){
        int breakerSequence = BeyonderUtil.getSequence(breaker);
        int sealSequence = getSealSequence(sealed, sealUUID);
        if(BeyonderUtil.currentPathwayAndSequenceMatchesNoException(breaker, BeyonderClassInit.APPRENTICE.get(), 2)) {
            breakerSequence--;
            sealSequence++;
        }
        int cost = BREAK_SEAL_COST.getOrDefault(sealSequence, 0);
        cost = (int) Math.max(0, cost*(1+0.2*(breakerSequence - sealSequence)));
        return cost;
    }

    public static BeyonderClass pathwayByType(SEAL_TYPES type){
        BeyonderClass pathway;

        if(type.equals(SEAL_TYPES.TSUNAMI_SEAL)) pathway = BeyonderClassInit.SAILOR.get();
        else if(type.equals(SEAL_TYPES.STORM_SEAL)) pathway = BeyonderClassInit.SAILOR.get();
        else if(type.equals(SEAL_TYPES.SPATIAL_CAGE)) pathway = BeyonderClassInit.APPRENTICE.get();
        else if(type.equals(SEAL_TYPES.PLANES_WALKER_SEAL)) pathway = BeyonderClassInit.APPRENTICE.get();
        else if(type.equals(SEAL_TYPES.SPATIAL_MAZE)) pathway = BeyonderClassInit.APPRENTICE.get();
        else pathway = BeyonderClassInit.APPRENTICE.get();

        return pathway;
    }

    public static String nameByType(SEAL_TYPES type){
        String name;

        if(type.equals(SEAL_TYPES.TSUNAMI_SEAL)) name = "Tsunami Seal";
        else if(type.equals(SEAL_TYPES.STORM_SEAL)) name = "Storm Seal";
        else if(type.equals(SEAL_TYPES.SPATIAL_CAGE)) name = "Spatial Cage Seal";
        else if(type.equals(SEAL_TYPES.PLANES_WALKER_SEAL)) name = "Abilities Seal";
        else if(type.equals(SEAL_TYPES.SPATIAL_MAZE)) name = "Spatial Maze Seal";
        else name = "Seal";

        return name;
    }
}