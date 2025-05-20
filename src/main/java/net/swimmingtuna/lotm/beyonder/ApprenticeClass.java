package net.swimmingtuna.lotm.beyonder;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.util.BeyonderUtil;

import java.util.List;

public class ApprenticeClass implements BeyonderClass {
    @Override
    public List<String> sequenceNames() {
        return List.of(
                "Door",
                "Key of Stars",
                "Planeswalker",
                "Wanderer",
                "Secrets Sorcerer",
                "Traveler",
                "Scribe",
                "Astrologer",
                "Trickmaster",
                "Apprentice"
        );
    }

    @Override
    public List<Integer> antiDivination() {
        return List.of(50, 40, 35, 25, 20, 10, 7, 5, 1, 1);
    }

    @Override
    public List<Integer> divination() {
        return List.of(50, 40, 35, 25, 20, 10, 7, 5, 1, 1);
    }

    @Override
    public List<Integer> spiritualityLevels() {
        return List.of(10000, 5000, 3000, 1800, 1200, 700, 450, 300, 175, 125);
    }

    @Override
    public List<Integer> mentalStrength() {
        return List.of(560, 380, 285, 220, 180, 140, 100, 80, 65, 40);
    }

    @Override
    public List<Integer> spiritualityRegen() {
        return List.of(34, 22, 16, 12, 10, 8, 6, 5, 3, 2);
    }

    @Override
    public List<Double> maxHealth() {
        return List.of(350.0, 250.0, 186.0, 136.0, 96.0, 66.0, 54.0, 48.0, 28.0, 22.0);
    }

    @Override
    public void tick(LivingEntity player, int sequenceLevel) {
        if (player.level().getGameTime() % 50 == 0) {
            CompoundTag tag = player.getPersistentData();
            if (sequenceLevel == 9) {
            }
            if (sequenceLevel == 8) {
            }
            if (sequenceLevel == 7) {
            }
            if (sequenceLevel == 6) {
                tag.putInt("maxScribedAbilities", 20);
            }
            if (sequenceLevel == 5) {
                tag.putInt("maxScribedAbilities", 25);
            }
            if (sequenceLevel == 4) {
                tag.putInt("maxScribedAbilities", 30);
            }
            if (sequenceLevel == 3) {
                tag.putInt("maxScribedAbilities", 35);
            }
            if (sequenceLevel == 2) {
                tag.putInt("maxScribedAbilities", 40);
            }
            if (sequenceLevel == 1) {
                tag.putInt("maxScribedAbilities", 45);
            }
            if (sequenceLevel == 0) {
                tag.putInt("maxScribedAbilities", 50);
            }
        }

    }

    @Override
    public Multimap<Integer, Item> getItems() {
        HashMultimap<Integer, Item> items = HashMultimap.create();

        items.put(9, ItemInit.BEYONDER_ABILITY_USER.get());
        items.put(9, ItemInit.ALLY_MAKER.get());
        items.put(9, ItemInit.CREATEDOOR.get());

        items.put(8, ItemInit.TRICKBURNING.get());
        items.put(8, ItemInit.TRICKFREEZING.get());
        items.put(8, ItemInit.TRICKTUMBLE.get());
        items.put(8, ItemInit.TRICKWIND.get());
        items.put(8, ItemInit.TRICKFOG.get());
        items.put(8, ItemInit.TRICKELECTRICSHOCK.get());
        items.put(8, ItemInit.TRICKTELEKENISIS.get());
        items.put(8, ItemInit.TRICKESCAPETRICK.get());
        items.put(8, ItemInit.TRICKFLASH.get());
        items.put(8, ItemInit.TRICKLOUDNOISE.get());
        items.put(8, ItemInit.TRICKBLACKCURTAIN.get());

        items.put(7, ItemInit.ASTROLOGER_SPIRIT_VISION.get());

        items.put(6, ItemInit.RECORDSCRIBE.get());
        items.put(6, ItemInit.SCRIBEABILITIES.get());

        items.put(5, ItemInit.TRAVELERSDOOR.get());
        items.put(5, ItemInit.INVISIBLEHAND.get());
        items.put(5, ItemInit.BLINK.get());
        items.put(5, ItemInit.BLINKAFTERIMAGE.get());

        items.put(4, ItemInit.BLINK_STATE.get());
        items.put(4, ItemInit.EXILE.get());
        items.put(4, ItemInit.DOOR_MIRAGE.get());

        items.put(3, ItemInit.SPATIAL_CAGE.get());
        items.put(3, ItemInit.SPATIAL_TEARING.get());

        items.put(2, ItemInit.SYMBOLIZATION.get());
        items.put(2, ItemInit.DIMENSIONAL_SIGHT.get());
        items.put(2, ItemInit.REPLICATE.get());
        items.put(2, ItemInit.SEALING.get());

        items.put(1, ItemInit.SPACE_FRAGMENTATION.get());
        items.put(1, ItemInit.GRAVITY_MANIPULATION.get());
        items.put(1, ItemInit.SPATIAL_SEAL.get());
        items.put(1, ItemInit.SPATIAL_LOCK_ON.get());

        items.put(0, ItemInit.DOOR_SPATIAL_LOCK_ON.get());
        items.put(0, ItemInit.DOOR_DIMENSION_CLOSING.get());
        items.put(0, ItemInit.DOOR_SEALED_SPACE.get());
        items.put(0, ItemInit.DOOR_LAYERING.get());
        items.put(0, ItemInit.DOOR_GAMMA_RAY_BURST.get());
        items.put(0, ItemInit.CONCEPTUALIZATION.get());
        items.put(0, ItemInit.REPLICATION.get());

        return items;
    }

    @Override
    public ChatFormatting getColorFormatting() {
        return ChatFormatting.BLUE;
    }

    public void scribeRecordedAbilitiesMenu(ServerPlayer player, ItemStack... item){
        SimpleContainer menu = new SimpleContainer(45);

    }
    public SimpleContainer getRegisteredAbilityItemsContainer(int sequenceLevel) {
        SimpleContainer container = new SimpleContainer(45);
        for (int i = 9; i >= sequenceLevel; i--) {
            getItems().get(i)
                    .stream()
                    .map(Item::getDefaultInstance)
                    .forEach(container::addItem);
        }
        return container;
    }

    public static void doorRightClick(PlayerInteractEvent.RightClickBlock event){
        Player player = event.getEntity();
        BlockPos pos = event.getPos();
        Level level = event.getLevel();
        BlockState state = level.getBlockState(pos);
        if(!level.isClientSide){
            if (event.getHand() != event.getEntity().getUsedItemHand()){
                return;
            }
            if(BeyonderUtil.currentPathwayMatchesNoException(player, BeyonderClassInit.APPRENTICE.get())) {
                if (state.getBlock() instanceof DoorBlock) {
                    if (state.getBlock().getStateDefinition().getProperty("open") instanceof BooleanProperty open) {
                        boolean isCurrentlyOpen = state.getValue(open);
                        BlockState newState = state.setValue(open, !isCurrentlyOpen);
                        player.swing(InteractionHand.MAIN_HAND, true);
                        if(!player.isShiftKeyDown()){
                            level.setBlock(pos, newState, 3);
                            level.playSound(null, pos,
                                    isCurrentlyOpen ? SoundEvents.IRON_DOOR_CLOSE : SoundEvents.IRON_DOOR_OPEN,
                                    SoundSource.BLOCKS,
                                    0.9F, 0.9F
                            );
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }

    public static void apprenticeWindSlowFall(LivingEvent.LivingTickEvent event) {
        //WIND MANIPULATION GLIDE
        LivingEntity player = event.getEntity();
        if (!player.level().isClientSide()) {
            boolean x = player instanceof Player pPlayer && pPlayer.getAbilities().instabuild;
            if (BeyonderUtil.currentPathwayMatches(player, BeyonderClassInit.APPRENTICE.get()) && BeyonderUtil.getSequence(player) <= 8 && player.isShiftKeyDown() && player.fallDistance >= 3 && !x) {
                Vec3 movement = player.getDeltaMovement();
                double deltaX = Math.cos(Math.toRadians(player.getYRot() + 90)) * 0.06;
                double deltaZ = Math.sin(Math.toRadians(player.getYRot() + 90)) * 0.06;
                player.setDeltaMovement(movement.x + deltaX, -0.05, movement.z + deltaZ);
                player.fallDistance = 5;
                player.hurtMarked = true;
            }
        }
    }
}
