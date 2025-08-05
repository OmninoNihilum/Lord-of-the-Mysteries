package net.swimmingtuna.lotm.attributes.PathwayAttributes;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.attributes.BaseAttributes;
import net.swimmingtuna.lotm.attributes.ModAttributes;

import java.util.List;

public class MonsterAttributes extends BaseAttributes {
    public static final List<Double> healthList
            = List.of(60.0, 45.0, 40.0, 40.0, 35.0, 25.0, 25.0, 25.0, 20.0, 20.0);
    public static final List<Double> speedList
            = List.of(0.1, 0.08, 0.06, 0.06, 0.06, 0.04, 0.04, 0.02, 0.02, 0.0);
    public static final List<Double> attackList
            = List.of(16.0 ,12.0, 12.0, 9.0, 9.0, 6.0, 6.0, 3.0, 3.0, 0.0);
    public static final List<Double> nightVisionList
            = List.of(3.0 ,3.0, 3.0, 3.0, 2.5, 2.0, 1.5, 1.0, 0.5, 0.0);
    public static final List<Double> fireResistanceList =
            List.of(3.0, 3.0, 3.0, 3.0, 3.0, 2.0, 1.0, 0.0, 0.0, 0.0);
    public static final List<Double> jumpList
            = List.of(0.125, 0.125, 0.125, 0.125, 0.125, 0.125, 0.125, 0.055, 0.055, 0.055);
    public static final List<Double> armorList
            = List.of(20.0, 18.0, 15.0, 12.0, 10.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    public static final List<Double> armorToughnessList
            = List.of(12.0, 8.0, 8.0, 6.0, 6.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    public static final List<Double> waterBreathingList
            = List.of(1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    public static final List<Double> digSpeedList
            = List.of(5.0, 4.0, 4.0, 3.0, 3.0, 2.0, 2.0, 1.0, 1.0, 0.0);

    public static void applyAll(LivingEntity entity, int seq) {
        AttributeInstance healthAttr = entity.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr != null) {
            apply(healthAttr, healthBoostID, healthList.get(seq) - 20.0, "HealthBoost");
        }

        AttributeInstance speedAttr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            apply(speedAttr, speedID, speedList.get(seq), "SpeedBoost");
        }

        AttributeInstance attackAttr = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackAttr != null) {
            apply(attackAttr, attackID, attackList.get(seq), "AttackBoost");
        }

        AttributeInstance armorAttr = entity.getAttribute(Attributes.ARMOR);
        if (armorAttr != null) {
            apply(armorAttr, armorID, armorList.get(seq), "ArmorBoost");
        }

        AttributeInstance armorToughnessAttr = entity.getAttribute(Attributes.ARMOR_TOUGHNESS);
        if (armorToughnessAttr != null) {
            apply(armorToughnessAttr, armorToughnessID, armorToughnessList.get(seq), "ArmorToughnessBoost");
        }

        AttributeInstance nightVisionAttr = entity.getAttribute(ModAttributes.NIGHT_VISION.get());
        if (nightVisionAttr != null) {
            apply(nightVisionAttr, nightVisionID, nightVisionList.get(seq), "NightVision");
        }

        AttributeInstance fireResistanceAttr = entity.getAttribute(ModAttributes.FIRE_RESISTANCE.get());
        if (fireResistanceAttr != null) {
            apply(fireResistanceAttr, fireResistanceID, fireResistanceList.get(seq), "FireResistance");
        }

        AttributeInstance jumpAttr = entity.getAttribute(ModAttributes.JUMP_BOOST.get());
        if (jumpAttr != null) {
            apply(jumpAttr, jumpID, jumpList.get(seq), "JumpBoost");
        }

        AttributeInstance waterBreathingAttr = entity.getAttribute(ModAttributes.WATER_BREATHING.get());
        if (waterBreathingAttr != null) {
            apply(waterBreathingAttr, waterBreathingID, waterBreathingList.get(seq), "WaterBreathingBoost");
        }

        AttributeInstance digSpeedAttr = entity.getAttribute(ModAttributes.DIG_SPEED.get());
        if (digSpeedAttr != null) {
            apply(digSpeedAttr, digSpeedID, digSpeedList.get(seq), "DigSpeed");
        }
    }
}