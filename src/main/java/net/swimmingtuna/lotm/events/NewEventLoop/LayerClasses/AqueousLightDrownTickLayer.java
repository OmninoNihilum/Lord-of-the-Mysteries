package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.AcidicRain;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.AqueousLightDrown;

public class AqueousLightDrownTickLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        AqueousLightDrown.aqueousLightDrownTick(event);
    }

    @Override
    public String getID() {
        return "AqueousLightDrownEventID";
    }
}
