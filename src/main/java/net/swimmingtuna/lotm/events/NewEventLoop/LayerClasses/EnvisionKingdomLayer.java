package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;


import static net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.EnvisionKingdom.envisionKingdom;

public class EnvisionKingdomLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        LivingEntity livingEntity = event.getEntity();

        envisionKingdom(livingEntity, livingEntity.level());
    }

    @Override
    public String getID() {
        return "EnvisionKingdomEventID";
    }
}
