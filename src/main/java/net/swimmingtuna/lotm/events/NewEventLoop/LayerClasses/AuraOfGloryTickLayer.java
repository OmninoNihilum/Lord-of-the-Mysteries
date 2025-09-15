package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.AcidicRain;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Warrior.FinishedItems.AuraOfGlory;

public class AuraOfGloryTickLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        AuraOfGlory.auraOfGloryAndTwilightTick(event);
    }

    @Override
    public String getID() {
        return "AuraOfGloryEventID";
    }
}
