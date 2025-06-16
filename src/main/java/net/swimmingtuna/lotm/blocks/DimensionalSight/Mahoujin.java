package net.swimmingtuna.lotm.blocks.DimensionalSight;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class Mahoujin extends MahoujinBlockTileEntity<MahoujinTileEntity> {
    public Mahoujin() {
        super("mahoujin");
    }


    public Class<MahoujinTileEntity> getTileEntityClass() {
        return MahoujinTileEntity.class;
    }

    @Nullable
    public MahoujinTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new MahoujinTileEntity(blockPos, blockState);
    }
}

