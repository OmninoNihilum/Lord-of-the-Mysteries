package net.swimmingtuna.lotm.item.BeyonderAbilities.Apprentice;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.swimmingtuna.lotm.blocks.DimensionalSight.DimensionalSightTileEntity;
import net.swimmingtuna.lotm.capabilities.concealed_data.CONCEALMENT_TYPES;
import net.swimmingtuna.lotm.capabilities.concealed_data.ConcealedUtils;
import net.swimmingtuna.lotm.capabilities.concealed_space.ConcealedSpaceUtils;
import net.swimmingtuna.lotm.entity.ApprenticeDoorEntity;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import net.swimmingtuna.lotm.init.BlockInit;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.item.BeyonderAbilities.SimpleAbilityItem;
import net.swimmingtuna.lotm.networking.packet.UpdateItemInHandC2S;
import net.swimmingtuna.lotm.util.BeyonderUtil;
import net.swimmingtuna.lotm.util.LeftClickHandler.LeftClickHandlerSkillP;
import net.swimmingtuna.lotm.util.LeftClickHandler.LeftClickType;
import net.swimmingtuna.lotm.world.worldgen.dimension.DimensionInit;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class CreateConcealedSpace extends LeftClickHandlerSkillP {

    public CreateConcealedSpace(Properties properties) {
        super(properties, BeyonderClassInit.APPRENTICE, 4, 400, 300);
    }

    @Override
    public InteractionResult useAbility(Level level, LivingEntity player, InteractionHand hand) {
        if (!checkAll(player)) {
            return InteractionResult.FAIL;
        }
        useSpirituality(player);
        addCooldown(player, this, 300 * (int) (float) BeyonderUtil.getDamage(player).get(ItemInit.CREATE_CONCEALED_SPACE.get()));
        concealedSpace(player);
        return InteractionResult.SUCCESS;
    }

    public static boolean insideOwnSpace(LivingEntity entity){
        for(UUID concealment : ConcealedUtils.getAllConcealments(entity)){
            if(ConcealedUtils.getConcealmentType(entity, concealment).equals(CONCEALMENT_TYPES.CONCEALED_SPACE)){
                if(ConcealedUtils.getCreator(entity, concealment).equals(entity.getUUID())){
                    System.out.println("true");
                    return true;
                }
            }
        }
        System.out.println("false");
        return false;
    }

    private static void concealedSpace(LivingEntity entity) {
        if (entity.level().isClientSide()) return;
        if (!ConcealedSpaceUtils.hasConcealedSpace(entity)) createConcealedSpace(entity);
        else {
            if (insideOwnSpace(entity)) {
                if (entity.isShiftKeyDown()) {
                    changeConcealedSpaceSpawn(entity);
                }
                else {
                    createDoorLeaveConcealedSpace(entity);
                }
            } else {
                if (entity.isShiftKeyDown()) {
                    createDoorItem(entity);
                } else {
                    DimensionalSightTileEntity dimensionalSightTileEntity = BeyonderUtil.findNearbyDimensionalSight(entity);
                    if (dimensionalSightTileEntity != null && dimensionalSightTileEntity.getScryTarget() != null) {
                        entity.sendSystemMessage(Component.literal("You brought your Dimensional Sight Target to your concealed space").withStyle(ChatFormatting.AQUA));
                        dimensionalSightTileEntity.getScryTarget().teleportTo(entity.getX(), entity.getY(), entity.getZ());
                    }
                    createDoorEnterConcealedSpace(entity);
                }
                upgradeConcealedSpace(entity);
            }
        }
    }

    private static void upgradeConcealedSpace(LivingEntity entity) {
        int sequence = BeyonderUtil.getSequence(entity);
        MinecraftServer server = entity.getServer();
        if (server == null) return;
        ResourceKey<Level> dimensionKey = DimensionInit.CONCEALED_SPACE_LEVEL_KEY;
        ServerLevel level = server.getLevel(dimensionKey);

        if (sequence == ConcealedSpaceUtils.getConcealedSpaceSequence(entity)) return;

        int diameter = 43 - 4 * sequence;
        int radius = (diameter - 1) / 2;

        BlockPos destination = ConcealedSpaceUtils.getConcealedSpaceCenter(entity);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                for (int dy = 0; dy <= diameter; dy++) {
                    int x = destination.getX() + dx;
                    int y = destination.getY() + dy;
                    int z = destination.getZ() + dz;
                    mutablePos.set(x, y, z);
                    if (level.getBlockState(mutablePos).is(BlockInit.VOID_BLOCK.get()))
                        level.destroyBlock(mutablePos, false);
                }
            }
        }
        ConcealedSpaceUtils.setSequence(entity, sequence);
    }

    private static void changeConcealedSpaceSpawn(LivingEntity entity) {
        ConcealedSpaceUtils.setConcealedSpaceSpawn(entity, new BlockPos(entity.getBlockX(), entity.getBlockY(), entity.getBlockZ()));
        if (entity instanceof Player player) {
            player.displayClientMessage(Component.literal("Spawn changed").withStyle(BeyonderUtil.getStyle(player)), true);
        }
    }

    private static void createDoorItem(LivingEntity entity) {
        if (entity.getOffhandItem().isEmpty()) {
            ItemStack stack = new ItemStack(ItemInit.CONCEALED_DOOR.get());
            CompoundTag tag = stack.getOrCreateTag();
            entity.setItemInHand(InteractionHand.OFF_HAND, stack);
            tag.putUUID("concealedSpaceOwner", entity.getUUID());
        }
    }

    private static void createConcealedSpace(LivingEntity entity) {
        int sequence = BeyonderUtil.getSequence(entity);
        MinecraftServer server = entity.getServer();
        if (server == null) return;
        ResourceKey<Level> dimensionKey = DimensionInit.CONCEALED_SPACE_LEVEL_KEY;
        ServerLevel level = server.getLevel(dimensionKey);

        int diameter = 43 - 4 * sequence;
        int radius = (diameter - 1) / 2;

        BlockPos destination;
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        int attempts = 0;

        outer:
        while (true) {
            BlockPos center = new BlockPos(
                    ThreadLocalRandom.current().nextInt(-100000, 100001),
                    2,
                    ThreadLocalRandom.current().nextInt(-100000, 100001)
            );

            for (int dx = -radius * 2; dx <= radius * 2; dx++) {
                for (int dz = -radius * 2; dz <= radius * 2; dz++) {
                    for (int dy = 0; dy <= diameter * 2; dy++) {
                        int x = center.getX() + dx;
                        int y = center.getY() + dy;
                        int z = center.getZ() + dz;

                        ChunkAccess chunk = level.getChunk(x >> 4, z >> 4, ChunkStatus.FULL, true);
                        if (chunk == null) continue;

                        mutablePos.set(x, y, z);
                        BlockState state = chunk.getBlockState(mutablePos);

                        if (!state.is(BlockInit.VOID_BLOCK.get())) {
                            attempts++;
                            if (attempts >= 1000) {
                                if (entity instanceof Player player)
                                    player.displayClientMessage(Component.literal("It wasn't possible to find any safe space to build your concealed space"), false);
                                return;
                            }
                            continue outer;
                        }
                    }
                }
            }

            destination = center;
            break;
        }

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                for (int dy = 0; dy <= diameter; dy++) {
                    int x = destination.getX() + dx;
                    int y = destination.getY() + dy;
                    int z = destination.getZ() + dz;
                    mutablePos.set(x, y, z);
                    level.destroyBlock(mutablePos, false);
                }
            }
        }

        ConcealedSpaceUtils.createConcealedSpace(entity, destination);
        createDoorEnterConcealedSpace(entity);
    }

    private static void createDoorEnterConcealedSpace(LivingEntity entity) {
        MinecraftServer server = entity.getServer();
        if (server == null) return;
        ResourceKey<Level> dimensionKey = DimensionInit.CONCEALED_SPACE_LEVEL_KEY;
        ServerLevel concealedDimension = server.getLevel(dimensionKey);

        ConcealedSpaceUtils.setConcealedSpaceExit(entity, new BlockPos(entity.getBlockX(), entity.getBlockY(), entity.getBlockZ()));
        ConcealedSpaceUtils.setConcealedSpaceExitDimension(entity, entity.level());

        int x = ConcealedSpaceUtils.getConcealedSpaceSpawn(entity).getX();
        int y = ConcealedSpaceUtils.getConcealedSpaceSpawn(entity).getY();
        int z = ConcealedSpaceUtils.getConcealedSpaceSpawn(entity).getZ();

        float yaw = -entity.getYRot() + 180;
        ApprenticeDoorEntity.DoorAnimationKind animationKind = ApprenticeDoorEntity.DoorAnimationKind.BELLOW;
        if (entity.level().getBlockState(new BlockPos((int) Math.floor(getHorizontalLookCoordinates(entity, 2)[0]),
                (int) Math.floor(entity.getY() - 1),
                (int) Math.floor(getHorizontalLookCoordinates(entity, 2)[1]))).isAir()) {
            animationKind = ApprenticeDoorEntity.DoorAnimationKind.FADE_IN;
        }
        ApprenticeDoorEntity enterDoor = new ApprenticeDoorEntity(entity.level(), entity.getUUID(), BeyonderUtil.getSequence(entity), 150, yaw, x, y, z, true, concealedDimension, animationKind);
        enterDoor.setPos(getHorizontalLookCoordinates(entity, 2)[0], entity.getY(), getHorizontalLookCoordinates(entity, 2)[1]);
        entity.level().addFreshEntity(enterDoor);
    }

    public static double[] getHorizontalLookCoordinates(LivingEntity player, double distance) {
        float yaw = player.getYRot();
        double angleRadians = Math.toRadians(-yaw);
        double x = player.getX() + distance * Math.sin(angleRadians);
        double z = player.getZ() + distance * Math.cos(angleRadians);
        return new double[]{x, z};
    }

    private static void createDoorLeaveConcealedSpace(LivingEntity entity) {
        Level level = ConcealedSpaceUtils.getConcealedSpaceExitDimension(entity);
        if (level == null) return;

        float yaw = -entity.getYRot() + 180;
        ApprenticeDoorEntity.DoorAnimationKind animationKind = ApprenticeDoorEntity.DoorAnimationKind.BELLOW;
        if (entity.level().getBlockState(new BlockPos((int) Math.floor(getHorizontalLookCoordinates(entity, 2)[0]),
                (int) Math.floor(entity.getY() - 1),
                (int) Math.floor(getHorizontalLookCoordinates(entity, 2)[1]))).isAir()) {
            animationKind = ApprenticeDoorEntity.DoorAnimationKind.FADE_IN;
        }

        int x = ConcealedSpaceUtils.getConcealedSpaceExit(entity).getX();
        int y = ConcealedSpaceUtils.getConcealedSpaceExit(entity).getY();
        int z = ConcealedSpaceUtils.getConcealedSpaceExit(entity).getZ();

        ApprenticeDoorEntity leaveDoor = new ApprenticeDoorEntity(entity.level(), entity.getUUID(), BeyonderUtil.getSequence(entity), 150, yaw, x, y, z, false, level, animationKind);
        leaveDoor.setPos(getHorizontalLookCoordinates(entity, 2)[0], entity.getY(), getHorizontalLookCoordinates(entity, 2)[1]);
        entity.level().addFreshEntity(leaveDoor);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Upon use, Conceal a part of the Spirit World, to be used at you will."));
        tooltipComponents.add(Component.literal("Left click for Create Concealed Bundle."));
        tooltipComponents.add(Component.literal("If used while sneaking, in your off hand, you will receive a special door that leads to the users Concealed Space."));
        tooltipComponents.add(Component.literal("Spirituality Used: ").append(Component.literal("400").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(Component.literal("Cooldown: ").append(Component.literal("15 Seconds").withStyle(ChatFormatting.YELLOW)));
        tooltipComponents.add(SimpleAbilityItem.getPathwayText(this.requiredClass.get()));
        tooltipComponents.add(SimpleAbilityItem.getClassText(this.requiredSequence, this.requiredClass.get()));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    @Override
    public Rarity getRarity(ItemStack pStack) {
        return Rarity.create("APPRENTICE_ABILITY", ChatFormatting.AQUA);
    }

    @Override
    public int getPriority(LivingEntity livingEntity, LivingEntity target) {
        return 0;
    }

    @Override
    public <T> LeftClickType getleftClickEmpty(T item) {
        return new UpdateItemInHandC2S((Integer) item, new ItemStack(ItemInit.CREATE_CONCEALED_BUNDLE.get()));
    }
}