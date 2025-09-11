package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.CalamityIncarnationTsunami;


public class CalamityIncarnationTsunamiTickLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        CalamityIncarnationTsunami.calamityIncarnationTsunamiTick(event.getEntity());
    }

    @Override
    public String getID() {
        return "ExtremeColdnessEventID";
    }
}
