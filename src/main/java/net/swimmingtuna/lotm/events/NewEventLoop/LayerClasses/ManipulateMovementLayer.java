package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.ManipulateMovement;


public class ManipulateMovementLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        ManipulateMovement.manipulateMovement(event.getEntity());
    }

    @Override
    public String getID() {
        return "ManipulateMovementEventID";
    }
}
