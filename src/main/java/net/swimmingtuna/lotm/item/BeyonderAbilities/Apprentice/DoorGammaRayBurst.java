package net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.blocks.DimensionalSight.DimensionalSightTileEntity;
import net.swimmingtuna.lotm.entity.ApprenticeDoorEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.EntityInit;
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

public class DoorGammaRayBurst extends LeftClickHandlerSkillP {

    public DoorGammaRayBurst(Properties properties) {
        super(properties, BeyonderClassInit.APPRENTICE, 0, 10000, 1200, 100, 100);
    }

    @Override
    public InteractionResult useAbility(Level level, LivingEntity player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        useSpirituality(player);
        addCooldown(player);
        gammaRayBurst(player);
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
            gammaRayBurst(player, interactionTarget);
        }
        return InteractionResult.SUCCESS;
    }


    public static void gammaRayBurst(LivingEntity livingEntity) {
        Level level = livingEntity.level();
        if (!level.isClientSide()) {
            int gammaRayDistance = livingEntity.getPersistentData().getInt("doorGammaRay");
            DimensionalSightTileEntity dimensionalSightTileEntity = BeyonderUtil.findNearbyDimensionalSight(livingEntity);
            if (dimensionalSightTileEntity != null && dimensionalSightTileEntity.getScryTarget() != null) {
                Vec3 scryTargetPos = dimensionalSightTileEntity.getScryTarget().getOnPos().getCenter();
                BlockPos pos = new BlockPos((int) scryTargetPos.x(), (int) scryTargetPos.y(), (int) scryTargetPos.z());
                Vec3 targetPos = Vec3.atCenterOf(pos);

                int damage = (int) (float) BeyonderUtil.getDamage(livingEntity).get(ItemInit.DOOR_GAMMA_RAY_BURST.get());
                double sphereRadius = 120.0;
                for (int i = 0; i < damage; i++) {
                    double theta = Math.random() * 2 * Math.PI;
                    double phi = Math.acos(1 - 2 * Math.random());
                    double x = sphereRadius * Math.sin(phi) * Math.cos(theta);
                    double y = sphereRadius * Math.cos(phi);
                    double z = sphereRadius * Math.sin(phi) * Math.sin(theta);
                    Vec3 spawnPos = targetPos.add(x, y, z);
                    Vec3 directionToCenter = targetPos.subtract(spawnPos).normalize();
                    float yaw = (float) (Math.atan2(-directionToCenter.x(), directionToCenter.z()) * 180.0 / Math.PI);
                    double horizontalDistance = Math.sqrt(directionToCenter.x() * directionToCenter.x() + directionToCenter.z() * directionToCenter.z());
                    float pitch = (float) (Math.atan2(-directionToCenter.y(), horizontalDistance) * 180.0 / Math.PI);
                    ApprenticeDoorEntity gammaRayDoor = new ApprenticeDoorEntity(livingEntity.level(), livingEntity, 340, 0, 0);
                    gammaRayDoor.getPersistentData().putInt("gammaRayTargetX", pos.getX());
                    gammaRayDoor.getPersistentData().putInt("gammaRayTargetY", pos.getY());
                    gammaRayDoor.getPersistentData().putInt("gammaRayTargetZ", pos.getZ());
                    BeyonderUtil.setScale(gammaRayDoor, 4);
                    gammaRayDoor.teleportTo(spawnPos.x, spawnPos.y, spawnPos.z);
                    gammaRayDoor.setPitch(pitch);
                    gammaRayDoor.setYaw(yaw);
                    livingEntity.level().addFreshEntity(gammaRayDoor);
                }
            } else {
                if (livingEntity instanceof Mob mob && mob.getTarget() != null) {
                    gammaRayDistance = (int) mob.distanceTo(mob.getTarget());
                }
                Vec3 scale = livingEntity.getLookAngle().scale(gammaRayDistance);
                Vec3 playerPos = livingEntity.position();
                BlockPos pos = new BlockPos((int) (playerPos.x + scale.x()), (int) (playerPos.y + scale.y()), (int) (playerPos.z + scale.z()));
                Vec3 targetPos = Vec3.atCenterOf(pos);

                int damage = (int) (float) BeyonderUtil.getDamage(livingEntity).get(ItemInit.DOOR_GAMMA_RAY_BURST.get());
                double sphereRadius = 120.0;
                for (int i = 0; i < damage; i++) {
                    double theta = Math.random() * 2 * Math.PI;
                    double phi = Math.acos(1 - 2 * Math.random());
                    double x = sphereRadius * Math.sin(phi) * Math.cos(theta);
                    double y = sphereRadius * Math.cos(phi);
                    double z = sphereRadius * Math.sin(phi) * Math.sin(theta);
                    Vec3 spawnPos = targetPos.add(x, y, z);
                    Vec3 directionToCenter = targetPos.subtract(spawnPos).normalize();
                    float yaw = (float) (Math.atan2(-directionToCenter.x(), directionToCenter.z()) * 180.0 / Math.PI);
                    double horizontalDistance = Math.sqrt(directionToCenter.x() * directionToCenter.x() + directionToCenter.z() * directionToCenter.z());
                    float pitch = (float) (Math.atan2(-directionToCenter.y(), horizontalDistance) * 180.0 / Math.PI);
                    ApprenticeDoorEntity gammaRayDoor = new ApprenticeDoorEntity(livingEntity.level(), livingEntity, 340, 0, 0);
                    gammaRayDoor.getPersistentData().putInt("gammaRayTargetX", pos.getX());
                    gammaRayDoor.getPersistentData().putInt("gammaRayTargetY", pos.getY());
                    gammaRayDoor.getPersistentData().putInt("gammaRayTargetZ", pos.getZ());
                    BeyonderUtil.setScale(gammaRayDoor, 4);
                    gammaRayDoor.teleportTo(spawnPos.x, spawnPos.y, spawnPos.z);
                    gammaRayDoor.setPitch(pitch);
                    gammaRayDoor.setYaw(yaw);
                    livingEntity.level().addFreshEntity(gammaRayDoor);
                }
            }
        }
    }

    public static void gammaRayBurst(LivingEntity livingEntity, LivingEntity living) {
        Level level = livingEntity.level();
        if (!level.isClientSide()) {

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
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 100, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 100, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, choose a target spot. Then, doors will be summoned all around the target spot which will lead to gamma ray bursts across the universe, sending them all around the spot and causing them to shoot a gamma ray at it."));
        tooltipComponents.add(Component.literal("Entities hit with gamma ray will be unable to regenerate for time depending on how long they're hit as well as take damage."));
        tooltipComponents.add(Component.literal("Shift to increase distance that the target spot is."));
        tooltipComponents.add(Component.literal("Left click for Door Authority: Seal Strengthening."));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("10000").withStyle(ChatFormatting.YELLOW)));
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
        if (target != null) {
            return 75;
        }
        return 0;
    }

    @Override
    public <T> LeftClickType getleftClickEmpty(T item) {
        return new UpdateItemInHandC2S((Integer) item, new ItemStack(ItemInit.DOOR_SEAL_STRENGTHENING.get()));
    }
}