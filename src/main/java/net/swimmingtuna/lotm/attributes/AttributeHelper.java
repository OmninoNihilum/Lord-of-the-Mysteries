package net.swimmingtuna.lotm.attributes;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;

public class AttributeHelper {

    public static double getHealth(LivingEntity entity) {
        return get(entity, Attributes.MAX_HEALTH);
    }

    public static double getMovementSpeed(LivingEntity entity) {
        return get(entity, Attributes.MOVEMENT_SPEED);
    }

    public static double getSwimSpeed(LivingEntity entity) {
        return get(entity, ForgeMod.SWIM_SPEED.get());
    }

    public static double getAttackDamage(LivingEntity entity) {
        return get(entity, Attributes.ATTACK_DAMAGE);
    }

    public static double getArmor(LivingEntity entity) {
        return get(entity, Attributes.ARMOR);
    }

    public static double getArmorToughness(LivingEntity entity) {
        return get(entity, Attributes.ARMOR_TOUGHNESS);
    }

    public static double getAttackSpeed(LivingEntity entity) {
        return get(entity, Attributes.ATTACK_SPEED);
    }

    public static double getKnockbackResistance(LivingEntity entity) {
        return get(entity, Attributes.KNOCKBACK_RESISTANCE);
    }

    // Custom Attributes - Get Methods
    public static double getNightVision(LivingEntity entity) {
        return get(entity, ModAttributes.NIGHT_VISION.get());
    }

    public static double getFireResistance(LivingEntity entity) {
        return get(entity, ModAttributes.FIRE_RESISTANCE.get());
    }

    public static double getJumpBoost(LivingEntity entity) {
        return get(entity, ModAttributes.JUMP_BOOST.get());
    }

    public static double getWaterBreathing(LivingEntity entity) {
        return get(entity, ModAttributes.WATER_BREATHING.get());
    }

    public static double getDigSpeed(LivingEntity entity) {
        return get(entity, ModAttributes.DIG_SPEED.get());
    }

    // Vanilla Attributes - Set Methods
    public static void setHealth(LivingEntity entity, double value) {
        set(entity, Attributes.MAX_HEALTH, value);
    }


    public static void setMovementSpeed(LivingEntity entity, double value) {
        set(entity, Attributes.MOVEMENT_SPEED, value);
    }

    public static void setSwimSpeed(LivingEntity entity, double value) {
        set(entity, ForgeMod.SWIM_SPEED.get(), value);
    }

    public static void setAttackDamage(LivingEntity entity, double value) {
        set(entity, Attributes.ATTACK_DAMAGE, value);
    }

    public static void setArmor(LivingEntity entity, double value) {
        set(entity, Attributes.ARMOR, value);
    }

    public static void setArmorToughness(LivingEntity entity, double value) {
        set(entity, Attributes.ARMOR_TOUGHNESS, value);
    }

    public static void setAttackSpeed(LivingEntity entity, double value) {
        set(entity, Attributes.ATTACK_SPEED, value);
    }

    public static void setKnockbackResistance(LivingEntity entity, double value) {
        set(entity, Attributes.KNOCKBACK_RESISTANCE, value);
    }

    // Custom Attributes - Set Methods
    public static void setNightVision(LivingEntity entity, double value) {
        set(entity, ModAttributes.NIGHT_VISION.get(), value);
    }

    public static void setFireResistance(LivingEntity entity, double value) {
        set(entity, ModAttributes.FIRE_RESISTANCE.get(), value);
    }

    public static void setJumpBoost(LivingEntity entity, double value) {
        set(entity, ModAttributes.JUMP_BOOST.get(), value);
    }

    public static void setWaterBreathing(LivingEntity entity, double value) {
        set(entity, ModAttributes.WATER_BREATHING.get(), value);
    }

    public static void setDigSpeed(LivingEntity entity, double value) {
        set(entity, ModAttributes.DIG_SPEED.get(), value);
    }

    // Private helper methods
    private static double get(LivingEntity entity, net.minecraft.world.entity.ai.attributes.Attribute attribute) {
        if (entity == null || attribute == null) {
            return 0.0;
        }

        var instance = entity.getAttribute(attribute);
        if (instance == null) {
            return 0.0;
        }

        return instance.getValue();
    }

    private static void set(LivingEntity entity, net.minecraft.world.entity.ai.attributes.Attribute attribute, double value) {
        if (entity == null || attribute == null) {
            return;
        }

        var instance = entity.getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(value);
        }
    }
}