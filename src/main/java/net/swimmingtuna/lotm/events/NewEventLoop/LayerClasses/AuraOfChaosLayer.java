package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Monster.AuraOfChaos;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.AcidicRain;

public class AuraOfChaosLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        AuraOfChaos.auraOfChaos(event);
    }

    @Override
    public String getID() {
        return "AuraOfChaosEventID";
    }
}
