package net.swimmingtuna.lotm.item.BeyonderAbilities.EmptyLeftClick;

import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface EmptyLeftClickType{
    public abstract boolean handle(Supplier<NetworkEvent.Context> supplier);
}
