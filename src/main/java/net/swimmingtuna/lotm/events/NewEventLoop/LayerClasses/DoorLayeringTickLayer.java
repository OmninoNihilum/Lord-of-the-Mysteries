package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice.DoorLayering;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.AcidicRain;

public class DoorLayeringTickLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        DoorLayering.doorLayeringTick(event);
    }

    @Override
    public String getID() {
        return "DoorLayeringEventID";
    }
}
