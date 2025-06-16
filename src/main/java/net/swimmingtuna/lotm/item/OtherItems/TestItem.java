package net.swimmingtuna.lotm.item.OtherItems;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;
import net.swimmingtuna.lotm.blocks.DimensionalSight.DimensionalSightTileEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.BlockInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.networking.packet.SpiritWorldSyncPacket;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import net.swimmingtuna.lotm.util.SpiritWorld.SpiritWorldHandler;

import java.util.List;

public class TestItem extends SimpleAbilityItem {


    private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = Lazy.of(this::createAttributeMap);

    public TestItem(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 3, 600, 40);
    }


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

        //reach should be___
        attributeBuilder.putAll(super.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND));
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 200, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 200, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }

    @Override
    public InteractionResult useAbilityOnBlock(UseOnContext pContext) {
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useAbilityOnEntity(ItemStack stack, LivingEntity player, LivingEntity interactionTarget, InteractionHand hand) {
        if (!player.level().isClientSide()) {
            LOTMNetworkHandler.sendToAllPlayers(new SpiritWorldSyncPacket(SpiritWorldHandler.getEntityVisibilityMap()));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useAbility(Level level, LivingEntity player, InteractionHand hand) {
        if (!level.isClientSide()) {
            if (player instanceof Player pPlayer) {
                // Clear cooldowns (existing functionality)
                for (int i = 0; i < pPlayer.getInventory().getContainerSize(); i++) {
                    ItemStack stack = pPlayer.getInventory().getItem(i);
                    if (!stack.isEmpty()) {
                        pPlayer.getCooldowns().removeCooldown(stack.getItem());
                    }
                }

                // Find a suitable location to place the Dimensional Sight block
                BlockPos playerPos = pPlayer.blockPosition();
                BlockPos targetPos = findSuitableBlockPos(level, playerPos);

                if (targetPos != null) {
                    // Place the Dimensional Sight block
                    BlockState dimensionalSightState = BlockInit.DIMENSIONAL_SIGHT.get().defaultBlockState();
                    level.setBlock(targetPos, dimensionalSightState, 3);

                    // Get the tile entity and configure it
                    BlockEntity blockEntity = level.getBlockEntity(targetPos);
                    if (blockEntity instanceof DimensionalSightTileEntity sightEntity) {
                        sightEntity.setCaster(pPlayer);
                        LivingEntity nearestEntity = findNearestEntity(level, pPlayer);

                        if (nearestEntity != null) {
                            sightEntity.viewTarget = nearestEntity.getName().getString();
                            sightEntity.scryUniqueID = nearestEntity.getUUID();
                            sightEntity.setChanged();
                            sightEntity.sendUpdates();
                            System.out.println("  - TEST ITEM TARGET IS: " + nearestEntity.getName().getString());
                        }
                    }
                } else {
                    pPlayer.sendSystemMessage(Component.literal("Could not find a suitable location to place Dimensional Sight."));
                }
            }
        }

        return InteractionResult.SUCCESS;
    }

    private BlockPos findSuitableBlockPos(Level level, BlockPos playerPos) {
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
    private LivingEntity findNearestEntity(Level level, Player player) {
        double searchRadius = 32.0;
        AABB searchArea = new AABB(player.getX() - searchRadius, player.getY() - searchRadius, player.getZ() - searchRadius, player.getX() + searchRadius, player.getY() + searchRadius, player.getZ() + searchRadius);
        List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, searchArea);
        LivingEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (LivingEntity entity : nearbyEntities) {
            if (entity.equals(player)) {
                continue;
            }
            if (!entity.isAlive()) {
                continue;
            }

            double distance = player.distanceToSqr(entity);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = entity;
            }
        }
        if (nearest != null) {
            System.out.println("[DimensionalSight] Selected nearest entity: " + nearest.getName().getString() +
                    " at distance: " + Math.sqrt(nearestDistance));
        }
        return nearest;
    }
}
