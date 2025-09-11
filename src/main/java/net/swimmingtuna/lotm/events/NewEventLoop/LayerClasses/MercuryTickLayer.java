package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Warrior.FinishedItems.MercuryLiquefication;


public class MercuryTickLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        MercuryLiquefication.mercuryArmorTick(event);
    }

    @Override
    public String getID() {
        return "MercuryTickEventID";
    }
}
