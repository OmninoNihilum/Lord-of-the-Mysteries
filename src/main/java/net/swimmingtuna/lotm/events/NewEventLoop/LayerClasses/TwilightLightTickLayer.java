package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Warrior.FinishedItems.TwilightLight;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;

public class TwilightLightTickLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        TwilightLight.twilightLightTick(event);
    }

    @Override
    public String getID() {
        return "TwilightLightTickEventID";
    }
}
