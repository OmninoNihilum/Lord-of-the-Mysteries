package net.swimmingtuna.lotm.capabilities.concealed_data;

import net.minecraft.world.entity.LivingEntity;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.EFunctions;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.EventManager;

import javax.annotation.Nullable;
import java.util.*;

public class ConcealedUtils {
    private static final Map<Integer, Integer> BREAK_CONCEALMENT_COST = new HashMap<>();

    static{
        BREAK_CONCEALMENT_COST.put(9, 100);
        BREAK_CONCEALMENT_COST.put(8, 150);
        BREAK_CONCEALMENT_COST.put(7, 300);
        BREAK_CONCEALMENT_COST.put(6, 400);
        BREAK_CONCEALMENT_COST.put(5, 700);
        BREAK_CONCEALMENT_COST.put(4, 1500);
        BREAK_CONCEALMENT_COST.put(3, 3000);
        BREAK_CONCEALMENT_COST.put(2, 6000);
        BREAK_CONCEALMENT_COST.put(1, 10000);
        BREAK_CONCEALMENT_COST.put(0, 20000);
        BREAK_CONCEALMENT_COST.put(-1, 30000);
    }

    public static Optional<IConcealedDataCapability> getConcealedData(LivingEntity entity) {
        return entity.getCapability(ConcealedDataProvider.CONCEALED_DATA).resolve();
    }

    public static boolean isConcealed(LivingEntity entity){
        return getConcealedData(entity).map(data -> !data.concealmentsCreators().isEmpty()).orElse(false);
    }

    public static boolean hasSpecificConcealment(LivingEntity entity, UUID concealmentUUID){
        return getConcealedData(entity).map(data -> data.concealmentsCreators().containsKey(concealmentUUID)).orElse(false);
    }

    public static HashSet<UUID> getAllConcealments(LivingEntity entity){
        HashSet<UUID> concealments = new HashSet<>();
        entity.getCapability(ConcealedDataProvider.CONCEALED_DATA).ifPresent(data -> {
            concealments.addAll(data.concealmentsCreators().keySet());
        });
        return concealments;
    }

    public static int getHowManyConcealments(LivingEntity entity){
        return getConcealedData(entity).map(data -> data.concealmentsCreators().size()).orElse(0);
    }

    @Nullable
    public static UUID getCreator(LivingEntity entity, UUID concealmentCreator){
        return getConcealedData(entity).map(data -> data.concealmentsCreators().get(concealmentCreator)).orElse(null);
    }

    public static int getConcealmentSequence(LivingEntity entity, UUID concealmentUUID){
        return getConcealedData(entity).map(data -> data.concealmentsSequences().get(concealmentUUID)).orElse(10);
    }

    public static boolean hasTimer(LivingEntity entity, UUID concealmentUUID){
        return getConcealedData(entity).map(data -> data.concealmentsHasTimers().get(concealmentUUID)).orElse(false);
    }

    public static int getTimer(LivingEntity entity, UUID concealmentUUID){
        return getConcealedData(entity).map(data -> data.concealmentsTimers().get(concealmentUUID)).orElse(0);
    }

    public static HashSet<UUID> getAllConcealmentsWithTimers(LivingEntity entity){
        return getConcealedData(entity).map(IConcealedDataCapability::concealmentsWithTimers).orElse(new HashSet<>());
    }

    public static CONCEALMENT_TYPES getConcealmentType(LivingEntity entity, UUID concealmentUUID){
        return getConcealedData(entity).map(data -> data.concealmentsTypes().get(concealmentUUID)).orElse(CONCEALMENT_TYPES.NONE);
    }

    public static void setCreator(LivingEntity entity, UUID concealmentUUID, UUID creatorUUID){
        entity.getCapability(ConcealedDataProvider.CONCEALED_DATA).ifPresent(data -> {
            data.setCreator(concealmentUUID, creatorUUID);
        });
    }

    public static void setSequence(LivingEntity entity, UUID concealmentUUID, int sequence){
        entity.getCapability(ConcealedDataProvider.CONCEALED_DATA).ifPresent(data -> {
            data.setSequence(concealmentUUID, sequence);
        });
    }

    public static void toggleTimer(LivingEntity entity, UUID concealmentUUID){
        entity.getCapability(ConcealedDataProvider.CONCEALED_DATA).ifPresent(data -> {
            data.toggleTimer(concealmentUUID);
        });
    }

    public static void setTimer(LivingEntity entity, UUID concealmentUUID, int timer){
        entity.getCapability(ConcealedDataProvider.CONCEALED_DATA).ifPresent(data -> {
            data.setTimer(concealmentUUID, timer);
        });
    }

    public static void setConcealmentType(LivingEntity entity, UUID concealmentUUID, CONCEALMENT_TYPES type){
        entity.getCapability(ConcealedDataProvider.CONCEALED_DATA).ifPresent(data -> data.setConcealmentType(concealmentUUID, type));
    }

    public static boolean isValidUUID(LivingEntity entity, UUID concealmentUUID){
        return !getAllConcealments(entity).contains(concealmentUUID);
    }

    public static UUID generateValidUUID(LivingEntity entity){
        UUID concealedUUID;
        do{
            concealedUUID = UUID.randomUUID();
        } while (!isValidUUID(entity, concealedUUID));
        return concealedUUID;
    }

    public static UUID conceal(LivingEntity entity, UUID creator, int sequence, CONCEALMENT_TYPES type){
        UUID concealmentUUID = generateValidUUID(entity);
        setCreator(entity, concealmentUUID, creator);
        setSequence(entity, concealmentUUID, sequence);
        setConcealmentType(entity, concealmentUUID, type);
        EventManager.addToRegularLoop(entity, EFunctions.CONCEAL_TIMER.get());
        return concealmentUUID;
    }

    public static UUID conceal(LivingEntity entity, UUID creator, int sequence, int timer, CONCEALMENT_TYPES type){
        UUID concealmentUUID = generateValidUUID(entity);
        setCreator(entity, concealmentUUID, creator);
        setSequence(entity, concealmentUUID, sequence);
        toggleTimer(entity, concealmentUUID);
        setTimer(entity, concealmentUUID, timer);
        setConcealmentType(entity, concealmentUUID, type);
        EventManager.addToRegularLoop(entity, EFunctions.CONCEAL_TIMER.get());
        return concealmentUUID;
    }

    public static void removeConcealment(LivingEntity entity, UUID concealmentUUID){
        entity.getCapability(ConcealedDataProvider.CONCEALED_DATA).ifPresent(data -> {
            data.removeConcealment(concealmentUUID);
        });
    }

    public static void timerTick(LivingEntity entity){

        entity.getCapability(ConcealedDataProvider.CONCEALED_DATA).ifPresent(data -> {
            HashSet<UUID> concealmentsWithTimers = data.concealmentsWithTimers();
            if(concealmentsWithTimers.isEmpty()) return;
            List<UUID> concealmentsToRemove = new ArrayList<>();
            for(UUID concealment : concealmentsWithTimers){
                int currentTime = data.concealmentsTimers().getOrDefault(concealment, 0);
                int newTime = currentTime - 1;

                if(newTime <= 0){
                    concealmentsToRemove.add(concealment);
                }else{
                    data.setTimer(concealment, newTime);
                }
            }
            for(UUID concealment : concealmentsToRemove){
                data.removeConcealment(concealment);
            }
            if(getAllConcealments(entity).isEmpty()){
                EventManager.removeFromRegularLoop(entity, EFunctions.CONCEAL_TIMER.get());
            }
        });
    }

    public static int getBreakFreeCost(int sequence){
        return BREAK_CONCEALMENT_COST.getOrDefault(sequence, 0);
    }
}