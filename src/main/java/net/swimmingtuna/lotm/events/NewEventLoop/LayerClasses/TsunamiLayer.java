package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.Tsunami;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;

public class TsunamiLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        Tsunami.tsunami(event.getEntity());
    }

    @Override
    public String getID() {
        return "TsunamiEventID";
    }
}
