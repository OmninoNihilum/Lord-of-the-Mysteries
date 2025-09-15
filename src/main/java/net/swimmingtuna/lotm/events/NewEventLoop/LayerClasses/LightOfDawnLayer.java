package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Warrior.FinishedItems.LightOfDawn;

public class LightOfDawnLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        LightOfDawn.lightOfDawn(event.getEntity());
    }

    @Override
    public String getID() {
        return "LightOfDawnEventID";
    }
}
