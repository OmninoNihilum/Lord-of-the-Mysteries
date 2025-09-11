package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.RagingBlows;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;

public class RagingBlowsTickLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        RagingBlows.ragingBlowsTick(event.getEntity());
    }

    @Override
    public String getID() {
        return "RagingBlowsEventID";
    }
}
