package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.EFunctions;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.EventManager;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.Earthquake;


public class EarthquakeLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        LivingEntity livingEntity = event.getEntity();
        int sailorEarthquake = livingEntity.getPersistentData().getInt("sailorEarthquake");
        if (sailorEarthquake == 0) {
            EventManager.removeFromRegularLoop(livingEntity, EFunctions.EARTHQUAKE.get());
        }
        Earthquake.earthquake(event.getEntity());
    }

    @Override
    public String getID() {
        return "EarthquakeEventID";
    }
}
