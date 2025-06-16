package net.swimmingtuna.lotm.blocks.DimensionalSight;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class UpdatingTileEntity extends BlockEntity {

    public UpdatingTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    /**
     * Sends block updates to clients when the tile entity data changes
     */
    public void sendUpdates() {
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition), this.level.getBlockState(this.worldPosition), 3);
            this.level.setBlockAndUpdate(this.worldPosition, this.level.getBlockState(this.worldPosition));
            this.level.updateNeighbourForOutputSignal(this.worldPosition, this.level.getBlockState(this.worldPosition).getBlock());
        }
        this.setChanged();
    }

    /**
     * Called when receiving data packet from server on client side
     */
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        super.onDataPacket(net, packet);
        if (this.level != null) {
            this.handleUpdateTag(packet.getTag());
        }
    }

    /**
     * Creates the packet to send tile entity data to clients
     */
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    /**
     * Saves the tile entity data to NBT when the chunk is saved
     */
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        this.saveAdditional(tag);
        return tag;
    }
}