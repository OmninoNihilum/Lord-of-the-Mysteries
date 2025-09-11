package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Monster.FateReincarnation;


public class MonsterReincarnationCheckerLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        FateReincarnation.monsterReincarnationChecker(event);
    }

    @Override
    public String getID() {
        return "MercuryTickEventID";
    }
}
