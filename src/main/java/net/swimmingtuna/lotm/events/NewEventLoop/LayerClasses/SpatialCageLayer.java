package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;


import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.entity.SpatialCageEntity;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;

public class SpatialCageLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        SpatialCageEntity.cageTick(event.getEntity());
    }

    @Override
    public String getID() {
        return "SpatialCageEventID";
    }
}
