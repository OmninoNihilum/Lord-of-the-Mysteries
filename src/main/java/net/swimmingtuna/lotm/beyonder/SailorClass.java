package net.swimmingtuna.lotm.beyonder;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.Event;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.util.BeyonderUtil;

import java.util.*;

public class SailorClass implements BeyonderClass {
    public static int dolhpinsGrace;
    public static int speed;
    public static int strength;
    public static int haste;
    public static int resistance;
    public static int regeneration;

    @Override
    public List<String> sequenceNames() {
        return List.of(
                "Tyrant",
                "Thunder God",
                "Calamity",
                "Sea King",
                "Cataclysmic Interrer",
                "Ocean Songster",
                "Wind-blessed",
                "Seafarer",
                "Folk of Rage",
                "Sailor"
        );
    }

    @Override
    public List<Integer> antiDivination() {
        return List.of(20, 15, 13, 9, 5, 2, 1, 1, 1, 1);
    }

    @Override
    public List<Integer> divination() {
        return List.of(20, 15, 13, 9, 5, 2, 1, 1, 1, 1);
    }

    @Override
    public List<Integer> spiritualityLevels() {
        return List.of(25000, 10000, 6000, 3000, 1800, 800, 500, 350, 175, 125);
    }

    @Override
    public List<Integer> mentalStrength() {
        return List.of(400, 300, 220, 170, 150, 110, 80, 70, 50, 30);
    }

    @Override
    public List<Integer> spiritualityRegen() {
        return List.of(45, 30, 20, 15, 12, 9, 6, 5, 3, 2);
    }

    @Override
    public List<Double> maxHealth() {
        return List.of(80.0, 65.0, 60.0, 50.0, 45.0, 35.0, 30.0, 30.0, 25.0, 23.0);
    }

    @Override
    public void tick(LivingEntity player, int sequenceLevel) {
        if (player.tickCount % 60 != 0) {
            return;
        }
        CompoundTag tag = player.getPersistentData();

        boolean sailorFlight1 = tag.getBoolean("sailorFlight1");
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

        if (sequenceLevel == 9) {
            applyMobEffect(player, MobEffects.DOLPHINS_GRACE, 300, 0, false, false);
            applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 300, 0, false, false);
            applyMobEffect(player, MobEffects.DAMAGE_RESISTANCE, 300, 0, false, false);
            applyMobEffect(player, MobEffects.NIGHT_VISION, 300, 0, false, false);
            dolhpinsGrace = 0;
            regeneration = -1;
            speed = 0;
            strength = -1;
            resistance = 0;
            haste = -1;
        } else if (sequenceLevel == 8) {
            applyMobEffect(player, MobEffects.DOLPHINS_GRACE, 300, 0, false, false);
            applyMobEffect(player, MobEffects.DAMAGE_BOOST, 300, 0, false, false);
            applyMobEffect(player, MobEffects.DIG_SPEED, 300, 0, false, false);
            applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 300, 0, false, false);
            applyMobEffect(player, MobEffects.DAMAGE_RESISTANCE, 300, 0, false, false);
            applyMobEffect(player, MobEffects.NIGHT_VISION, 300, 0, false, false);
            dolhpinsGrace = 0;
            regeneration = -1;
            speed = 0;
            strength = 0;
            resistance = 0;
            haste = 0;
        } else if (sequenceLevel == 7) {
            applyMobEffect(player, MobEffects.DOLPHINS_GRACE, 300, 1, false, false);
            applyMobEffect(player, MobEffects.DAMAGE_BOOST, 300, 1, false, false);
            applyMobEffect(player, MobEffects.DIG_SPEED, 300, 0, false, false);
            applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 300, 1, false, false);
            applyMobEffect(player, MobEffects.DAMAGE_RESISTANCE, 300, 0, false, false);
            applyMobEffect(player, MobEffects.NIGHT_VISION, 300, 0, false, false);
            applyMobEffect(player, MobEffects.WATER_BREATHING, 300, 0, false, false);
            dolhpinsGrace = 1;
            regeneration = -1;
            speed = 1;
            strength = 1;
            resistance = 0;
            haste = 0;
        } else if (sequenceLevel == 6) {
            applyMobEffect(player, MobEffects.DOLPHINS_GRACE, 300, 1, false, false);
            applyMobEffect(player, MobEffects.NIGHT_VISION, 300, 0, false, false);
            applyMobEffect(player, MobEffects.DAMAGE_BOOST, 300, 1, false, false);
            applyMobEffect(player, MobEffects.DIG_SPEED, 300, 1, false, false);
            applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 300, 1, false, false);
            applyMobEffect(player, MobEffects.DAMAGE_RESISTANCE, 300, 0, false, false);
            applyMobEffect(player, MobEffects.WATER_BREATHING, 300, 0, false, false);
            dolhpinsGrace = 1;
            regeneration = -1;
            speed = 1;
            strength = 1;
            resistance = 0;
            haste = 1;
        } else if (sequenceLevel == 5) {
            applyMobEffect(player, MobEffects.DOLPHINS_GRACE, 300, 1, false, false);
            applyMobEffect(player, MobEffects.NIGHT_VISION, 300, 0, false, false);
            applyMobEffect(player, MobEffects.DIG_SPEED, 300, 1, false, false);
            applyMobEffect(player, MobEffects.DAMAGE_BOOST, 300, 2, false, false);
            applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 300, 1, false, false);
            applyMobEffect(player, MobEffects.DAMAGE_RESISTANCE, 300, 1, false, false);
            applyMobEffect(player, MobEffects.WATER_BREATHING, 300, 1, false, false);
            dolhpinsGrace = 1;
            regeneration = -1;
            speed = 1;
            strength = 2;
            resistance = 1;
            haste = 1;
        } else if (sequenceLevel == 4) {
            applyMobEffect(player, MobEffects.DOLPHINS_GRACE, 300, 2, false, false);
            applyMobEffect(player, MobEffects.NIGHT_VISION, 300, 0, false, false);
            applyMobEffect(player, MobEffects.DIG_SPEED, 300, 2, false, false);
            applyMobEffect(player, MobEffects.DAMAGE_BOOST, 300, 3, false, false);
            applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 300, 2, false, false);
            applyMobEffect(player, MobEffects.DAMAGE_RESISTANCE, 300, 2, false, false);
            applyMobEffect(player, MobEffects.WATER_BREATHING, 300, 2, false, false);
            dolhpinsGrace = 2;
            regeneration = -1;
            speed = 2;
            strength = 3;
            resistance = 2;
            haste = 2;
        } else if (sequenceLevel == 3) {
            applyMobEffect(player, MobEffects.DOLPHINS_GRACE, 300, 2, false, false);
            applyMobEffect(player, MobEffects.NIGHT_VISION, 300, 0, false, false);
            applyMobEffect(player, MobEffects.DIG_SPEED, 300, 3, false, false);
            applyMobEffect(player, MobEffects.DAMAGE_BOOST, 300, 3, false, false);
            applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 300, 2, false, false);
            applyMobEffect(player, MobEffects.DAMAGE_RESISTANCE, 300, 2, false, false);
            applyMobEffect(player, MobEffects.WATER_BREATHING, 300, 2, false, false);
            dolhpinsGrace = 2;
            regeneration = -1;
            speed = 2;
            strength = 3;
            resistance = 2;
            haste = 3;
        } else if (sequenceLevel == 2) {
            applyMobEffect(player, MobEffects.DOLPHINS_GRACE, 300, 2, false, false);
            applyMobEffect(player, MobEffects.NIGHT_VISION, 300, 0, false, false);
            applyMobEffect(player, MobEffects.DIG_SPEED, 300, 3, false, false);
            applyMobEffect(player, MobEffects.DAMAGE_BOOST, 300, 4, false, false);
            applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 300, 3, false, false);
            applyMobEffect(player, MobEffects.DAMAGE_RESISTANCE, 300, 2, false, false);
            applyMobEffect(player, MobEffects.WATER_BREATHING, 300, 3, false, false);
            dolhpinsGrace = 2;
            regeneration = -1;
            speed = 3;
            strength = 4;
            resistance = 2;
            haste = 3;

        } else if (sequenceLevel == 1) {
            applyMobEffect(player, MobEffects.DOLPHINS_GRACE, 300, 2, false, false);
            applyMobEffect(player, MobEffects.NIGHT_VISION, 300, 0, false, false);
            applyMobEffect(player, MobEffects.DAMAGE_BOOST, 300, 4, false, false);
            applyMobEffect(player, MobEffects.DIG_SPEED, 300, 4, false, false);
            applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 300, 3, false, false);
            applyMobEffect(player, MobEffects.DAMAGE_RESISTANCE, 300, 2, false, false);
            applyMobEffect(player, MobEffects.WATER_BREATHING, 300, 4, false, false);
            dolhpinsGrace = 2;
            regeneration = -1;
            speed = 3;
            strength = 4;
            resistance = 2;
            haste = 4;
        } else if (sequenceLevel == 0) {
            applyMobEffect(player, MobEffects.DOLPHINS_GRACE, 300, 2, false, false);
            applyMobEffect(player, MobEffects.NIGHT_VISION, 300, 0, false, false);
            applyMobEffect(player, MobEffects.DAMAGE_BOOST, 300, 5, false, false);
            applyMobEffect(player, MobEffects.DIG_SPEED, 300, 4, false, false);
            applyMobEffect(player, MobEffects.MOVEMENT_SPEED, 300, 3, false, false);
            applyMobEffect(player, MobEffects.DAMAGE_RESISTANCE, 300, 2, false, false);
            applyMobEffect(player, MobEffects.WATER_BREATHING, 300, 4, false, false);
            dolhpinsGrace = 2;
            regeneration = -1;
            speed = 3;
            strength = 5;
            resistance = 2;
            haste = 5;
        }
    }

    @Override
    public SimpleContainer getAbilityItemsContainer(int sequenceLevel) {
        SimpleContainer container = new SimpleContainer(45);
        Map<Integer, List<ItemStack>> orderedItems = new LinkedHashMap<>();
        for (int i = 9; i >= sequenceLevel; i--) {
            orderedItems.put(i, new ArrayList<>());
        }

        Multimap<Integer, Item> items = getItems();
        for (Map.Entry<Integer, Item> entry : items.entries()) {
            int level = entry.getKey();
            Item item = entry.getValue();

            if (level >= sequenceLevel) {
                if (item == ItemInit.AQUATIC_LIFE_MANIPULATION.get() && sequenceLevel < 3) {
                    continue;
                }
                if (item == ItemInit.LIGHTNING_BALL.get() && sequenceLevel < 2) {
                    continue;
                }
                orderedItems.get(level).add(item.getDefaultInstance());
            }
        }

        int slotIndex = 0;
        for (int i = 9; i >= sequenceLevel; i--) {
            List<ItemStack> levelItems = orderedItems.get(i);
            for (ItemStack stack : levelItems) {
                container.setItem(slotIndex++, stack);
            }
        }

        return container;
    }


    @Override
    public Multimap<Integer, Item> getItems() {
        HashMultimap<Integer, Item> items = HashMultimap.create();
        items.put(9, ItemInit.BEYONDER_ABILITY_USER.get());
        items.put(9, ItemInit.ALLY_MAKER.get());

        items.put(8, ItemInit.RAGING_BLOWS.get());
        items.put(8, ItemInit.SAILORPROJECTILECTONROL.get());

        items.put(7, ItemInit.ENABLE_OR_DISABLE_LIGHTNING.get());
        items.put(7, ItemInit.AQUEOUS_LIGHT_DROWN.get());
        items.put(7, ItemInit.AQUEOUS_LIGHT_PULL.get());
        items.put(7, ItemInit.AQUEOUS_LIGHT_PUSH.get());

        items.put(6, ItemInit.WIND_MANIPULATION_BLADE.get());
        items.put(6, ItemInit.WIND_MANIPULATION_FLIGHT.get());
        items.put(6, ItemInit.WIND_MANIPULATION_SENSE.get());

        items.put(5, ItemInit.SAILOR_LIGHTNING.get());
        items.put(5, ItemInit.SIREN_SONG_HARM.get());
        items.put(5, ItemInit.SIREN_SONG_STRENGTHEN.get());
        items.put(5, ItemInit.SIREN_SONG_WEAKEN.get());
        items.put(5, ItemInit.SIREN_SONG_STUN.get());
        items.put(5, ItemInit.ACIDIC_RAIN.get());
        items.put(5, ItemInit.WATER_SPHERE.get());

        items.put(4, ItemInit.TSUNAMI.get());
        items.put(4, ItemInit.TSUNAMI_SEAL.get());
        items.put(4, ItemInit.HURRICANE.get());
        items.put(4, ItemInit.TORNADO.get());
        items.put(4, ItemInit.EARTHQUAKE.get());
        items.put(4, ItemInit.ROAR.get());

        items.put(3, ItemInit.AQUATIC_LIFE_MANIPULATION.get());
        items.put(3, ItemInit.LIGHTNING_STORM.get());
        items.put(3, ItemInit.LIGHTNING_BRANCH.get());
        items.put(3, ItemInit.SONIC_BOOM.get());
        items.put(3, ItemInit.THUNDER_CLAP.get());

        items.put(2, ItemInit.LIGHTNING_BALL.get());
        items.put(2, ItemInit.VOLCANIC_ERUPTION.get());
        items.put(2, ItemInit.RAIN_EYES.get());
        items.put(2, ItemInit.EXTREME_COLDNESS.get());

        items.put(1, ItemInit.LIGHTNING_BALL_ABSORB.get());
        items.put(1, ItemInit.STAR_OF_LIGHTNING.get());
        items.put(1, ItemInit.SAILOR_LIGHTNING_TRAVEL.get());
        items.put(1, ItemInit.LIGHTNING_REDIRECTION.get());

        items.put(0, ItemInit.STORM_SEAL.get());
        items.put(0, ItemInit.WATER_COLUMN.get());
        items.put(0, ItemInit.MATTER_ACCELERATION_BLOCKS.get());
        items.put(0, ItemInit.MATTER_ACCELERATION_SELF.get());
        items.put(0, ItemInit.MATTER_ACCELERATION_ENTITIES.get());
        items.put(0, ItemInit.TYRANNY.get());


        return items;
    }

    @Override
    public ChatFormatting getColorFormatting() {
        return ChatFormatting.BLUE;
    }


    public void applyMobEffect(LivingEntity pPlayer, MobEffect mobEffect, int duration, int amplifier, boolean ambient, boolean visible) {
        MobEffectInstance currentEffect = pPlayer.getEffect(mobEffect);
        MobEffectInstance newEffect = new MobEffectInstance(mobEffect, duration, amplifier, ambient, visible);
        if (currentEffect == null) {
            pPlayer.addEffect(newEffect);
        } else if (currentEffect.getAmplifier() < amplifier) {
            pPlayer.addEffect(newEffect);
        } else if (currentEffect.getAmplifier() == amplifier && duration >= currentEffect.getDuration()) {
            pPlayer.addEffect(newEffect);
        }
    }

    public static void sailorLightningPassive(AttackEntityEvent event) {
        LivingEntity player = event.getEntity();
        if (event.getTarget() instanceof LivingEntity livingEntity) {
            boolean sailorLightning = player.getPersistentData().getBoolean("SailorLightning");
            if (BeyonderUtil.currentPathwayMatchesNoException(livingEntity, BeyonderClassInit.SAILOR.get()) && BeyonderUtil.getSequence(livingEntity) <= 7 && event.getTarget() instanceof LivingEntity livingTarget && sailorLightning && livingTarget != player) {
                int sequence = BeyonderUtil.getSequence(livingEntity);
                double chanceOfDamage = (100.0 - (sequence * 12.5));
                if (Math.random() * 100 < chanceOfDamage) {
                    LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, livingTarget.level());
                    lightningBolt.moveTo(livingTarget.getX(), livingTarget.getY(), livingTarget.getZ());
                    lightningBolt.setVisualOnly(false);
                    lightningBolt.setDamage(Math.max(3, 15 - (sequence * 2)));
                    if (BeyonderUtil.getSequence(livingEntity) <= 1) {
                        float amount = 3;
                        if (BeyonderUtil.getSequence(player) == 1) {
                            amount = 2;
                        }
                        BeyonderUtil.applyMentalDamage(player, livingTarget, amount);
                    }
                    livingTarget.level().addFreshEntity(lightningBolt);
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    public static void sailorAttackEvent(LivingAttackEvent event) {
        LivingEntity attacked = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        if (attacker != null) {
            if (!attacked.level().isClientSide() && !attacker.level().isClientSide() && attacker instanceof LivingEntity livingAttacker) {
                if (BeyonderUtil.currentPathwayAndSequenceMatchesNoException(livingAttacker, BeyonderClassInit.SAILOR.get(), 2) && !BeyonderUtil.areAllies(attacked, livingAttacker)) {
                    for (Mob mob : livingAttacker.level().getEntitiesOfClass(Mob.class, livingAttacker.getBoundingBox().inflate(100))) {
                        if (mob == attacked) {
                            continue;
                        }
                        if (mob.canBreatheUnderwater() || mob.getNavigation() instanceof WaterBoundPathNavigation || mob instanceof WaterAnimal || mob.getName().getString().toLowerCase().contains("fish")) {
                            if (mob.getPersistentData().getInt("rainEyesMobAttackTarget") == 0) {
                                mob.getPersistentData().putInt("rainEyesMobAttackTarget", (int) ((int) (float) BeyonderUtil.getDamage(livingAttacker).get(ItemInit.RAIN_EYES.get()) * 0.75f));
                                mob.getPersistentData().putUUID("rainEyesMobAttackTargetUUID", attacked.getUUID());
                            }
                        }
                    }
                }
            }
        }
    }

    public static void rainEyesTickEvent(LivingEvent.LivingTickEvent event) {
        LivingEntity living = event.getEntity();
        CompoundTag tag = living.getPersistentData();
        int rainEyesAttackCounter = living.getPersistentData().getInt("rainEyesMobAttackTarget");
        if (rainEyesAttackCounter >= 1) {
            tag.putInt("rainEyesMobAttackTarget", rainEyesAttackCounter - 1);
            if (tag.contains("rainEyesMobAttackTargetUUID")) {
                UUID target = tag.getUUID("rainEyesMobAttackTargetUUID");
                LivingEntity livingTarget = BeyonderUtil.getLivingEntityFromUUID(living.level(), target);
                if (livingTarget != null && livingTarget.level().dimension() == living.level().dimension()) {
                    if (livingTarget instanceof Mob mob && mob.getTarget() == null) {
                        mob.setTarget(livingTarget);
                    }
                    Vec3 targetPos = livingTarget.position().add(0, 1, 0);
                    Vec3 currentPos = living.position();
                    Vec3 direction = targetPos.subtract(currentPos).normalize();
                    double speed = 1.2;
                    Vec3 motion = direction.scale(speed);
                    if (motion.y < 0.1) {
                        motion = motion.add(0, 0.2, 0);
                    }
                    living.setDeltaMovement(motion);
                    double distanceToTarget = currentPos.distanceTo(targetPos);
                    if (living.getHealth() < 20.0f && distanceToTarget < 5) {
                        explodeMob(living);
                        return;
                    } else if (living.getHealth() > 20.0f && distanceToTarget < 4 && living.tickCount % 20 == 0) {
                        livingTarget.hurt(BeyonderUtil.genericSource(living, livingTarget), (Math.min(50, living.getMaxHealth() / 10)));
                    }
                } else if (livingTarget instanceof Mob mob && mob.getTarget() == null) {
                    mob.setTarget(livingTarget);
                }
                BeyonderUtil.sendParticles(living, ParticleTypes.RAIN, living.getX(), living.getY(), living.getZ(), 0, -1, 0);
            }
            BeyonderUtil.sendParticles(living, ParticleTypes.RAIN, living.getX(), living.getY(), living.getZ(), 0, -1, 0);
        }
    }

    private static void explodeMob(LivingEntity mob) {
        if (!mob.level().isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) mob.level();
            Vec3 mobPos = mob.position();
            for (int i = 0; i < 50; i++) {
                double x = (Math.random() - 0.5) * 2.0;
                double y = (Math.random() - 0.5) * 2.0;
                double z = (Math.random() - 0.5) * 2.0;
                Vec3 direction = new Vec3(x, y, z).normalize().scale(0.5);

                serverLevel.sendParticles(ParticleTypes.DAMAGE_INDICATOR, mobPos.x, mobPos.y + 1, mobPos.z, 1, direction.x, direction.y, direction.z, 0.3);
            }
            for (int i = 0; i < 30; i++) {
                double x = mobPos.x + (Math.random() - 0.5) * 4.0;
                double y = mobPos.y + Math.random() * 2.0;
                double z = mobPos.z + (Math.random() - 0.5) * 4.0;
                serverLevel.sendParticles(new DustParticleOptions(Vec3.fromRGB24(0xFF0000).toVector3f(), 1.0f), x, y, z, 1, 0, 0, 0, 0);
            }
            for (LivingEntity entity : mob.level().getEntitiesOfClass(LivingEntity.class, mob.getBoundingBox().inflate(4.0 * BeyonderUtil.getScale(mob)))) {
                if (entity != mob) {
                    float damage = Math.min(60, mob.getMaxHealth());
                    entity.hurt(mob.damageSources().lightningBolt(), damage);
                    entity.invulnerableTime = 3;
                    entity.hurtTime = 3;
                    entity.hurtDuration = 3;
                }
            }
            mob.discard();
            if (mob != null) {
                mob.kill();
            }
        }
    }

    public static void sailorProjectileLightning(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();
        if (!projectile.level().isClientSide()) {
            CompoundTag tag = projectile.getPersistentData();
            int x = tag.getInt("sailorLightningProjectileCounter");
            if (event.getRayTraceResult().getType() == HitResult.Type.ENTITY && x >= 1) {
                EntityHitResult entityHit = (EntityHitResult) event.getRayTraceResult();
                Entity entity = entityHit.getEntity();
                if (!entity.level().isClientSide()) {
                    if (entity instanceof LivingEntity) {
                        entity.hurt(BeyonderUtil.lightningSource(projectile, entity), (x * 5));
                        LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, entity.level());
                        lightningBolt.moveTo(entity.getX(), entity.getY(), entity.getZ());
                        entity.level().addFreshEntity(lightningBolt);
                        event.setResult(Event.Result.DENY);
                    }
                }
            }
            if (event.getRayTraceResult().getType() == HitResult.Type.BLOCK && x >= 1) {
                Vec3 blockPos = event.getRayTraceResult().getLocation();
                LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, projectile.level());
                lightningBolt.moveTo(blockPos);
                projectile.level().addFreshEntity(lightningBolt);
                projectile.level().explode(null, blockPos.x(), blockPos.y(), blockPos.z(), 4, Level.ExplosionInteraction.BLOCK);
            }
        }
    }
}
