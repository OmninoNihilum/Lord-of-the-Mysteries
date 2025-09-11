package net.swimmingtuna.lotm.events.NewEventLoop.EventManager;

import net.minecraftforge.event.entity.living.LivingEvent;

public interface IFunction {
    void use(LivingEvent.LivingTickEvent event);

    String getID();
}
