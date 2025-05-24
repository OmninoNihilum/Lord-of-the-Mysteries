package net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.entity.ApprenticeDoorEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class TravelersDoor extends SimpleAbilityItem {

    public TravelersDoor(Properties properties) {
        super(properties, BeyonderClassInit.APPRENTICE, 5, 300, 20);
    }

    @Override
    public InteractionResult useAbility(Level level, LivingEntity player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        CompoundTag tag = player.getPersistentData();
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        if (player.isShiftKeyDown()) {
            setWaypoint(player, stack, tag);
        } else {
            useSpirituality(player);
            teleportToWaypoint(player, tag, stack);
        }
        addCooldown(player);
        return InteractionResult.SUCCESS;
    }

    private void setWaypoint(LivingEntity player, ItemStack stack, CompoundTag tag) {
        if (!player.level().isClientSide) {
            int waypoint = tag.getInt("doorWaypoint");
            tag.putDouble("x" + waypoint, player.getX());
            tag.putDouble("y" + waypoint, player.getY());
            tag.putDouble("z" + waypoint, player.getZ());
            String coords = String.format("Waypoint %d set at: %.1f, %.1f, %.1f", waypoint, player.getX(), player.getY(), player.getZ());
            if (player instanceof Player pPlayer) {
                pPlayer.displayClientMessage(Component.literal(coords).withStyle(BeyonderUtil.getStyle(player)), true);
            }
        }
    }

    private void teleportToWaypoint(LivingEntity livingEntity, CompoundTag tag, ItemStack stack) {
        if (!livingEntity.level().isClientSide) {
            int waypoint = tag.getInt("doorWaypoint");
            double x = tag.getDouble("x" + waypoint);
            double y = tag.getDouble("y" + waypoint);
            double z = tag.getDouble("z" + waypoint);
            if (x != 0 && y != 0 && z != 0) {
                livingEntity.teleportTo(x, y, z);
                String coords = String.format("Teleported to %.1f, %.1f, %.1f", x, y, z);
                if (livingEntity instanceof Player pPlayer) {
                    pPlayer.displayClientMessage(Component.literal(coords).withStyle(BeyonderUtil.getStyle(pPlayer)), true);
                }
            } else if (livingEntity instanceof Player pPlayer) {
                pPlayer.displayClientMessage(Component.literal("No waypoint found").withStyle(ChatFormatting.RED), true);
            }
        }
    }

    public static boolean coordsTravel(String message) {
        message = message.replace(",", " ").trim();
        message = message.replaceAll("\\s+", " ");
        try {
            String[] parts = message.split(" ");

            // If we have 3 parts, they must all be integers
            if (parts.length == 3) {
                for (String part : parts) {
                    Integer.parseInt(part);
                }
                return true;
            }
            // If we have 4+ parts, the last 3 parts must be integers
            else if (parts.length >= 4) {
                // Try to parse the last 3 elements as integers
                for (int i = parts.length - 3; i < parts.length; i++) {
                    Integer.parseInt(parts[i]);
                }
                return true;
            }

            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean hasDimensionId(String message) {
        message = message.replace(",", " ");
        String[] parts = message.trim().split("\\s+");
        if (parts.length > 3) {
            try {
                for (int i = parts.length - 3; i < parts.length; i++) {
                    Integer.parseInt(parts[i]);
                }
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    public static String getDimensionId(String message) {
        message = message.replace(",", " ");
        String[] parts = message.trim().split("\\s+");
        if (parts.length > 3) {
            try {
                // Make sure the last 3 parts are integers
                for (int i = parts.length - 3; i < parts.length; i++) {
                    Integer.parseInt(parts[i]);
                }

                // Extract dimension ID from the message
                // If it's just one word before the coordinates
                if (parts.length == 4) {
                    String dim = parts[0].toLowerCase();
                    // Ensure the dimension has the correct minecraft: namespace
                    if (dim.equals("nether")) {
                        return "minecraft:the_nether";
                    } else if (dim.equals("end")) {
                        return "minecraft:the_end";
                    } else if (dim.equals("overworld")) {
                        return "minecraft:overworld";
                    } else {
                        // For modded dimensions, assume they use their own namespace
                        return dim;
                    }
                } else {
                    // If the dimension name has multiple words (like "the end")
                    StringBuilder dimensionId = new StringBuilder();
                    for (int i = 0; i < parts.length - 3; i++) {
                        if (i > 0) dimensionId.append("_");
                        dimensionId.append(parts[i].toLowerCase());
                    }

                    String dim = dimensionId.toString();
                    // Apply the correct namespace
                    if (dim.equals("the_nether") || dim.equals("nether")) {
                        return "minecraft:the_nether";
                    } else if (dim.equals("the_end") || dim.equals("end")) {
                        return "minecraft:the_end";
                    } else if (dim.equals("overworld")) {
                        return "minecraft:overworld";
                    } else {
                        // For modded dimensions, attempt to add minecraft: namespace
                        return dim;
                    }
                }
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public static Level getTargetLevel(Player player, String dimensionId) {
        if (dimensionId == null) {
            return player.level();
        }

        MinecraftServer server = player.level().getServer();
        if (server == null) return player.level();
        ResourceKey<Level> dimKey;
        try {
            dimKey = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dimensionId));
        } catch (Exception e) {
            if (dimensionId.toLowerCase().contains("nether")) {
                dimKey = Level.NETHER;
            } else if (dimensionId.toLowerCase().contains("end")) {
                dimKey = Level.END;
            } else {
                dimKey = Level.OVERWORLD;
            }
        }

        ServerLevel level = server.getLevel(dimKey);
        if (level != null) {
            return level;
        } else {
            return player.level();
        }
    }

    public static double[] getHorizontalLookCoordinates(Player player, double distance){
        float yaw = player.getYRot();
        double angleRadians = Math.toRadians(-yaw);
        double x = player.getX() + distance * Math.sin(angleRadians);
        double z = player.getZ() + distance * Math.cos(angleRadians);
        return new double[] {x, z};
    }

    public static void spawnDoor(Player player, int x, int y, int z, String dimensionId){
        Level targetLevel = getTargetLevel(player, dimensionId);
        float yaw = -player.getYRot() + 180;
        ApprenticeDoorEntity.DoorAnimationKind animationKind = ApprenticeDoorEntity.DoorAnimationKind.BELLOW;
        if(player.level().getBlockState(new BlockPos((int) Math.floor(getHorizontalLookCoordinates(player, 2)[0]),
                (int) Math.floor(player.getY() - 1),
                (int) Math.floor(getHorizontalLookCoordinates(player, 2)[1]))).isAir()){
            animationKind = ApprenticeDoorEntity.DoorAnimationKind.FADE_IN;
        }

        ApprenticeDoorEntity door = new ApprenticeDoorEntity(player.level(), player, BeyonderUtil.getSequence(player), 150, yaw, x, y, z, targetLevel, animationKind);
        door.teleportTo(getHorizontalLookCoordinates(player, 2)[0], player.getY(), getHorizontalLookCoordinates(player, 2)[1]);

        player.level().addFreshEntity(door);
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, blink in the direction you're looking."));
        tooltipComponents.add(Component.literal("You can also type coordinates or an ally name in the chat on the following format while holding this item in order to go to that location."));
        tooltipComponents.add(Component.literal("\"X\", \"Y\", \"Z\", \"Dimension\"(Optional), \"Instant\"(Optional)"));
        tooltipComponents.add(Component.literal("\"Ally Name\", \"Instant\"(Optional)"));
        tooltipComponents.add(Component.literal("Shift + Right-click to set waypoint at current position"));
        tooltipComponents.add(Component.literal("Left-click air to cycle between waypoints"));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("300 to teleport to waypoint or create a door to a location. None to set a waypoint. ").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("1 Second").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player && !level.isClientSide && player.getMainHandItem().getItem() == ItemInit.TRAVELERSDOOR.get()) {
            if (isSelected) {
                CompoundTag tag = player.getPersistentData();
                int currentWaypoint = tag.getInt("doorWaypoint");
                double x = tag.getDouble("x" + currentWaypoint);
                double y = tag.getDouble("y" + currentWaypoint);
                double z = tag.getDouble("z" + currentWaypoint);

                if (tag.contains("x" + currentWaypoint)) {
                    String coords = String.format("Waypoint %d: %.1f, %.1f, %.1f",
                            currentWaypoint, x, y, z);
                    player.displayClientMessage(Component.literal(coords)
                            .withStyle(BeyonderUtil.getStyle(player)), true);
                }
            }
        }
    }

    public static void clearAllWaypoints(LivingEntity livingEntity) {
        if (!livingEntity.level().isClientSide) {
            CompoundTag tag = livingEntity.getPersistentData();
            for (int i = 0; i < 100; i++) {
                tag.remove("x" + i);
                tag.remove("y" + i);
                tag.remove("z" + i);
            }
        }
    }


    @Override
    public Rarity getRarity(ItemStack pStack) {
        return Rarity.create("APPRENTICES_ABILITY", ChatFormatting.BLUE);
    }
}