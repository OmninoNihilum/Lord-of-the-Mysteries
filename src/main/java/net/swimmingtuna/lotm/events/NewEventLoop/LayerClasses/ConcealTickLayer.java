package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.capabilities.concealed_data.ConcealedUtils;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.AcidicRain;

public class ConcealTickLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        ConcealedUtils.timerTick(event.getEntity());
    }

    @Override
    public String getID() {
        return "ConcealEventID";
    }
}
