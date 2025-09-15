package net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;
import net.swimmingtuna.lotm.capabilities.sealed_data.ABILITIES_SEAL_TYPES;
import net.swimmingtuna.lotm.capabilities.sealed_data.SEAL_TYPES;
import net.swimmingtuna.lotm.capabilities.sealed_data.SealedUtils;
import net.swimmingtuna.lotm.entity.ApprenticeDoorEntity;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.EFunctions;
import net.swimmingtuna.lotm.events.NewEventLoop.EventManager.EventManager;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.BlockInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.ReachChangeUUIDs;
import net.swimmingtuna.lotm.world.worldgen.dimension.DimensionInit;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SpatialMaze extends SimpleAbilityItem {
    public SpatialMaze(Properties properties) {
        super(properties, BeyonderClassInit.APPRENTICE, 1, 2500, 2400,200,200);
    }
    public static HashMap<UUID, BlockPos> mazes = new HashMap<>();

    @Override
    public InteractionResult useAbilityOnEntity(ItemStack stack, LivingEntity livingEntity, LivingEntity interactionTarget, InteractionHand hand) {
        if (!livingEntity.level().isClientSide && !interactionTarget.level().isClientSide) {
            if (!checkAll(livingEntity)) {
                return InteractionResult.FAIL;
            }
            createMaze(livingEntity, interactionTarget);
            useSpirituality(livingEntity);
            addCooldown(livingEntity);
        }
        return InteractionResult.SUCCESS;
    }

    private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeMap = Lazy.of(this::createAttributeMap);

    @SuppressWarnings("deprecation")
    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            return this.lazyAttributeMap.get();
        }
        return super.getDefaultAttributeModifiers(slot);
    }

    private Multimap<Attribute, AttributeModifier> createAttributeMap() {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> attributeBuilder = ImmutableMultimap.builder();
        attributeBuilder.putAll(super.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND));
        attributeBuilder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_ENTITY_REACH, "Reach modifier", 200, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with entities
        attributeBuilder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(ReachChangeUUIDs.BEYONDER_BLOCK_REACH, "Reach modifier", 200, AttributeModifier.Operation.ADDITION)); //adds a 12 block reach for interacting with blocks, p much useless for this item
        return attributeBuilder.build();
    }

    public void createMaze(LivingEntity user, LivingEntity target) {
        if (target instanceof Player) {
            MinecraftServer server = target.getServer();
            if (server == null) return;

            ResourceKey<Level> dimensionKey = DimensionInit.CONCEALED_SPACE_LEVEL_KEY;
            ServerLevel level = server.getLevel(dimensionKey);

            Maze gen = new Maze(20, 20, System.currentTimeMillis());
            int[][] maze = gen.generate();

            int endX = maze[0].length - 1;
            int endZ = maze.length - 1;

            int roomSize = 3;
            int pathLength = 5;
            int pathWidth = 3;
            int height = 4;

            BlockPos origin = buildOuterLayer(user, target);
            UUID sealUUID = SealedUtils.seal(target, user.getUUID(), user.getName().getString(), BeyonderUtil.getSequence(user), ABILITIES_SEAL_TYPES.ALL, null, false, null, SEAL_TYPES.SPATIAL_MAZE);
            target.getPersistentData().putUUID("mazeSealUUID", sealUUID);
            EventManager.addToRegularLoop(target, EFunctions.SPATIAL_MAZE_TICK.get());

            for (int y = 0; y < maze.length; y++) {
                if (origin == null) break;
                for (int x = 0; x < maze[0].length; x++) {
                    int cell = maze[y][x];

                    int worldX = x * (roomSize + pathLength);
                    int worldZ = y * (roomSize + pathLength);
                    BlockPos roomOrigin = origin.offset(worldX, 0, worldZ);

                    boolean isExit = x == endX && y == endZ;

                    buildRoom(level, roomOrigin, roomSize, height, roomSize, isExit, user, target, sealUUID);

                    if ((cell & 2) != 0) {
                        BlockPos corridorOrigin = roomOrigin.offset(roomSize, 0, (roomSize - pathWidth) / 2);
                        buildRoom(level, corridorOrigin, pathLength, height, pathWidth, false, user, target, sealUUID);
                    }

                    if ((cell & 1) != 0) {
                        BlockPos corridorOrigin = roomOrigin.offset((roomSize - pathWidth) / 2, 0, roomSize);
                        buildRoom(level, corridorOrigin, pathWidth, height, pathLength, false, user, target, sealUUID);
                    }
                }
            }
            if (origin != null) {
                BeyonderUtil.teleportEntityThroughDimensions(target, dimensionKey.location(), origin.getX() + 1.5, origin.getY(), origin.getZ() + 1.5);
            }
        } else {
            target.getPersistentData().putInt("mazeTrap", (int) (float) BeyonderUtil.getDamage(user).get(ItemInit.SPATIAL_MAZE.get()));
            target.getPersistentData().putInt("mazeTrapX", (int) target.getX());
            target.getPersistentData().putInt("mazeTrapY", (int) target.getY());
            target.getPersistentData().putInt("mazeTrapZ", (int) target.getZ());
        }
    }

    private static BlockPos buildOuterLayer(LivingEntity user, LivingEntity target){
        MinecraftServer server = user.getServer();
        if (server == null) return null;

        ResourceKey<Level> dimensionKey = DimensionInit.CONCEALED_SPACE_LEVEL_KEY;
        ServerLevel level = server.getLevel(dimensionKey);

        int attempts = 0;
        BlockPos destination = null;

        outer:
        while (attempts < 1000) {
            BlockPos center = new BlockPos(
                    ThreadLocalRandom.current().nextInt(-100000, 100001),
                    150,
                    ThreadLocalRandom.current().nextInt(-100000, 100001)
            );

            int sizeX = 160;
            int sizeY = 10;
            int sizeZ = 160;

            BlockPos[] pointsToCheck = new BlockPos[] {
                    center,
                    center.offset(0, 0, 0),
                    center.offset(sizeX, 0, 0),
                    center.offset(0, 0, sizeZ),
                    center.offset(sizeX, 0, sizeZ),
                    center.offset(sizeX / 2, 0, sizeZ / 2)
            };

            for (BlockPos pos : pointsToCheck) {
                for (int dy = 0; dy < sizeY; dy++) {
                    BlockPos checkPos = pos.above(dy);
                    ChunkAccess chunk = level.getChunk(checkPos.getX() >> 4, checkPos.getZ() >> 4, ChunkStatus.FULL, true);
                    if (chunk == null) continue;
                    if (!chunk.getBlockState(checkPos).isAir()) {
                        attempts++;
                        continue outer;
                    }
                }
            }

            destination = center;
            break;
        }

        if (destination == null) {
            if (user instanceof Player player) {
                player.displayClientMessage(Component.literal("It wasn't possible to find any safe space to create the maze"), false);
            }
            return null;
        }

        for (int x = 0; x <= 160; x++) {
            for (int y = 0; y < 10; y++) {
                for (int z = 0; z <= 160; z++) {
                    BlockPos currentPos = destination.offset(x, y, z);
                    level.setBlock(currentPos, BlockInit.VOID_BLOCK.get().defaultBlockState(), 0);
                }
            }
        }

        target.getPersistentData().putLong("mazeSealBlockPos", destination.asLong());
        return new BlockPos(destination.getX() + 3, destination.getY() + 3, destination.getZ() + 3);
    }

    private static void buildRoom(ServerLevel level, BlockPos origin, int sizeX, int sizeY, int sizeZ, boolean isExit, LivingEntity user, LivingEntity target, UUID sealUUID) {
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                for (int z = 0; z < sizeZ; z++) {
                    BlockPos pos = origin.offset(x, y, z);
                    BeyonderUtil.setAirBE(level, pos);
                    if(isExit && x == 1 && y == 0 && z == 1){
                        boolean isNorth = level.getBlockState(pos.offset(0, 0, -3)).isAir();
                        boolean isWest = level.getBlockState(pos.offset(-3, 0, 0)).isAir();

                        float yaw;
                        double xMod = 0, zMod = 0;

                        if (isNorth && isWest) {
                            yaw = 225;
                            xMod = zMod = 1;
                        } else if (isNorth) {
                            yaw = 180;
                            zMod = 1.2;
                        } else {
                            yaw = 270;
                            if (isWest) xMod = 1.2;
                        }

                        CompoundTag tag = target.getPersistentData();
                        tag.putDouble("mazeExitX", target.getX());
                        tag.putDouble("mazeExitY", target.getY());
                        tag.putDouble("mazeExitZ", target.getZ());
                        tag.putString("mazeExitDimension", target.level().dimension().location().toString());

                        ApprenticeDoorEntity door = new ApprenticeDoorEntity(level, user.getUUID(), sealUUID, BeyonderUtil.getSequence(user), yaw, (float) target.getX(), (float) target.getY(), (float) target.getZ(), target.level());
                        door.setPos(pos.getX() + xMod + 0.5, pos.getY(), pos.getZ() + zMod + 0.5);
                        level.addFreshEntity(door);
                    }
                }
            }
        }
    }

    public static void removeSeal(LivingEntity entity){
        if(entity.level().isClientSide) return;
        clearMaze(entity);

        CompoundTag tag = entity.getPersistentData();
        double x = tag.getDouble("mazeExitX");
        double y = tag.getDouble("mazeExitY");
        double z = tag.getDouble("mazeExitZ");
        String dimString = tag.getString("mazeExitDimension");

        MinecraftServer server = entity.getServer();
        if(server == null) return;

        ResourceKey<Level> id = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dimString));
        Level level = server.getLevel(id);
        if(level == null) return;

        BeyonderUtil.trueTeleportEntity(entity, level, x, y, z);
    }

    public static void clearMaze(LivingEntity entity){
        MinecraftServer server = entity.getServer();
        if (server == null) return;

        ResourceKey<Level> dimensionKey = DimensionInit.CONCEALED_SPACE_LEVEL_KEY;
        ServerLevel level = server.getLevel(dimensionKey);
        if (level == null) return;

        if(entity.getPersistentData().contains("mazeSealBlockPos")){
            BlockPos destination = BlockPos.of(entity.getPersistentData().getLong("mazeSealBlockPos"));
            for (int x = 0; x <= 160; x++) {
                for (int y = 0; y < 10; y++) {
                    for (int z = 0; z <= 160; z++) {
                        BlockPos currentPos = destination.offset(x, y, z);
                        level.setBlock(currentPos, Blocks.AIR.defaultBlockState(), 0);
                    }
                }
            }
            entity.getPersistentData().remove("mazeSealBlockPos");
            entity.getPersistentData().remove("mazeSealUUID");
        }
    }

    public static void mazeTick(LivingEntity entity){
        if(entity.getPersistentData().contains("mazeSealUUID") && SealedUtils.hasSpecificSeal(entity, entity.getPersistentData().getUUID("mazeSealUUID"))){
            entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20, 0, false, false));
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 1, false, false));
            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 20, 3, false, false));
        } else {
            EventManager.removeFromRegularLoop(entity, EFunctions.SPATIAL_MAZE_TICK.get());
        }
    }

    @Override
    public Rarity getRarity(ItemStack pStack) {
        return Rarity.create("APPRENTICE_ABILITY", ChatFormatting.AQUA);
    }

    @Override
    public int getPriority(LivingEntity livingEntity, LivingEntity target) {
        if (target != null && target.getHealth() > livingEntity.getHealth()) {
            return 40;
        }
        return 0;
    }

    class Maze {
        private static final int SOUTH = 1;
        private static final int EAST = 2;

        private final int width;
        private final int height;
        private final Random random;

        public Maze(int width, int height, long seed) {
            this.width = width;
            this.height = height;
            this.random = new Random(seed);
        }

        private static class State {
            private final int width;
            private final Map<Integer, List<Integer>> sets = new HashMap<>();
            private final Map<Integer, Integer> cells = new HashMap<>();
            private int nextSetId;

            public State(int width, int nextSetId) {
                this.width = width;
                this.nextSetId = nextSetId;
            }

            public State next() {
                return new State(width, nextSetId);
            }

            public void populate() {
                for (int i = 0; i < width; i++) {
                    if (!cells.containsKey(i)) {
                        int setId = ++nextSetId;
                        sets.put(setId, new ArrayList<>(List.of(i)));
                        cells.put(i, setId);
                    }
                }
            }

            public boolean same(int a, int b) {
                return cells.get(a).equals(cells.get(b));
            }

            public void merge(int a, int b) {
                int setA = cells.get(a);
                int setB = cells.get(b);
                if (setA == setB) return;

                List<Integer> setBList = sets.remove(setB);
                sets.get(setA).addAll(setBList);
                for (int cell : setBList) {
                    cells.put(cell, setA);
                }
            }

            public void add(int cell, int setId) {
                cells.put(cell, setId);
                sets.computeIfAbsent(setId, k -> new ArrayList<>()).add(cell);
            }

            public Set<Map.Entry<Integer, List<Integer>>> getSets() {
                return sets.entrySet();
            }
        }

        public int[][] generate() {
            int[][] maze = new int[height][width];
            State state = new State(width, -1);
            state.populate();

            for (int y = 0; y < height; y++) {
                boolean isLastRow = y == height - 1;
                State nextState = state.next();

                for (int x = 0; x < width - 1; x++) {
                    if (!state.same(x, x + 1)) {
                        if (isLastRow) {
                            state.merge(x, x + 1);
                            maze[y][x] |= EAST;
                        } else if (random.nextBoolean()) {
                            state.merge(x, x + 1);
                            maze[y][x] |= EAST;
                        }
                    }
                }

                if (!isLastRow) {
                    Set<Integer> connected = new HashSet<>();
                    for (Map.Entry<Integer, List<Integer>> entry : state.getSets()) {
                        List<Integer> cells = entry.getValue();
                        Collections.shuffle(cells, random);
                        int numConnections = 1 + random.nextInt(cells.size());
                        for (int i = 0; i < numConnections; i++) {
                            int cell = cells.get(i);
                            maze[y][cell] |= SOUTH;
                            nextState.add(cell, entry.getKey());
                        }
                    }
                }

                state = nextState;
                state.populate();
            }

            return maze;
        }
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.literal("Upon use, traps the target in a near unbreakable maze that they must escape in order to return to the overworld."));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("2400").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("2 Minutes").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}