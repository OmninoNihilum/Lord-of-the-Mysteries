package net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class CreateConcealedBundle extends SimpleAbilityItem {
    public CreateConcealedBundle(Properties properties) {
        super(properties, BeyonderClassInit.APPRENTICE, 4, 0, 0);
    }

    @Override
    public InteractionResult useAbility(Level level, LivingEntity livingEntity, InteractionHand hand){
        if(!checkAll(livingEntity) && checkBundle(livingEntity)){
            return InteractionResult.FAIL;
        }
        createBundle(livingEntity);
        addCooldown(livingEntity);
        useSpirituality(livingEntity);
        return InteractionResult.SUCCESS;
    }

    public static void createBundle(LivingEntity entity){
        if(entity.level().isClientSide()) return;
        if(checkBundle(entity)){
            ItemStack stack = new ItemStack(ItemInit.CONCEALED_BUNDLE.get());
            entity.setItemInHand(InteractionHand.OFF_HAND, stack);
            stack.getOrCreateTag().putInt("concealedBundleRows",  9 - BeyonderUtil.getSequence(entity));
            if(BeyonderUtil.getSequence(entity) > 4) stack.getOrCreateTag().putInt("concealedBundleMaxDurability",  (9 - BeyonderUtil.getSequence(entity)) * 5);
            else stack.getOrCreateTag().putInt("concealedBundleMaxDurability",  (9 - BeyonderUtil.getSequence(entity) - 4) * 50);
            if(BeyonderUtil.getSequence(entity) == 0) stack.getOrCreateTag().putBoolean("concealedBundleUnbreakable",  true);
        }
    }

    public static boolean checkBundle(LivingEntity entity){
        boolean isBundle = entity.getItemInHand(InteractionHand.OFF_HAND).getItem() == Items.BUNDLE;
        if(!isBundle && entity instanceof Player player) player.displayClientMessage(Component.literal("No bundle in you off-hand.").withStyle(BeyonderUtil.getStyle(player)), true);
        return isBundle;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, transform a bundle in your off-hand in a special mystical item, that contains a very big space."));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("50").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("10 Second").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
    @Override
    public Rarity getRarity(ItemStack pStack) {
        return Rarity.create("APPRENTICE_ABILITY", ChatFormatting.BLUE);
    }
}