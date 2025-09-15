package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.AcidicRain;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.DreamWalking;

public class DreamWalkingTickLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        DreamWalking.dreamWalkingTick(event);
    }

    @Override
    public String getID() {
        return "DreamWalkingEventID";
    }
}
