package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.EFunctions;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.EventManager;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.SailorLightningTravel;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;

public class SailorLightningTravelLayer implements IFunction {

    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        LivingEntity player = event.getEntity();
        if (player.getPersistentData().getInt("sailorLightningTravel") >= 1) {
            EventManager.removeFromRegularLoop(player, EFunctions.LIGHTNINGTRAVEL.get());
        }
        SailorLightningTravel.sailorLightningTravel(event.getEntity());
    }

    @Override
    public String getID() {
        return "SailorLightningTravelEventID";
    }
}
