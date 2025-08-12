package net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice;


import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.entity.ApprenticeDoorEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class Starfall extends SimpleAbilityItem {

    public Starfall(Properties properties) {
        super(properties, BeyonderClassInit.APPRENTICE, 1, 3000, 1000);
    }

    @Override
    public InteractionResult useAbility(Level level, LivingEntity player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        addCooldown(player);
        useSpirituality(player);
        starfall(player);
        return InteractionResult.SUCCESS;
    }



    public void starfall(LivingEntity player) {
        if (!player.level().isClientSide()) {
            CompoundTag tag = player.getPersistentData();
            tag.putFloat("starfallYaw", player.getYHeadRot());
            tag.putFloat("starfallPitch", player.getXRot());
            var lookBehind = player.getLookAngle().scale(-20);
            int baseX = (int) (player.getX() + lookBehind.x);
            int baseY = (int) (player.getY() + lookBehind.y);
            int baseZ = (int) (player.getZ() + lookBehind.z);
            tag.putInt("starfallX", baseX);
            tag.putInt("starfallY", baseY);
            tag.putInt("starfallZ", baseZ);
            tag.putInt("starfallTimer", (int) (float) BeyonderUtil.getDamage(player).get(ItemInit.STARFALL.get()));
        }
    }

    public static void starfallTick(LivingEvent.LivingTickEvent event) {
        LivingEntity player = event.getEntity();
        CompoundTag tag = player.getPersistentData();
        if (!player.level().isClientSide() && tag.getInt("starfallTimer") >= 1) {
            int timer = tag.getInt("starfallTimer");
            tag.putInt("starfallTimer", timer - 1);
            int x = tag.getInt("starfallX");
            int y = tag.getInt("starfallY");
            int z = tag.getInt("starfallZ");
            float yaw = player.getYHeadRot();
            float pitch = player.getXRot();
            ApprenticeDoorEntity doorEntity = new ApprenticeDoorEntity(player, player.level(), yaw, pitch, 75);
            doorEntity.getPersistentData().putFloat("starfallPitch", pitch);
            doorEntity.getPersistentData().putFloat("starfallYaw", yaw);
            int randomX = (int) BeyonderUtil.getRandomInRange(30);
            int randomY = (int) BeyonderUtil.getRandomInRange(15);
            int randomZ = (int) BeyonderUtil.getRandomInRange(30);
            BeyonderUtil.setScale(doorEntity, 3);
            doorEntity.teleportTo(x + randomX, y + 50 + randomY, z + randomZ);
            player.level().addFreshEntity(doorEntity);
            if (timer == 1) {
                ApprenticeDoorEntity doorEntityNew = new ApprenticeDoorEntity(player, player.level(), yaw, pitch, 100);
                doorEntity.getPersistentData().putFloat("starfallPitch", pitch);
                doorEntity.getPersistentData().putFloat("starfallYaw", yaw);
                BeyonderUtil.setScale(doorEntity, 10);
                doorEntity.teleportTo(x, y + 50, z);
                doorEntity.getPersistentData().putBoolean("largeStarfall", true);
                player.level().addFreshEntity(doorEntityNew);
            }
        }
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Created doors connected to space, appearing right in front of miniature stars. These doors will shoot out these stars in the direction you looked when using this ability"));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("3000").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("30 Seconds").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    @Override
    public @NotNull Rarity getRarity(ItemStack pStack) {
        return Rarity.create("APPRENTICE_ABILITY", ChatFormatting.AQUA);
    }

    @Override
    public int getPriority(LivingEntity livingEntity, LivingEntity target) {
        if (target != null) {
            return 80;
        }
        return 0;
    }
}