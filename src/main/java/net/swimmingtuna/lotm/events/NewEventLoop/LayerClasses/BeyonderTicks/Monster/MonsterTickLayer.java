package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses.BeyonderTicks.Monster;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.beyonder.MonsterClass;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import virtuoel.pehkui.api.ScaleTypes;

import static net.swimmingtuna.lotm.util.BeyonderUtil.applyMobEffect;

public class MonsterTickLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        int speed = 0;
        int resistance = 0;
        int strength = 0;
        int regen = 0;
        LivingEntity livingEntity = event.getEntity();
        int sequence = BeyonderUtil.getSequence(livingEntity);
        Projectile projectile = BeyonderUtil.getProjectiles(livingEntity, 50);
        if (projectile != null) {
            LivingEntity target = BeyonderUtil.getTarget(projectile, 75, 0);
            if (target != null) {
                if (sequence <= 8 && livingEntity.getPersistentData().getBoolean("monsterProjectileControl")) {
                    double dx = target.getX() - projectile.getX();
                    double dy = target.getY() - projectile.getY();
                    double dz = target.getZ() - projectile.getZ();
                    double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    double projectileSpeed = 1.2;
                    projectile.setDeltaMovement((dx / length) * projectileSpeed, (dy / length) * projectileSpeed, (dz / length) * projectileSpeed);
                    projectile.hurtMarked = true;
                }
            }
        }
        if (sequence <= 2 && livingEntity.tickCount % 100 == 0) {
            MonsterClass.showMonsterParticles(livingEntity);
        }
        if (livingEntity.tickCount % 3 == 0 && sequence <= 7) {
            int reverseChance = (int) (Math.random() * 20 - sequence);
            for (Projectile pProjectile : livingEntity.level().getEntitiesOfClass(Projectile.class, livingEntity.getBoundingBox().inflate(100))) {
                if (pProjectile.getPersistentData().getInt("monsterReverseProjectiles") == 0) {
                    if (pProjectile instanceof Arrow arrow && arrow.tickCount >= 80) {
                        return;
                    }
                    if (reverseChance >= 10) {
                        float scale = ScaleTypes.BASE.getScaleData(pProjectile).getScale();
                        double maxDistance = 6 * scale;
                        double deltaX = Math.abs(pProjectile.getX() - livingEntity.getX());
                        double deltaY = Math.abs(pProjectile.getY() - livingEntity.getY());
                        double deltaZ = Math.abs(pProjectile.getZ() - livingEntity.getZ());
                        if ((deltaX <= maxDistance && deltaY <= maxDistance && deltaZ <= maxDistance) && pProjectile.getOwner() != livingEntity) {
                            double x = pProjectile.getDeltaMovement().x() * -1;
                            double y = pProjectile.getDeltaMovement().y() * -1;
                            double z = pProjectile.getDeltaMovement().z() * -1;
                            pProjectile.setDeltaMovement(x, y, z);
                            pProjectile.hurtMarked = true;
                            if (livingEntity instanceof Player player) {
                                player.displayClientMessage(Component.literal("A strong breeze luckily reversed a projectile headed towards you").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GREEN), true);
                            }
                        }
                    }
                    pProjectile.getPersistentData().putInt("monsterReverseProjectiles", 60);
                } else {
                    pProjectile.getPersistentData().putInt("monsterReverseProjectiles", pProjectile.getPersistentData().getInt("windDodgeProjectilesCounter") - 1);
                }
            }
        }
        if (livingEntity.tickCount % 20 == 0) {
            if (livingEntity instanceof Player) {
                if (sequence == 8 || sequence == 7) {
                    if (livingEntity.getMainHandItem().getItem() instanceof SwordItem) {
                        applyMobEffect(livingEntity, MobEffects.MOVEMENT_SPEED, 60, speed + 1, true, true);
                    }
                    if (livingEntity.getMainHandItem().getItem() instanceof AxeItem) {
                        applyMobEffect(livingEntity, MobEffects.DAMAGE_BOOST, 60, strength + 1, true, true);
                    }
                    if (livingEntity.getMainHandItem().getItem() instanceof PickaxeItem || livingEntity.getMainHandItem().getItem() instanceof ShovelItem) {
                        applyMobEffect(livingEntity, MobEffects.DIG_SPEED, 60, 1, true, true);
                    }
                    if (livingEntity.getMainHandItem().getItem() instanceof BowItem || livingEntity.getMainHandItem().getItem() instanceof CrossbowItem) {
                        applyMobEffect(livingEntity, MobEffects.MOVEMENT_SPEED, 60, speed + 1, true, true);
                    }
                    if (livingEntity.getMainHandItem().getItem() instanceof ShieldItem || livingEntity.getOffhandItem().getItem() instanceof ShieldItem) {
                        applyMobEffect(livingEntity, MobEffects.DAMAGE_RESISTANCE, 60, resistance + 1, true, true);
                    }
                } else if (sequence == 6 || sequence == 5) {
                    if (livingEntity.getMainHandItem().getItem() instanceof SwordItem) {
                        applyMobEffect(livingEntity, MobEffects.MOVEMENT_SPEED, 60, speed + 1, true, true);
                        applyMobEffect(livingEntity, MobEffects.DIG_SPEED, 60, 0, true, true);
                    }
                    if (livingEntity.getMainHandItem().getItem() instanceof AxeItem) {
                        applyMobEffect(livingEntity, MobEffects.DAMAGE_BOOST, 60, strength + 1, true, true);
                        applyMobEffect(livingEntity, MobEffects.DAMAGE_RESISTANCE, 60, resistance + 1, true, true);
                    }
                    if (livingEntity.getMainHandItem().getItem() instanceof PickaxeItem || livingEntity.getMainHandItem().getItem() instanceof ShovelItem) {
                        applyMobEffect(livingEntity, MobEffects.DIG_SPEED, 60, 2, true, true);
                    }
                    if (livingEntity.getMainHandItem().getItem() instanceof BowItem || livingEntity.getMainHandItem().getItem() instanceof CrossbowItem) {
                        applyMobEffect(livingEntity, MobEffects.MOVEMENT_SPEED, 60, speed + 1, true, true);
                    }
                    if (livingEntity.getMainHandItem().getItem() instanceof ShieldItem || livingEntity.getOffhandItem().getItem() instanceof ShieldItem) {
                        applyMobEffect(livingEntity, MobEffects.DAMAGE_RESISTANCE, 60, resistance + 1, true, true);
                    }
                } else if (sequence <= 4) {
                    if (livingEntity.getMainHandItem().getItem() instanceof SwordItem) {
                        applyMobEffect(livingEntity, MobEffects.MOVEMENT_SPEED, 60, speed + 2, true, true);
                        applyMobEffect(livingEntity, MobEffects.DIG_SPEED, 60, 0, true, true);
                    }
                    if (livingEntity.getMainHandItem().getItem() instanceof AxeItem) {
                        applyMobEffect(livingEntity, MobEffects.DAMAGE_BOOST, 60, strength + 1, true, true);
                        applyMobEffect(livingEntity, MobEffects.DAMAGE_RESISTANCE, 60, resistance + 1, true, true);
                    }
                    if (livingEntity.getMainHandItem().getItem() instanceof PickaxeItem || livingEntity.getMainHandItem().getItem() instanceof ShovelItem) {
                        applyMobEffect(livingEntity, MobEffects.DIG_SPEED, 60, 3, true, true);
                    }
                    if (livingEntity.getMainHandItem().getItem() instanceof BowItem || livingEntity.getMainHandItem().getItem() instanceof CrossbowItem) {
                        applyMobEffect(livingEntity, MobEffects.MOVEMENT_SPEED, 60, speed + 2, true, true);
                        applyMobEffect(livingEntity, MobEffects.REGENERATION, 60, regen + 1, true, true);
                    }
                    if (livingEntity.getMainHandItem().getItem() instanceof ShieldItem || livingEntity.getOffhandItem().getItem() instanceof ShieldItem) {
                        applyMobEffect(livingEntity, MobEffects.DAMAGE_RESISTANCE, 60, resistance + 2, true, true);
                    }
                }
            } else {
                applyRandomWeaponEffects(livingEntity, sequence, speed, regen, resistance, strength);
            }
        }
    }

    @Override
    public String getID() {
        return "MonsterTickEventID";
    }

    private void applyRandomWeaponEffects(LivingEntity entity, int sequenceLevel, int speed, int regen, int resistance, int strength) {
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
                applyBowEffects(entity, sequenceLevel, speed, regen);
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
            // Select a random weapon type and store it persistently
            String[] weaponTypes = {"sword", "axe", "pickaxe", "bow", "shield"};
            weaponType = weaponTypes[entity.getRandom().nextInt(weaponTypes.length)];
            persistentData.putString("randomWeaponType", weaponType);
        }

        return weaponType;
    }

    private void applySwordEffects(LivingEntity entity, int sequenceLevel, int speed) {
        if (sequenceLevel == 8 || sequenceLevel == 7) {
            applyMobEffect(entity, MobEffects.MOVEMENT_SPEED, 60, speed + 1, true, true);
        } else if (sequenceLevel == 6 || sequenceLevel == 5) {
            applyMobEffect(entity, MobEffects.MOVEMENT_SPEED, 60, speed + 1, true, true);
            applyMobEffect(entity, MobEffects.DIG_SPEED, 60, 0, true, true);
        } else if (sequenceLevel <= 4) {
            applyMobEffect(entity, MobEffects.MOVEMENT_SPEED, 60, speed + 2, true, true);
            applyMobEffect(entity, MobEffects.DIG_SPEED, 60, 0, true, true);
        }
    }

    private void applyAxeEffects(LivingEntity entity, int sequenceLevel, int strength, int resistance) {
        if (sequenceLevel == 8 || sequenceLevel == 7) {
            applyMobEffect(entity, MobEffects.DAMAGE_BOOST, 60, strength + 1, true, true);
        } else if (sequenceLevel == 6 || sequenceLevel == 5) {
            applyMobEffect(entity, MobEffects.DAMAGE_BOOST, 60, strength + 1, true, true);
            applyMobEffect(entity, MobEffects.DAMAGE_RESISTANCE, 60, resistance + 1, true, true);
        } else if (sequenceLevel <= 4) {
            applyMobEffect(entity, MobEffects.DAMAGE_BOOST, 60, strength + 1, true, true);
            applyMobEffect(entity, MobEffects.DAMAGE_RESISTANCE, 60, resistance + 1, true, true);
        }
    }

    private void applyPickaxeEffects(LivingEntity entity, int sequenceLevel, int speed) {
        if (sequenceLevel == 8 || sequenceLevel == 7) {
            applyMobEffect(entity, MobEffects.MOVEMENT_SPEED, 60, speed + 1, true, true);

        } else if (sequenceLevel == 6 || sequenceLevel == 5) {
            applyMobEffect(entity, MobEffects.MOVEMENT_SPEED, 60, speed + 2, true, true);

        } else if (sequenceLevel <= 4) {
            applyMobEffect(entity, MobEffects.MOVEMENT_SPEED, 60, speed + 3, true, true);
        }
    }

    private void applyBowEffects(LivingEntity entity, int sequenceLevel, int speed, int regen) {
        if (sequenceLevel == 8 || sequenceLevel == 7) {
            applyMobEffect(entity, MobEffects.MOVEMENT_SPEED, 60, speed + 1, true, true);
        } else if (sequenceLevel == 6 || sequenceLevel == 5) {
            applyMobEffect(entity, MobEffects.MOVEMENT_SPEED, 60, speed + 1, true, true);
        } else if (sequenceLevel <= 4) {
            applyMobEffect(entity, MobEffects.MOVEMENT_SPEED, 60, speed + 2, true, true);
            applyMobEffect(entity, MobEffects.REGENERATION, 60, regen + 1, true, true);
        }
    }

    private void applyShieldEffects(LivingEntity entity, int sequenceLevel, int resistance) {
        if (sequenceLevel == 8 || sequenceLevel == 7) {
            applyMobEffect(entity, MobEffects.DAMAGE_RESISTANCE, 60, resistance + 1, true, true);
        } else if (sequenceLevel == 6 || sequenceLevel == 5) {
            applyMobEffect(entity, MobEffects.DAMAGE_RESISTANCE, 60, resistance + 1, true, true);
        } else if (sequenceLevel <= 4) {
            applyMobEffect(entity, MobEffects.DAMAGE_RESISTANCE, 60, resistance + 1, true, true);
        }
    }
}
