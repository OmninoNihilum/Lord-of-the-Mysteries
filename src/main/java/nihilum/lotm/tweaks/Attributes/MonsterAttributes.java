package nihilum.lotm.tweaks.Attributes;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;


public class MonsterAttributes {
    private static final Modifier<Double> speedModifier = new Modifier<Double>(0.4, 0.32, 0.32, 0.32, 0.32, 0.16, 0.16, 0.08, 0.08, 0.0);
    private static final Modifier<Double> attackModifier = new Modifier<>(12.0 ,9.0, 9.0, 9.0, 9.0, 3.0, 3.0, 3.0, 3.0, 0.0);

    

    public static void applySpeedModifier(AttributeInstance attr, int sequence) {
        speedModifier.clean(attr);

        attr.addPermanentModifier(new AttributeModifier(
                speedModifier.getID(),
                "MonsterSpeedBonus",
                speedModifier.getValue(sequence),
                AttributeModifier.Operation.ADDITION));
    }

    public static void applyAttackModifier(AttributeInstance attr, int sequence){
        attackModifier.clean(attr);

        attr.addPermanentModifier(new AttributeModifier(
                attackModifier.getID(),
                "MonsterAttackBonus",
                attackModifier.getValue(sequence),
                AttributeModifier.Operation.ADDITION
        ));
    }

    public static void applyAllModifiers(Player player, int sequence){
        applySpeedModifier(player.getAttribute(Attributes.MOVEMENT_SPEED), sequence);
        applyAttackModifier(player.getAttribute(Attributes.ATTACK_DAMAGE), sequence);
    }

    public static void cleanAll(Player player){
        speedModifier.clean(Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED)));
        attackModifier.clean(Objects.requireNonNull(player.getAttribute(Attributes.ATTACK_DAMAGE)));

    }
}
