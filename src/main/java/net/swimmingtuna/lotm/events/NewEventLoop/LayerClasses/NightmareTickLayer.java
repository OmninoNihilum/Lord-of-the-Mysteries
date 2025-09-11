package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.Nightmare;


public class NightmareTickLayer implements IFunction {

    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        Nightmare.nightmareTick(event.getEntity());
    }

    @Override
    public String getID() {
        return "NightmareTickEventID";
    }
}
