package net.pitan76.itemalchemy.item;

import net.minecraft.block.BlockState;
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
import net.pitan76.mcpitanlib.api.util.EntityUtil;
import net.pitan76.mcpitanlib.api.util.WorldUtil;
import net.pitan76.mcpitanlib.api.util.math.BoxUtil;
import net.pitan76.mcpitanlib.api.util.math.PosUtil;
import net.pitan76.mcpitanlib.api.util.world.TickerUtil;
import net.pitan76.mcpitanlib.midohra.block.entity.BlockEntityWrapper;

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

        net.pitan76.mcpitanlib.midohra.util.math.BlockPos pos2 = net.pitan76.mcpitanlib.midohra.util.math.BlockPos.of(pos);

        Box effectBox = BoxUtil.createBox(
                pos2.getX() - DMPedestalTile.RANGE, pos2.getY() - DMPedestalTile.RANGE, pos2.getZ() - DMPedestalTile.RANGE,
                pos2.getX() + DMPedestalTile.RANGE + 1, pos2.getY() + DMPedestalTile.RANGE + 1, pos2.getZ() + DMPedestalTile.RANGE + 1
        );

        speedUpBlockEntities(world, effectBox);
        speedUpRandomTicks(world, effectBox);
        slowMobs(world, effectBox);

        return false;
    }

    private void speedUpBlockEntities(World world, Box box) {
        if (!(world instanceof ServerWorld)) return;
//        ServerWorld serverWorld = (ServerWorld) world;

        BlockPos min = PosUtil.flooredBlockPos((int) box.minX, (int) box.minY, (int) box.minZ);
        BlockPos max = PosUtil.flooredBlockPos((int) box.maxX, (int) box.maxY, (int) box.maxZ);

        for (BlockPos pos : PosUtil.iterate(min, max)) {
            BlockEntity be = WorldUtil.getBlockEntity(world, pos);
            BlockEntityWrapper beWrapper = BlockEntityWrapper.of(be);
            if (be != null && !beWrapper.isRemoved() && !(be instanceof DMPedestalTile)) {
                BlockState state = WorldUtil.getBlockState(world, pos);
//                net.minecraft.block.entity.BlockEntityTicker<?> ticker = state.getBlockEntityTicker(world, be.getType());
//                if (ticker != null) {
                for (int i = 0; i < BONUS_TICKS; i++) {
                    TickerUtil.tick(be, world, pos, state);
                }
//                }
            }
        }
    }

    private void speedUpRandomTicks(World world, Box box) {
        if (!(world instanceof ServerWorld)) return;
        ServerWorld serverWorld = (ServerWorld) world;

        BlockPos min = PosUtil.flooredBlockPos((int) box.minX, (int) box.minY, (int) box.minZ);
        BlockPos max = PosUtil.flooredBlockPos((int) box.maxX, (int) box.maxY, (int) box.maxZ);

        for (BlockPos pos : PosUtil.iterate(min, max)) {
            BlockState state = WorldUtil.getBlockState(world, pos);
            if (BlockStateUtil.hasRandomTicks(state)) {
                BlockPos immutable = pos.toImmutable();
                for (int i = 0; i < BONUS_TICKS; i++) {
                    BlockStateUtil.randomTick(state, serverWorld, immutable);
                }
            }
        }
    }

    private void slowMobs(World world, Box box) {
        List<MobEntity> mobs = WorldUtil.getEntitiesByClass(world, MobEntity.class, box, e -> true);
        for (MobEntity mob : mobs) {
            mob.setVelocity(EntityUtil.getVelocity(mob).multiply(MOB_SLOWDOWN, 1.0, MOB_SLOWDOWN));
        }
    }
}
