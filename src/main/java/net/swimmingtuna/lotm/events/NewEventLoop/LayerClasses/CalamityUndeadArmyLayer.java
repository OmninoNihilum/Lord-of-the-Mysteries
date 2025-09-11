package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.beyonder.MonsterClass;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;


public class CalamityUndeadArmyLayer implements IFunction {

    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        MonsterClass.calamityUndeadArmy(event.getEntity());
    }

    @Override
    public String getID() {
        return "CalamityUndeadArmyEventID";
    }
}
