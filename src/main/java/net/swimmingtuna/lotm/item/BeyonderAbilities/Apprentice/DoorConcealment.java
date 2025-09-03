package net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.entity.living.LivingEvent;
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

public class DoorConcealment extends LeftClickHandlerSkillP {


    public DoorConcealment(Properties properties) {
        super(properties, BeyonderClassInit.APPRENTICE, 0, 1000, 1200);
    }

    @Override
    public InteractionResult useAbility(Level level, LivingEntity player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        useSpirituality(player);
        addCooldown(player);
        conceal(player);
        return InteractionResult.SUCCESS;
    }

    //ESQUIZO
    public static void conceal(LivingEntity player) {
        if (!player.level().isClientSide()) {
            CompoundTag tag = player.getPersistentData();
            int damage = (int) (float) BeyonderUtil.getDamage(player).get(ItemInit.DOOR_CONCEALMENT.get());
            tag.putInt("doorConcealment", damage);
        }
    }

    public static void concealmentTick(LivingEvent.LivingTickEvent event) {
        LivingEntity player = event.getEntity();
        CompoundTag tag = player.getPersistentData();
        int concealment = tag.getInt("doorConcealment");
        if (concealment >= 1) {
            tag.putInt("doorConcealment", concealment - 1);
            if (concealment % 1200 == 0) {
                int minutesLeft = concealment / 1200;
                player.sendSystemMessage(Component.literal("You are concealed for another " + minutesLeft + " minute" + (minutesLeft == 1 ? "" : "s")).withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.BOLD));
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
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 50, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 50, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Use on an entity or yourself in order to conceal them from divination and abilities that are targeted via chat for an hour."));
        tooltipComponents.add(Component.literal("Left click for Concealment Authority: Secret Keeping."));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("1000").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("1 Minute").withStyle(ChatFormatting.YELLOW)));
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
        CompoundTag tag = livingEntity.getPersistentData();
        int concealment = tag.getInt("doorConcealment");
        if (target == null && concealment == 0) {
            return 50;
        }
        return 0;
    }

    @Override
    public <T> LeftClickType getleftClickEmpty(T item) {
        return new UpdateItemInHandC2S((Integer) item, new ItemStack(ItemInit.SECRET_KEEPING.get()));
    }
}