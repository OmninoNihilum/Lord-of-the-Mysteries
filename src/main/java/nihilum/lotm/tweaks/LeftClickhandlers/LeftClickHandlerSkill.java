package nihilum.lotm.tweaks.LeftClickhandlers;

import net.minecraft.world.item.Item;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;

import java.util.function.Supplier;

public abstract class LeftClickHandlerSkill extends SimpleAbilityItem {

    protected LeftClickHandlerSkill(Item.Properties properties, BeyonderClass requiredClass, int requiredSequence, int requiredSpirituality, int cooldown) {
        super(properties, requiredClass, requiredSequence, requiredSpirituality, cooldown);
    }

    protected LeftClickHandlerSkill(Item.Properties properties, Supplier<? extends BeyonderClass> requiredClass, int requiredSequence, int requiredSpirituality, int cooldown) {
        super(properties, requiredClass, requiredSequence, requiredSpirituality, cooldown);
    }

    protected LeftClickHandlerSkill(Item.Properties properties, BeyonderClass requiredClass, int requiredSequence, int requiredSpirituality, int cooldown, double entityReach, double blockReach) {
        super(properties, requiredClass, requiredSequence, requiredSpirituality, cooldown, entityReach, blockReach);
    }

    protected LeftClickHandlerSkill(Item.Properties properties, Supplier<? extends BeyonderClass> requiredClass, int requiredSequence, int requiredSpirituality, int cooldown, double entityReach, double blockReach) {
        super(properties, requiredClass, requiredSequence, requiredSpirituality, cooldown, entityReach, blockReach);
    }

    public abstract LeftClickType getleftClickEmpty();
}

