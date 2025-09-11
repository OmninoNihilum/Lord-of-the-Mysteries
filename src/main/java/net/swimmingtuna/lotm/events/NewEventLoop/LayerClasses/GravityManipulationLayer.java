package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice.GravityManipulation;


public class GravityManipulationLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        GravityManipulation.gravityManipulationTickEvent(event);
    }

    @Override
    public String getID() {
        return "GravityManipulationEventID";
    }
}
