package net.swimmingtuna.lotm.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.client.Configs;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.networking.LOTMNetworkHandler;
import net.swimmingtuna.lotm.networking.packet.ClearAbilitiesS2C;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.ClientData.ClientAbilitiesData;
import net.swimmingtuna.lotm.world.worlddata.Faction.FactionData;
import net.swimmingtuna.lotm.world.worlddata.Faction.FactionData.FactionRank;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static net.swimmingtuna.lotm.commands.AbilityRegisterCommand.REGISTERED_ABILITIES_KEY;


public class BeyonderCommand {
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_BEYONDER_CLASS = new DynamicCommandExceptionType(arg1 -> Component.translatable("argument.lotm.beyonder_class.id.invalid", arg1));

    public static void register(CommandBuildContext buildContext, CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("beyonder")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("pathway", BeyonderClassArgument.beyonderClass())
                        .then(Commands.argument("sequence", IntegerArgumentType.integer(0, 9))
                                .executes(context -> {
                                    BeyonderClass result = BeyonderClassArgument.getBeyonderClass(context, "pathway");
                                    int level = IntegerArgumentType.getInteger(context, "sequence");
                                    if (result == null) {
                                        throw ERROR_UNKNOWN_BEYONDER_CLASS.create(context.getInput());
                                    }
                                    for (int i = 0; i < context.getSource().getPlayerOrException().getInventory().getContainerSize(); i++) {
                                        ItemStack stack = context.getSource().getPlayerOrException().getInventory().getItem(i);
                                        if (!stack.isEmpty()) {
                                            context.getSource().getPlayerOrException().getCooldowns().removeCooldown(stack.getItem());
                                        }
                                    }
                                    BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(context.getSource().getPlayerOrException());
                                    if (result != holder.getCurrentClass()) {
                                        Player player = context.getSource().getPlayerOrException();
                                        ScaleData scaleData = ScaleTypes.BASE.getScaleData(player);
                                        scaleData.setScale(1);
                                        Abilities playerAbilities = player.getAbilities();
                                        player.onUpdateAbilities();
                                        if (player instanceof ServerPlayer serverPlayer) {
                                            serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(playerAbilities));
                                            LOTMNetworkHandler.sendToPlayer(new ClearAbilitiesS2C(), serverPlayer);
                                            ClientAbilitiesData.clearAbilities();
                                        }
                                        CompoundTag persistentData = player.getPersistentData();
                                        if (persistentData.contains(REGISTERED_ABILITIES_KEY)) {
                                            persistentData.remove(REGISTERED_ABILITIES_KEY);
                                        }
                                    }
                                    holder.setPathwayAndSequence(result, level);

                                    String sequenceName = result.sequenceNames().get(level);
                                    context.getSource().getPlayerOrException().sendSystemMessage(Component.translatable("item.lotm.beholder_potion.alert", sequenceName)
                                            .withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.BOLD));
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("remove")
                        .executes(context -> {
                            Player player = context.getSource().getPlayerOrException();
                            BeyonderHolder holder = BeyonderHolderAttacher.getHolderUnwrap(player);
                            ScaleData scaleData = ScaleTypes.BASE.getScaleData(player);
                            holder.removePathway();
                            BeyonderUtil.removeTags(context.getSource().getPlayerOrException());
                            scaleData.setScale(1);
                            Abilities playerAbilities = player.getAbilities();
                            playerAbilities.setFlyingSpeed(0.05F);
                            playerAbilities.setWalkingSpeed(0.1F);
                            player.onUpdateAbilities();
                            if (player instanceof ServerPlayer serverPlayer) {
                                serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(playerAbilities));
                            }
                            return 1;
                        })
                )
                .then(Commands.literal("kit")
                        .then(Commands.literal("low")
                                .executes(context -> {
                                    Player player = context.getSource().getPlayerOrException();
                                    Inventory inventory = player.getInventory();
                                    player.setItemSlot(EquipmentSlot.HEAD, createArmorLow(Items.IRON_HELMET.getDefaultInstance()));
                                    player.setItemSlot(EquipmentSlot.CHEST, createArmorLow(Items.IRON_CHESTPLATE.getDefaultInstance()));
                                    player.setItemSlot(EquipmentSlot.LEGS, createArmorLow(Items.IRON_LEGGINGS.getDefaultInstance()));
                                    player.setItemSlot(EquipmentSlot.FEET, createArmorLow(Items.IRON_BOOTS.getDefaultInstance()));
                                    inventory.setItem(findClosestEmptySlot(player), createSwordLow(Items.IRON_SWORD.getDefaultInstance()));
                                    return 1;
                                }))
                        .then(Commands.literal("mid")
                                .executes(context -> {
                                    Player player = context.getSource().getPlayerOrException();
                                    Inventory inventory = player.getInventory();
                                    player.setItemSlot(EquipmentSlot.HEAD, createArmorMid(Items.DIAMOND_HELMET.getDefaultInstance()));
                                    player.setItemSlot(EquipmentSlot.CHEST, createArmorMid(Items.DIAMOND_CHESTPLATE.getDefaultInstance()));
                                    player.setItemSlot(EquipmentSlot.LEGS, createArmorMid(Items.DIAMOND_LEGGINGS.getDefaultInstance()));
                                    player.setItemSlot(EquipmentSlot.FEET, createArmorMid(Items.DIAMOND_BOOTS.getDefaultInstance()));
                                    inventory.setItem(findClosestEmptySlot(player), createSwordMid(Items.DIAMOND_SWORD.getDefaultInstance()));
                                    return 1;
                                }))
                        .then(Commands.literal("high")
                                .executes(context -> {
                                    Player player = context.getSource().getPlayerOrException();
                                    Inventory inventory = player.getInventory();
                                    player.setItemSlot(EquipmentSlot.HEAD, createArmorHigh(Items.NETHERITE_HELMET.getDefaultInstance()));
                                    player.setItemSlot(EquipmentSlot.CHEST, createArmorHigh(Items.NETHERITE_CHESTPLATE.getDefaultInstance()));
                                    player.setItemSlot(EquipmentSlot.LEGS, createArmorHigh(Items.NETHERITE_LEGGINGS.getDefaultInstance()));
                                    player.setItemSlot(EquipmentSlot.FEET, createArmorHigh(Items.NETHERITE_BOOTS.getDefaultInstance()));
                                    inventory.setItem(findClosestEmptySlot(player), createSwordHigh(Items.NETHERITE_SWORD.getDefaultInstance()));
                                    return 1;
                                }))
                )
                // Faction commands
                .then(Commands.literal("faction")
                        .then(Commands.literal("create")
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .executes(context -> {
                                            if (!Configs.COMMON.factionsEnabled.get()) {
                                                context.getSource().sendFailure(
                                                        Component.literal("This command doesn't work as factions are disabled.")
                                                                .withStyle(ChatFormatting.RED));
                                                return 0;
                                            }

                                            String factionName = StringArgumentType.getString(context, "name");
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ServerLevel level = context.getSource().getLevel();
                                            FactionData factionData = FactionData.getInstance(level);

                                            if (factionData.createFaction(factionName, player.getUUID())) {
                                                context.getSource().sendSuccess(() ->
                                                        Component.literal("Successfully created faction: " + factionName)
                                                                .withStyle(ChatFormatting.GREEN), false);
                                            } else {
                                                context.getSource().sendFailure(
                                                        Component.literal("Failed to create faction. You may already be in a faction or the name is taken.")
                                                                .withStyle(ChatFormatting.RED));
                                            }
                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("disband")
                                .executes(context -> {
                                    if (!Configs.COMMON.factionsEnabled.get()) {
                                        context.getSource().sendFailure(
                                                Component.literal("This command doesn't work as factions are disabled.")
                                                        .withStyle(ChatFormatting.RED));
                                        return 0;
                                    }

                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    ServerLevel level = context.getSource().getLevel();
                                    FactionData factionData = FactionData.getInstance(level);

                                    String factionName = factionData.getPlayerFaction(player.getUUID());
                                    if (factionName == null) {
                                        context.getSource().sendFailure(
                                                Component.literal("You are not in a faction.")
                                                        .withStyle(ChatFormatting.RED));
                                        return 0;
                                    }

                                    if (factionData.disbandFaction(factionName, player.getUUID())) {
                                        context.getSource().sendSuccess(() ->
                                                Component.literal("Successfully disbanded faction: " + factionName)
                                                        .withStyle(ChatFormatting.GREEN), false);
                                    } else {
                                        context.getSource().sendFailure(
                                                Component.literal("Failed to disband faction. You must be the leader.")
                                                        .withStyle(ChatFormatting.RED));
                                    }
                                    return 1;
                                })
                        )
                        .then(Commands.literal("invite")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(context -> {
                                            if (!Configs.COMMON.factionsEnabled.get()) {
                                                context.getSource().sendFailure(
                                                        Component.literal("This command doesn't work as factions are disabled.")
                                                                .withStyle(ChatFormatting.RED));
                                                return 0;
                                            }

                                            ServerPlayer leader = context.getSource().getPlayerOrException();
                                            ServerPlayer invitee = EntityArgument.getPlayer(context, "player");
                                            ServerLevel level = context.getSource().getLevel();
                                            FactionData factionData = FactionData.getInstance(level);

                                            String factionName = factionData.getPlayerFaction(leader.getUUID());
                                            if (factionName == null) {
                                                context.getSource().sendFailure(
                                                        Component.literal("You are not in a faction.")
                                                                .withStyle(ChatFormatting.RED));
                                                return 0;
                                            }

                                            if (factionData.invitePlayer(factionName, leader.getUUID(), invitee.getUUID())) {
                                                context.getSource().sendSuccess(() ->
                                                        Component.literal("Successfully invited " + invitee.getName().getString() + " to faction: " + factionName)
                                                                .withStyle(ChatFormatting.GREEN), false);
                                                invitee.sendSystemMessage(
                                                        Component.literal("You have been invited to join faction: " + factionName)
                                                                .withStyle(ChatFormatting.YELLOW));
                                            } else {
                                                context.getSource().sendFailure(
                                                        Component.literal("Failed to invite player. They may already be in a faction or you don't have permission.")
                                                                .withStyle(ChatFormatting.RED));
                                            }
                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("promote")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("rank", StringArgumentType.string())
                                                .executes(context -> {
                                                    if (!Configs.COMMON.factionsEnabled.get()) {
                                                        context.getSource().sendFailure(
                                                                Component.literal("This command doesn't work as factions are disabled.")
                                                                        .withStyle(ChatFormatting.RED));
                                                        return 0;
                                                    }

                                                    ServerPlayer promoter = context.getSource().getPlayerOrException();
                                                    ServerPlayer target = EntityArgument.getPlayer(context, "player");
                                                    String rankString = StringArgumentType.getString(context, "rank");
                                                    ServerLevel level = context.getSource().getLevel();
                                                    FactionData factionData = FactionData.getInstance(level);

                                                    String factionName = factionData.getPlayerFaction(promoter.getUUID());
                                                    if (factionName == null) {
                                                        context.getSource().sendFailure(
                                                                Component.literal("You are not in a faction.")
                                                                        .withStyle(ChatFormatting.RED));
                                                        return 0;
                                                    }

                                                    // Check if target is in the same faction
                                                    String targetFaction = factionData.getPlayerFaction(target.getUUID());
                                                    if (!factionName.equals(targetFaction)) {
                                                        context.getSource().sendFailure(
                                                                Component.literal("Target player is not in your faction.")
                                                                        .withStyle(ChatFormatting.RED));
                                                        return 0;
                                                    }

                                                    FactionRank newRank = FactionRank.fromString(rankString);
                                                    if (newRank == null) {
                                                        context.getSource().sendFailure(
                                                                Component.literal("Invalid rank. Valid ranks: Leader, Vice-Leader, Officer, Member")
                                                                        .withStyle(ChatFormatting.RED));
                                                        return 0;
                                                    }

                                                    if (factionData.setPlayerRank(factionName, promoter.getUUID(), target.getUUID(), newRank)) {
                                                        context.getSource().sendSuccess(() ->
                                                                Component.literal("Successfully set " + target.getName().getString() + "'s rank to " + newRank.getDisplayName())
                                                                        .withStyle(ChatFormatting.GREEN), false);
                                                        target.sendSystemMessage(
                                                                Component.literal("Your rank in faction " + factionName + " has been set to " + newRank.getDisplayName())
                                                                        .withStyle(ChatFormatting.YELLOW));
                                                    } else {
                                                        context.getSource().sendFailure(
                                                                Component.literal("Failed to set rank. You may not have permission or the operation is invalid.")
                                                                        .withStyle(ChatFormatting.RED));
                                                    }
                                                    return 1;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("kick")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(context -> {
                                            if (!Configs.COMMON.factionsEnabled.get()) {
                                                context.getSource().sendFailure(
                                                        Component.literal("This command doesn't work as factions are disabled.")
                                                                .withStyle(ChatFormatting.RED));
                                                return 0;
                                            }

                                            ServerPlayer kicker = context.getSource().getPlayerOrException();
                                            ServerPlayer target = EntityArgument.getPlayer(context, "player");
                                            ServerLevel level = context.getSource().getLevel();
                                            FactionData factionData = FactionData.getInstance(level);

                                            String factionName = factionData.getPlayerFaction(kicker.getUUID());
                                            if (factionName == null) {
                                                context.getSource().sendFailure(
                                                        Component.literal("You are not in a faction.")
                                                                .withStyle(ChatFormatting.RED));
                                                return 0;
                                            }

                                            // Check if target is in the same faction
                                            String targetFaction = factionData.getPlayerFaction(target.getUUID());
                                            if (!factionName.equals(targetFaction)) {
                                                context.getSource().sendFailure(
                                                        Component.literal("Target player is not in your faction.")
                                                                .withStyle(ChatFormatting.RED));
                                                return 0;
                                            }

                                            if (factionData.removePlayer(factionName, kicker.getUUID(), target.getUUID())) {
                                                context.getSource().sendSuccess(() ->
                                                        Component.literal("Successfully removed " + target.getName().getString() + " from faction: " + factionName)
                                                                .withStyle(ChatFormatting.GREEN), false);
                                                target.sendSystemMessage(
                                                        Component.literal("You have been removed from faction: " + factionName)
                                                                .withStyle(ChatFormatting.RED));
                                            } else {
                                                context.getSource().sendFailure(
                                                        Component.literal("Failed to remove player. You may not have permission.")
                                                                .withStyle(ChatFormatting.RED));
                                            }
                                            return 1;
                                        })
                                )
                        )

                        .then(Commands.literal("claim")
                                .executes(context -> {
                                    return executeClaimCommand(context, 1);
                                })
                                .then(Commands.argument("radius", IntegerArgumentType.integer(1, 10))
                                        .executes(context -> {
                                            int radius = IntegerArgumentType.getInteger(context, "radius");
                                            return executeClaimCommand(context, radius);
                                        })
                                )
                        )
                        .then(Commands.literal("unclaim")
                                .executes(context -> {
                                    if (!Configs.COMMON.factionsEnabled.get()) {
                                        context.getSource().sendFailure(
                                                Component.literal("This command doesn't work as factions are disabled.")
                                                        .withStyle(ChatFormatting.RED));
                                        return 0;
                                    }

                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    ServerLevel level = context.getSource().getLevel();
                                    FactionData factionData = FactionData.getInstance(level);

                                    ChunkPos chunkPos = new ChunkPos(player.blockPosition());
                                    if (factionData.unclaimChunk(chunkPos, level.dimension(), player.getUUID())) {
                                        context.getSource().sendSuccess(() ->
                                                Component.literal("Successfully unclaimed chunk at " + chunkPos.x + ", " + chunkPos.z)
                                                        .withStyle(ChatFormatting.GREEN), false);
                                    } else {
                                        context.getSource().sendFailure(
                                                Component.literal("Failed to unclaim chunk. This chunk may not belong to your faction or you lack permission.")
                                                        .withStyle(ChatFormatting.RED));
                                    }
                                    return 1;
                                })
                        )
                        .then(Commands.literal("info")
                                .executes(context -> {
                                    if (!Configs.COMMON.factionsEnabled.get()) {
                                        context.getSource().sendFailure(
                                                Component.literal("This command doesn't work as factions are disabled.")
                                                        .withStyle(ChatFormatting.RED));
                                        return 0;
                                    }

                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    ServerLevel level = context.getSource().getLevel();
                                    FactionData factionData = FactionData.getInstance(level);

                                    String factionName = factionData.getPlayerFaction(player.getUUID());
                                    if (factionName == null) {
                                        context.getSource().sendFailure(
                                                Component.literal("You are not in a faction.")
                                                        .withStyle(ChatFormatting.RED));
                                        return 0;
                                    }

                                    FactionData.Faction faction = factionData.getFaction(factionName);
                                    if (faction != null) {
                                        StringBuilder info = new StringBuilder();
                                        info.append("=== Faction Info ===\n");
                                        info.append("Name: ").append(faction.getName()).append("\n");

                                        // Get leader name
                                        ServerPlayer leaderPlayer = level.getServer().getPlayerList().getPlayer(faction.getLeader());
                                        String leaderName = leaderPlayer != null ? leaderPlayer.getName().getString() : "Unknown";
                                        info.append("Leader: ").append(leaderName).append("\n");

                                        // Count members by rank
                                        Map<UUID, FactionRank> allMembers = faction.getAllMembersWithRanks();
                                        int viceLeaderCount = 0, officerCount = 0, memberCount = 0;

                                        for (FactionRank rank : allMembers.values()) {
                                            switch (rank) {
                                                case VICE_LEADER -> viceLeaderCount++;
                                                case OFFICER -> officerCount++;
                                                case MEMBER -> memberCount++;
                                            }
                                        }

                                        info.append("Vice-Leaders: ").append(viceLeaderCount).append("\n");
                                        info.append("Officers: ").append(officerCount).append("\n");
                                        info.append("Members: ").append(memberCount).append("\n");
                                        info.append("Total Members: ").append(allMembers.size());

                                        context.getSource().sendSuccess(() ->
                                                Component.literal(info.toString())
                                                        .withStyle(ChatFormatting.AQUA), false);
                                    }
                                    return 1;
                                })
                        )
                        .then(Commands.literal("members")
                                .executes(context -> {
                                    if (!Configs.COMMON.factionsEnabled.get()) {
                                        context.getSource().sendFailure(
                                                Component.literal("This command doesn't work as factions are disabled.")
                                                        .withStyle(ChatFormatting.RED));
                                        return 0;
                                    }

                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    ServerLevel level = context.getSource().getLevel();
                                    FactionData factionData = FactionData.getInstance(level);

                                    String factionName = factionData.getPlayerFaction(player.getUUID());
                                    if (factionName == null) {
                                        context.getSource().sendFailure(
                                                Component.literal("You are not in a faction.")
                                                        .withStyle(ChatFormatting.RED));
                                        return 0;
                                    }

                                    FactionData.Faction faction = factionData.getFaction(factionName);
                                    if (faction != null) {
                                        StringBuilder membersList = new StringBuilder();
                                        membersList.append("=== Faction Members ===\n");

                                        Map<UUID, FactionRank> allMembers = faction.getAllMembersWithRanks();

                                        // Group by rank for organized display
                                        for (FactionRank rank : FactionRank.values()) {
                                            boolean hasRank = false;
                                            StringBuilder rankSection = new StringBuilder();

                                            for (Map.Entry<UUID, FactionRank> entry : allMembers.entrySet()) {
                                                if (entry.getValue() == rank) {
                                                    if (!hasRank) {
                                                        rankSection.append(rank.getDisplayName()).append("s:\n");
                                                        hasRank = true;
                                                    }
                                                    ServerPlayer memberPlayer = level.getServer().getPlayerList().getPlayer(entry.getKey());
                                                    String memberName = memberPlayer != null ? memberPlayer.getName().getString() : "Offline";
                                                    rankSection.append("  - ").append(memberName).append("\n");
                                                }
                                            }

                                            if (hasRank) {
                                                membersList.append(rankSection);
                                            }
                                        }

                                        context.getSource().sendSuccess(() ->
                                                Component.literal(membersList.toString())
                                                        .withStyle(ChatFormatting.YELLOW), false);
                                    }
                                    return 1;
                                })
                        )
                        .then(Commands.literal("here")
                                .executes(context -> {
                                    if (!Configs.COMMON.factionsEnabled.get()) {
                                        context.getSource().sendFailure(
                                                Component.literal("This command doesn't work as factions are disabled.")
                                                        .withStyle(ChatFormatting.RED));
                                        return 0;
                                    }

                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    ServerLevel level = context.getSource().getLevel();
                                    FactionData factionData = FactionData.getInstance(level);

                                    ChunkPos chunkPos = new ChunkPos(player.blockPosition());
                                    String owner = factionData.getChunkOwner(chunkPos, level.dimension());

                                    if (owner != null) {
                                        context.getSource().sendSuccess(() ->
                                                Component.literal("This chunk is claimed by faction: " + owner)
                                                        .withStyle(ChatFormatting.YELLOW), false);
                                    } else {
                                        context.getSource().sendSuccess(() ->
                                                Component.literal("This chunk is unclaimed.")
                                                        .withStyle(ChatFormatting.GREEN), false);
                                    }
                                    return 1;
                                })
                        )
                        .then(Commands.literal("bypass")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("minutes", IntegerArgumentType.integer(1, 1440))
                                                .executes(context -> {
                                                    if (!Configs.COMMON.factionsEnabled.get()) {
                                                        context.getSource().sendFailure(
                                                                Component.literal("This command doesn't work as factions are disabled.")
                                                                        .withStyle(ChatFormatting.RED));
                                                        return 0;
                                                    }

                                                    ServerPlayer requester = context.getSource().getPlayerOrException();
                                                    ServerPlayer target = EntityArgument.getPlayer(context, "player");
                                                    int minutes = IntegerArgumentType.getInteger(context, "minutes");
                                                    ServerLevel level = context.getSource().getLevel();
                                                    FactionData factionData = FactionData.getInstance(level);

                                                    String factionName = factionData.getPlayerFaction(requester.getUUID());
                                                    if (factionName == null) {
                                                        context.getSource().sendFailure(
                                                                Component.literal("You are not in a faction.")
                                                                        .withStyle(ChatFormatting.RED));
                                                        return 0;
                                                    }

                                                    if (factionData.grantProtectionBypass(factionName, requester.getUUID(), target, minutes)) {
                                                        context.getSource().sendSuccess(() ->
                                                                Component.literal("Successfully granted " + minutes + " minutes of faction protection bypass to " + target.getName().getString())
                                                                        .withStyle(ChatFormatting.GREEN), false);
                                                        target.sendSystemMessage(
                                                                Component.literal("You have been granted " + minutes + " minutes of faction protection bypass")
                                                                        .withStyle(ChatFormatting.YELLOW));
                                                    } else {
                                                        context.getSource().sendFailure(
                                                                Component.literal("Failed to grant protection bypass. You may not have permission, insufficient power, or the target is not in your faction.")
                                                                        .withStyle(ChatFormatting.RED));
                                                    }
                                                    return 1;
                                                })
                                        )
                                )
                        )
                )
        );
    }

    private static int executeClaimCommand(CommandContext<CommandSourceStack> context, int radius) {
        if (!Configs.COMMON.factionsEnabled.get()) {
            context.getSource().sendFailure(
                    Component.literal("This command doesn't work as factions are disabled.")
                            .withStyle(ChatFormatting.RED));
            return 0;
        }

        try {
            ServerPlayer player = context.getSource().getPlayerOrException();
            ServerLevel level = context.getSource().getLevel();
            FactionData factionData = FactionData.getInstance(level);

            ChunkPos centerChunk = new ChunkPos(player.blockPosition());
            FactionData.ClaimResult result = factionData.claimChunksInRadius(centerChunk, level.dimension(), player.getUUID(), radius);

            if (result.isSuccess()) {
                //summonClaimParticles(level, result.getClaimedChunks());
                if (radius == 1) {
                    context.getSource().sendSuccess(() ->
                            Component.literal("Successfully claimed chunk at " + centerChunk.x + ", " + centerChunk.z)
                                    .withStyle(ChatFormatting.GREEN), false);
                } else {
                    int totalArea = (2 * radius - 1) * (2 * radius - 1);
                    context.getSource().sendSuccess(() ->
                            Component.literal("Successfully claimed " + result.getClaimedCount() +
                                            "/" + totalArea + " chunks in a " + radius + "x" + radius +
                                            " area around " + centerChunk.x + ", " + centerChunk.z)
                                    .withStyle(ChatFormatting.GREEN), false);
                }
            } else {
                String errorMessage = result.getMessage();
                if (result.getConflictCount() > 0) {
                    errorMessage += " (" + result.getConflictCount() + " chunks already claimed by others)";
                }
                context.getSource().sendFailure(
                        Component.literal(errorMessage)
                                .withStyle(ChatFormatting.RED));
            }
        } catch (Exception e) {
            context.getSource().sendFailure(
                    Component.literal("An error occurred while claiming chunks.")
                            .withStyle(ChatFormatting.RED));
            return 0;
        }

        return 1;
    }

    private static void summonClaimParticles(ServerLevel level, Set<ChunkPos> claimedChunks) {
        if (claimedChunks == null || claimedChunks.isEmpty()) {
            return;
        }
        for (ChunkPos chunkPos : claimedChunks) {
            createChunkBorderParticles(level, chunkPos);
        }
    }

    private static void createChunkBorderParticles(ServerLevel level, ChunkPos chunkPos) {
        int startX = chunkPos.x * 16;
        int startZ = chunkPos.z * 16;
        int endX = startX + 16;
        int endZ = startZ + 16;
        int groundY = level.getHeight(Heightmap.Types.WORLD_SURFACE, startX + 8, startZ + 8);
        ParticleOptions particles = ParticleTypes.HAPPY_VILLAGER;
        double particleHeight = 3.0;
        double particleSpacing = 0.5;
        createParticleLine(level, startX, startZ, endX, startZ, groundY, particleHeight, particleSpacing, particles);
        createParticleLine(level, startX, endZ, endX, endZ, groundY, particleHeight, particleSpacing, particles);
        createParticleLine(level, startX, startZ, startX, endZ, groundY, particleHeight, particleSpacing, particles);
        createParticleLine(level, endX, startZ, endX, endZ, groundY, particleHeight, particleSpacing, particles);
    }

    private static void createParticleLine(ServerLevel level, double x1, double z1, double x2, double z2, int groundY, double height, double spacing, ParticleOptions particles) {
        double deltaX = x2 - x1;
        double deltaZ = z2 - z1;
        double lineLength = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        if (lineLength == 0) {
            return;
        }
        double stepX = (deltaX / lineLength) * spacing;
        double stepZ = (deltaZ / lineLength) * spacing;
        int steps = (int) Math.ceil(lineLength / spacing);
        for (int step = 0; step <= steps; step++) {
            double x = x1 + stepX * step;
            double z = z1 + stepZ * step;
            for (double y = groundY; y < groundY + height; y += 0.3) {
                level.sendParticles(particles, x, y, z, 1, 0.1, 0.0, 0.1, 0.0);
            }
        }
    }

    private static ItemStack createSwordLow(ItemStack sword) {
        sword.enchant(Enchantments.SHARPNESS, 1);
        sword.enchant(Enchantments.UNBREAKING, 3);
        return sword;
    }

    private static ItemStack createSwordMid(ItemStack sword) {
        sword.enchant(Enchantments.SHARPNESS, 3);
        sword.enchant(Enchantments.UNBREAKING, 3);
        return sword;
    }

    private static ItemStack createSwordHigh(ItemStack sword) {
        sword.enchant(Enchantments.SHARPNESS, 5);
        sword.enchant(Enchantments.UNBREAKING, 3);
        return sword;
    }

    private static ItemStack createArmorLow(ItemStack armor) {
        armor.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 1);
        armor.enchant(Enchantments.UNBREAKING, 1);
        return armor;
    }

    private static ItemStack createArmorMid(ItemStack armor) {
        armor.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 3);
        armor.enchant(Enchantments.UNBREAKING, 2);
        return armor;
    }

    private static ItemStack createArmorHigh(ItemStack armor) {
        armor.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 4);
        armor.enchant(Enchantments.UNBREAKING, 3);
        return armor;
    }

    public static int findClosestEmptySlot(Player player) {
        Inventory inventory = player.getInventory();
        int selectedSlot = player.getInventory().selected;
        if (inventory.getItem(selectedSlot).isEmpty()) {
            return selectedSlot;
        }
        for (int distance = 1; distance < 9; distance++) {
            int rightSlot = (selectedSlot + distance) % 9;
            if (inventory.getItem(rightSlot).isEmpty()) {
                return rightSlot;
            }
            int leftSlot = (selectedSlot - distance + 9) % 9;
            if (inventory.getItem(leftSlot).isEmpty()) {
                return leftSlot;
            }
        }
        for (int i = 9; i < 36; i++) {
            if (inventory.getItem(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }
}