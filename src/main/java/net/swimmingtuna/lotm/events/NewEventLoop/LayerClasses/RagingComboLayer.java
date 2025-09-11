package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.RagingBlows;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;

public class RagingComboLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        RagingBlows.ragingCombo(event);
    }

    @Override
    public String getID() {
        return "RagingComboEventID";
    }
}
