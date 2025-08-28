package net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice;


import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.networking.packet.SealStrengtheningLeftClickC2S;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.LeftClickHandler.LeftClickHandlerSkill;
import net.swimmingtuna.lotm.util.LeftClickHandler.LeftClickType;
import net.swimmingtuna.lotm.world.worlddata.CalamityEnhancementData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class DoorSealStrenghtening extends LeftClickHandlerSkill {

    public DoorSealStrenghtening(Properties properties) {
        super(properties, BeyonderClassInit.APPRENTICE, 0, 0, 20);
    }

    @Override
    public InteractionResult useAbility(Level level, LivingEntity player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        sealAmplification(player);
        useSpirituality(player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof Player player) {
            if (player.tickCount % 2 == 0 && !level.isClientSide()) {
                if (player.getMainHandItem().getItem() instanceof DoorSealStrenghtening) {
                    player.displayClientMessage(Component.literal("Current Seal Strenghtening Value will be: " + player.getPersistentData().getInt("sealStrengtheningItemValue")).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA), true);
                }
            }
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }

    private void sealAmplification(LivingEntity player) {
        int value = player.getPersistentData().getInt("sealStrengtheningItemValue");
        if (!player.level().isClientSide() && player.level() instanceof ServerLevel serverLevel) {
            BeyonderUtil.setSealStrengthen(player, value);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, increases the strength of all seals by the amount set, with the timer going down on each seal that amount slower."));
        tooltipComponents.add(Component.literal("Left Click to increase strenghtening"));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("0").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("1 Second").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
    @Override
    public Rarity getRarity(ItemStack pStack) {
        return Rarity.create("APPRENTICE_ABILITY", ChatFormatting.AQUA);
    }

    @Override
    public int getPriority(LivingEntity livingEntity, LivingEntity target) {
        if (!livingEntity.level().isClientSide() && livingEntity.level() instanceof ServerLevel serverLevel) {
            CalamityEnhancementData data = CalamityEnhancementData.getInstance(serverLevel);
            if (data.getCalamityEnhancement() < 2) {
                livingEntity.getPersistentData().putInt("sealStrengtheningItemValue", 2);
                return 50;
            }
        }
        return 0;
    }
    @Override
    public LeftClickType getleftClickEmpty() {
        return new SealStrengtheningLeftClickC2S();
    }
}
