package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.beyonder.MonsterClass;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.AcidicRain;

public class CalamityExplosionLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        MonsterClass.calamityExplosion(event.getEntity());
    }

    @Override
    public String getID() {
        return "CalamityExplosionEventID";
    }
}
