package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Monster.MonsterCalamityIncarnation;

public class CalamityIncarnationTornadoLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        MonsterCalamityIncarnation.calamityIncarnationTornado(event.getEntity());
    }

    @Override
    public String getID() {
        return "CalamityIncarnationTornadoEventID";
    }
}
