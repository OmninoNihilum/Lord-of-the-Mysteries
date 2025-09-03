package net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice;


import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;
import net.swimmingtuna.lotm.capabilities.replicated_entity.ReplicatedEntityUtils;
import net.swimmingtuna.lotm.entity.PlayerMobEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import net.swimmingtuna.lotm.util.Replicating.ReplicatedEntityMenu;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class Replicate extends SimpleAbilityItem {

    public Replicate(Properties properties) {
        super(properties, BeyonderClassInit.APPRENTICE, 2, 50, 300,100,100);
    }


    @Override
    public InteractionResult useAbility(Level level, LivingEntity player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        openMenu(player);
        return InteractionResult.SUCCESS;
    }


    @Override
    public InteractionResult useAbilityOnEntity(ItemStack stack, LivingEntity livingEntity, LivingEntity interactionTarget, InteractionHand hand) {
        if (!livingEntity.level().isClientSide && !interactionTarget.level().isClientSide) {
            if (!checkAll(livingEntity)) {
                return InteractionResult.FAIL;
            }
            if (livingEntity instanceof Player) {
                replicatePlayer(livingEntity, interactionTarget);
            } else if (livingEntity instanceof Mob mob) {
                if (mob.getTarget() != null) {
                    int sequence = BeyonderUtil.getSequence(livingEntity);
                    int targetSequence = BeyonderUtil.getSequence(mob.getTarget());
                    int chance = 100;
                    if (sequence == targetSequence) {
                        chance = 70;
                    } else if (sequence < targetSequence) {
                        chance = 30;
                    }
                    if (chance > BeyonderUtil.getPositiveRandomInRange(100)) {
                        PlayerMobEntity playerMobEntity = PlayerMobEntity.playerCopy(mob.getTarget());
                        playerMobEntity.setCreator(livingEntity);
                        playerMobEntity.setCreator(livingEntity.getUUID());
                        playerMobEntity.setTarget(mob.getTarget());
                    }
                }
            }
        }
        return InteractionResult.SUCCESS;

    }

    public void replicatePlayer(LivingEntity userEntity, LivingEntity targetEntity) {
        if (userEntity instanceof Player player && targetEntity instanceof Player target) {
            ReplicatedEntityUtils.addReplicatedEntities(player, target);
        }
    }

    public static void openMenu(LivingEntity living) {
        if (living instanceof Player player) {
            player.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    int max = ReplicatedEntityUtils.getMaxEntities(player);
                    int amount = ReplicatedEntityUtils.getEntities(player).size();
                    return Component.literal(amount + "/" + max).withStyle(ChatFormatting.BOLD);
                }

                @Override
                public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
                    return new ReplicatedEntityMenu(containerId, playerInventory, ReplicatedEntityUtils.getEntities(player));
                }
            });
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Use this ability on an entity in order to save them to a menu of entities you can replicate. Use this on the air to open up the menu, allowing you to click on an entity to replicate in front of you.").withStyle(ChatFormatting.RED));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("0 to copy,").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("15 Seconds").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    @Override
    public Rarity getRarity(ItemStack pStack) {
        return Rarity.create("APPRENTICE_ABILITY", ChatFormatting.AQUA);
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
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 100, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 100, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }
    @Override
    public int getPriority(LivingEntity livingEntity, LivingEntity target) {
        if (target != null) {
            int sequence = BeyonderUtil.getSequence(livingEntity);
            int targetSequence = BeyonderUtil.getSequence(target);
            if (sequence > targetSequence) {
                return 30;
            } else if (sequence == targetSequence) {
                return 60;
            } else {
                return 90;
            }
        }
        return 0;
    }

}