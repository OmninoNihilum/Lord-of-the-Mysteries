package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Monster.PsycheStorm;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.AcidicRain;

public class PsycheStormTickLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        PsycheStorm.psycheStormTick(event);
    }

    @Override
    public String getID() {
        return "PsycheStormEventID";
    }
}
