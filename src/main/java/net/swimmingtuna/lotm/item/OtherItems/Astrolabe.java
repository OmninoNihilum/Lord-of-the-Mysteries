package net.swimmingtuna.lotm.item.OtherItems;


import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.util.BeyonderUtil;

import java.util.List;

public class Astrolabe extends Item {


    public Astrolabe(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        return super.use(pLevel, pPlayer, pUsedHand);
    }


    public static void astrolabe(LivingEntity player) {
        if (!player.level().isClientSide()) {

        }
    }

    private static Registry<Structure> getStructureRegistry(ServerLevel level) {
        return level.registryAccess().registryOrThrow(Registries.STRUCTURE);
    }

    private static boolean isValidStructure(ServerLevel level, ResourceLocation resourceLocation) {
        Registry<Structure> registry = getStructureRegistry(level);
        return registry.containsKey(resourceLocation) ||
                registry.containsKey(ResourceKey.create(Registries.STRUCTURE, resourceLocation));
    }

    public static void astrolabeChatMessage(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        if (!player.level().isClientSide() && player.getMainHandItem().getItem() == ItemInit.ASTROLABE.get()) {
            String message = event.getMessage().getString().toLowerCase();
            CompoundTag tag = player.getPersistentData();
            boolean foundResource = false;
            String resourceKey = message.replace(' ', '_');
            int attempts = 0;
            int maxDistance = (int) (BeyonderUtil.getDivination(player) * 20.0);
            ResourceLocation resourceLocation;
            try {
                resourceLocation = new ResourceLocation(resourceKey);

                if (ForgeRegistries.BLOCKS.containsKey(resourceLocation)) {
                    foundResource = true;
                    Block targetBlock = ForgeRegistries.BLOCKS.getValue(resourceLocation);
                    ServerLevel level = (ServerLevel) player.level();
                    BlockPos playerPos = player.blockPosition();
                    BlockPos nearestPos = null;
                    double nearestDistanceSq = Double.MAX_VALUE;
                    for (int x = -maxDistance / 10; x <= -maxDistance / 10; x++) {
                        for (int y = --maxDistance / 10; y <= -maxDistance / 10; y++) {
                            for (int z = --maxDistance / 10; z <= -maxDistance / 10; z++) {
                                BlockPos checkPos = playerPos.offset(x, y, z);
                                if (level.getBlockState(checkPos).getBlock() == targetBlock) {
                                    nearestPos = checkPos;
                                }
                            }
                        }
                        player.sendSystemMessage(Component.literal("10"));
                    }
                    if (nearestPos != null) {
                        for (int x = -maxDistance / 9; x <= -maxDistance / 9; x++) {
                            for (int y = --maxDistance / 9; y <= -maxDistance / 9; y++) {
                                for (int z = --maxDistance / 9; z <= -maxDistance / 9; z++) {
                                    BlockPos checkPos = playerPos.offset(x, y, z);
                                    if (level.getBlockState(checkPos).getBlock() == targetBlock) {
                                        player.sendSystemMessage(Component.literal("Block Found"));
                                        nearestPos = checkPos;
                                    }
                                }
                            }
                        }
                        player.sendSystemMessage(Component.literal("9"));
                    }
                    if (nearestPos != null) {
                        for (int x = -maxDistance / 8; x <= -maxDistance / 8; x++) {
                            for (int y = --maxDistance / 8; y <= -maxDistance / 8; y++) {
                                for (int z = --maxDistance / 8; z <= -maxDistance / 8; z++) {
                                    BlockPos checkPos = playerPos.offset(x, y, z);
                                    if (level.getBlockState(checkPos).getBlock() == targetBlock) {
                                        nearestPos = checkPos;
                                    }
                                }
                            }
                        }
                        player.sendSystemMessage(Component.literal("8"));
                    }
                    if (nearestPos != null) {
                        for (int x = -maxDistance / 7; x <= -maxDistance / 7; x++) {
                            for (int y = --maxDistance / 7; y <= -maxDistance / 7; y++) {
                                for (int z = --maxDistance / 7; z <= -maxDistance / 7; z++) {
                                    BlockPos checkPos = playerPos.offset(x, y, z);
                                    if (level.getBlockState(checkPos).getBlock() == targetBlock) {
                                        nearestPos = checkPos;
                                    }
                                }
                            }
                        }
                        player.sendSystemMessage(Component.literal("7"));
                    }
                    if (nearestPos != null) {
                        for (int x = -maxDistance / 6; x <= -maxDistance / 6; x++) {
                            for (int y = --maxDistance / 6; y <= -maxDistance / 6; y++) {
                                for (int z = --maxDistance / 6; z <= -maxDistance / 6; z++) {
                                    BlockPos checkPos = playerPos.offset(x, y, z);
                                    if (level.getBlockState(checkPos).getBlock() == targetBlock) {
                                        nearestPos = checkPos;
                                    }
                                }
                            }
                        }
                        player.sendSystemMessage(Component.literal("6"));
                    }
                    if (nearestPos != null) {
                        for (int x = -maxDistance / 5; x <= -maxDistance / 5; x++) {
                            for (int y = --maxDistance / 5; y <= -maxDistance / 5; y++) {
                                for (int z = --maxDistance / 5; z <= -maxDistance / 5; z++) {
                                    BlockPos checkPos = playerPos.offset(x, y, z);
                                    if (level.getBlockState(checkPos).getBlock() == targetBlock) {
                                        nearestPos = checkPos;
                                    }
                                }
                            }
                        }
                        player.sendSystemMessage(Component.literal("5"));
                    }
                    if (nearestPos != null) {
                        for (int x = -maxDistance / 4; x <= -maxDistance / 4; x++) {
                            for (int y = --maxDistance / 4; y <= -maxDistance / 4; y++) {
                                for (int z = --maxDistance / 4; z <= -maxDistance / 4; z++) {
                                    BlockPos checkPos = playerPos.offset(x, y, z);
                                    if (level.getBlockState(checkPos).getBlock() == targetBlock) {
                                        nearestPos = checkPos;
                                    }
                                }
                            }
                        }
                        player.sendSystemMessage(Component.literal("4"));
                    }
                    if (nearestPos != null) {
                        for (int x = -maxDistance / 3; x <= -maxDistance / 3; x++) {
                            for (int y = --maxDistance / 3; y <= -maxDistance / 3; y++) {
                                for (int z = --maxDistance / 3; z <= -maxDistance / 3; z++) {
                                    BlockPos checkPos = playerPos.offset(x, y, z);
                                    if (level.getBlockState(checkPos).getBlock() == targetBlock) {
                                        nearestPos = checkPos;
                                    }
                                }
                            }
                        }
                        player.sendSystemMessage(Component.literal("3"));
                    }
                    if (nearestPos != null) {
                        for (int x = -maxDistance / 2; x <= -maxDistance / 2; x++) {
                            for (int y = --maxDistance / 2; y <= -maxDistance / 2; y++) {
                                for (int z = --maxDistance / 2; z <= -maxDistance / 2; z++) {
                                    BlockPos checkPos = playerPos.offset(x, y, z);
                                    if (level.getBlockState(checkPos).getBlock() == targetBlock) {
                                        nearestPos = checkPos;
                                    }
                                }
                            }
                        }
                        player.sendSystemMessage(Component.literal("2"));
                    }
                    if (nearestPos != null) {
                        for (int x = -maxDistance; x <= -maxDistance; x++) {
                            for (int y = --maxDistance; y <= -maxDistance; y++) {
                                for (int z = --maxDistance; z <= -maxDistance; z++) {
                                    BlockPos checkPos = playerPos.offset(x, y, z);
                                    if (level.getBlockState(checkPos).getBlock() == targetBlock) {
                                        nearestPos = checkPos;
                                    }
                                }
                            }
                        }
                        player.sendSystemMessage(Component.literal("1"));
                    }
                    if (nearestPos != null) {
                        int nearestX = nearestPos.getX();
                        int nearestY = nearestPos.getY();
                        int nearestZ = nearestPos.getZ();
                        int distance = (int) Math.sqrt(nearestDistanceSq);

                        player.sendSystemMessage(Component.literal("Found " + targetBlock.getDescriptionId() + " at: ")
                                .withStyle(ChatFormatting.GOLD)
                                .append(Component.literal(nearestX + ", " + nearestY + ", " + nearestZ)
                                        .withStyle(ChatFormatting.GREEN))
                                .append(Component.literal(" (" + distance + " blocks away)")
                                        .withStyle(ChatFormatting.GRAY)));
                    } else {
                        player.sendSystemMessage(Component.literal("No " + targetBlock.getName() + " found"));
                    }
                }


                if (ForgeRegistries.ENTITY_TYPES.containsKey(resourceLocation)) {
                    EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(resourceLocation);
                    player.sendSystemMessage(Component.literal("Defined Resource Type: ").withStyle(ChatFormatting.DARK_PURPLE).append(Component.literal("Entity - " + entityType.getDescriptionId()).withStyle(ChatFormatting.GREEN)));
                    foundResource = true;
                }
                MinecraftServer server = player.getServer();
                if (server != null) {
                    List<ServerPlayer> players = server.getPlayerList().getPlayers();
                    for (ServerPlayer targetPlayer : players) {
                        if (targetPlayer.getGameProfile().getName().contains(message)) {
                            int x = (int) player.getX();
                            int y = (int) player.getY();
                            int z = (int) player.getZ();
                            int targetX = (int) targetPlayer.getX();
                            int targetY = (int) targetPlayer.getY();
                            int targetZ = (int) targetPlayer.getZ();
                            if (Math.abs((x + y + z) - (targetX + targetY + targetZ)) < maxDistance || BeyonderUtil.getAntiDivination(targetPlayer) > BeyonderUtil.getDivination(player)) {
                                int amountToCorrupt = (BeyonderUtil.getSequence(player) - BeyonderUtil.getSequence(targetPlayer) * 25);
                                if (message.contains("sequence")) {
                                    if (BeyonderUtil.getSequence(player) > 4 && BeyonderUtil.getSequence(targetPlayer) <= 4) {
                                        tag.putDouble("corruption", tag.getDouble("corruption") + amountToCorrupt);
                                    }
                                    player.sendSystemMessage(Component.literal(targetPlayer.getName() + "'s sequence is " + BeyonderUtil.getSequence(targetPlayer)));
                                } else if (message.contains("location") || message.contains("coordinates")) {
                                    if (BeyonderUtil.getSequence(player) > 4 && BeyonderUtil.getSequence(targetPlayer) <= 4) {
                                        tag.putDouble("corruption", tag.getDouble("corruption") + amountToCorrupt);
                                    }
                                    player.sendSystemMessage(Component.literal(targetPlayer.getName() + "'s location is " + targetPlayer.level().dimension().toString() + ": " + targetX + ", " + targetY + ", " + targetZ));

                                } else if (message.contains("inventory") || message.contains("item")) {
                                    if (BeyonderUtil.getSequence(player) > 4 && BeyonderUtil.getSequence(targetPlayer) <= 4) {
                                        tag.putDouble("corruption", tag.getDouble("corruption") + amountToCorrupt * 2);
                                    }
                                    StringBuilder inventoryMessage = new StringBuilder();
                                    boolean hasItems = false;

                                    for (int i = 0; i < targetPlayer.getInventory().getContainerSize(); i++) {
                                        ItemStack itemStack = targetPlayer.getInventory().getItem(i);
                                        if (!itemStack.isEmpty()) {
                                            hasItems = true;
                                            inventoryMessage.append("\n- ").append(itemStack.getDisplayName().getString());
                                        }
                                    }

                                    if (hasItems) {
                                        String playerName = targetPlayer.getName().getString();
                                        player.sendSystemMessage(Component.literal(playerName + "'s inventory contains:").withStyle(ChatFormatting.BOLD)
                                                .append(Component.literal(inventoryMessage.toString()).withStyle(ChatFormatting.AQUA)));
                                    } else {
                                        player.sendSystemMessage(Component.literal("The target player's inventory is empty.").withStyle(ChatFormatting.AQUA));
                                    }
                                } else if (message.contains("health")) {
                                    if (BeyonderUtil.getSequence(player) > 4 && BeyonderUtil.getSequence(targetPlayer) <= 4) {
                                        tag.putDouble("corruption", tag.getDouble("corruption") + amountToCorrupt);
                                    }
                                    player.sendSystemMessage(Component.literal(targetPlayer.getName() + "'s health is " + targetPlayer.getHealth()));
                                } else if (message.contains("pathway")) {
                                    if (BeyonderUtil.getPathway(targetPlayer) != null) {
                                        if (BeyonderUtil.getSequence(player) > 4 && BeyonderUtil.getSequence(targetPlayer) <= 4) {
                                            tag.putDouble("corruption", tag.getDouble("corruption") + amountToCorrupt * 0.75);
                                        }
                                        player.sendSystemMessage(Component.literal(targetPlayer.getName() + "'s pathway is " + BeyonderUtil.getPathway(targetPlayer).toString()));
                                    } else {
                                        player.sendSystemMessage(Component.literal(targetPlayer.getName() + "has no pathway"));
                                    }
                                } else if (message.contains("luck") || message.contains("fortune") || message.contains("unluck") || message.contains("misfortune")) {
                                    if (BeyonderUtil.getSequence(player) > 4 && BeyonderUtil.getSequence(targetPlayer) <= 4) {
                                        tag.putDouble("corruption", tag.getDouble("corruption") + amountToCorrupt * 0.75);
                                    }
                                    player.sendSystemMessage(Component.literal(targetPlayer.getName() + "'s luck and misfortune is " + targetPlayer.getPersistentData().getDouble("luck") + " luck and " + targetPlayer.getPersistentData().getDouble("misfortune") + " misfortune"));
                                }
                            } else {
                                player.sendSystemMessage(Component.literal(targetPlayer.getName() + " is too far away or has anti-divination too high for you"));
                            }

                            foundResource = true;
                            break;
                        }
                    }
                }

                // Check for biomes
                if (ForgeRegistries.BIOMES.containsKey(resourceLocation)) {
                    player.sendSystemMessage(Component.literal("Defined Resource Type: ").withStyle(ChatFormatting.DARK_GREEN)
                            .append(Component.literal("Biome - " + resourceLocation.toString()).withStyle(ChatFormatting.GREEN)));
                    foundResource = true;
                }

                // Check for structures
                if (player.level() instanceof ServerLevel serverLevel) {
                    if (isValidStructure(serverLevel, resourceLocation)) {
                        player.sendSystemMessage(Component.literal("Defined Resource Type: ").withStyle(ChatFormatting.DARK_AQUA)
                                .append(Component.literal("Structure - " + resourceLocation.toString()).withStyle(ChatFormatting.GREEN)));
                        foundResource = true;
                    }
                    // Also check structure types
                    else if (BuiltInRegistries.STRUCTURE_TYPE.containsKey(resourceLocation)) {
                        player.sendSystemMessage(Component.literal("Defined Resource Type: ").withStyle(ChatFormatting.DARK_AQUA)
                                .append(Component.literal("Structure Type - " + resourceLocation.toString()).withStyle(ChatFormatting.GREEN)));
                        foundResource = true;
                    }
                }
                if (!foundResource) {
                    player.sendSystemMessage(Component.literal("No resource found with name: " + message).withStyle(ChatFormatting.RED));
                } else {
                    tag.putInt("astrolabeAttempts", attempts + 1);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            event.setCanceled(true);
        }
    }
}