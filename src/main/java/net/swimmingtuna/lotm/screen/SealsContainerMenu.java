package net.swimmingtuna.lotm.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.swimmingtuna.lotm.capabilities.sealed_data.SealedUtils;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.util.BeyonderUtil;

import java.util.UUID;

public class SealsContainerMenu extends AbstractContainerMenu {
    private final SimpleContainer container;
    private final LivingEntity target;
    private final Player user;

    public SealsContainerMenu(int containerID, Inventory playerInventory, Player player, LivingEntity target) {
        super(MenuType.GENERIC_9x5, containerID);
        this.container = new SimpleContainer(45);
        this.target = target;
        this.user = player;

        for (int i = 0; i < this.container.getContainerSize(); i++) {
            this.addSlot(new Slot(this.container, i, 8 + (i % 9) * 18, 18 + (i / 9) * 18) {
                @Override
                public boolean mayPickup(Player player) {
                    return false;
                }
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 86 + i * 18));
            }
        }
        for (int k = 0; k < 9; k++) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 144));
        }

        refreshContainer();
    }

    private boolean hasEnoughSpirituality(UUID sealUUID){
        return BeyonderUtil.getSpirituality(user) >= SealedUtils.getBreakFreeCost(user, target, sealUUID);
    }

    private void refreshContainer() {
        int index = 0;
        for (UUID seal : SealedUtils.getAllSeals(target)) {
            if (index >= container.getContainerSize()) break;

            ItemStack sealIcon = new ItemStack(ItemInit.SEAL_ICON.get());
            CompoundTag sealData = sealIcon.getOrCreateTag();
            sealData.putUUID("sealUUID", seal);

            CompoundTag displayTag = sealIcon.getOrCreateTagElement("display");
            displayTag.putString("Name", "{\"text\":\""+ SealedUtils.nameByType(SealedUtils.getSealType(target, seal)) +"\",\"color\":\""+SealedUtils.pathwayByType(SealedUtils.getSealType(target, seal)).getColorFormatting().getName()+"\",\"italic\":false}");

            ListTag loreList = new ListTag();
            loreList.add(StringTag.valueOf(
                    "[{\"text\":\"Creator: \",\"color\":\"gray\",\"italic\":false}," +
                            "{\"text\":\"" + SealedUtils.getCreatorName(target, seal) + "\",\"color\":\"white\",\"italic\":false}]"
            ));
            loreList.add(StringTag.valueOf(
                    "[{\"text\":\"Sequence: \",\"color\":\"gray\",\"italic\":false}," +
                            "{\"text\":\"" + SealedUtils.getSealSequence(target, seal) + "\",\"color\":\""+SealedUtils.pathwayByType(SealedUtils.getSealType(target, seal)).getColorFormatting().getName()+"\",\"italic\":false}]"
            ));
            if(SealedUtils.hasTimer(target, seal)) loreList.add(StringTag.valueOf(
                    "[{\"text\":\"Time remaining: \",\"color\":\"gray\",\"italic\":false}," +
                            "{\"text\":\"" + (int) (SealedUtils.getTimer(target, seal)*0.05) + " Seconds"+ "\",\"color\":\"white\",\"italic\":false}]"
            ));
            loreList.add(StringTag.valueOf(
                    "[{\"text\":\"Break Free Cost: \",\"color\":\"gray\",\"italic\":false}," +
                            "{\"text\":\"" + SealedUtils.getBreakFreeCost(user, target, seal) + "\",\"color\":\""+(hasEnoughSpirituality(seal) ? "aqua" : "red")+"\",\"italic\":false}]"
            ));

            displayTag.put("Lore", loreList);

            container.setItem(index++, sealIcon);
        }

        while (index < container.getContainerSize()) {
            container.setItem(index++, ItemStack.EMPTY);
        }
    }

    @Override
    public void broadcastChanges() {
        refreshContainer();
        super.broadcastChanges();
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < this.container.getContainerSize()) {
            Slot slot = this.slots.get(slotId);
            if (slot.hasItem()) {
                ItemStack clickedItem = slot.getItem();
                if (clickedItem.hasTag()) {
                    UUID sealUUID = clickedItem.getTag().getUUID("sealUUID");
                    if(!BeyonderUtil.breakSeal(user, target, sealUUID)){
                        player.displayClientMessage(Component.literal("Not enough Spirituality").withStyle(ChatFormatting.RED), true);
                        player.closeContainer();
                    }
                    refreshContainer();
                }
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}