package net.swimmingtuna.lotm.item.BeyonderAbilities.Monster;


import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.networking.packet.UpdateItemInHandC2S;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.LeftClickHandler.LeftClickHandlerSkillP;
import net.swimmingtuna.lotm.util.LeftClickHandler.LeftClickType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RebootSelf extends LeftClickHandlerSkillP {

    public RebootSelf(Properties properties) {
        super(properties, BeyonderClassInit.MONSTER, 1, 2000, 900);
    }

    @Override
    public InteractionResult useAbility(Level level, LivingEntity player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        activateSpiritVision(player);
        if (!player.isShiftKeyDown()) {
            useSpirituality(player);
            addCooldown(player);
        }
        return InteractionResult.SUCCESS;
    }

    private void activateSpiritVision(LivingEntity player) {
        if (!player.level().isClientSide()) {
            if (player.isShiftKeyDown()) {
                saveDataReboot(player, player.getPersistentData());
                if (player instanceof Player pPlayer) {
                    pPlayer.displayClientMessage(Component.literal("Saved State.").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD), true);
                }
            } else {
                restoreDataReboot(player, player.getPersistentData());
                if (player instanceof Player pPlayer) {
                    pPlayer.displayClientMessage(Component.literal("Loaded State.").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD), true);
                }
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use if you're shifting, save your current state including all persistent data, health, spirituality, potion effects, and item cooldowns. If not shifting, load your saved state"));
        tooltipComponents.add(Component.literal("Left click for Fate Authority: Cycle of Fate."));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("2000").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("45 Seconds").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    private static void saveDataReboot(LivingEntity player, CompoundTag tag) {
        LOTM.LOGGER.info("=== DEBUG: Starting saveDataReboot ===");

        // Debug current values before saving
        float currentHealth = player.getHealth();
        int currentSpirituality = BeyonderUtil.getSpirituality(player);
        Collection<MobEffectInstance> currentEffects = player.getActiveEffects();

        LOTM.LOGGER.info("DEBUG: Current Health: {}", currentHealth);
        LOTM.LOGGER.info("DEBUG: Current Spirituality: {}", currentSpirituality);
        LOTM.LOGGER.info("DEBUG: Current Potion Effects Count: {}", currentEffects.size());

        // Create backup of current persistent data
        CompoundTag backupTag = new CompoundTag();
        backupTag.merge(tag);
        tag.put("monsterRebootAllData", backupTag);
        LOTM.LOGGER.info("DEBUG: Backed up persistent data with {} keys", backupTag.getAllKeys().size());

        // Save potion effects
        tag.putInt("monsterRebootPotionEffectsCount", currentEffects.size());
        int i = 0;
        for (MobEffectInstance effect : currentEffects) {
            CompoundTag effectTag = new CompoundTag();
            effect.save(effectTag);
            tag.put("monsterRebootPotionEffect_" + i, effectTag);
            LOTM.LOGGER.info("DEBUG: Saved potion effect {}: {} for {} ticks", i, effect.getEffect().getDescriptionId(), effect.getDuration());
            i++;
        }

        // Save health and spirituality
        tag.putFloat("monsterRebootHealth", currentHealth);
        tag.putInt("monsterRebootSpirituality", currentSpirituality);
        LOTM.LOGGER.info("DEBUG: Saved Health: {}", currentHealth);
        LOTM.LOGGER.info("DEBUG: Saved Spirituality: {}", currentSpirituality);

        // Save item cooldowns
        List<Item> beyonderAbilities = BeyonderUtil.getAbilities(player);
        LOTM.LOGGER.info("DEBUG: Found {} beyonder abilities", beyonderAbilities.size());

        if (player instanceof Player pPlayer) {
            for (Item item : beyonderAbilities) {
                if (item != ItemInit.REBOOTSELF.get()) {
                    String itemCooldowns = item.getDescription().toString();
                    float cooldownPercent = pPlayer.getCooldowns().getCooldownPercent(item, 0);
                    tag.putFloat("monsterRebootCooldown" + itemCooldowns, cooldownPercent);
                    LOTM.LOGGER.info("DEBUG: Saved cooldown for {}: {}", itemCooldowns, cooldownPercent);
                }
            }
        }

        LOTM.LOGGER.info("=== DEBUG: Completed saveDataReboot ===");
    }

    private static void restoreDataReboot(LivingEntity player, CompoundTag tag) {
        LOTM.LOGGER.info("=== DEBUG: Starting restoreDataReboot ===");

        if (!tag.contains("monsterRebootAllData")) {
            LOTM.LOGGER.warn("DEBUG: No saved state found in persistent data!");
            if (player instanceof Player pPlayer) {
                pPlayer.displayClientMessage(Component.literal("No saved state found!").withStyle(ChatFormatting.RED), true);
            }
            return;
        }

        // **CRITICAL FIX**: Extract reboot-specific data BEFORE doing any tag operations
        float savedHealth = tag.getFloat("monsterRebootHealth");
        int savedSpirituality = tag.getInt("monsterRebootSpirituality");
        int effectCount = tag.getInt("monsterRebootPotionEffectsCount");

        // Extract all potion effect data before tag operations
        List<CompoundTag> savedEffects = new ArrayList<>();
        for (int i = 0; i < effectCount; i++) {
            CompoundTag effectTag = tag.getCompound("monsterRebootPotionEffect_" + i);
            if (!effectTag.isEmpty()) {
                savedEffects.add(effectTag.copy());
                LOTM.LOGGER.info("DEBUG: Extracted potion effect {}: {} bytes", i, effectTag.toString().length());
            } else {
                LOTM.LOGGER.warn("DEBUG: Empty effect tag found for effect {}", i);
            }
        }

        // Extract cooldown data before tag operations
        List<Item> beyonderAbilities = BeyonderUtil.getAbilities(player);
        List<Float> savedCooldowns = new ArrayList<>();
        List<String> cooldownKeys = new ArrayList<>();

        if (player instanceof Player) {
            for (Item item : beyonderAbilities) {
                if (item != ItemInit.REBOOTSELF.get()) {
                    String itemCooldowns = item.getDescription().toString();
                    float savedCooldownPercent = tag.getFloat("monsterRebootCooldown" + itemCooldowns);
                    savedCooldowns.add(savedCooldownPercent);
                    cooldownKeys.add(itemCooldowns);
                    LOTM.LOGGER.info("DEBUG: Extracted cooldown for {}: {}", itemCooldowns, savedCooldownPercent);
                }
            }
        }

        LOTM.LOGGER.info("DEBUG: About to restore - Health: {}, Spirituality: {}, Effects: {}", savedHealth, savedSpirituality, effectCount);

        // Remove current potion effects
        Collection<MobEffectInstance> currentEffects = new ArrayList<>(player.getActiveEffects());
        LOTM.LOGGER.info("DEBUG: Removing {} current potion effects", currentEffects.size());
        for (MobEffectInstance activeEffect : currentEffects) {
            player.removeEffect(activeEffect.getEffect());
            LOTM.LOGGER.info("DEBUG: Removed effect: {}", activeEffect.getEffect().getDescriptionId());
        }

        // Save the backup data BEFORE clearing the tag
        CompoundTag backupTag = tag.getCompound("monsterRebootAllData");
        CompoundTag rebootBackup = backupTag.copy(); // Keep a copy of the backup for future use

        LOTM.LOGGER.info("DEBUG: Backup tag has {} keys", backupTag.getAllKeys().size());

        // Clear current data and restore from backup
        tag.getAllKeys().clear();
        tag.merge(backupTag);

        // Re-add the backup data so we can use it again
        tag.put("monsterRebootAllData", rebootBackup);

        LOTM.LOGGER.info("DEBUG: Restored persistent data");

        // Now restore potion effects from our extracted data
        for (int i = 0; i < savedEffects.size(); i++) {
            CompoundTag effectTag = savedEffects.get(i);
            MobEffectInstance effect = MobEffectInstance.load(effectTag);
            if (effect != null) {
                player.addEffect(effect);
                LOTM.LOGGER.info("DEBUG: Restored potion effect {}: {} for {} ticks", i, effect.getEffect().getDescriptionId(), effect.getDuration());
            } else {
                LOTM.LOGGER.warn("DEBUG: Failed to load potion effect {}", i);
            }
        }

        // Restore health and spirituality
        float healthToSet = Math.max(1, savedHealth);
        player.setHealth(healthToSet);
        BeyonderUtil.setSpirituality(player, savedSpirituality);

        LOTM.LOGGER.info("DEBUG: Set Health to: {} (was {})", healthToSet, savedHealth);
        LOTM.LOGGER.info("DEBUG: Set Spirituality to: {}", savedSpirituality);

        // Restore item cooldowns from extracted data
        LOTM.LOGGER.info("DEBUG: Restoring cooldowns for {} abilities", beyonderAbilities.size());

        if (player instanceof Player pPlayer) {
            for (int i = 0; i < Math.min(beyonderAbilities.size(), savedCooldowns.size()); i++) {
                Item item = beyonderAbilities.get(i);
                if (item instanceof SimpleAbilityItem simpleAbilityItem && item != ItemInit.REBOOTSELF.get()) {
                    float savedCooldownPercent = savedCooldowns.get(i);

                    if (savedCooldownPercent > 0) {
                        int remainingCooldownTicks = (int) (simpleAbilityItem.getCooldown() * savedCooldownPercent);
                        pPlayer.getCooldowns().addCooldown(item, remainingCooldownTicks);
                        LOTM.LOGGER.info("DEBUG: Restored cooldown for {}: {}% ({} ticks)", cooldownKeys.get(i), savedCooldownPercent * 100, remainingCooldownTicks);
                    }
                }
            }
        }

        // Final verification
        LOTM.LOGGER.info("DEBUG: Final verification - Current Health: {}, Current Spirituality: {}, Active Effects: {}",
                player.getHealth(), BeyonderUtil.getSpirituality(player), player.getActiveEffects().size());

        LOTM.LOGGER.info("=== DEBUG: Completed restoreDataReboot ===");
    }

    @Override
    public @NotNull Rarity getRarity(@NotNull ItemStack pStack) {
        return Rarity.create("MONSTER_ABILITY", ChatFormatting.GRAY);
    }

    @Override
    public int getPriority(LivingEntity livingEntity, LivingEntity target) {
        CompoundTag tag = livingEntity.getPersistentData();
        if (!tag.contains("monsterRebootHealth") || tag.getFloat("monsterRebootHealth") == 0) {
            if (livingEntity.getHealth() >= livingEntity.getMaxHealth() - 1) {
                return 100;
            }
        } else if (livingEntity.getHealth() / livingEntity.getMaxHealth() < 0.3) {
            return 100;
        }
        return 0;
    }

    @Override
    public <T> LeftClickType getleftClickEmpty(T item) {
        return new UpdateItemInHandC2S((Integer) item, new ItemStack(ItemInit.CYCLEOFFATE.get()));
    }
}