package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.ConsciousnessStroll;


public class ConsciousnessStrollLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        ConsciousnessStroll.consciousnessStroll(event.getEntity());
    }

    @Override
    public String getID() {
        return "ConsciousnessStrollEventID";
    }
}
