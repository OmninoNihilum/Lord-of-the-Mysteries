package net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;
import net.swimmingtuna.lotm.blocks.DimensionalSight.DimensionalSightTileEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.BlockInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class DimensionalSight extends SimpleAbilityItem {

    public DimensionalSight(Properties properties) {
        super(properties, BeyonderClassInit.APPRENTICE, 2, 1000, 6000);
    }

    @Override
    public InteractionResult useAbilityOnEntity(ItemStack pStack, LivingEntity player, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        useSpirituality(player);
        dimensionalSight(player, pInteractionTarget);
        return InteractionResult.SUCCESS;
    }

    public static void dimensionalSight(LivingEntity livingEntity, LivingEntity interactionTarget) {
        Level level = livingEntity.level();
        if (!level.isClientSide()) {
            BlockPos playerPos = livingEntity.blockPosition();
            BlockPos targetPos = findSuitableBlockPos(level, playerPos);
            if (targetPos != null) {
                BlockState dimensionalSightState = BlockInit.DIMENSIONAL_SIGHT.get().defaultBlockState();
                level.setBlock(targetPos, dimensionalSightState, 3);
                BlockEntity blockEntity = level.getBlockEntity(targetPos);
                if (blockEntity instanceof DimensionalSightTileEntity sightEntity) {
                    sightEntity.setCaster(livingEntity);
                    if (interactionTarget != null) {
                        sightEntity.viewTarget = interactionTarget.getName().getString();
                        sightEntity.scryUniqueID = interactionTarget.getUUID();
                        sightEntity.setCaster(livingEntity);
                        sightEntity.setChanged();
                        sightEntity.sendUpdates();
                    }
                }
            } else {
                SimpleAbilityItem.removeCooldown(livingEntity, ItemInit.DIMENSIONAL_SIGHT.get(), 0);
                livingEntity.sendSystemMessage(Component.literal("Could not find a suitable location to place Dimensional Sight.").withStyle(ChatFormatting.RED));
            }
        }
    }

    public static BlockPos findSuitableBlockPos(Level level, BlockPos playerPos) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos checkPos = playerPos.offset(x, -1, z);
                if (level.getBlockState(checkPos).isAir() && !level.getBlockState(checkPos.below()).isAir()) {
                    return checkPos;
                }
            }
        }
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0) continue;
                BlockPos checkPos = playerPos.offset(x, 0, z);
                if (level.getBlockState(checkPos).isAir()) {
                    return checkPos;
                }
            }
        }
        return null;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use on an entity, mark it with a dimensional sight in front of you, able to see all their surrounding blocks and themselves. You can also type the name of a player into chat to view them from anywhere."));
        tooltipComponents.add(Component.literal("If used while sneaking, will create in the off hand a special door that leads to the users Concealed Space."));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("1000").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("5 Minutes").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
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
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 500, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 500, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }

    @Override
    public Rarity getRarity(ItemStack pStack) {
        return Rarity.create("APPRENTICE_ABILITY", ChatFormatting.BLUE);
    }

    @Override
    public int getPriority(LivingEntity livingEntity, LivingEntity target) {
        return super.getPriority(livingEntity, target);
    }
}