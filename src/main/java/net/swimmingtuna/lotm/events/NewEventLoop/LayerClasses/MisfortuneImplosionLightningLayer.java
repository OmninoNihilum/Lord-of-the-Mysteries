package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.EFunctions;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.EventManager;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Monster.MisfortuneImplosion;


public class MisfortuneImplosionLightningLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        if (event.getEntity().getPersistentData().getInt("monsterImplosionLightning") == 0) {
            EventManager.removeFromRegularLoop(event.getEntity(), EFunctions.MISFORTUNEIMPLOSIONLIGHTNING.get());
        }
        MisfortuneImplosion.misfortuneImplosionLightning(event);
    }

    @Override
    public String getID() {
        return "MisfortuneImplosionLightningEventID";
    }
}
