package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.AcidicRain;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.DreamWeaving;

public class DreamWeavingLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        DreamWeaving.dreamWeaving(event.getEntity());
    }

    @Override
    public String getID() {
        return "DreamWeavingEventID";
    }
}
