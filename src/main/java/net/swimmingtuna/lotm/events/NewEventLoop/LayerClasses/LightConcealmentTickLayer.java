package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.DreamWeaving;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Warrior.FinishedItems.LightConcealment;

public class LightConcealmentTickLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        LightConcealment.lightConcealmentTick(event);
    }

    @Override
    public String getID() {
        return "LightConcealmentEventID";
    }
}
