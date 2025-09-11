package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.DreamIntoReality;


public class DreamIntoRealityLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        DreamIntoReality.dreamIntoReality(event.getEntity());
    }

    @Override
    public String getID() {
        return "DreamIntoRealityEventID";
    }
}
