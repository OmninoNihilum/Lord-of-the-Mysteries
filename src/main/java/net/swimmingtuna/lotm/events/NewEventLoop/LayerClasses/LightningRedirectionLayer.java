package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.LightningRedirection;


public class LightningRedirectionLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        LightningRedirection.lightningRedirectionTick(event);
    }

    @Override
    public String getID() {
        return "LightningRedirectionEventID";
    }
}
