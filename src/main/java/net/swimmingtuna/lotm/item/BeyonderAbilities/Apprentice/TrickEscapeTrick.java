package net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class TrickEscapeTrick extends SimpleAbilityItem {
    public TrickEscapeTrick(Properties properties) {
        super(properties, BeyonderClassInit.APPRENTICE, 8, 150, 200);
    }

    @Override
    public InteractionResult useAbility(Level level, LivingEntity livingEntity, InteractionHand hand){
        if(!checkAll(livingEntity)){
            return InteractionResult.FAIL;
        }
        escape(livingEntity);
        if(livingEntity instanceof Player player){
            if (!player.isShiftKeyDown()) {
                addCooldown(player);
                useSpirituality(player);
            }
        } else {
            addCooldown(livingEntity);
            useSpirituality(livingEntity);
        }
        return InteractionResult.SUCCESS;
    }

    public static void escape(LivingEntity entity){
        if(!entity.level().isClientSide()){
            int maxEscapes = (int) (float) BeyonderUtil.getDamage(entity).get(ItemInit.TRICKESCAPETRICK.get());
            CompoundTag tag = entity.getPersistentData();
            if(entity instanceof Player player) {
                if(player.isShiftKeyDown()) {
                    player.displayClientMessage(Component.literal("Escape Tricks prepared: ").withStyle(BeyonderUtil.getStyle(player)).append(Component.literal("" + tag.getInt("escapeTrickCount")).withStyle(ChatFormatting.WHITE)), true);
                    return;
                }
            }
            if(tag.getInt("escapeTrickCount") < maxEscapes) {
                tag.putInt("escapeTrickCount", tag.getInt("escapeTrickCount") + 1);
                if(entity instanceof Player player) {
                    player.displayClientMessage(Component.literal("Trick prepared!").withStyle(BeyonderUtil.getStyle(player)), true);
                }
            } else {
                if(entity instanceof Player player) {
                    player.displayClientMessage(Component.literal("Cant prepare anymore Escape Tricks.").withStyle(BeyonderUtil.getStyle(player)), true);
                }
            }
        }
    }

    public static boolean canTeleportSafeSpace(LivingEntity entity){
        Random random = new Random();
        int range = (int) (float) BeyonderUtil.getDamage(entity).get(ItemInit.TRICKESCAPETRICK.get()) * 10;
        int maxAttempts = range * 2;
        for (int i = 0; i < maxAttempts; i++) {
            int xOffSet = random.nextInt((int) range * 2) - (int) range;
            int yOffSet = random.nextInt((int) range * 2) - (int) range;
            int zOffSet = random.nextInt((int) range * 2) - (int) range;
            double distanceSq = xOffSet*xOffSet + yOffSet*yOffSet + zOffSet*zOffSet;
            if (distanceSq <= range*range) {
                BlockPos pos = entity.blockPosition().offset(xOffSet, yOffSet, zOffSet);
                if (entity.level().isEmptyBlock(pos) && entity.level().isEmptyBlock(pos.above()) && !entity.level().containsAnyLiquid(new AABB(pos, pos.above())) && entity.level().getBlockState(pos.below()).isFaceSturdy(entity.level(), pos.below(), Direction.UP)){
                    entity.teleportTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                    entity.getPersistentData().putInt("escapeTrickCount", entity.getPersistentData().getInt("escapeTrickCount") - 1);
                    if (entity.level() instanceof ServerLevel serverLevel) {
                        for (int p = 0; p < 10 * BeyonderUtil.getScale(entity); i++) {
                            float randomInt = BeyonderUtil.getRandomInRange(BeyonderUtil.getScale(entity) * 2);
                            float randomOne = BeyonderUtil.getRandomInRange(1);
                            serverLevel.sendParticles(ParticleTypes.SMOKE, entity.getX() + randomInt, entity.getY() + randomInt, entity.getZ() + randomInt, 0, randomOne, randomOne, randomOne, 0.5f);
                        }
                    }
                    if (entity instanceof Player player) {
                        player.displayClientMessage(Component.literal("Remaining Escape Tricks: ").withStyle(BeyonderUtil.getStyle(player)).append(Component.literal("" + player.getPersistentData().getInt("escapeTrickCount")).withStyle(ChatFormatting.WHITE)), true);
                    }
                    return true;
                }
            }
        }
        if (entity instanceof Player player) {
            player.displayClientMessage(Component.literal("No safe spaces found").withStyle(BeyonderUtil.getStyle(player)), true);
        }
        return false;
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