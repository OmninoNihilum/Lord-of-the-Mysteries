package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Warrior.FinishedItems.Gigantification;

public class GigantificationScaleLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        Gigantification.gigantificationScale(event);
    }

    @Override
    public String getID() {
        return "GigantificationScaleEventID";
    }
}
