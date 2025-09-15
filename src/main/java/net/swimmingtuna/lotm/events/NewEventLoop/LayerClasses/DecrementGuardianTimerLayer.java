package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.entity.GuardianBoxEntity;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.AcidicRain;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Warrior.FinishedItems.EnableOrDisableProtection;

public class DecrementGuardianTimerLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        GuardianBoxEntity.decrementGuardianTimer(event.getEntity());
    }

    @Override
    public String getID() {
        return "DecrementGuardianTimerEventID";
    }
}
