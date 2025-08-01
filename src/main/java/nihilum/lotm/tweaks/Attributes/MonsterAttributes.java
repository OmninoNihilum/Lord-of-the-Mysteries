package nihilum.lotm.tweaks.Attributes;


import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.swimmingtuna.lotm.spirituality.ModAttributes;

import java.util.List;

public class MonsterAttributes extends BaseAttributes {
    private static final List<Double> speedList
            = List.of(0.1, 0.08, 0.06, 0.06, 0.06, 0.04, 0.04, 0.02, 0.02, 0.0);
    private static final List<Double> attackList
            = List.of(16.0 ,12.0, 12.0, 9.0, 9.0, 6.0, 6.0, 3.0, 3.0, 0.0);
    private static final List<Double> nightVisionList
            = List.of(4.0 ,4.0, 3.5, 3.0, 2.5, 2.0, 1.5, 1.0, 0.5, 0.0);
    private static  final List<Double> fireResistanceList =
            List.of(3.0, 3.0, 3.0, 3.0, 3.0, 2.0, 1.0, 0.0, 0.0, 0.0);
    private static final List<Double> jumpList
            = List.of(0.125, 0.125, 0.125, 0.125, 0.125, 0.125, 0.125, 0.055, 0.055, 0.055);

    private static final List<Double> armorList
            = List.of(20.0, 18.0, 16.0, 14.0, 12.0, 10.0, 8.0, 6.0, 4.0, 0.0);
    private static final List<Double> armorToughnessList
            = List.of(12.0, 8.0, 8.0, 6.0, 6.0, 4.0, 3.0, 3.0, 2.0, 0.0);

    public static void applyAll(Player player, int seq){
        //apply(player.getAttribute(Attributes.MAX_HEALTH), healthBoostID, , "HealthBoost");
        apply(player.getAttribute(Attributes.MOVEMENT_SPEED), speedID, speedList.get(seq), "SpeedBoost");
        apply(player.getAttribute(Attributes.ATTACK_DAMAGE), attackID, attackList.get(seq), "AttackBoost");

        apply(player.getAttribute(Attributes.ARMOR), armorID, armorList.get(seq), "ArmorBoost");
        apply(player.getAttribute(Attributes.ARMOR_TOUGHNESS), armorToughnessID,
                armorToughnessList.get(seq), "ArmorToughnessBoost");

        apply(player.getAttribute(ModAttributes.NIGHT_VISION.get()),
               nightVisionID, nightVisionList.get(seq), "NightVision");
        apply(player.getAttribute(ModAttributes.FIRE_RESISTANCE.get()),
                fireResistanceID, fireResistanceList.get(seq), "FireResistance");
        apply(player.getAttribute(ModAttributes.JUMP_BOOST.get()),
                jumpID, jumpList.get(seq), "JumpBoost");

    }
}
