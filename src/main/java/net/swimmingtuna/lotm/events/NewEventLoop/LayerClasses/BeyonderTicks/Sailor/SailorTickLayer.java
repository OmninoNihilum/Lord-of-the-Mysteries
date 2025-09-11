package net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses.BeyonderTicks.Sailor;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.IFunction;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.util.BeyonderUtil;

import static net.swimmingtuna.lotm.util.BeyonderUtil.applyMobEffect;

public class SailorTickLayer implements IFunction {
    @Override
    public void use(LivingEvent.LivingTickEvent event) {
        LivingEntity player = event.getEntity();
        if (player.tickCount % 60 != 0) {
            return;
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
