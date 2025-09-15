package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.beyonder.SpectatorClass;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.AcidicRain;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.Prophecy;

public class ProphesizeDemiseTickLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        SpectatorClass.demiseTick(event);
    }

    @Override
    public String getID() {
        return "DemiseTickEventID";
    }
}
