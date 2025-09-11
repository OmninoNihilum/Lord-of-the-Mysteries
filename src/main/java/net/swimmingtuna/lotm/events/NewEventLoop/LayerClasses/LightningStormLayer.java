package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.EFunctions;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.EventManager;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.LightningStorm;


public class LightningStormLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        LivingEntity livingEntity = event.getEntity();
        CompoundTag tag = livingEntity.getPersistentData();
        int sailorMentioned = tag.getInt("tyrantMentionedInChat");
        int sailorLightningStorm = tag.getInt("sailorLightningStorm");
        if (sailorMentioned == 0 && sailorLightningStorm == 0) {
            EventManager.removeFromRegularLoop(livingEntity, EFunctions.LIGHTNING_STORM.get());
        }
        LightningStorm.lightningStorm(event.getEntity());
    }

    @Override
    public String getID() {
        return "LightningStormEventID";
    }
}
