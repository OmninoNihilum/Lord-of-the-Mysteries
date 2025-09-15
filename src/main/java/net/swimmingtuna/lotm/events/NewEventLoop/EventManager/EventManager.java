package net.swimmingtuna.lotm.events.NewEventLoop.EventManager;

import net.minecraft.world.entity.LivingEntity;
import net.swimmingtuna.lotm.LOTM;

public class EventManager {

    public static void addToRegularLoop(LivingEntity entity, IFunction func){
        entity.getCapability(EventsProvider.EVENTS_DATA).ifPresent(cap -> {
            LOTM.LOGGER.info("Adding event " + func.toString());
            cap.addR(func);
        });
        if (!entity.getCapability(EventsProvider.EVENTS_DATA).isPresent()) {
            LOTM.LOGGER.info("NOT PRESENT");
        }
    }

    public static void removeFromRegularLoop(LivingEntity entity, IFunction func){
        entity.getCapability(EventsProvider.EVENTS_DATA).ifPresent(cap -> {
            LOTM.LOGGER.info("Removing event " + func.toString());
            cap.markDeleteR(func);
        });
        if (!entity.getCapability(EventsProvider.EVENTS_DATA).isPresent()) {
            LOTM.LOGGER.info("NOT REMOVE `PRESENT");
        }
    }

    public static void addToWorldLoop(LivingEntity entity, IFunction func){
        entity.getCapability(EventsProvider.EVENTS_DATA).ifPresent(cap -> {
            cap.addW(func);
        });
    }

    public static void removeFromWorldLoop(LivingEntity entity, IFunction func){
        entity.getCapability(EventsProvider.EVENTS_DATA).ifPresent(cap -> {
            cap.markDeleteW(func);
        });
    }
}
