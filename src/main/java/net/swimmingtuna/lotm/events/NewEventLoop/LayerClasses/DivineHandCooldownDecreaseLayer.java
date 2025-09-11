package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.entity.DivineHandRightEntity;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;


public class DivineHandCooldownDecreaseLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        DivineHandRightEntity.divineHandCooldownDecrease(event.getEntity());
    }

    @Override
    public String getID() {
        return "DivineHandCooldownDecreaseEventID";
    }
}
