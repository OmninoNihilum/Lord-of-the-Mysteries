package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Warrior.FinishedItems.TwilightAccelerate;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;

public class TwilightAccelerateTickLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        TwilightAccelerate.twilightAccelerateTick(event);
    }

    @Override
    public String getID() {
        return "TwilightAccelerateTickEventID";
    }
}
