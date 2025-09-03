package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.blocks.DimensionalSight.DimensionalSightTileEntity;
import net.swimmingtuna.lotm.entity.DimensionalSightSealEntity;
import net.swimmingtuna.lotm.init.EntityInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice.DimensionalSight;
import net.swimmingtuna.lotm.util.BeyonderUtil;

import java.util.function.Supplier;

public class DimensionalSightSealC2S {

    public DimensionalSightSealC2S() {
    }

    public DimensionalSightSealC2S(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && !player.level().isClientSide()) {
                handleServerSide(player);
            }
        });
        return true;
    }

    private void handleServerSide(ServerPlayer player) {
        try {
            ItemStack heldItem = player.getMainHandItem();
            if (heldItem.isEmpty()) {
                heldItem = player.getOffhandItem();
            }
            if (heldItem.getItem() instanceof DimensionalSight || player.getPersistentData().getInt("dimensionalSightUsed") >= 1) {
                DimensionalSightTileEntity dimensionalSightTileEntity = BeyonderUtil.findNearbyDimensionalSight(player);
                if (dimensionalSightTileEntity != null && dimensionalSightTileEntity.scryUniqueID != null && dimensionalSightTileEntity.getCasterUUID() != null && dimensionalSightTileEntity.getCasterUUID().equals(player.getUUID())) {
                    LOTM.LOGGER.info("2");
                    DimensionalSightSealEntity sightSealEntity = new DimensionalSightSealEntity(EntityInit.DIMENSIONAL_SIGHT_SEAL_ENTITY.get(), player.level());
                    sightSealEntity.setSealX((float) dimensionalSightTileEntity.getScryTarget().getX());
                    sightSealEntity.setSealY((float) dimensionalSightTileEntity.getScryTarget().getY());
                    sightSealEntity.setSealZ((float) dimensionalSightTileEntity.getScryTarget().getZ());
                    sightSealEntity.setOwner(player);
                    Vec3 lookVec = player.getLookAngle().scale(-10);
                    BlockPos pos = player.getOnPos();
                    sightSealEntity.setMaxLife((int) (float) BeyonderUtil.getDamage(player).get(ItemInit.DIMENSIONAL_SIGHT.get()));
                    sightSealEntity.teleportTo(pos.getX() + lookVec.x(), pos.getY() + lookVec.y(), pos.getZ() + lookVec.z());
                    player.level().addFreshEntity(sightSealEntity);
                    dimensionalSightTileEntity.removeThis();
                }
            }
        } catch (Exception e) {
            LOTM.LOGGER.error("Error handling DimensionalSightSeal packet: ", e);
        }
    }
}