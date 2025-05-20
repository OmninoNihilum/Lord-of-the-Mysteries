package net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class TrickTelekenisis extends SimpleAbilityItem {


    public TrickTelekenisis(Properties properties) {
        super(properties, BeyonderClassInit.APPRENTICE, 8, 50, 200);
    }

    @Override
    public InteractionResult useAbility(Level level, LivingEntity player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        useSpirituality(player);
        addCooldown(player);
        enableDisableTelekenesis(player);
        return InteractionResult.SUCCESS;
    }

    public static void enableDisableTelekenesis(LivingEntity player) {
        if (!player.level().isClientSide()) {
            CompoundTag tag = player.getPersistentData();
            boolean telekenisis = tag.getBoolean("trickmasterTelekenisis");
            tag.putBoolean("trickmasterTelekenisis", !telekenisis);
            if (player instanceof Player pPlayer) {
                pPlayer.displayClientMessage(Component.literal("Telekenisis Turned " + (telekenisis ? "Off" : "On")).withStyle(ChatFormatting.BOLD, ChatFormatting.GRAY), true);
            }
        }
    }


    public static void trickMasterTelekenisisPassive(LivingEvent.LivingTickEvent event) {
        LivingEntity livingEntity = event.getEntity();
        CompoundTag tag = livingEntity.getPersistentData();
        if (!livingEntity.level().isClientSide() && tag.getBoolean("trickmasterTelekenisis") && livingEntity.tickCount % 5 == 0) {
            if (BeyonderUtil.getSpirituality(livingEntity) >= 10) {
                for (Entity entity : livingEntity.level().getEntitiesOfClass(Entity.class, livingEntity.getBoundingBox().inflate(BeyonderUtil.getDamage(livingEntity).get(ItemInit.TRICKTELEKENISIS.get())))) {
                    if (entity != livingEntity && (entity instanceof Projectile projectile && projectile.getOwner() != null && projectile.getOwner() instanceof LivingEntity livingOwner && !BeyonderUtil.areAllies(livingOwner, livingEntity)) || (entity instanceof LivingEntity living && !BeyonderUtil.areAllies(livingEntity, living))) {
                        double x = entity.getX() - livingEntity.getX();
                        double y = entity.getY() - livingEntity.getY();
                        double z = entity.getZ() - livingEntity.getZ();
                        double magnitude = Math.sqrt(x * x + y * y + z * z);
                        if (entity != livingEntity) {
                            entity.setDeltaMovement(x / magnitude * 4, y / magnitude * 4, z / magnitude * 4);
                        }
                        entity.hurtMarked = true;
                        float amount;
                        if (entity instanceof LivingEntity living) {
                            amount = 10 - BeyonderUtil.getSequence(living);
                        } else if (entity instanceof Projectile projectile) {
                            amount = (int) ((BeyonderUtil.getScale(entity) * 3) + (Math.abs(projectile.getDeltaMovement().y() + projectile.getDeltaMovement().x() + projectile.getDeltaMovement().z())));
                        } else {
                            amount = 0;
                        }
                        BeyonderUtil.useSpirituality(livingEntity, (int) amount);
                    }
                }
            } else {
                if (livingEntity instanceof Player pPlayer) {
                    pPlayer.displayClientMessage(Component.literal("Telekenisis turned off due to lack of spirituality").withStyle(ChatFormatting.BOLD, ChatFormatting.RED), true);
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

        //reach should be___
        attributeBuilder.putAll(super.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND));
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 12, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 12, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal(""));
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