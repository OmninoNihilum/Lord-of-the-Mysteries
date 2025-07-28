package nihilum.lotm.tweaks.Attributes;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.List;
import java.util.UUID;

public class MonsterAttributes {
    private static final UUID SPEED_UUID = UUID.randomUUID();
    private static final List<Double> speed = List.of(0.4, 0.32, 0.32, 0.32, 0.32, 0.16, 0.16, 0.08, 0.08, 0.0);

    public static void applySpeedModifier(AttributeInstance attr, int sequence) {
        if (attr.getModifier(SPEED_UUID) != null)
            attr.removeModifier(SPEED_UUID);

        attr.addPermanentModifier(new AttributeModifier(
                SPEED_UUID,
                "MonsterSpeedBonus",
                speed.get(sequence),
                AttributeModifier.Operation.ADDITION));
    }
}
