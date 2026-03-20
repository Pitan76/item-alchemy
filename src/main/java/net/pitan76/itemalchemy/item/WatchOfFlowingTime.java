package net.pitan76.itemalchemy.item;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.pitan76.itemalchemy.block.pedestal.IPedestalItem;
import net.pitan76.itemalchemy.tile.DMPedestalTile;
import net.pitan76.mcpitanlib.api.item.v2.CompatItem;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.util.BlockStateUtil;

import java.util.List;

public class WatchOfFlowingTime extends CompatItem implements IPedestalItem {

    private static final int BONUS_TICKS = 8;
    private static final double MOB_SLOWDOWN = 0.25;

    public WatchOfFlowingTime(CompatibleItemSettings settings) {
        super(settings);
    }

    @Override
    public boolean updateInPedestal(ItemStack stack, World world, BlockPos pos) {
        if (world.isClient()) return false;

        Box effectBox = new Box(
                pos.getX() - DMPedestalTile.RANGE, pos.getY() - DMPedestalTile.RANGE, pos.getZ() - DMPedestalTile.RANGE,
                pos.getX() + DMPedestalTile.RANGE + 1, pos.getY() + DMPedestalTile.RANGE + 1, pos.getZ() + DMPedestalTile.RANGE + 1
        );

        speedUpBlockEntities(world, effectBox);
        speedUpRandomTicks(world, effectBox);
        slowMobs(world, effectBox);

        return false;
    }

    private void speedUpBlockEntities(World world, Box box) {
        if (!(world instanceof ServerWorld)) return;
        ServerWorld serverWorld = (ServerWorld) world;

        BlockPos min = new BlockPos((int) box.minX, (int) box.minY, (int) box.minZ);
        BlockPos max = new BlockPos((int) box.maxX, (int) box.maxY, (int) box.maxZ);

        for (BlockPos pos : BlockPos.iterate(min, max)) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be != null && !be.isRemoved() && !(be instanceof DMPedestalTile)) {
                net.minecraft.block.BlockState state = world.getBlockState(pos);
                net.minecraft.block.entity.BlockEntityTicker<?> ticker = state.getBlockEntityTicker(world, be.getType());
                if (ticker != null) {
                    for (int i = 0; i < BONUS_TICKS; i++) {
                        ((net.minecraft.block.entity.BlockEntityTicker) ticker).tick(world, pos, state, be);
                    }
                }
            }
        }
    }

    private void speedUpRandomTicks(World world, Box box) {
        if (!(world instanceof ServerWorld)) return;
        ServerWorld serverWorld = (ServerWorld) world;

        BlockPos min = new BlockPos((int) box.minX, (int) box.minY, (int) box.minZ);
        BlockPos max = new BlockPos((int) box.maxX, (int) box.maxY, (int) box.maxZ);

        for (BlockPos pos : BlockPos.iterate(min, max)) {
            net.minecraft.block.BlockState state = world.getBlockState(pos);
            if (BlockStateUtil.hasRandomTicks(state)) {
                BlockPos immutable = pos.toImmutable();
                for (int i = 0; i < BONUS_TICKS; i++) {
                    BlockStateUtil.randomTick(state, serverWorld, immutable);
                }
            }
        }
    }

    private void slowMobs(World world, Box box) {
        List<MobEntity> mobs = world.getEntitiesByClass(MobEntity.class, box, e -> true);
        for (MobEntity mob : mobs) {
            mob.setVelocity(mob.getVelocity().multiply(MOB_SLOWDOWN, 1.0, MOB_SLOWDOWN));
        }
    }
}
