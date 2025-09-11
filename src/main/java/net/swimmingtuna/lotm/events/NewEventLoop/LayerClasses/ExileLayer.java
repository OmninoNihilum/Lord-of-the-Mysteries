package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice.Exile;


public class ExileLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        Exile.exileTickEvent(event);
    }

    @Override
    public String getID() {
        return "ExileEventID";
    }
}
