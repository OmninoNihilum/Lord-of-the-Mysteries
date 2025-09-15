package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice.TrickTelekenisis;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;

public class TrickmasterTelekenisisPassiveLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        LOTM.LOGGER.info("TRICKMASTER TICK WORKING"); //for some reason nothing is happening
        TrickTelekenisis.trickMasterTelekenisisPassive(event);
    }

    @Override
    public String getID() {
        return "TelekinesisEventID";
    }
}
