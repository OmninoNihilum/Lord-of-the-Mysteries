package net.swimmingtuna.lotm.item.BeyonderAbilities.EmptyLeftClick;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public abstract class EmptyLeftClickHandlerSword extends SwordItem {
    public EmptyLeftClickHandlerSword(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    public abstract EmptyLeftClickType getleftClickEmpty();
}
