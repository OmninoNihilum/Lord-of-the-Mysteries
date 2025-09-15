package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice.SpatialMaze;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.AcidicRain;

public class SpatialMazeTickLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        SpatialMaze.mazeTick(event.getEntity());
    }

    @Override
    public String getID() {
        return "SpatialMazeEventID";
    }
}
