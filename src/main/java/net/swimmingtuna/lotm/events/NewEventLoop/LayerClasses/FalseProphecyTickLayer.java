package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Monster.FalseProphecy;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.AcidicRain;

public class FalseProphecyTickLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        FalseProphecy.falseProphecyTick(event.getEntity());
    }

    @Override
    public String getID() {
        return "FalseProphecyEventID";
    }
}
