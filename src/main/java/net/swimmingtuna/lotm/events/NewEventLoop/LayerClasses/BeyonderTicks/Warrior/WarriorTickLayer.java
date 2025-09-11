package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses.BeyonderTicks.Warrior;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import static net.swimmingtuna.lotm.util.BeyonderUtil.applyMobEffect;

public class WarriorTickLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        LivingEntity player = event.getEntity();
        int sequenceLevel = BeyonderUtil.getSequence(player);

        if (!player.level().isClientSide()) {
            ScaleData scaleData = ScaleTypes.BASE.getScaleData(player);
            boolean isGiant = player.getPersistentData().getBoolean("warriorGiant");
            boolean isHoGGiant = player.getPersistentData().getBoolean("handOfGodGiant");
            boolean isTwilightGiant = player.getPersistentData().getBoolean("twilightGiant");
            boolean x = !isGiant && !isHoGGiant && !isTwilightGiant;
            int speed = 0;
            int resistance = 0;
            int strength = 0;
            int regen = 0;
            if (player.tickCount % 10 == 0) {
                if (player instanceof Player) {
                    if (sequenceLevel == 8) {
                        if (player.getMainHandItem().getItem() instanceof ShieldItem || player.getOffhandItem().getItem() instanceof ShieldItem) {
                            applyMobEffect(player, MobEffects.DAMAGE_RESISTANCE, 20, resistance + 1, true, true);
                        }
                        if (player.getMainHandItem().getItem() instanceof SwordItem) {
                            applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 20, speed + 1, true, true);
                        }
                    } else if (sequenceLevel <= 7 && sequenceLevel >= 6) {
                        if (player.getMainHandItem().getItem() instanceof SwordItem) {
                            applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 20, speed + 1, true, true);
                        }
                        if (player.getMainHandItem().getItem() instanceof AxeItem) {
                            applyMobEffect(player, MobEffects.DAMAGE_BOOST, 20, strength + 1, true, true);
                        }
                        if (player.getMainHandItem().getItem() instanceof PickaxeItem || player.getMainHandItem().getItem() instanceof ShovelItem) {
                            applyMobEffect(player, MobEffects.DIG_SPEED, 20, 1, true, true);
                        }
                        if (player.getMainHandItem().getItem() instanceof BowItem || player.getMainHandItem().getItem() instanceof CrossbowItem) {
                            applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 20, speed + 1, true, true);
                        }
                        if (player.getMainHandItem().getItem() instanceof ShieldItem || player.getOffhandItem().getItem() instanceof ShieldItem) {
                            applyMobEffect(player, MobEffects.DAMAGE_RESISTANCE, 20, resistance + 1, true, true);
                        }
                    } else if (sequenceLevel <= 5) {
                        if (player.getMainHandItem().getItem() instanceof SwordItem) {
                            applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 20, speed + 2, true, true);
                            applyMobEffect(player, MobEffects.DIG_SPEED, 20, 0, true, true);
                        }
                        if (player.getMainHandItem().getItem() instanceof AxeItem) {
                            applyMobEffect(player, MobEffects.DAMAGE_BOOST, 20, strength + 1, true, true);
                            applyMobEffect(player, MobEffects.DAMAGE_RESISTANCE, 20, resistance + 1, true, true);
                        }
                        if (player.getMainHandItem().getItem() instanceof PickaxeItem || player.getMainHandItem().getItem() instanceof ShovelItem) {
                            applyMobEffect(player, MobEffects.DIG_SPEED, 20, 3, true, true);
                        }
                        if (player.getMainHandItem().getItem() instanceof BowItem || player.getMainHandItem().getItem() instanceof CrossbowItem) {
                            applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 20, speed + 2, true, true);
                            applyMobEffect(player, MobEffects.REGENERATION, 20, regen + 1, true, true);
                        }
                        if (player.getMainHandItem().getItem() instanceof ShieldItem || player.getOffhandItem().getItem() instanceof ShieldItem) {
                            applyMobEffect(player, MobEffects.DAMAGE_RESISTANCE, 20, resistance + 1, true, true);
                        }
                    }
                } else {
                    applyRandomWeaponEffects(player, sequenceLevel, speed, strength, resistance, regen);
                }
            }
        }
    }

    private void applyRandomWeaponEffects(LivingEntity entity, int sequenceLevel, int speed, int strength, int resistance, int regeneration) {
        String weaponType = getOrSetRandomWeaponType(entity);
        switch (weaponType) {
            case "sword":
                applySwordEffects(entity, sequenceLevel, speed);
                break;
            case "axe":
                applyAxeEffects(entity, sequenceLevel, strength, resistance);
                break;
            case "pickaxe":
                applyPickaxeEffects(entity, sequenceLevel, speed);
                break;
            case "bow":
                applyBowEffects(entity, sequenceLevel, speed , regeneration);
                break;
            case "shield":
                applyShieldEffects(entity, sequenceLevel, resistance);
                break;
        }
    }
    private String getOrSetRandomWeaponType(LivingEntity entity) {
        CompoundTag persistentData = entity.getPersistentData();
        String weaponType = persistentData.getString("randomWeaponType");

        if (weaponType.isEmpty()) {
            String[] weaponTypes = {"sword", "axe", "pickaxe", "bow", "shield"};
            weaponType = weaponTypes[entity.getRandom().nextInt(weaponTypes.length)];
            persistentData.putString("randomWeaponType", weaponType);
        }

        return weaponType;
    }
    private void applySwordEffects(LivingEntity entity, int sequenceLevel, int speed) {
        if (sequenceLevel == 8) {
            applyMobEffect(entity, MobEffects.MOVEMENT_SPEED, 20, speed + 1, true, true);
        } else if (sequenceLevel <= 7 && sequenceLevel >= 6) {
            applyMobEffect(entity, MobEffects.MOVEMENT_SPEED, 20, speed + 1, true, true);
        } else if (sequenceLevel <= 5) {
            applyMobEffect(entity, MobEffects.MOVEMENT_SPEED, 20, speed + 2, true, true);
            applyMobEffect(entity, MobEffects.DIG_SPEED, 20, 0, true, true);
        }
    }

    private void applyAxeEffects(LivingEntity entity, int sequenceLevel, int strength, int resistance) {
        if (sequenceLevel <= 7 && sequenceLevel >= 6) {
            applyMobEffect(entity, MobEffects.DAMAGE_BOOST, 20, strength + 1, true, true);
        } else if (sequenceLevel <= 5) {
            applyMobEffect(entity, MobEffects.DAMAGE_BOOST, 20, strength + 1, true, true);
            applyMobEffect(entity, MobEffects.DAMAGE_RESISTANCE, 20, resistance + 1, true, true);
        }
    }

    private void applyPickaxeEffects(LivingEntity entity, int sequenceLevel, int speed) {
        if (sequenceLevel <= 7 && sequenceLevel >= 6) {
            applyMobEffect(entity, MobEffects.MOVEMENT_SPEED, 20, speed + 1, true, true);
        } else if (sequenceLevel <= 5) {
            applyMobEffect(entity, MobEffects.MOVEMENT_SPEED, 20, speed + 2, true, true);
        }
    }

    private void applyBowEffects(LivingEntity entity, int sequenceLevel, int speed, int regen) {
        if (sequenceLevel <= 7 && sequenceLevel >= 6) {
            applyMobEffect(entity, MobEffects.MOVEMENT_SPEED, 20, speed + 1, true, true);
        } else if (sequenceLevel <= 5) {
            applyMobEffect(entity, MobEffects.MOVEMENT_SPEED, 20, speed + 2, true, true);
            applyMobEffect(entity, MobEffects.REGENERATION, 20, regen + 1, true, true);
        }
    }

    private void applyShieldEffects(LivingEntity entity, int sequenceLevel, int resistance) {
        if (sequenceLevel == 8) {
            applyMobEffect(entity, MobEffects.DAMAGE_RESISTANCE, 20, resistance + 1, true, true);
        } else if (sequenceLevel <= 7 && sequenceLevel >= 6) {
            applyMobEffect(entity, MobEffects.DAMAGE_RESISTANCE, 20, resistance + 1, true, true);
        } else if (sequenceLevel <= 5) {
            applyMobEffect(entity, MobEffects.DAMAGE_RESISTANCE, 20, resistance + 1, true, true);
        }
    }

    @Override
    public String getID() {
        return "WarriorTickEventID";
    }
}
