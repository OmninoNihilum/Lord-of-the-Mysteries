package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Monster.MonsterCalamityIncarnation;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.AcidicRain;

public class MonsterCalamityIncarnationLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        MonsterCalamityIncarnation.calamityTickEvent(event);
    }

    @Override
    public String getID() {
        return "MonsterCalamityIncarnationEventID";
    }
}
