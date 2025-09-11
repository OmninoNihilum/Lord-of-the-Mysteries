package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Warrior.FinishedItems.TwilightFreeze;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;

public class TwilightFreezeTickLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        TwilightFreeze.twilightFreezeTick(event);
    }

    @Override
    public String getID() {
        return "TwilightFreezeTickEventID";
    }
}
