package nihilum.lotm.tweaks.Attributes;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;


public class MonsterAttributes {
    private static final Modifier<Double> speedModifier = new Modifier<Double>(0.4, 0.32, 0.32, 0.32, 0.32, 0.16, 0.16, 0.08, 0.08, 0.0);
    private static final Modifier<Double> attackModifier = new Modifier<>(,0.0);

    public static void applySpeedModifier(AttributeInstance attr, int sequence) {
        speedModifier.clean(attr);

        attr.addPermanentModifier(new AttributeModifier(
                speedModifier.getID(),
                "MonsterSpeedBonus",
                speedModifier.getValue(sequence),
                AttributeModifier.Operation.ADDITION));
    }
}
