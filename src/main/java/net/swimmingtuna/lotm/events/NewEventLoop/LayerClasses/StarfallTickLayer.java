package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice.Conceptualization;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice.Starfall;

public class StarfallTickLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        Starfall.starfallTick(event);
    }

    @Override
    public String getID() {
        return "StarfallTickEventID";
    }
}
