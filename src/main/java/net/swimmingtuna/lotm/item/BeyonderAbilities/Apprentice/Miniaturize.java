package net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;
import net.swimmingtuna.lotm.blocks.DimensionalSight.DimensionalSightTileEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.item.OtherItems.Doll;
import net.swimmingtuna.lotm.item.OtherItems.DollStructure;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.networking.packet.CleanupDimensionalSightPacketS2C;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class Miniaturize extends SimpleAbilityItem {
    public Miniaturize(Properties properties) {
        super(properties, BeyonderClassInit.APPRENTICE, 2, 1500, 1200);
    }


    @Override
    public InteractionResult useAbility(Level level, LivingEntity player, InteractionHand hand) {
        DimensionalSightTileEntity dimensionalSightTileEntity = BeyonderUtil.findNearbyDimensionalSight(player);
        if (dimensionalSightTileEntity != null && dimensionalSightTileEntity.getScryTarget() != null) {
            if (!checkAll(player)) {
                return InteractionResult.FAIL;
            }
            if (!level.isClientSide()) {
                CleanupDimensionalSightPacketS2C packet = new CleanupDimensionalSightPacketS2C(dimensionalSightTileEntity.getBlockPos(), dimensionalSightTileEntity.getScryTarget().getId());
                LOTMNetworkHandler.sendToAllPlayers(packet);
            }
            miniaturize(player, dimensionalSightTileEntity.getScryTarget());
            if (dimensionalSightTileEntity.getScryTarget() != null) {
                if (level instanceof ClientLevel clientLevel) {
                    clientLevel.removeEntity(dimensionalSightTileEntity.getScryTarget().getId(), Entity.RemovalReason.DISCARDED);
                }
                dimensionalSightTileEntity.getScryTarget().remove(Entity.RemovalReason.DISCARDED);
            }
            int actualSequence = BeyonderUtil.getSequence(player);
            int sequence = BeyonderUtil.getSequence(player);
            if (actualSequence == 0) {
                sequence = 1;
            }
            int amount = (sequence / BeyonderUtil.getSequence(dimensionalSightTileEntity.getScryTarget()));
            if (actualSequence == 0 && BeyonderUtil.getSequence(dimensionalSightTileEntity.getScryTarget()) != 0) {
                amount /= 2;
            }
            addCooldown(player, this, 1200 * amount);
            useSpirituality(player, 1500 * amount);
            dimensionalSightTileEntity.removeThis();
        } else {

            if (!checkAll(player)) {
                return InteractionResult.FAIL;
            }
            CompoundTag tag = player.getItemInHand(hand).getOrCreateTag();
            if (!tag.contains("miniaturizeAreaRange")) {
                tag.putInt("miniaturizeAreaRange", 3);
            }
            addCooldown(player);
            useSpirituality(player);
            miniaturizeArea(player, tag.getInt("miniaturizeAreaRange"));

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useAbilityOnEntity(ItemStack stack, LivingEntity livingEntity, LivingEntity interactionTarget, InteractionHand hand) {
        if (!livingEntity.level().isClientSide && !interactionTarget.level().isClientSide) {
            if (!checkAll(livingEntity)) {
                return InteractionResult.FAIL;
            }
            miniaturize(livingEntity, interactionTarget);
            int actualSequence = BeyonderUtil.getSequence(livingEntity);
            int sequence = BeyonderUtil.getSequence(livingEntity);
            if (actualSequence == 0) {
                sequence = 1;
            }
            int amount = (sequence / BeyonderUtil.getSequence(interactionTarget));
            if (actualSequence == 0 && BeyonderUtil.getSequence(interactionTarget) != 0) {
                amount /= 2;
            }
            addCooldown(livingEntity, this, 1200 * amount);
            useSpirituality(livingEntity, 1500 * amount);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (selected && !level.isClientSide) {
            if (entity.isShiftKeyDown()) {
                CompoundTag tag = stack.getOrCreateTag();
                if (entity instanceof Player player) {
                    player.displayClientMessage(Component.literal("Range: " + tag.getInt("miniaturizeAreaRange")), true);
                }
                if (level.getGameTime() % 4 == 0 && entity.isShiftKeyDown()) {
                    if (!tag.contains("miniaturizeAreaRange")) {
                        tag.putInt("miniaturizeAreaRange", 3);
                    }
                    if (tag.getInt("miniaturizeAreaRange") < 10) {
                        tag.putInt("miniaturizeAreaRange", tag.getInt("miniaturizeAreaRange") + 1);
                    } else {
                        tag.putInt("miniaturizeAreaRange", 3);
                    }
                }
            }
        }
    }

    public void miniaturize(LivingEntity user, LivingEntity target) {
        boolean x = BeyonderUtil.getSequence(user) < BeyonderUtil.getSequence(target) - 1;

        if (target.getHealth() < target.getMaxHealth() / (10 * BeyonderUtil.getDamage(user).get(ItemInit.MINIATURIZE.get()))) {
            x = true;
        }
        if (x) {
            ItemStack doll = ((Doll) ItemInit.DOLL.get()).getDollFromEntity(user, target, true);
            if (user instanceof Player player) {
                boolean added = player.getInventory().add(doll);
                if (!added || !doll.isEmpty()) {
                    ItemEntity itemEntity = player.drop(doll, false);
                    if (itemEntity != null) {
                        itemEntity.setNoPickUpDelay();
                    }
                }
            }
        } else {
            if (user instanceof Player player) {
                player.displayClientMessage(Component.literal("The target has too much health to miniaturize them").withStyle(ChatFormatting.RED), true);
            }
        }
    }

    public void miniaturizeArea(LivingEntity user, int range) {
        ItemStack doll = DollStructure.createWithCapturedStructure(user, range);

        if (user instanceof Player player) {
            boolean added = player.getInventory().add(doll);

            if (!added || !doll.isEmpty()) {
                ItemEntity itemEntity = player.drop(doll, false);
                if (itemEntity != null) {
                    itemEntity.setNoPickUpDelay();
                }
            }
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
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 15, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 15, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use on an entity with low health, miniaturize them and get them as an item, causing you to be able to place them back down again at the state they were miniaturized in."));
        tooltipComponents.add(Component.literal("vv WORK IN PROGRESS vv").withStyle(ChatFormatting.RED));
        tooltipComponents.add(Component.literal("You can also use this while not looking at an entity to miniaturize the area around you into your inventory."));
        tooltipComponents.add(Component.literal("^^ WORK IN PROGRESS ^^").withStyle(ChatFormatting.RED));
        tooltipComponents.add(Component.literal("Shift to increase miniaturized area."));
        tooltipComponents.add(Component.literal("Cooldown and spirituality will vary depending on strength of target compared to yourself."));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("~1500").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("~1 Minute").withStyle(ChatFormatting.YELLOW)));
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
        return 0;
    }
}