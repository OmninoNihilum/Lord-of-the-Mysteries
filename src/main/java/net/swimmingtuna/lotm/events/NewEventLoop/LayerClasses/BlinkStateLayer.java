package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice.BlinkState;

public class BlinkStateLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        BlinkState.secretsSorcererBlinkState(event);
    }

    @Override
    public String getID() {
        return "BlinkStateEventID";
    }
}
