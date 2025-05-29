package net.swimmingtuna.lotm.item.OtherItems;


import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Astrolabe extends Item {


    public Astrolabe(Properties pProperties) {
        super(pProperties.durability(2000));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        if (!pLevel.isClientSide()) {
            for (LivingEntity living : BeyonderUtil.getNonAlliesNearby(pPlayer, 30)) {
                int totalDamage = 10 - Math.min(10, BeyonderUtil.getSequence(living));
                if (totalDamage > 0) {
                    stack.hurtAndBreak(totalDamage, pPlayer, (player) -> {
                        player.broadcastBreakEvent(pHand);
                    });
                }
            }
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide());
    }


    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return pStack.isDamaged();
    }


    private static Registry<Structure> getStructureRegistry(ServerLevel level) {
        return level.registryAccess().registryOrThrow(Registries.STRUCTURE);
    }

    public static void astrolabeChatMessage(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        if (!player.level().isClientSide() && player.getMainHandItem().getItem() == ItemInit.ASTROLABE.get()) {
            String message = event.getMessage().getString().toLowerCase();
            CompoundTag tag = player.getPersistentData();
            boolean foundResource = false;
            String resourceKey = message.replace(' ', '_');
            int attempts = 0;
            int maxDistance = (int) (BeyonderUtil.getDivination(player) * 30.0);
            ResourceLocation resourceLocation;
            try {
                resourceLocation = new ResourceLocation(resourceKey);
                if (ForgeRegistries.BLOCKS.containsKey(resourceLocation)) {
                    maxDistance = maxDistance / 15;
                    foundResource = true;
                    Block targetBlock = ForgeRegistries.BLOCKS.getValue(resourceLocation);
                    ServerLevel level = (ServerLevel) player.level();
                    BlockPos playerPos = player.blockPosition();
                    Vec3 nearestPos = null;
                    double nearestDistanceSq = Double.MAX_VALUE;
                    for (int x = -maxDistance / 10; x <= maxDistance / 10; x++) {
                        for (int y = -maxDistance / 10; y <= maxDistance / 10; y++) {
                            for (int z = -maxDistance / 10; z <= maxDistance / 10; z++) {
                                BlockPos checkPos = playerPos.offset(x, y, z);
                                double distanceSq = playerPos.distSqr(checkPos);
                                if (distanceSq < nearestDistanceSq) {
                                    if (level.getBlockState(checkPos).getBlock() == targetBlock) {
                                        nearestPos = checkPos.getCenter();
                                        nearestDistanceSq = distanceSq;
                                    }
                                }
                            }
                        }
                    }
                    if (nearestPos == null) {
                        for (int x = -maxDistance / 9; x <= maxDistance / 9; x++) {
                            for (int y = -maxDistance / 9; y <= maxDistance / 9; y++) {
                                for (int z = -maxDistance / 9; z <= maxDistance / 9; z++) {
                                    BlockPos checkPos = playerPos.offset(x, y, z);
                                    double distanceSq = playerPos.distSqr(checkPos);
                                    if (distanceSq < nearestDistanceSq) {
                                        if (level.getBlockState(checkPos).getBlock() == targetBlock) {
                                            nearestPos = checkPos.getCenter();
                                            nearestDistanceSq = distanceSq;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (nearestPos == null) {
                        for (int x = -maxDistance / 8; x <= maxDistance / 8; x++) {
                            for (int y = -maxDistance / 8; y <= maxDistance / 8; y++) {
                                for (int z = -maxDistance / 8; z <= maxDistance / 8; z++) {
                                    BlockPos checkPos = playerPos.offset(x, y, z);
                                    double distanceSq = playerPos.distSqr(checkPos);
                                    if (distanceSq < nearestDistanceSq) {
                                        if (level.getBlockState(checkPos).getBlock() == targetBlock) {
                                            nearestPos = checkPos.getCenter();
                                            nearestDistanceSq = distanceSq;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (nearestPos == null) {
                        for (int x = -maxDistance / 7; x <= maxDistance / 7; x++) {
                            for (int y = -maxDistance / 7; y <= maxDistance / 7; y++) {
                                for (int z = -maxDistance / 7; z <= maxDistance / 7; z++) {
                                    BlockPos checkPos = playerPos.offset(x, y, z);
                                    double distanceSq = playerPos.distSqr(checkPos);
                                    if (distanceSq < nearestDistanceSq) {
                                        if (level.getBlockState(checkPos).getBlock() == targetBlock) {
                                            nearestPos = checkPos.getCenter();
                                            nearestDistanceSq = distanceSq;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (nearestPos == null) {
                        for (int x = -maxDistance / 6; x <= maxDistance / 6; x++) {
                            for (int y = -maxDistance / 6; y <= maxDistance / 6; y++) {
                                for (int z = -maxDistance / 6; z <= maxDistance / 6; z++) {
                                    BlockPos checkPos = playerPos.offset(x, y, z);
                                    double distanceSq = playerPos.distSqr(checkPos);
                                    if (distanceSq < nearestDistanceSq) {
                                        if (level.getBlockState(checkPos).getBlock() == targetBlock) {
                                            nearestPos = checkPos.getCenter();
                                            nearestDistanceSq = distanceSq;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (nearestPos == null) {
                        for (int x = -maxDistance / 5; x <= maxDistance / 5; x++) {
                            for (int y = -maxDistance / 5; y <= maxDistance / 5; y++) {
                                for (int z = -maxDistance / 5; z <= maxDistance / 5; z++) {
                                    BlockPos checkPos = playerPos.offset(x, y, z);
                                    double distanceSq = playerPos.distSqr(checkPos);
                                    if (distanceSq < nearestDistanceSq) {
                                        if (level.getBlockState(checkPos).getBlock() == targetBlock) {
                                            nearestPos = checkPos.getCenter();
                                            nearestDistanceSq = distanceSq;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (nearestPos == null) {
                        for (int x = -maxDistance / 4; x <= maxDistance / 4; x++) {
                            for (int y = -maxDistance / 4; y <= maxDistance / 4; y++) {
                                for (int z = -maxDistance / 4; z <= maxDistance / 4; z++) {
                                    BlockPos checkPos = playerPos.offset(x, y, z);
                                    double distanceSq = playerPos.distSqr(checkPos);
                                    if (distanceSq < nearestDistanceSq) {
                                        if (level.getBlockState(checkPos).getBlock() == targetBlock) {
                                            nearestPos = checkPos.getCenter();
                                            nearestDistanceSq = distanceSq;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (nearestPos == null) {
                        for (int x = -maxDistance / 3; x <= maxDistance / 3; x++) {
                            for (int y = -maxDistance / 3; y <= maxDistance / 3; y++) {
                                for (int z = -maxDistance / 3; z <= maxDistance / 3; z++) {
                                    BlockPos checkPos = playerPos.offset(x, y, z);
                                    double distanceSq = playerPos.distSqr(checkPos);
                                    if (distanceSq < nearestDistanceSq) {
                                        if (level.getBlockState(checkPos).getBlock() == targetBlock) {
                                            nearestPos = checkPos.getCenter();
                                            nearestDistanceSq = distanceSq;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (nearestPos == null) {
                        for (int x = -maxDistance / 2; x <= maxDistance / 2; x++) {
                            for (int y = -maxDistance / 2; y <= maxDistance / 2; y++) {
                                for (int z = -maxDistance / 2; z <= maxDistance / 2; z++) {
                                    BlockPos checkPos = playerPos.offset(x, y, z);
                                    double distanceSq = playerPos.distSqr(checkPos);
                                    if (distanceSq < nearestDistanceSq) {
                                        if (level.getBlockState(checkPos).getBlock() == targetBlock) {
                                            nearestPos = checkPos.getCenter();
                                            nearestDistanceSq = distanceSq;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (nearestPos == null) {
                        for (int x = -maxDistance; x <= maxDistance; x++) {
                            for (int y = -maxDistance; y <= maxDistance; y++) {
                                for (int z = -maxDistance; z <= maxDistance; z++) {
                                    BlockPos checkPos = playerPos.offset(x, y, z);
                                    double distanceSq = playerPos.distSqr(checkPos);
                                    if (distanceSq < nearestDistanceSq) {
                                        if (level.getBlockState(checkPos).getBlock() == targetBlock) {
                                            nearestPos = checkPos.getCenter();
                                            nearestDistanceSq = distanceSq;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (nearestPos != null) {
                        BlockPos nearestBlockpos = BlockPos.containing(nearestPos);
                        int nearestX = nearestBlockpos.getX();
                        int nearestY = nearestBlockpos.getY();
                        int nearestZ = nearestBlockpos.getZ();
                        player.sendSystemMessage(Component.literal(targetBlock.getName().getString() + " is at: ").withStyle(ChatFormatting.LIGHT_PURPLE).append(Component.literal(nearestX + ", " + nearestY + ", " + nearestZ).withStyle(ChatFormatting.GREEN)));
                        BeyonderUtil.useSpirituality(player, maxDistance * 3);
                    } else {
                        player.sendSystemMessage(Component.literal("No " + targetBlock.getName() + " found"));
                    }
                }


                if (ForgeRegistries.ENTITY_TYPES.containsKey(resourceLocation)) {
                    foundResource = true;
                    EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(resourceLocation);
                    ServerLevel level = (ServerLevel) player.level();
                    Entity nearestEntity = null;
                    double nearestDistanceSq = Double.MAX_VALUE;
                    for (Entity entity : level.getAllEntities()) {
                        if (entity.getType() == entityType) {
                            double distanceSq = player.distanceToSqr(entity);
                            if (distanceSq < nearestDistanceSq && distanceSq <= maxDistance * maxDistance) {
                                nearestEntity = entity;
                                nearestDistanceSq = distanceSq;
                            }
                        }
                    }

                    if (nearestEntity != null) {
                        int nearestX = (int) nearestEntity.getX();
                        int nearestY = (int) nearestEntity.getY();
                        int nearestZ = (int) nearestEntity.getZ();
                        BeyonderUtil.useSpirituality(player, maxDistance * 4);
                        player.sendSystemMessage(Component.literal(entityType.getDescription().getString() + " is at: ").withStyle(ChatFormatting.LIGHT_PURPLE).append(Component.literal(nearestX + ", " + nearestY + ", " + nearestZ).withStyle(ChatFormatting.GREEN)));
                    } else {
                        player.sendSystemMessage(Component.literal("No " + entityType.getDescription().getString() + " found within range").withStyle(ChatFormatting.RED));
                    }
                }
                MinecraftServer server = player.getServer();
                if (server != null) {
                    List<ServerPlayer> players = server.getPlayerList().getPlayers();
                    for (ServerPlayer targetPlayer : players) {
                        if (message.contains(targetPlayer.getGameProfile().getName().toLowerCase())) {
                            int x = (int) player.getX();
                            int y = (int) player.getY();
                            int z = (int) player.getZ();
                            int targetX = (int) targetPlayer.getX();
                            int targetY = (int) targetPlayer.getY();
                            int targetZ = (int) targetPlayer.getZ();
                            if (Math.abs((x + y + z) - (targetX + targetY + targetZ)) < maxDistance * 10 || BeyonderUtil.getAntiDivination(targetPlayer) > BeyonderUtil.getDivination(player)) {
                                int amountToCorrupt = (BeyonderUtil.getSequence(player) - BeyonderUtil.getSequence(targetPlayer) * 25);
                                if (message.contains("sequence")) {
                                    if (BeyonderUtil.getSequence(player) > 4 && BeyonderUtil.getSequence(targetPlayer) <= 4) {
                                        tag.putDouble("corruption", tag.getDouble("corruption") + amountToCorrupt);
                                        targetPlayer.sendSystemMessage(Component.literal(player.getName().getString() + "divined about you").withStyle(ChatFormatting.RED));
                                    } else if (BeyonderUtil.getSequence(targetPlayer) == 0) {
                                        if (BeyonderUtil.getSequence(targetPlayer) > 1) {
                                            tag.putDouble("corruption", tag.getDouble("corruption") + amountToCorrupt);
                                            targetPlayer.sendSystemMessage(Component.literal(player.getName().getString() + "divined about you").withStyle(ChatFormatting.RED));
                                        }
                                    }
                                    player.sendSystemMessage(Component.literal(targetPlayer.getName().getString() + "'s sequence is " + BeyonderUtil.getSequence(targetPlayer)));
                                } else if (message.contains("location") || message.contains("coordinates")) {
                                    if (BeyonderUtil.getSequence(player) > 4 && BeyonderUtil.getSequence(targetPlayer) <= 4) {
                                        tag.putDouble("corruption", tag.getDouble("corruption") + amountToCorrupt);
                                        targetPlayer.sendSystemMessage(Component.literal(player.getName().getString() + "divined about you").withStyle(ChatFormatting.RED));
                                    } else if (BeyonderUtil.getSequence(targetPlayer) == 0) {
                                        if (BeyonderUtil.getSequence(targetPlayer) > 1) {
                                            tag.putDouble("corruption", tag.getDouble("corruption") + amountToCorrupt);
                                            targetPlayer.sendSystemMessage(Component.literal(player.getName().getString() + "divined about you").withStyle(ChatFormatting.RED));
                                        }
                                    }
                                    player.sendSystemMessage(Component.literal(targetPlayer.getName().getString() + "'s location is " + targetPlayer.level().dimension().toString() + ": " + targetX + ", " + targetY + ", " + targetZ));

                                } else if (message.contains("inventory") || message.contains("item")) {
                                    if (BeyonderUtil.getSequence(player) > 4 && BeyonderUtil.getSequence(targetPlayer) <= 4) {
                                        tag.putDouble("corruption", tag.getDouble("corruption") + amountToCorrupt * 2);
                                        targetPlayer.sendSystemMessage(Component.literal(player.getName().getString() + "divined about you").withStyle(ChatFormatting.RED));
                                    } else if (BeyonderUtil.getSequence(targetPlayer) == 0) {
                                        if (BeyonderUtil.getSequence(targetPlayer) > 1) {
                                            tag.putDouble("corruption", tag.getDouble("corruption") + amountToCorrupt * 2);
                                            targetPlayer.sendSystemMessage(Component.literal(player.getName().getString() + "divined about you").withStyle(ChatFormatting.RED));
                                        }
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
                                    player.sendSystemMessage(Component.literal(targetPlayer.getName().getString() + "'s health is " + targetPlayer.getHealth()));
                                } else if (message.contains("pathway")) {
                                    if (BeyonderUtil.getPathway(targetPlayer) != null) {
                                        if (BeyonderUtil.getSequence(player) > 4 && BeyonderUtil.getSequence(targetPlayer) <= 4) {
                                            tag.putDouble("corruption", tag.getDouble("corruption") + amountToCorrupt * 0.75);
                                            targetPlayer.sendSystemMessage(Component.literal(player.getName().getString() + "divined about you").withStyle(ChatFormatting.RED));
                                        } else if (BeyonderUtil.getSequence(targetPlayer) == 0) {
                                            if (BeyonderUtil.getSequence(targetPlayer) > 1) {
                                                tag.putDouble("corruption", tag.getDouble("corruption") + amountToCorrupt * 0.75);
                                                targetPlayer.sendSystemMessage(Component.literal(player.getName().getString() + "divined about you").withStyle(ChatFormatting.RED));
                                            }
                                        }
                                        player.sendSystemMessage(Component.literal(targetPlayer.getName().getString() + "'s pathway is " + BeyonderUtil.getPathway(targetPlayer).toString()));
                                    } else {
                                        player.sendSystemMessage(Component.literal(targetPlayer.getName().getString() + "has no pathway"));
                                    }
                                } else if (message.contains("luck") || message.contains("fortune") || message.contains("unluck") || message.contains("misfortune")) {
                                    if (BeyonderUtil.getSequence(player) > 4 && BeyonderUtil.getSequence(targetPlayer) <= 4) {
                                        tag.putDouble("corruption", tag.getDouble("corruption") + amountToCorrupt * 0.75);
                                        targetPlayer.sendSystemMessage(Component.literal(player.getName().getString() + "divined about you").withStyle(ChatFormatting.RED));
                                    } else if (BeyonderUtil.getSequence(targetPlayer) == 0) {
                                        if (BeyonderUtil.getSequence(targetPlayer) > 1) {
                                            tag.putDouble("corruption", tag.getDouble("corruption") + amountToCorrupt * 0.75);
                                            targetPlayer.sendSystemMessage(Component.literal(player.getName().getString() + "divined about you").withStyle(ChatFormatting.RED));
                                        }
                                    }
                                    player.sendSystemMessage(Component.literal(targetPlayer.getName().getString() + "'s luck and misfortune is " + targetPlayer.getPersistentData().getDouble("luck") + " luck and " + targetPlayer.getPersistentData().getDouble("misfortune") + " misfortune"));
                                }
                                BeyonderUtil.useSpirituality(player, maxDistance * 5);
                            } else {
                                player.sendSystemMessage(Component.literal(targetPlayer.getName().getString() + " is too far away or has anti-divination too high for you"));
                            }

                            foundResource = true;
                            break;
                        }
                    }
                }
                Registry<Biome> biomeRegistry = player.level().registryAccess().registryOrThrow(Registries.BIOME);
                ResourceKey<Biome> targetBiomeKey = null;
                for (Map.Entry<ResourceKey<Biome>, Biome> entry : biomeRegistry.entrySet()) {
                    ResourceKey<Biome> biomeKey = entry.getKey();
                    ResourceLocation biomeId = biomeKey.location();
                    String path = biomeId.getPath().toLowerCase(Locale.ROOT);
                    String namespaceAndPath = biomeId.toString().toLowerCase(Locale.ROOT);

                    if (path.equals(message) || namespaceAndPath.equals(message)) {
                        foundResource = true;
                        targetBiomeKey = biomeKey;
                        break;
                    }
                }
                if (foundResource && targetBiomeKey != null) {
                    BlockPos origin = player.blockPosition();
                    int radius = maxDistance;
                    int step = 16; // Chunk step to make it not too heavy
                    BlockPos closest = null;
                    double closestDist = Double.MAX_VALUE;
                    for (int dx = -radius; dx <= radius; dx += step) {
                        for (int dz = -radius; dz <= radius; dz += step) {
                            BlockPos checkPos = origin.offset(dx, 0, dz);
                            ResourceKey<Biome> biomeAtPosKey = player.level().getBiome(checkPos).unwrapKey().orElse(null);
                            if (biomeAtPosKey != null && biomeAtPosKey.equals(targetBiomeKey)) {
                                double distSq = checkPos.distSqr(origin);
                                if (distSq < closestDist) {
                                    closest = checkPos;
                                    closestDist = distSq;
                                }
                            }
                        }
                    }

                    if (closest != null) {
                        String biomeName = Component.translatable("biome." + targetBiomeKey.location().getNamespace() + "." + targetBiomeKey.location().getPath()).getString();
                        BeyonderUtil.useSpirituality(player, maxDistance * 4);
                        player.sendSystemMessage(Component.literal("Nearest " + biomeName + " found at: ").withStyle(ChatFormatting.LIGHT_PURPLE).append(closest.toShortString()).withStyle(ChatFormatting.GREEN));
                    } else {
                        player.sendSystemMessage(Component.literal("Biome found but no nearby instance within 500 blocks.").withStyle(ChatFormatting.RED));
                    }
                }


                // Check for structures
                if (player.level() instanceof ServerLevel serverLevel) {
                    Registry<Structure> structureRegistry = getStructureRegistry(serverLevel);
                    ResourceKey<Structure> structureResourceKey = ResourceKey.create(Registries.STRUCTURE, resourceLocation);
                    TagKey<Structure> structureTagKey = TagKey.create(Registries.STRUCTURE, resourceLocation);
                    boolean isTag = serverLevel.registryAccess().registryOrThrow(Registries.STRUCTURE).containsKey(structureTagKey.location());
                    boolean isResource = structureRegistry.containsKey(structureResourceKey);
                    if (isResource) {
                        foundResource = true;
                        int searchRadius = maxDistance * 2;
                        BlockPos playerPos = player.blockPosition();
                        BlockPos nearestStructurePos = serverLevel.findNearestMapStructure(TagKey.create(Registries.STRUCTURE, structureResourceKey.location()), playerPos, searchRadius, false);
                        if (nearestStructurePos != null) {
                            int nearestX = nearestStructurePos.getX();
                            int nearestZ = nearestStructurePos.getZ();
                            int playerX = (int) player.getX();
                            int playerZ = (int) player.getZ();
                            int distanceFromStructure = Math.abs(nearestX - playerX) + Math.abs(nearestZ - playerZ);
                            String structureName = structureResourceKey.location().getPath().replace('_', ' ');
                            if (distanceFromStructure <= searchRadius) {
                                BeyonderUtil.useSpirituality(player, maxDistance * 4);
                                player.sendSystemMessage(Component.literal("Found " + structureName + " near: ").withStyle(ChatFormatting.LIGHT_PURPLE).append(Component.literal("X: " + nearestX + " Z: " + nearestZ).withStyle(ChatFormatting.GREEN)));
                            } else {
                                player.sendSystemMessage(Component.literal("No " + structureResourceKey.location().getPath().replace('_', ' ') + " found").withStyle(ChatFormatting.RED));
                            }
                        } else {
                            player.sendSystemMessage(Component.literal("No " + structureResourceKey.location().getPath().replace('_', ' ') + " found").withStyle(ChatFormatting.RED));
                        }
                    } else if (isTag) {
                        foundResource = true;
                        int searchRadius = maxDistance * 2;
                        BlockPos playerPos = player.blockPosition();
                        HolderSet<Structure> structuresInTag = serverLevel.registryAccess().registryOrThrow(Registries.STRUCTURE).getTag(structureTagKey).orElse(null);
                        if (structuresInTag != null) {
                            boolean foundAny = false;
                            for (Holder<Structure> structureHolder : structuresInTag) {
                                if (structureHolder.unwrapKey().isPresent()) {
                                    ResourceKey<Structure> key = structureHolder.unwrapKey().get();
                                    TagKey<Structure> structureTypeTag = TagKey.create(Registries.STRUCTURE, key.location());
                                    BlockPos nearestPos = serverLevel.findNearestMapStructure(structureTypeTag, playerPos, searchRadius, false);
                                    if (nearestPos != null) {
                                        foundAny = true;
                                        String structureName = key.location().getPath().replace('_', ' ');

                                        int nearestX = nearestPos.getX();
                                        int nearestZ = nearestPos.getZ();
                                        int playerX = (int) player.getX();
                                        int playerZ = (int) player.getZ();
                                        int distanceFromStructure = Math.abs(nearestX - playerX) + Math.abs(nearestZ - playerZ);
                                        if (distanceFromStructure <= searchRadius) {
                                            BeyonderUtil.useSpirituality(player, maxDistance * 4);
                                            player.sendSystemMessage(Component.literal("Found " + structureName + " near: ").withStyle(ChatFormatting.LIGHT_PURPLE).append(Component.literal("X: " + nearestX + " Z: " + nearestZ).withStyle(ChatFormatting.GREEN)));
                                        } else {
                                            player.sendSystemMessage(Component.literal("No " + structureResourceKey.location().getPath().replace('_', ' ') + " found").withStyle(ChatFormatting.RED));
                                        }
                                        break;
                                    }
                                }
                            }
                            if (!foundAny) {
                                player.sendSystemMessage(Component.literal("No structures from tag " + structureTagKey.location().getPath().replace('_', ' ') + " found within " + searchRadius + " blocks").withStyle(ChatFormatting.RED));
                            }
                        }
                    } else {
                        boolean structureFound = false;
                        boolean actuallyFoundStructure = false;
                        String searchName = resourceLocation.getPath().toLowerCase(Locale.ROOT);
                        for (Map.Entry<ResourceKey<Structure>, Structure> entry : structureRegistry.entrySet()) {
                            ResourceKey<Structure> key = entry.getKey();
                            String structurePath = key.location().getPath().toLowerCase(Locale.ROOT);
                            if (structurePath.contains(searchName)) {
                                foundResource = true;
                                structureFound = true;
                                BlockPos playerPos = player.blockPosition();
                                BlockPos nearestStructurePos = serverLevel.findNearestMapStructure(TagKey.create(Registries.STRUCTURE, key.location()), playerPos, maxDistance * 2, false);
                                if (nearestStructurePos != null) {
                                    int nearestX = nearestStructurePos.getX();
                                    int nearestZ = nearestStructurePos.getZ();
                                    String structureName = key.location().getPath().replace('_', ' ');
                                    int playerX = (int) player.getX();
                                    int playerZ = (int) player.getZ();
                                    int distanceFromStructure = Math.abs(nearestX - playerX) + Math.abs(nearestZ - playerZ);
                                    int searchRadius = maxDistance * 2;
                                    if (distanceFromStructure <= searchRadius) {
                                        BeyonderUtil.useSpirituality(player, maxDistance * 4);
                                        actuallyFoundStructure = true;
                                        player.sendSystemMessage(Component.literal("Found " + structureName + " near: ").withStyle(ChatFormatting.LIGHT_PURPLE).append(Component.literal("X: " + nearestX + " Z: " + nearestZ).withStyle(ChatFormatting.GREEN)));
                                    } else {
                                        player.sendSystemMessage(Component.literal("No " + structureResourceKey.location().getPath().replace('_', ' ') + " found").withStyle(ChatFormatting.RED));
                                    }
                                    break;
                                }
                            }
                        }
                        if (structureFound && !actuallyFoundStructure) {
                            player.sendSystemMessage(Component.literal("No structure in range").withStyle(ChatFormatting.RED));
                        }
                    }
                }
                if (!foundResource) {
                    player.sendSystemMessage(Component.literal("No divination target found with " + message).withStyle(ChatFormatting.RED));
                } else {
                    BeyonderUtil.useSpirituality(player, maxDistance * 2);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            event.setCanceled(true);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Use in order to gauge the danger around you, with the more damage this item takes, the higher the danger level.").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GOLD));
        tooltipComponents.add(Component.literal("Type in a biome, structure, entity name, or block to get it's location.").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
        tooltipComponents.add(Component.literal("You can also type in a player's name followed by either (sequence, location, inventory, luck, misfortune, health, or pathway) to get that data").withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.BOLD));
        tooltipComponents.add(Component.literal("Be warned, if you try to divine information about a player who is many sequences above you, they might know.").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}