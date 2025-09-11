package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Warrior.FinishedItems.GlobeOfTwilight;


public class GlobeOfTwilightTickLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        GlobeOfTwilight.globeOfTwilightTick(event);
    }

    @Override
    public String getID() {
        return "GlobeOfTwilightTickEventID";
    }
}
