package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;
import net.swimmingtuna.lotm.entity.LightningEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.networking.packet.UpdateItemInHandC2S;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.EntityUtil.BeamEntity;
import net.swimmingtuna.lotm.util.LeftClickHandler.LeftClickHandlerSkillP;
import net.swimmingtuna.lotm.util.LeftClickHandler.LeftClickType;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class MatterAccelerationEntities extends LeftClickHandlerSkillP {

    public MatterAccelerationEntities(Properties properties) {
        super(properties, BeyonderClassInit.SAILOR, 0, 800, 900);
    }

    @Override
    public InteractionResult useAbility(Level level, LivingEntity player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        matterAccelerationEntitiesSelf(player);
        addCooldown(player);
        useSpirituality(player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useAbilityOnBlock(UseOnContext pContext) {
        if (pContext.getPlayer() == null) {
            Entity entity = pContext.getItemInHand().getEntityRepresentation();
            if (entity instanceof LivingEntity user) {
                BlockPos targetPos = pContext.getClickedPos();
                if (!checkAll(user)) {
                    return InteractionResult.FAIL;
                }
                matterAccelerationEntitiesTarget(user, targetPos);
                return InteractionResult.SUCCESS;
            }
        } else {
            Player player = pContext.getPlayer();
            BlockPos targetPos = pContext.getClickedPos();

            if (!checkAll(player)) {
                return InteractionResult.FAIL;
            }
            matterAccelerationEntitiesTarget(player, targetPos);
            addCooldown(player);
            useSpirituality(player);

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useAbilityOnEntity(ItemStack stack, LivingEntity player, LivingEntity interactionTarget, InteractionHand hand) {
        if (!player.level().isClientSide()) {
            if (!checkAll(player)) {
                return InteractionResult.FAIL;
            }
            addCooldown(player);
            useSpirituality(player);
            matterAccelerationEntitiesTarget(player, BlockPos.containing(interactionTarget.position()));
        }
        return InteractionResult.SUCCESS;
    }

    public static void matterAccelerationEntitiesSelf(LivingEntity player) {
        if (!player.level().isClientSide()) {
            for (Entity entity : BeyonderUtil.getNonAllyEntitiesNearby(player, 250)) {
                if (BeyonderUtil.isBeyonder(player) || entity instanceof Monster) {
                    entity.getPersistentData().putInt("matterAccelerationEntitiesX", (int) player.getX());
                    entity.getPersistentData().putInt("matterAccelerationEntitiesY", (int) player.getY());
                    entity.getPersistentData().putInt("matterAccelerationEntitiesZ", (int) player.getZ());
                    entity.getPersistentData().putInt("matterAccelerationEntitiesTimer", (int) (float) BeyonderUtil.getDamage(player).get(ItemInit.MATTER_ACCELERATION_ENTITIES.get()));
                }
            }
        }
    }

    public static void matterAccelerationEntitiesTarget(LivingEntity player, BlockPos pos) {
        if (!player.level().isClientSide()) {
            for (Entity entity : BeyonderUtil.getNonAllyEntitiesNearby(player, 250)) {
                if (BeyonderUtil.isBeyonder(player) || entity instanceof Monster) {
                    entity.getPersistentData().putInt("matterAccelerationEntitiesX", pos.getX());
                    entity.getPersistentData().putInt("matterAccelerationEntitiesY", pos.getY());
                    entity.getPersistentData().putInt("matterAccelerationEntitiesZ", pos.getZ());
                    entity.getPersistentData().putInt("matterAccelerationEntitiesTimer", (int) (float) BeyonderUtil.getDamage(player).get(ItemInit.MATTER_ACCELERATION_ENTITIES.get()));
                }
                if (entity instanceof Projectile projectile) {
                    boolean x = projectile.getOwner() != null && projectile.getOwner() instanceof LivingEntity owner && !BeyonderUtil.areAllies(owner, player);
                    if (projectile.getOwner() == null) {
                        x = false;
                    }
                    if (x) {
                        entity.getPersistentData().putInt("matterAccelerationEntitiesX", (int) player.getX());
                        entity.getPersistentData().putInt("matterAccelerationEntitiesY", (int) player.getY());
                        entity.getPersistentData().putInt("matterAccelerationEntitiesZ", (int) player.getZ());
                        entity.getPersistentData().putInt("matterAccelerationEntitiesTimer", (int) (float) BeyonderUtil.getDamage(player).get(ItemInit.MATTER_ACCELERATION_ENTITIES.get()));
                        double projX = player.getX() - entity.getX();
                        double projY = player.getY() - entity.getY();
                        double projZ = player.getZ() - entity.getZ();
                        entity.setDeltaMovement(projX, projY, projZ);
                    }
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
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 150, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 150, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, accelerates the speed of all entities to the clicked block or entity, or if none are selected, your own position. ONLY if a block or entity is clicked, projectiles are included. Entities that collide together here hurt each other. In addition, projectiles are unable to approach you, as you accelerate them in another direction"));
        tooltipComponents.add(Component.literal("Left Click for Matter Acceleration (Self)"));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("800").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("45 Seconds").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
    @Override
    public Rarity getRarity(ItemStack pStack) {
        return Rarity.create("SAILOR_ABILITY", ChatFormatting.BLUE);
    }

    @Override
    public int getPriority(LivingEntity livingEntity, LivingEntity target) {
        if (target != null) {
            return 10;
        }
        return 0;
    }

    @Override
    public <T> LeftClickType getleftClickEmpty(T item) {
        return new UpdateItemInHandC2S((Integer) item, new ItemStack(ItemInit.MATTER_ACCELERATION_SELF.get()));
    }
}
