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
        CompoundTag backupTag = new CompoundTag();
        backupTag.merge(tag);
        tag.put("monsterRebootAllData", backupTag);
        Collection<MobEffectInstance> activeEffects = player.getActiveEffects();
        tag.putInt("monsterRebootPotionEffectsCount", activeEffects.size());
        int i = 0;
        for (MobEffectInstance effect : activeEffects) {
            CompoundTag effectTag = new CompoundTag();
            effect.save(effectTag);
            tag.put("monsterRebootPotionEffect_" + i, effectTag);
            i++;
        }
        tag.putFloat("monsterRebootHealth", player.getHealth());
        tag.putInt("monsterRebootSpirituality", BeyonderUtil.getSpirituality(player));
        List<Item> beyonderAbilities = BeyonderUtil.getAbilities(player);
        if (player instanceof Player pPlayer) {
            for (Item item : beyonderAbilities) {
                if (item != ItemInit.REBOOTSELF.get()) {
                    String itemCooldowns = item.getDescription().toString();
                    tag.putFloat("monsterRebootCooldown" + itemCooldowns, pPlayer.getCooldowns().getCooldownPercent(item, 0));
                }
            }
        }
    }

    private static void restoreDataReboot(LivingEntity player, CompoundTag tag) {
        if (!tag.contains("monsterRebootAllData")) {
            if (player instanceof Player pPlayer) {
                pPlayer.displayClientMessage(Component.literal("No saved state found!").withStyle(ChatFormatting.RED), true);
            }
            return;
        }
        CompoundTag backupTag = tag.getCompound("monsterRebootAllData");
        Collection<MobEffectInstance> currentEffects = new ArrayList<>(player.getActiveEffects());
        for (MobEffectInstance activeEffect : currentEffects) {
            player.removeEffect(activeEffect.getEffect());
        }
        CompoundTag rebootBackup = tag.getCompound("monsterRebootAllData").copy();
        tag.getAllKeys().clear();
        tag.merge(backupTag);
        tag.put("monsterRebootAllData", rebootBackup);
        int effectCount = tag.getInt("monsterRebootPotionEffectsCount");
        for (int i = 0; i < effectCount; i++) {
            CompoundTag effectTag = tag.getCompound("monsterRebootPotionEffect_" + i);
            MobEffectInstance effect = MobEffectInstance.load(effectTag);
            if (effect != null) {
                player.addEffect(effect);
            }
        }
        float savedHealth = tag.getFloat("monsterRebootHealth");
        int savedSpirituality = tag.getInt("monsterRebootSpirituality");
        player.setHealth(Math.max(1, savedHealth));
        BeyonderUtil.setSpirituality(player, savedSpirituality);
        List<Item> beyonderAbilities = BeyonderUtil.getAbilities(player);
        for (Item item : beyonderAbilities) {
            if (player instanceof Player pPlayer) {
                if (item instanceof SimpleAbilityItem simpleAbilityItem) {
                    String itemCooldowns = item.getDescription().toString();
                    float savedCooldownPercent = tag.getFloat("monsterRebootCooldown" + itemCooldowns);
                    int remainingCooldownTicks = (int) (simpleAbilityItem.getCooldown() * savedCooldownPercent);
                    pPlayer.getCooldowns().addCooldown(item, remainingCooldownTicks);
                }
            }
        }
    }

    @Override
    public Rarity getRarity(ItemStack pStack) {
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