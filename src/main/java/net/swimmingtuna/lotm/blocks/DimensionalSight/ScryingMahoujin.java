package net.swimmingtuna.lotm.blocks.DimensionalSight;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ScryingMahoujin extends MahoujinBlockTileEntity<DimensionalSightTileEntity> {
    public ScryingMahoujin() {
        super("dimensional_sight");
    }

    public Class<DimensionalSightTileEntity> getTileEntityClass() {
        return DimensionalSightTileEntity.class;
    }

    @Nullable
    public DimensionalSightTileEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new DimensionalSightTileEntity(blockPos, blockState);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return (a, b, c, tile) -> {
            if (tile instanceof DimensionalSightTileEntity) {
                ((DimensionalSightTileEntity)tile).tick(a, b, c, (DimensionalSightTileEntity)tile);
            }

        };
    }
}

