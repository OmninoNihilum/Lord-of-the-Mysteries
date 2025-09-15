package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses.BeyonderTicks.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;
import net.swimmingtuna.lotm.entity.LightningEntity;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.EntityUtil.BeamEntity;

import static net.swimmingtuna.lotm.util.BeyonderUtil.applyMobEffect;

public class SailorTickLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        LivingEntity player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }
        if (player.tickCount % 60 != 0) {
            return;
        }
        int matterTimer = player.getPersistentData().getInt("matterAccelerationEntitiesTimer");
        if (player.tickCount % 2 == 0) {
            if (BeyonderUtil.currentPathwayAndSequenceMatchesNoException(player, BeyonderClassInit.SAILOR.get(), 0) || (player.getPersistentData().getBoolean("rainEyes"))) {
                for (Entity pEntity : player.level().getEntitiesOfClass(Entity.class, player.getBoundingBox().inflate(100))) {
                    if (!BeyonderUtil.isEntityAlly(player, pEntity)) {
                        if (BeyonderUtil.getSequence(player) == 0) {
                            if (pEntity instanceof Projectile projectile) {
                                float projectileHeight = pEntity.getBbHeight();
                                float projectileWidth = pEntity.getBbWidth();
                                float projectileSize = Math.min(50, Math.max(10, projectileHeight * projectileWidth * 100));
                                double distance = pEntity.distanceTo(player);
                                double pushThreshold = Math.max(8.0, projectileSize * 0.25);
                                if (distance <= pushThreshold) {
                                    Vec3 playerPos = player.position();
                                    Vec3 projectilePos = pEntity.position();
                                    Vec3 pushDirection = projectilePos.subtract(playerPos).normalize();
                                    double pushForce = (projectileSize / 15.0) * (pushThreshold / Math.max(0.3, distance)) * 1.5;
                                    pushForce = Math.min(pushForce, 5.0);
                                    Vec3 pushVelocity = pushDirection.scale(pushForce);
                                    pEntity.setDeltaMovement(pEntity.getDeltaMovement().add(pushVelocity));
                                    pEntity.hurtMarked = true;
                                }
                            } else if (pEntity instanceof LightningEntity lightningEntity) {
                                if (lightningEntity.getLastPos().distanceTo(player.getOnPos().getCenter()) <= 30) {
                                    float randomX = Math.max(50, BeyonderUtil.getRandomInRange(100));
                                    float randomZ = Math.max(50, BeyonderUtil.getRandomInRange(100));
                                    if (lightningEntity.getTargetEntity() == player) {
                                        lightningEntity.setTargetEntity(null);
                                        lightningEntity.setTargetPos(new Vec3(randomX, player.getY() - 100, randomZ));
                                    }
                                    if (lightningEntity.getTargetPos().distanceTo(Vec3.atLowerCornerOf(player.getOnPos())) <= 20) {
                                        lightningEntity.setTargetPos(new Vec3(randomX, player.getY() - 100, randomZ));
                                    }
                                }
                            } else if (pEntity instanceof BeamEntity beamEntity) {
                                boolean x = beamEntity.getPersistentData().getBoolean("matterBeam");
                                if (beamEntity.getOwner() == null || beamEntity.getOwner() instanceof Mob) {
                                    x = true;
                                } else if (beamEntity.getOwner() instanceof Player owner && BeyonderUtil.isLookingTowards2D(owner, player, 30)) {
                                    x = true;
                                }
                                if (x) {
                                    if (beamEntity.getPersistentData().getInt("beamRange") == 0) {
                                        beamEntity.getPersistentData().putInt("beamRange", (int) beamEntity.getRange());
                                    }
                                    beamEntity.getPersistentData().putBoolean("matterBeam", true);
                                    beamEntity.setRange((int) beamEntity.distanceTo(player) - (5 * beamEntity.getSize()));
                                }
                            }
                        }
                        if (BeyonderUtil.getSequence(player) <= 2) {
                            if (player.getPersistentData().getBoolean("rainEyes")) {
                                if (pEntity instanceof LivingEntity living && living.tickCount % 200 == 0 && !BeyonderUtil.isInvisible(living) && pEntity != player) {
                                    if (BeyonderUtil.getPathway(living) != null || living instanceof Player) {
                                        int x = (int) living.getX();
                                        int y = (int) living.getY();
                                        int z = (int) living.getZ();
                                        String pathway = "non-existent";
                                        BeyonderClass pathwayResult = BeyonderUtil.getPathway(living);
                                        if (pathwayResult != null) {
                                            pathway = BeyonderUtil.getPathwayName(pathwayResult);
                                        }
                                        Style chatFormatting = BeyonderUtil.getStyle(living);
                                        int sequence = BeyonderUtil.getSequence(living);
                                        String name = living.getName().getString();
                                        int distance = (int) living.distanceTo(player);
                                        ChatFormatting sequenceColor;
                                        if (sequence >= 7 && sequence <= 9) {
                                            sequenceColor = ChatFormatting.GREEN;
                                        } else if (sequence >= 5 && sequence <= 6) {
                                            sequenceColor = ChatFormatting.YELLOW;
                                        } else if (sequence >= 3 && sequence <= 4) {
                                            sequenceColor = ChatFormatting.RED;
                                        } else if (sequence >= 0 && sequence <= 2) {
                                            sequenceColor = ChatFormatting.DARK_RED;
                                        } else {
                                            sequenceColor = ChatFormatting.WHITE;
                                        }
                                        Component message = Component.literal("")
                                                .append(Component.literal(name).withStyle(ChatFormatting.BOLD, ChatFormatting.GREEN))
                                                .append(Component.literal(" is a ").withStyle(ChatFormatting.BOLD))
                                                .append(Component.literal("Sequence ").withStyle(ChatFormatting.BOLD))
                                                .append(Component.literal(String.valueOf(sequence)).withStyle(ChatFormatting.BOLD, sequenceColor))
                                                .append(Component.literal(" ").withStyle(ChatFormatting.BOLD))
                                                .append(Component.literal(pathway).withStyle(chatFormatting)
                                                        .append(Component.literal(", and located ").withStyle(ChatFormatting.BOLD))
                                                        .append(Component.literal(String.valueOf(distance)).withStyle(ChatFormatting.BOLD, ChatFormatting.BLUE))
                                                        .append(Component.literal(" blocks away from you at ").withStyle(ChatFormatting.BOLD))
                                                        .append(Component.literal(x + "," + y + "," + z).withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA)));

                                        player.sendSystemMessage(message);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (matterTimer < 1) {
            return;
        }
        player.getPersistentData().putInt("matterAccelerationEntitiesTimer", matterTimer - 1);
        if (!BeyonderUtil.isCreative(player)) {
            if (player.getPersistentData().getInt("matterAccelerationEntitiesX") != 0) {
                int x = player.getPersistentData().getInt("matterAccelerationEntitiesX");
                int y = player.getPersistentData().getInt("matterAccelerationEntitiesY");
                int z = player.getPersistentData().getInt("matterAccelerationEntitiesZ");
                Vec3 matterPos = new Vec3(x, y, z);
                int damage = 0;
                if (player.tickCount % 2 == 0) {
                    for (LivingEntity living : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(15))) {
                        if (living.getPersistentData().getInt("matterAccelerationEntitiesTimer") >= 1 && living != player && player.isAlive()) {
                            damage++;
                        }
                    }
                    if (damage != 0) {
                        player.hurt(player.damageSources().generic(), damage * 0.2f);
                        player.invulnerableTime = 1;
                        player.hurtTime = 1;
                        player.hurtDuration = 1;
                    }
                }
                Vec3 entityVec3 = player.getOnPos().getCenter();
                Vec3 movement = new Vec3(matterPos.x() - entityVec3.x(), matterPos.y() - entityVec3.y(), matterPos.z() - entityVec3.z()).scale(1);
                player.setDeltaMovement(movement.x(), movement.y(), movement.z());
                player.hurtMarked = true;
                for (Projectile projectile : player.level().getEntitiesOfClass(Projectile.class, player.getBoundingBox().inflate(25))) {
                    if (projectile.getPersistentData().getInt("matterAccelerationEntitiesTimer") >= 1) {
                        Vec3 projectileVec3 = projectile.getOnPos().getCenter();
                        Vec3 projectileMovement = new Vec3(matterPos.x() - projectileVec3.x(), matterPos.y() - projectileVec3.y(), matterPos.z() - projectileVec3.z()).scale(1);
                        projectile.setDeltaMovement(projectileMovement.x(), projectileMovement.y(), projectileMovement.z());
                        projectile.hurtMarked = true;
                    }
                }
            }
        }
        int sequenceLevel = BeyonderUtil.getSequence(player);
        CompoundTag tag = player.getPersistentData();
        int dolhpinsGrace = 0;
        int speed = 0;
        int strength = 0;
        int haste = 0;
        int resistance = 0;
        int regeneration = 0;
        boolean sailorFlight1 = tag.getBoolean("sailorFlight1");
        Projectile projectile = BeyonderUtil.getProjectiles(player, 50);
        if (projectile != null) {
            LivingEntity target = BeyonderUtil.getTarget(projectile, 75, 0);
            if (target != null) {
                if (sequenceLevel <= 8 && player.getPersistentData().getBoolean("sailorProjectileMovement")) {
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
        if (player.isInWaterOrRain()) {
            if (player instanceof Player pPlayer) {
                Abilities playerAbilites = pPlayer.getAbilities();
                playerAbilites.setFlyingSpeed(0.1F);
                pPlayer.onUpdateAbilities();
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(serverPlayer.getAbilities()));
                }
            }
            if (sequenceLevel <= 4) {
                applyMobEffect(player, MobEffects.DOLPHINS_GRACE, 300, dolhpinsGrace + 2, false, false);
                applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 300, speed + 1, false, false);
                applyMobEffect(player, MobEffects.DIG_SPEED, 300, haste + 1, false, false);
                applyMobEffect(player, MobEffects.DAMAGE_RESISTANCE, 300, resistance + 1, false, false);
                applyMobEffect(player, MobEffects.DAMAGE_BOOST, 300, strength + 2, false, false);
                applyMobEffect(player, MobEffects.REGENERATION, 300, regeneration + 2, false, false);
            } else if (sequenceLevel <= 6) {
                applyMobEffect(player, MobEffects.DOLPHINS_GRACE, 300, dolhpinsGrace + 1, false, false);
                applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 300, speed + 1, false, false);
                applyMobEffect(player, MobEffects.DIG_SPEED, 300, haste + 1, false, false);
                applyMobEffect(player, MobEffects.DAMAGE_RESISTANCE, 300, resistance + 1, false, false);
                applyMobEffect(player, MobEffects.DAMAGE_BOOST, 300, strength + 1, false, false);
                applyMobEffect(player, MobEffects.REGENERATION, 300, regeneration + 1, false, false);
            } else {
                applyMobEffect(player, MobEffects.DOLPHINS_GRACE, 300, dolhpinsGrace + 1, false, false);
                applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 300, speed + 1, false, false);
                applyMobEffect(player, MobEffects.DIG_SPEED, 300, haste + 1, false, false);
                applyMobEffect(player, MobEffects.REGENERATION, 300, regeneration + 1, false, false);
            }
        }
        if (!player.level().isRaining() && !sailorFlight1 && player instanceof Player pPlayer) {
            Abilities playerAbilites = pPlayer.getAbilities();
            playerAbilites.setFlyingSpeed(0.05F);
            pPlayer.onUpdateAbilities();
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(serverPlayer.getAbilities()));
            }
        }
    }

    @Override
    public String getID() {
        return "SailorTickEventID";
    }
}
