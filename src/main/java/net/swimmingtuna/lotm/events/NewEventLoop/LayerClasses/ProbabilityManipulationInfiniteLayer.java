package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Monster.ProbabilityManipulationInfiniteMisfortune;

public class ProbabilityManipulationInfiniteLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        ProbabilityManipulationInfiniteMisfortune.inifniteMisfortune(event);
    }

    @Override
    public String getID() {
        return "ProbabilityManipulationInfiniteEventID";
    }
}
