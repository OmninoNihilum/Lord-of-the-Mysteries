package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.AcidicRain;
import net.swimmingtuna.lotm.item.SealedArtifacts.WintryBlade;

public class WintryBladeTickLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        WintryBlade.wintryBladeTick(event);
    }

    @Override
    public String getID() {
        return "WintryBladeEventID";
    }
}
