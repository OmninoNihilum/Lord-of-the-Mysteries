package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.AcidicRain;
import net.swimmingtuna.lotm.item.SealedArtifacts.DeathKnell;

public class DeathKnellNegativeTickLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        DeathKnell.deathKnellNegativeTick(event.getEntity());
    }

    @Override
    public String getID() {
        return "DeathKnellEventID";
    }
}
