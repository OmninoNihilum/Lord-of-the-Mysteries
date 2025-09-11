package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.beyonder.MonsterClass;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;


public class DecrementMonsterAttackEventLayer implements IFunction {

    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        MonsterClass.decrementMonsterAttackEvent(event.getEntity());
    }

    @Override
    public String getID() {
        return "DecrementMonsterAttackEventID";
    }
}
