package net.swimmingtuna.lotm.events.NewEventLoop.PathwaysPassiveEvents;

import net.minecraft.world.entity.LivingEntity;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.EFunctions;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.EventManager;


public class WarriorPassiveEvents {
    public static void removeAllEvents(LivingEntity entity) {
        EventManager.removeFromWorldLoop(entity, EFunctions.WARRIOR_TICK.get());

        EventManager.removeFromRegularLoop(entity, EFunctions.REGENERATE_SPIRITUALITY.get());
    }

    public static void addAllEvents(LivingEntity entity, int sequence) {
        EventManager.addToWorldLoop(entity, EFunctions.WARRIOR_TICK.get());

        EventManager.addToRegularLoop(entity, EFunctions.REGENERATE_SPIRITUALITY.get());
    }
}
