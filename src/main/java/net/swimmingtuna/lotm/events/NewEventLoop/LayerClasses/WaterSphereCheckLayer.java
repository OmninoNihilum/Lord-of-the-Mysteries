package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.WaterSphere;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;

public class WaterSphereCheckLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        WaterSphere.waterSphereCheck(event.getEntity());
    }

    @Override
    public String getID() {
        return "WaterSphereEventID";
    }
}
