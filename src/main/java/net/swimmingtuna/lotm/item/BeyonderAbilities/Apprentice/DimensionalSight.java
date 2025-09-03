package net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.blocks.DimensionalSight.DimensionalSightTileEntity;
import net.swimmingtuna.lotm.entity.DimensionalSightSealEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.BlockInit;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class


DimensionalSight extends SimpleAbilityItem {

    public DimensionalSight(Properties properties) {
        super(properties, BeyonderClassInit.APPRENTICE, 2, 1000, 3600,500,500);
    }

    @Override
    public InteractionResult useAbilityOnEntity(ItemStack pStack, LivingEntity player, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        if (!player.level().isClientSide()) {
            if (!checkAll(player)) {
                return InteractionResult.FAIL;
            }
            addCooldown(player);
            useSpirituality(player);
            dimensionalSight(player, pInteractionTarget);
        }
        return InteractionResult.SUCCESS;
    }


    public static void dimensionalSight(LivingEntity livingEntity, LivingEntity interactionTarget) {
        dimensionalSightDelayed(livingEntity, interactionTarget, 2);
        if (livingEntity instanceof Player player && player.isCreative()) {
            player.getCooldowns().addCooldown(ItemInit.DIMENSIONAL_SIGHT.get(), 10);
        }
    }

    private static void dimensionalSightDelayed(LivingEntity livingEntity, LivingEntity interactionTarget, int ticksToWait) {
        Level level = livingEntity.level();
        if (!level.isClientSide()) {
            MinecraftServer server = level.getServer();
            if (server != null) {
                if (ticksToWait <= 0) {
                    server.execute(() -> {
                        BlockPos playerPos = livingEntity.blockPosition();
                        Vec3 lookPos = livingEntity.getLookAngle().scale(5);
                        BlockPos targetPos = new BlockPos(playerPos.offset((int) lookPos.x(), -2, (int) lookPos.z()));
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
                            if (interactionTarget instanceof Player player) {
                                player.getPersistentData().putUUID("dimensionalSightPlayerUUID", livingEntity.getUUID());
                                player.getPersistentData().putInt("ignoreShouldntRender", 10);
                            }
                        }
                    });
                } else {
                    server.execute(() -> dimensionalSightDelayed(livingEntity, interactionTarget, ticksToWait - 1));
                }
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
        tooltipComponents.add(Component.literal("Use this ability on a dimensional sight, regardless of cooldown, to trap the target in a seal that you can carry around with you. You can punch this seal twice in order to break the seal and teleport all trapped entities to it."));
        tooltipComponents.add(Component.literal("You can use MOST Door pathway abilities while near and looking at a dimensional sight in order to have your abilities be cast at it's location."));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("1000").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("3").withStyle(ChatFormatting.YELLOW)));
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
        return Rarity.create("APPRENTICE_ABILITY", ChatFormatting.AQUA);
    }

    @Override
    public int getPriority(LivingEntity livingEntity, LivingEntity target) {
        return 0;
    }
}