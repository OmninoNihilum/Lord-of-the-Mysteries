package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Monster.MonsterCalamityIncarnation;


public class CalamityLightningStormLayer implements IFunction {

    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        MonsterCalamityIncarnation.calamityLightningStorm(event.getEntity());
    }

    @Override
    public String getID() {
        return "CalamityLightningStormEventID";
    }
}
