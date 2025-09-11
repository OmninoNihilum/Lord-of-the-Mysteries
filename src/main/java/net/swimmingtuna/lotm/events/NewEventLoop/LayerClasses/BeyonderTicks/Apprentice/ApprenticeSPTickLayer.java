package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses.BeyonderTicks.Apprentice;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.beyonder.ApprenticeClass;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;

public class ApprenticeSPTickLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        ApprenticeClass.apprenticeTick(event);
    }

    @Override
    public String getID() {
        return "ApprenticeSPTickEventID";
    }
}
