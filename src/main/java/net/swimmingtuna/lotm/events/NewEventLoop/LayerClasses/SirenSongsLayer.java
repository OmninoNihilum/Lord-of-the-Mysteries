package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor.SirenSongHarm;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;

public class SirenSongsLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        SirenSongHarm.sirenSongsTick(event.getEntity());
    }

    @Override
    public String getID() {
        return "SirenSongEventID";
    }
}
