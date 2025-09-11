package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.Hurricane;


public class HurricaneLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        Hurricane.hurricane(event.getEntity());
    }

    @Override
    public String getID() {
        return "HurricaneEventID";
    }
}
