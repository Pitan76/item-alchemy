package net.pitan76.itemalchemy.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.pitan76.itemalchemy.block.pedestal.IPedestalItem;
import net.pitan76.itemalchemy.tile.DMPedestalTile;
import net.pitan76.mcpitanlib.api.item.v2.CompatItem;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.util.BlockEntityUtil;
import net.pitan76.mcpitanlib.api.util.BlockStateUtil;
import net.pitan76.mcpitanlib.api.util.EntityUtil;
import net.pitan76.mcpitanlib.api.util.WorldUtil;
import net.pitan76.mcpitanlib.api.util.math.PosUtil;
import net.pitan76.mcpitanlib.api.util.world.TickerUtil;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import net.pitan76.mcpitanlib.midohra.util.math.Box;
import net.pitan76.mcpitanlib.midohra.util.math.Vector3d;
import net.pitan76.mcpitanlib.midohra.world.ServerWorld;
import net.pitan76.mcpitanlib.midohra.world.World;

import java.util.List;
import java.util.Optional;

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
        if (world.isClient()) return;
//        ServerWorld serverWorld = (ServerWorld) world;

        BlockPos min = BlockPos.of((int) box.getMinX(), (int) box.getMinY(), (int) box.getMinZ());
        BlockPos max = BlockPos.of((int) box.getMaxX(), (int) box.getMaxY(), (int) box.getMaxZ());

        for (net.minecraft.util.math.BlockPos pos : PosUtil.iterate(min.toRaw(), max.toRaw())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be != null && !BlockEntityUtil.isRemoved(be) && !(be instanceof DMPedestalTile)) {
                BlockState state = world.getBlockState(pos);
                for (int i = 0; i < BONUS_TICKS; i++) {
                    TickerUtil.tick(be, world.getRaw(), pos, state);
                }
            }
        }
    }

    private void speedUpRandomTicks(World world, Box box) {
        Optional<ServerWorld> optionalServerWorld = world.toServerWorld();
        if (!optionalServerWorld.isPresent()) return;
        ServerWorld serverWorld = optionalServerWorld.get();

        BlockPos min = BlockPos.of((int) box.getMinX(), (int) box.getMinY(), (int) box.getMinZ());
        BlockPos max = BlockPos.of((int) box.getMaxX(), (int) box.getMaxY(), (int) box.getMaxZ());

        for (net.minecraft.util.math.BlockPos pos : PosUtil.iterate(min.toRaw(), max.toRaw())) {
            BlockState state = world.getBlockState(pos);
            if (BlockStateUtil.hasRandomTicks(state)) {
                net.minecraft.util.math.BlockPos immutable = PosUtil.toImmutable(pos);
                for (int i = 0; i < BONUS_TICKS; i++) {
                    BlockStateUtil.randomTick(state, serverWorld.getRaw(), immutable);
                }
            }
        }
    }

    private void slowMobs(World world, Box box) {
        List<MobEntity> mobs = WorldUtil.getEntitiesByClass(world.getRaw(), MobEntity.class, box);
        for (MobEntity mob : mobs) {
            Vector3d velocity = Vector3d.of(EntityUtil.getVelocity(mob));
            EntityUtil.setVelocity(mob, velocity.mul(MOB_SLOWDOWN, 1.0, MOB_SLOWDOWN).toMinecraft());
        }
    }
}
