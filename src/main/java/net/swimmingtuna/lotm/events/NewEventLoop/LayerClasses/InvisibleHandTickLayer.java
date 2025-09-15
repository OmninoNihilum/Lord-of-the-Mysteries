package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice.InvisibleHand;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.AcidicRain;

public class InvisibleHandTickLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        InvisibleHand.invisibleHandTick(event);
    }

    @Override
    public String getID() {
        return "InvisibleHandTickEventID";
    }
}
