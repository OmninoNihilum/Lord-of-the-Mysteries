package nihilum.lotm.tweaks.Attributes;


import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class MonsterAttributes extends BaseAttributes {
    private static final List<Double> speedList
            = List.of(0.1, 0.08, 0.06, 0.06, 0.06, 0.04, 0.04, 0.02, 0.02, 0.0);
    private static final List<Double> attackList
            = List.of(16.0 ,12.0, 12.0, 9.0, 9.0, 6.0, 6.0, 3.0, 3.0, 0.0);

    public static void applyAll(Player player, int seq){
        //apply(player.getAttribute(Attributes.MAX_HEALTH), healthBoostID, , "HealthBoost");

        apply(player.getAttribute(Attributes.MOVEMENT_SPEED), speedID, speedList.get(seq), "SpeedBoost");
        apply(player.getAttribute(Attributes.ATTACK_DAMAGE), attackID, attackList.get(seq), "AttackBoost");
    }
}
