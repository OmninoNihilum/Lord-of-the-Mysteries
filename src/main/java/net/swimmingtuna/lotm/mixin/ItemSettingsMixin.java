package net.swimmingtuna.lotm.mixin;

import net.minecraft.world.item.Item;
import net.swimmingtuna.lotm.LOTM;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Item.Properties.class})
public abstract class ItemSettingsMixin {


    @Shadow
    int maxStackSize;

    public ItemSettingsMixin() {
    }

    @Inject(
            at = {@At("TAIL")},
            method = {"<init>"}
    )
    private void init(CallbackInfo ci) {
        this.maxStackSize = LOTM.getMaxStackCount();
    }
}
