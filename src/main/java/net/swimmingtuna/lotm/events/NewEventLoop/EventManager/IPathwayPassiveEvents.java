package net.swimmingtuna.lotm.events.NewEventLoop.EventManager;

import net.minecraft.world.entity.LivingEntity;

public interface IPathwayPassiveEvents {
    void removeAllEvents(LivingEntity entity);
    void addAllEvents(LivingEntity entity, int sequence);
}
