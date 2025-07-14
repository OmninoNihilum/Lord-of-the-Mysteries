package net.swimmingtuna.lotm.item.OtherItems;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.swimmingtuna.lotm.init.ItemInit;

public class DollStructure extends Item {
    public DollStructure(Properties pProperties) {
        super(pProperties);
    }

    public static ItemStack createWithCapturedStructure(LivingEntity user, int radius) {
        ItemStack stack = new ItemStack(ItemInit.DOLL_STRUCTURE.get());

        CompoundTag tag = new CompoundTag();
        ListTag blocksTag = new ListTag();

        Level level = user.level();
        BlockPos origin = user.blockPosition();

        double radiusSquared = radius * radius;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distanceSquared = x * x + y * y + z * z;
                    if (distanceSquared > radiusSquared) continue;

                    BlockPos pos = origin.offset(x, y, z);
                    BlockState state = level.getBlockState(pos);

                    if (state.isAir()) continue;

                    CompoundTag blockTag = new CompoundTag();
                    blockTag.putInt("x", x);
                    blockTag.putInt("y", y);
                    blockTag.putInt("z", z);

                    ResourceLocation id = ForgeRegistries.BLOCKS.getKey(state.getBlock());
                    if (id == null) continue;

                    blockTag.putString("block", id.toString());
                    blockTag.putInt("meta", Block.getId(state));

                    blocksTag.add(blockTag);
                }
            }
        }

        tag.put("StructureBlocks", blocksTag);
        stack.setTag(tag);
        return stack;
    }

    public static ListTag getStructureBlocks(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains("Blocks", Tag.TAG_LIST) ? tag.getList("Blocks", Tag.TAG_COMPOUND) : new ListTag();
    }

    public static int getStructureSize(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null ? tag.getInt("Size") : 0;
    }
}