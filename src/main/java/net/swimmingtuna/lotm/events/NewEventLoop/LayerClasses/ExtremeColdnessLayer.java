package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.EFunctions;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.EventManager;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.ExtremeColdness;


public class ExtremeColdnessLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        CompoundTag tag = entity.getPersistentData();
        int affectedBySailorExtremeColdness = tag.getInt("affectedBySailorExtremeColdness");
        if (affectedBySailorExtremeColdness == 0) {
            EventManager.removeFromRegularLoop(entity, EFunctions.AFFECTEDBYEXTREMECOLDNESS.get());
        }
        ExtremeColdness.extremeColdnessTick(event);
    }

    @Override
    public String getID() {
        return "ExtremeColdnessEventID";
    }
}
