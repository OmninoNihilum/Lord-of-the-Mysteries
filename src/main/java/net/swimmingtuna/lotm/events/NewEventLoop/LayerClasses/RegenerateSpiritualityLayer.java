package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.world.worlddata.BeyonderEntityData;

public class RegenerateSpiritualityLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        BeyonderEntityData.regenerateSpirituality(event);
    }

    @Override
    public String getID() {
        return "RegenerateSpiritualityEventID";
    }
}
