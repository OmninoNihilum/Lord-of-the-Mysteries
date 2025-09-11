package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Monster.ChaosWalkerDisableEnable;


public class OnChaosWalkerCombatLayer implements IFunction {

    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        ChaosWalkerDisableEnable.onChaosWalkerCombat(event.getEntity());
    }

    @Override
    public String getID() {
        return "OnChaosWalkerCombatEventID";
    }
}
