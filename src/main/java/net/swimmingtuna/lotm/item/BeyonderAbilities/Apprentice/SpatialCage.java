package net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;
import net.swimmingtuna.lotm.blocks.DimensionalSight.DimensionalSightTileEntity;
import net.swimmingtuna.lotm.entity.SpatialCageEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.networking.packet.UpdateItemInHandC2S;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.LeftClickHandler.LeftClickHandlerSkillP;
import net.swimmingtuna.lotm.util.LeftClickHandler.LeftClickType;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;


public class SpatialCage extends LeftClickHandlerSkillP {
    public SpatialCage(Properties properties) {
        super(properties, BeyonderClassInit.APPRENTICE, 3, 800, 1000,50,50);
    }

    @Override
    public InteractionResult useAbilityOnEntity(ItemStack stack, LivingEntity livingEntity, LivingEntity interactionTarget, InteractionHand hand) {
        if (!livingEntity.level().isClientSide && !interactionTarget.level().isClientSide) {
            if (!checkAll(livingEntity)) {
                return InteractionResult.FAIL;
            }
            if (interactionTarget.getPersistentData().contains("spatialCageSealUUID")){
                if(livingEntity instanceof Player player){
                    player.displayClientMessage(Component.literal("Target is already in a Spatial Cage").withStyle(ChatFormatting.RED), true);
                }
                return InteractionResult.FAIL;
            }
            useSpirituality(livingEntity);
            SpatialCageEntity.setSealed(interactionTarget, livingEntity, BeyonderUtil.getSequence(livingEntity) - 1, (int) (float) BeyonderUtil.getDamage(livingEntity).get(ItemInit.SPATIAL_CAGE.get()));
            addCooldown(livingEntity);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useAbility(Level level, LivingEntity player, InteractionHand hand) {
        if (!checkAll(player, BeyonderClassInit.APPRENTICE.get(), 0, 5000,true)) {
            if (player instanceof Player pPlayer) {
                pPlayer.sendSystemMessage(Component.literal("You need to be the highest sequence in order to use this ability not on a target.").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
            }
            return InteractionResult.FAIL;
        }
        useSpirituality(player, 5000);
        addCooldown(player, this, 2000);
        spatialCage(player);
        return InteractionResult.SUCCESS;
    }

    public void spatialCage(LivingEntity living) {
        float damage = BeyonderUtil.getDamage(living).get(ItemInit.SPATIAL_CAGE.get());
        for (LivingEntity livingEntity : BeyonderUtil.getNonAlliesNearby(living, damage)) {
            SpatialCageEntity.setSealed(livingEntity, living, BeyonderUtil.getSequence(livingEntity) - 1, (int) (float) BeyonderUtil.getDamage(livingEntity).get(ItemInit.SPATIAL_CAGE.get()));
        }
    }


    private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = Lazy.of(this::createAttributeMap);

    @SuppressWarnings("deprecation")
    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            return this.lazyAttributeMap.get();
        }
        return super.getDefaultAttributeModifiers(slot);
    }

    private Multimap<Attribute, AttributeModifier> createAttributeMap() {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> attributeBuilder = ImmutableMultimap.builder();
        attributeBuilder.putAll(super.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND));
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 50, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 50, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.literal("Upon use, creates a powerful seal that can affect beings a full sequence above you, sealing them in a cage for a long duration. At Sequence 0, use this ability"));
        tooltipComponents.add(Component.literal("Left click for Spatial Authority: Tearing."));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("800").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("50 Seconds").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, isAdvanced);
    }

    @Override
    public Rarity getRarity(ItemStack pStack) {
        return Rarity.create("APPRENTICE_ABILITY", ChatFormatting.AQUA);
    }

    @Override
    public int getPriority(LivingEntity livingEntity, LivingEntity target) {
        if (target != null) {
            return (int) (livingEntity.getHealth() / livingEntity.getMaxHealth());
        }
        return 0;
    }

    @Override
    public <T> LeftClickType getleftClickEmpty(T item) {
        return new UpdateItemInHandC2S((Integer) item, new ItemStack(ItemInit.SPATIAL_TEARING.get()));
    }
}