package nihilum.lotm.tweaks.Attributes;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.swimmingtuna.lotm.spirituality.ModAttributes;

import java.util.List;

public class WarriorAttributes extends BaseAttributes{
    private static final List<Double> speedList
            = List.of(0.12, 0.1, 0.08, 0.08, 0.07, 0.06, 0.06, 0.04, 0.04, 0.02);
    private static final List<Double> attackList
            = List.of(20.0 ,18.0, 16.0, 14.0, 12.0, 10.0, 8.0, 6.0, 4.0, 2.0);
    private static final List<Double> jumpList
            = List.of(0.225, 0.2, 0.175, 0.175, 0.15, 0.15, 0.125, 0.125, 0.1, 0.0);

    private static final List<Double> armorList
            = List.of(20.0, 18.0, 16.0, 14.0, 12.0, 10.0, 8.0, 6.0, 4.0, 2.0);
    private static final List<Double> armorToughnessList
            = List.of(16.0, 15.0, 14.0, 13.0, 12.0, 10.0, 8.0, 6.0, 4.0, 2.0);


    public static void applyAll(Player player, int seq){
        //apply(player.getAttribute(Attributes.MAX_HEALTH), healthBoostID, , "HealthBoost");
        apply(player.getAttribute(Attributes.MOVEMENT_SPEED), speedID, speedList.get(seq), "SpeedBoost");
        apply(player.getAttribute(Attributes.ATTACK_DAMAGE), attackID, attackList.get(seq), "AttackBoost");

        apply(player.getAttribute(Attributes.ARMOR), armorID, armorList.get(seq), "ArmorBoost");
        apply(player.getAttribute(Attributes.ARMOR_TOUGHNESS), armorToughnessID,
                armorToughnessList.get(seq), "ArmorToughnessBoost");

        apply(player.getAttribute(ModAttributes.JUMP_BOOST.get()),
                jumpID, jumpList.get(seq), "JumpBoost");
    }
}
