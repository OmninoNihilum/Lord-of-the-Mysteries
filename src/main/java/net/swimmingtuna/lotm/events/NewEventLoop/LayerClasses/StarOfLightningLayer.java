package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.EFunctions;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.EventManager;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.StarOfLightning;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;

public class StarOfLightningLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        LivingEntity livingEntity = event.getEntity();
        CompoundTag tag = livingEntity.getPersistentData();
        int sailorLightningStar = tag.getInt("sailorLightningStar");
        int star = tag.getInt("sailorLightningStarLightning");
        if (sailorLightningStar == 0 && star == 0) {
            EventManager.removeFromRegularLoop(livingEntity, EFunctions.STAR_OF_LIGHTNING.get());
        }
        StarOfLightning.starOfLightning(event.getEntity());
    }

    @Override
    public String getID() {
        return "StarOfLightningEventID";
    }
}
