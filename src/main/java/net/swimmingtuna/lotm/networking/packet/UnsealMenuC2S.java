package net.swimmingtuna.lotm.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.capabilities.sealed_data.SealedUtils;
import net.swimmingtuna.lotm.screen.SealsContainerMenu;
import net.swimmingtuna.lotm.util.LeftClickHandler.LeftClickType;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.init.BeyonderClassInit;

import java.util.function.Supplier;

public class UnsealMenuC2S implements LeftClickType {
    private final int targetEntityId;

    public UnsealMenuC2S(int targetEntityId) {
        this.targetEntityId = targetEntityId;
    }

    public UnsealMenuC2S(FriendlyByteBuf buf) {
        this.targetEntityId = buf.readInt();
    }

    public void toByte(FriendlyByteBuf buf) {
        buf.writeInt(this.targetEntityId);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) {
                return;
            }
            if (!BeyonderUtil.currentPathwayAndSequenceMatchesNoException(player, BeyonderClassInit.APPRENTICE.get(), 2)) {
                return;
            }
            LivingEntity targetEntity = null;
            if (targetEntityId == player.getId()) {
                targetEntity = player;
            } else {
                for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(10.0))) {
                    if (entity.getId() == targetEntityId) {
                        targetEntity = entity;
                        break;
                    }
                }
            }
            if (targetEntity == null) {
                LOTM.sendMessageToAllPlayers("6");
                return;
            }
            final LivingEntity finalTarget = targetEntity;
            if (SealedUtils.isSealed(targetEntity)) {
                player.openMenu(new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.literal("All seals from: " + finalTarget.getName().getString());
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
                        return new SealsContainerMenu(containerId, playerInventory, player, finalTarget);
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
        return true;
    }
}