package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.WindManipulationSense;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;

public class WindManipulationSenseLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        WindManipulationSense.windManipulationSense(event.getEntity());
    }

    @Override
    public String getID() {
        return "RagingComboEventID";
    }
}
