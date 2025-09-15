package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Monster.MisfortuneManipulation;

public class MisfortuneLightningStorm implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        MisfortuneManipulation.misfortuneLightningStorm(event.getEntity());
    }

    @Override
    public String getID() {
        return "LivingLightningStormEventID";
    }
}
