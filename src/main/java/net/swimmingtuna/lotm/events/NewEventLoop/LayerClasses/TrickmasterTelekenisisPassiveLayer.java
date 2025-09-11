package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.EFunctions;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.EventManager;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice.TrickTelekenisis;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;

public class TrickmasterTelekenisisPassiveLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        boolean canFly = event.getEntity().getPersistentData().getBoolean("CanFly");
        if (!canFly) {
            EventManager.removeFromRegularLoop(event.getEntity(), EFunctions.SPECTATORPROPHECY.get());
        }
        TrickTelekenisis.trickMasterTelekenisisPassive(event);
    }

    @Override
    public String getID() {
        return "TelekinesisEventID";
    }
}
