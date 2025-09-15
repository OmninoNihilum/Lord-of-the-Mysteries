package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Monster.CycleOfFate;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.AcidicRain;

public class CycleOfFateTickLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        CycleOfFate.cycleOfFateTickEvent(event);
    }

    @Override
    public String getID() {
        return "CycleOfFateEventID";
    }
}
