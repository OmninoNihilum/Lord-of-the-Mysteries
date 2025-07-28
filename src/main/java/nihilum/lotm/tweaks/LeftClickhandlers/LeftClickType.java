package nihilum.lotm.tweaks.LeftClickhandlers;

import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface LeftClickType {
    public abstract boolean handle(Supplier<NetworkEvent.Context> supplier);
}
