package net.swimmingtuna.lotm.world.worlddata.Faction;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.swimmingtuna.lotm.client.Configs;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.EFunctions;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.EventManager;
import net.swimmingtuna.lotm.util.BeyonderUtil;

import java.util.*;

public class FactionData extends SavedData {
    private static final String DATA_NAME = "faction_data";

    // Maps faction name -> faction info
    private final Map<String, Faction> factions = new HashMap<>();

    // Maps player UUID -> faction name
    private final Map<UUID, String> playerFactions = new HashMap<>();

    // Maps chunk position + dimension -> faction name
    private final Map<ChunkClaimKey, String> claimedChunks = new HashMap<>();

    // Power regeneration rates by sequence
    private static final Map<Integer, Integer> SEQUENCE_POWER_RATES = Map.of(
            0, 40,  // Sequence 0: 40 per minute
            1, 25,  // Sequence 1: 25 per minute
            2, 20,  // Sequence 2: 20 per minute
            3, 12,  // Sequence 3: 12 per minute
            4, 10,  // Sequence 4: 10 per minute
            5, 7,   // Sequence 5: 7 per minute
            6, 6,   // Sequence 6: 6 per minute
            7, 5,   // Sequence 7: 5 per minute
            8, 3,   // Sequence 8: 3 per minute
            9, 2    // Sequence 9: 2 per minute
    );
    private static final int DEFAULT_POWER_RATE = 1;

    private static final Map<Integer, Integer> IGNORE_PROTECTION_POWER_COSTS = Map.of(
            0, 10000,  // Sequence 0: 10000 per minute
            1, 6000,   // Sequence 1: 6000 per minute
            2, 3000,   // Sequence 2: 3000 per minute
            3, 2000,   // Sequence 3: 2000 per minute
            4, 1000,   // Sequence 4: 1000 per minute
            5, 400,    // Sequence 5: 400 per minute
            6, 300,    // Sequence 6: 300 per minute
            7, 150,    // Sequence 7: 150 per minute
            8, 100,    // Sequence 8: 100 per minute
            9, 50      // Sequence 9: 50 per minute
    );
    private static final int DEFAULT_IGNORE_PROTECTION_POWER_COST = 25; // Default for unsequenced players

    public static FactionData create() {
        return new FactionData();
    }

    public static FactionData load(CompoundTag nbt) {
        FactionData data = new FactionData();

        // Load factions
        CompoundTag factionsNbt = nbt.getCompound("factions");
        for (String factionName : factionsNbt.getAllKeys()) {
            CompoundTag factionNbt = factionsNbt.getCompound(factionName);
            Faction faction = Faction.fromNBT(factionNbt);
            data.factions.put(factionName, faction);
        }

        // Load player-faction mappings
        CompoundTag playerFactionsNbt = nbt.getCompound("playerFactions");
        for (String uuidString : playerFactionsNbt.getAllKeys()) {
            UUID playerUuid = UUID.fromString(uuidString);
            String factionName = playerFactionsNbt.getString(uuidString);
            data.playerFactions.put(playerUuid, factionName);
        }

        // Load chunk claims
        ListTag claimsNbt = nbt.getList("claimedChunks", 10); // 10 = CompoundTag
        for (int i = 0; i < claimsNbt.size(); i++) {
            CompoundTag claimNbt = claimsNbt.getCompound(i);
            ChunkClaimKey key = ChunkClaimKey.fromNBT(claimNbt);
            String factionName = claimNbt.getString("faction");
            data.claimedChunks.put(key, factionName);
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        // Save factions
        CompoundTag factionsNbt = new CompoundTag();
        for (Map.Entry<String, Faction> entry : factions.entrySet()) {
            factionsNbt.put(entry.getKey(), entry.getValue().toNBT());
        }
        nbt.put("factions", factionsNbt);

        // Save player-faction mappings
        CompoundTag playerFactionsNbt = new CompoundTag();
        for (Map.Entry<UUID, String> entry : playerFactions.entrySet()) {
            playerFactionsNbt.putString(entry.getKey().toString(), entry.getValue());
        }
        nbt.put("playerFactions", playerFactionsNbt);

        // Save chunk claims
        ListTag claimsNbt = new ListTag();
        for (Map.Entry<ChunkClaimKey, String> entry : claimedChunks.entrySet()) {
            CompoundTag claimNbt = entry.getKey().toNBT();
            claimNbt.putString("faction", entry.getValue());
            claimsNbt.add(claimNbt);
        }
        nbt.put("claimedChunks", claimsNbt);

        return nbt;
    }

    // Power management methods
    public void updateFactionPower(ServerLevel level) {
        for (Faction faction : factions.values()) {
            int powerGain = calculateFactionPowerGain(faction, level);
            faction.addPower(powerGain);
        }
        setDirty();
    }

    private int calculateFactionPowerGain(Faction faction, ServerLevel level) {
        int totalPowerGain = 0;

        for (UUID memberUuid : faction.getAllMembers()) {
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(memberUuid);
            if (player != null && player.isAlive()) {
                // Player is online and alive, calculate their power contribution
                int sequence = getPlayerSequence(player);
                int powerRate = SEQUENCE_POWER_RATES.getOrDefault(sequence, DEFAULT_POWER_RATE);
                totalPowerGain += powerRate;
            } else {
                // Player is offline or dead, they still contribute 1 power per minute
                totalPowerGain += 1;
            }
        }

        return totalPowerGain;
    }

    // You'll need to implement this method based on your BeyonderUtil class
    private int getPlayerSequence(ServerPlayer player) {
        try {
            BeyonderUtil.getSequence(player);
            return BeyonderUtil.getSequence(player);
        } catch (Exception e) {
            return -1;
        }
    }

    public boolean spendFactionPower(String factionName, int amount) {
        Faction faction = factions.get(factionName);
        if (faction == null || faction.getPower() < amount) {
            return false;
        }

        faction.spendPower(amount);
        setDirty();
        return true;
    }

    public int getFactionPower(String factionName) {
        Faction faction = factions.get(factionName);
        return faction != null ? faction.getPower() : 0;
    }

    // Faction management methods
    public boolean createFaction(String name, UUID leader) {
        if (factions.containsKey(name)) {
            return false;
        }
        if (playerFactions.containsKey(leader)) {
            return false;
        }

        Faction faction = new Faction(name, leader);
        factions.put(name, faction);
        playerFactions.put(leader, name);
        setDirty();
        return true;
    }

    public boolean disbandFaction(String name, UUID player) {
        Faction faction = factions.get(name);
        if (faction == null || !faction.getLeader().equals(player)) {
            return false;
        }

        faction.getAllMembers().forEach(playerFactions::remove);
        playerFactions.remove(faction.getLeader());

        claimedChunks.entrySet().removeIf(entry -> entry.getValue().equals(name));

        factions.remove(name);
        setDirty();
        return true;
    }

    public boolean invitePlayer(String factionName, UUID inviter, UUID invitee) {
        Faction faction = factions.get(factionName);
        if (faction == null) {
            return false;
        }

        // Check if inviter has permission (Leader or Vice-Leader can invite)
        FactionRank inviterRank = faction.getPlayerRank(inviter);
        if (inviterRank != FactionRank.LEADER && inviterRank != FactionRank.VICE_LEADER) {
            return false; // Inviter doesn't have permission
        }

        if (playerFactions.containsKey(invitee)) {
            return false; // Player already in a faction
        }

        faction.addMember(invitee, FactionRank.MEMBER);
        playerFactions.put(invitee, factionName);
        setDirty();
        return true;
    }

    public boolean setPlayerRank(String factionName, UUID setter, UUID target, FactionRank newRank) {
        Faction faction = factions.get(factionName);
        if (faction == null) {
            return false; // Faction doesn't exist
        }

        FactionRank setterRank = faction.getPlayerRank(setter);
        FactionRank targetRank = faction.getPlayerRank(target);

        // Check permissions
        if (!canSetRank(setterRank, targetRank, newRank)) {
            return false;
        }

        // Can't demote yourself as leader unless promoting someone else to leader
        if (setter.equals(target) && setterRank == FactionRank.LEADER && newRank != FactionRank.LEADER) {
            return false;
        }

        // If setting someone to leader, demote current leader to vice-leader
        if (newRank == FactionRank.LEADER && setterRank == FactionRank.LEADER) {
            faction.setPlayerRank(setter, FactionRank.VICE_LEADER);
        }

        faction.setPlayerRank(target, newRank);
        setDirty();
        return true;
    }

    private boolean canSetRank(FactionRank setterRank, FactionRank targetRank, FactionRank newRank) {
        if (setterRank == null) return false; // Setter not in faction

        switch (setterRank) {
            case LEADER:
                return true; // Leaders can set anyone to any rank
            case VICE_LEADER:
                // Vice-leaders can promote/demote officers and members, but not other vice-leaders or leaders
                return targetRank != FactionRank.LEADER && targetRank != FactionRank.VICE_LEADER &&
                        newRank != FactionRank.LEADER && newRank != FactionRank.VICE_LEADER;
            case OFFICER:
                // Officers can only promote members to officers or demote officers to members
                return (targetRank == FactionRank.MEMBER && newRank == FactionRank.OFFICER) ||
                        (targetRank == FactionRank.OFFICER && newRank == FactionRank.MEMBER);
            case MEMBER:
                return false; // Members can't change ranks
        }
        return false;
    }

    public boolean removePlayer(String factionName, UUID remover, UUID target) {
        Faction faction = factions.get(factionName);
        if (faction == null) {
            return false; // Faction doesn't exist
        }

        if (target.equals(faction.getLeader())) {
            return false; // Can't remove the leader
        }

        FactionRank removerRank = faction.getPlayerRank(remover);
        FactionRank targetRank = faction.getPlayerRank(target);

        // Check permissions
        if (!canRemovePlayer(removerRank, targetRank)) {
            return false;
        }

        faction.removeMember(target);
        playerFactions.remove(target);
        setDirty();
        return true;
    }

    private boolean canRemovePlayer(FactionRank removerRank, FactionRank targetRank) {
        if (removerRank == null || targetRank == null) return false;

        switch (removerRank) {
            case LEADER:
                return targetRank != FactionRank.LEADER; // Can remove anyone except other leaders
            case VICE_LEADER:
                return targetRank == FactionRank.OFFICER || targetRank == FactionRank.MEMBER;
            case OFFICER:
                return targetRank == FactionRank.MEMBER;
            case MEMBER:
                return false; // Members can't remove anyone
        }
        return false;
    }

    public boolean claimChunk(ChunkPos chunkPos, ResourceKey<Level> dimension, UUID player) {
        String factionName = playerFactions.get(player);
        if (factionName == null) {
            return false; // Player not in a faction
        }

        Faction faction = factions.get(factionName);
        FactionRank playerRank = faction.getPlayerRank(player);

        // Only officers and above can claim chunks
        if (playerRank == FactionRank.MEMBER) {
            return false;
        }

        ChunkClaimKey key = new ChunkClaimKey(chunkPos.x, chunkPos.z, dimension);
        if (claimedChunks.containsKey(key)) {
            return false; // Chunk already claimed
        }

        claimedChunks.put(key, factionName);
        setDirty();
        return true;
    }

    public boolean unclaimChunk(ChunkPos chunkPos, ResourceKey<Level> dimension, UUID player) {
        String factionName = playerFactions.get(player);
        if (factionName == null) {
            return false; // Player not in a faction
        }

        Faction faction = factions.get(factionName);
        FactionRank playerRank = faction.getPlayerRank(player);

        // Only officers and above can unclaim chunks
        if (playerRank == FactionRank.MEMBER) {
            return false;
        }

        ChunkClaimKey key = new ChunkClaimKey(chunkPos.x, chunkPos.z, dimension);
        String claimingFaction = claimedChunks.get(key);
        if (!factionName.equals(claimingFaction)) {
            return false; // Chunk not claimed by player's faction
        }

        claimedChunks.remove(key);
        setDirty();
        return true;
    }

    // Getters
    public Faction getFaction(String name) {
        return factions.get(name);
    }

    public String getPlayerFaction(UUID player) {
        return playerFactions.get(player);
    }

    public FactionRank getPlayerRank(UUID player) {
        String factionName = playerFactions.get(player);
        if (factionName == null) return null;

        Faction faction = factions.get(factionName);
        return faction != null ? faction.getPlayerRank(player) : null;
    }

    public String getChunkOwner(ChunkPos chunkPos, ResourceKey<Level> dimension) {
        ChunkClaimKey key = new ChunkClaimKey(chunkPos.x, chunkPos.z, dimension);
        return claimedChunks.get(key);
    }

    public Set<String> getAllFactionNames() {
        return new HashSet<>(factions.keySet());
    }

    public static FactionData getInstance(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                FactionData::load,
                FactionData::create,
                DATA_NAME
        );
    }

    // Enums and Inner classes
    public enum FactionRank {
        LEADER("Leader"),
        VICE_LEADER("Vice-Leader"),
        OFFICER("Officer"),
        MEMBER("Member");

        private final String displayName;

        FactionRank(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static FactionRank fromString(String name) {
            for (FactionRank rank : values()) {
                if (rank.name().equalsIgnoreCase(name) || rank.displayName.equalsIgnoreCase(name)) {
                    return rank;
                }
            }
            return null;
        }
    }

    public static class Faction {
        private final String name;
        private UUID leader;
        private final Map<UUID, FactionRank> members; // Includes all members with their ranks
        private int power; // Faction power
        private long lastPowerUpdate; // Timestamp of last power update

        public Faction(String name, UUID leader) {
            this.name = name;
            this.leader = leader;
            this.members = new HashMap<>();
            this.members.put(leader, FactionRank.LEADER);
            this.power = 0; // Start with 0 power
            this.lastPowerUpdate = System.currentTimeMillis();
        }

        public void addMember(UUID player, FactionRank rank) {
            members.put(player, rank);
        }

        public void removeMember(UUID player) {
            members.remove(player);
        }

        public void setPlayerRank(UUID player, FactionRank rank) {
            if (rank == FactionRank.LEADER) {
                // Update leader reference
                this.leader = player;
            }
            members.put(player, rank);
        }

        public FactionRank getPlayerRank(UUID player) {
            return members.get(player);
        }

        public boolean isMember(UUID player) {
            return members.containsKey(player);
        }

        public Set<UUID> getMembersByRank(FactionRank rank) {
            Set<UUID> result = new HashSet<>();
            for (Map.Entry<UUID, FactionRank> entry : members.entrySet()) {
                if (entry.getValue() == rank) {
                    result.add(entry.getKey());
                }
            }
            return result;
        }

        // Power management methods
        public void addPower(int amount) {
            this.power = Math.max(0, this.power + amount);
            this.lastPowerUpdate = System.currentTimeMillis();
        }

        public boolean spendPower(int amount) {
            if (this.power >= amount) {
                this.power -= amount;
                return true;
            }
            return false;
        }

        public void setPower(int power) {
            this.power = Math.max(0, power);
        }

        public int getPower() {
            return power;
        }

        public long getLastPowerUpdate() {
            return lastPowerUpdate;
        }

        // Getters
        public String getName() {
            return name;
        }

        public UUID getLeader() {
            return leader;
        }

        public Set<UUID> getMembers() {
            return getMembersByRank(FactionRank.MEMBER);
        }

        public Set<UUID> getAllMembers() {
            return new HashSet<>(members.keySet());
        }

        public Set<UUID> getViceLeaders() {
            return getMembersByRank(FactionRank.VICE_LEADER);
        }

        public Set<UUID> getOfficers() {
            return getMembersByRank(FactionRank.OFFICER);
        }

        public Map<UUID, FactionRank> getAllMembersWithRanks() {
            return new HashMap<>(members);
        }

        public CompoundTag toNBT() {
            CompoundTag nbt = new CompoundTag();
            nbt.putString("name", name);
            nbt.putString("leader", leader.toString());
            nbt.putInt("power", power);
            nbt.putLong("lastPowerUpdate", lastPowerUpdate);

            CompoundTag membersNbt = new CompoundTag();
            for (Map.Entry<UUID, FactionRank> entry : members.entrySet()) {
                membersNbt.putString(entry.getKey().toString(), entry.getValue().name());
            }
            nbt.put("members", membersNbt);

            return nbt;
        }

        public static Faction fromNBT(CompoundTag nbt) {
            String name = nbt.getString("name");
            UUID leader = UUID.fromString(nbt.getString("leader"));
            Faction faction = new Faction(name, leader);

            // Load power data
            faction.power = nbt.getInt("power");
            faction.lastPowerUpdate = nbt.getLong("lastPowerUpdate");

            // Clear the auto-added leader to rebuild from NBT
            faction.members.clear();

            CompoundTag membersNbt = nbt.getCompound("members");
            for (String uuidString : membersNbt.getAllKeys()) {
                UUID member = UUID.fromString(uuidString);
                FactionRank rank = FactionRank.valueOf(membersNbt.getString(uuidString));
                faction.members.put(member, rank);
            }

            return faction;
        }
    }

    public static class ChunkClaimKey {
        private final int x;
        private final int z;
        private final ResourceKey<Level> dimension;

        public ChunkClaimKey(int x, int z, ResourceKey<Level> dimension) {
            this.x = x;
            this.z = z;
            this.dimension = dimension;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof ChunkClaimKey)) return false;
            ChunkClaimKey other = (ChunkClaimKey) obj;
            return x == other.x && z == other.z && Objects.equals(dimension, other.dimension);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, z, dimension);
        }

        public CompoundTag toNBT() {
            CompoundTag nbt = new CompoundTag();
            nbt.putInt("x", x);
            nbt.putInt("z", z);
            nbt.putString("dimension", dimension.location().toString());
            return nbt;
        }

        public static ChunkClaimKey fromNBT(CompoundTag nbt) {
            int x = nbt.getInt("x");
            int z = nbt.getInt("z");
            String dimensionString = nbt.getString("dimension");
            ResourceKey<Level> dimension = ResourceKey.create(
                    net.minecraft.core.registries.Registries.DIMENSION,
                    new net.minecraft.resources.ResourceLocation(dimensionString)
            );
            return new ChunkClaimKey(x, z, dimension);
        }

        // Getters
        public int getX() {
            return x;
        }

        public int getZ() {
            return z;
        }

        public ResourceKey<Level> getDimension() {
            return dimension;
        }
    }

    // Power costs per minute based on sequence level
    private static final Map<Integer, Integer> PROTECTION_BYPASS_COSTS = Map.of(
            0, 10000,  // Sequence 0: 10,000 per minute
            1, 6000,   // Sequence 1: 6,000 per minute
            2, 3000,   // Sequence 2: 3,000 per minute
            3, 2000,   // Sequence 3: 2,000 per minute
            4, 1000,   // Sequence 4: 1,000 per minute
            5, 400,    // Sequence 5: 400 per minute
            6, 300,    // Sequence 6: 300 per minute
            7, 150,    // Sequence 7: 150 per minute
            8, 100,    // Sequence 8: 100 per minute
            9, 50      // Sequence 9: 50 per minute
    );
    private static final int DEFAULT_PROTECTION_BYPASS_COST = 25; // Default for unsequenced players

    public static void factionDecrementer(LivingEvent.LivingTickEvent event) {
        if (event.getEntity().getPersistentData().getInt("ignoreFactionProtection") >= 1) {
            event.getEntity().getPersistentData().putInt("ignoreFactionProtection", event.getEntity().getPersistentData().getInt("ignoreFactionProtection") - 1);
        } else {
            EventManager.removeFromRegularLoop(event.getEntity(), EFunctions.FACTION_DATA_TICK.get());
        }
    }

    public boolean grantProtectionBypass(String factionName, UUID requester, ServerPlayer targetPlayer, int minutes) {
        if (minutes <= 0) {
            return false;
        }
        Faction faction = factions.get(factionName);
        if (faction == null) {
            return false;
        }
        FactionRank requesterRank = faction.getPlayerRank(requester);
        if (requesterRank != FactionRank.LEADER && requesterRank != FactionRank.VICE_LEADER) {
            return false;
        }
        String targetFaction = playerFactions.get(targetPlayer.getUUID());
        if (!factionName.equals(targetFaction)) {
            return false;
        }
        int targetSequence = getPlayerSequence(targetPlayer);
        int costPerMinute = PROTECTION_BYPASS_COSTS.getOrDefault(targetSequence, DEFAULT_PROTECTION_BYPASS_COST);
        int totalCost = costPerMinute * minutes;
        if (faction.getPower() < totalCost) {
            return false;
        }
        faction.spendPower(totalCost);
        int ticksToAdd = minutes * 1200;
        EventManager.removeFromRegularLoop(targetPlayer, EFunctions.FACTION_DATA_TICK.get());
        targetPlayer.getPersistentData().putInt("ignoreFactionProtection", targetPlayer.getPersistentData().getInt("ignoreFactionProtection") + ticksToAdd);

        setDirty();
        return true;
    }

    public static void factionHurtCheck(LivingHurtEvent event) {
        if (!Configs.COMMON.factionsEnabled.get()) {
            return;
        }
        LivingEntity attacked = event.getEntity();
        DamageSource source = event.getSource();
        Entity entitySource = source.getEntity();
        Entity entitySourceOwner = source.getEntity();
        if (entitySource != null) {
            if (entitySourceOwner instanceof Projectile projectile && projectile.getOwner() != null) {
                entitySourceOwner = projectile.getOwner();
            }
        }
        boolean ignoreFactionPower = false;
        if (entitySourceOwner != null) {
            if (entitySourceOwner.getPersistentData().getInt("ignoreFactionProtection") >= 1) {
                ignoreFactionPower = true;
            }
            if (!(entitySourceOwner instanceof Player)) {
                ignoreFactionPower = true;
            }
        }
        if (ignoreFactionPower) {
            return;
        }
        if (!(attacked instanceof Player victim)) {
            return;
        }
        if (victim.level() instanceof ServerLevel serverLevel) {
            ChunkPos chunkPos = new ChunkPos(victim.blockPosition());
            FactionData factionData = FactionData.getInstance(serverLevel);
            String chunkOwner = factionData.getChunkOwner(chunkPos, serverLevel.dimension());
            if (chunkOwner == null) {
                return;
            }
            String victimFaction = factionData.getPlayerFaction(victim.getUUID());
            if (victimFaction == null || !victimFaction.equals(chunkOwner)) {
                return;
            }
            event.setCanceled(true);
            if (event.getSource().getEntity() instanceof ServerPlayer attacker) {
                String attackerFaction = factionData.getPlayerFaction(attacker.getUUID());
                if (!victimFaction.equals(attackerFaction)) {
                    attacker.sendSystemMessage(Component.literal("You cannot harm players in their faction's territory!").withStyle(ChatFormatting.RED));
                }
            }
        }
    }

    public ClaimResult claimChunksInRadius(ChunkPos centerChunk, ResourceKey<Level> dimension, UUID player, int radius) {
        String factionName = playerFactions.get(player);
        if (factionName == null) {
            return new ClaimResult(false, 0, 0, "Player not in a faction");
        }

        Faction faction = factions.get(factionName);
        FactionRank playerRank = faction.getPlayerRank(player);
        if (playerRank == FactionRank.MEMBER) {
            return new ClaimResult(false, 0, 0, "Only officers and above can claim chunks");
        }
        int startX = centerChunk.x - (radius - 1);
        int endX = centerChunk.x + (radius - 1);
        int startZ = centerChunk.z - (radius - 1);
        int endZ = centerChunk.z + (radius - 1);
        List<ChunkClaimKey> chunksToCheck = new ArrayList<>();
        List<ChunkClaimKey> alreadyClaimed = new ArrayList<>();
        for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
                ChunkClaimKey key = new ChunkClaimKey(x, z, dimension);
                if (claimedChunks.containsKey(key)) {
                    String existingOwner = claimedChunks.get(key);
                    if (!existingOwner.equals(factionName)) {
                        alreadyClaimed.add(key);
                    } else {
                        // Already claimed by same faction, skip
                        continue;
                    }
                } else {
                    chunksToCheck.add(key);
                }
            }
        }

        if (!alreadyClaimed.isEmpty()) {
            return new ClaimResult(false, 0, alreadyClaimed.size(), "Some chunks are already claimed by other factions");
        }
        int CLAIM_POWER_COST = 4;
        int totalChunks = chunksToCheck.size();
        int powerCost = totalChunks * CLAIM_POWER_COST;
        if (faction.getPower() < powerCost) {
            return new ClaimResult(false, 0, 0, "Not enough faction power");
        }

        int successfulClaims = 0;
        for (ChunkClaimKey key : chunksToCheck) {
            claimedChunks.put(key, factionName);
            successfulClaims++;
        }

        faction.spendPower(powerCost);

        setDirty();
        return new ClaimResult(true, successfulClaims, 0, "Successfully claimed chunks");
    }

    /**
     * Result class for chunk claiming operations
     */
    public static class ClaimResult {
        private final boolean success;
        private final int claimedCount;
        private final int conflictCount;
        private final String message;

        public ClaimResult(boolean success, int claimedCount, int conflictCount, String message) {
            this.success = success;
            this.claimedCount = claimedCount;
            this.conflictCount = conflictCount;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public int getClaimedCount() {
            return claimedCount;
        }

        public int getConflictCount() {
            return conflictCount;
        }

        public String getMessage() {
            return message;
        }
    }
}