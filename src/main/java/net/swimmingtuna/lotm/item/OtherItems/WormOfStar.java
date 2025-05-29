package net.swimmingtuna.lotm.item.OtherItems;


import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WormOfStar extends Item {


    public WormOfStar(Properties pProperties) {
        super(pProperties.durability(2000));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        if (!pLevel.isClientSide()) {
            //summon copy
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide());
    }


    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return pStack.isDamaged();
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("A worm made from high sequences of the Apprentice Pathway.").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GOLD));
        tooltipComponents.add(Component.literal("If you're a high sequence beyonder of the Apprentice Pathway, you can shift while holding this item to choose how much spirituality that should be put into it.").withStyle(ChatFormatting.BLUE).withStyle(ChatFormatting.BOLD));
        tooltipComponents.add(Component.literal("Depending on the amount of spirituality, a copy of yourself will be made which can attack, however it will be unable to regenerate spirituality.").withStyle(ChatFormatting.BLUE).withStyle(ChatFormatting.GOLD));
        tooltipComponents.add(Component.literal("With this in your inventory, when you use an ability, it's cooldown will be shortened in exchange for an equivalent amount of these.").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}