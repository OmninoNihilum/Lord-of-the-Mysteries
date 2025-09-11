package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice.DoorMirage;


public class DoorMirageLayer implements IFunction {

    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        DoorMirage.mirageTick(event.getEntity());
    }

    @Override
    public String getID() {
        return "DoorMirageEventID";
    }
}
