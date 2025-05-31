package net.swimmingtuna.lotm.mixin;

import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.swimmingtuna.lotm.LOTM;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin({Container.class})
public interface InventoryStackMixin extends Clearable {


    @Overwrite
    default int getMaxStackSize() {
        return LOTM.getMaxStackCount();
    }
}
