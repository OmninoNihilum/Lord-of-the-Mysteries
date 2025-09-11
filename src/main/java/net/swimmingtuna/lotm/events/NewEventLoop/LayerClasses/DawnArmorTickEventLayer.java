package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Warrior.FinishedItems.DawnArmory;


public class DawnArmorTickEventLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        DawnArmory.dawnArmorTickEvent(event);
    }

    @Override
    public String getID() {
        return "DawnArmorEventID";
    }
}
