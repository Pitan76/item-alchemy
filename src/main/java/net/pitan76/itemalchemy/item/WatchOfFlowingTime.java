package net.pitan76.itemalchemy.item;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.pitan76.itemalchemy.block.pedestal.IPedestalItem;
import net.pitan76.itemalchemy.tile.DMPedestalTile;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.util.BlockStateUtil;
import net.pitan76.mcpitanlib.api.util.world.TickerUtil;
import net.pitan76.mcpitanlib.midohra.block.BlockState;
import net.pitan76.mcpitanlib.midohra.block.entity.BlockEntityWrapper;
import net.pitan76.mcpitanlib.midohra.entity.EntityWrapper;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import net.pitan76.mcpitanlib.midohra.util.math.Box;
import net.pitan76.mcpitanlib.midohra.util.math.Vector3d;
import net.pitan76.mcpitanlib.midohra.world.ServerWorld;
import net.pitan76.mcpitanlib.midohra.world.World;

import java.util.List;
import java.util.Optional;

public class WatchOfFlowingTime extends AlchemicalItem implements IPedestalItem {

    private static final int BONUS_TICKS = 8;
    private static final double MOB_SLOWDOWN = 0.25;

    public WatchOfFlowingTime(CompatibleItemSettings settings) {
        super(settings);
    }

    @Override
    public boolean updateInPedestal(ItemStack stack, World world, BlockPos pos) {
        if (world.isClient()) return false;

        Box effectBox = new Box(
                pos.subtract(DMPedestalTile.RANGE, DMPedestalTile.RANGE, DMPedestalTile.RANGE),
                pos.add(DMPedestalTile.RANGE + 1, DMPedestalTile.RANGE + 1, DMPedestalTile.RANGE + 1)
        );

        speedUpBlockEntities(world, effectBox);
        speedUpRandomTicks(world, effectBox);
        slowMobs(world, effectBox);

        return false;
    }

    private void speedUpBlockEntities(World world, Box box) {
        if (world.isClient()) return;

        BlockPos min = box.getMinPos();
        BlockPos max = box.getMaxPos();

        for (BlockPos pos : BlockPos.iterate(min, max)) {
            BlockEntityWrapper be = world.getBlockEntity(pos);
            if (be.isPresent() && !be.isRemoved() && !(be.get() instanceof DMPedestalTile)) {
                BlockState state = world.getBlockState(pos);
                for (int i = 0; i < BONUS_TICKS; i++) {
                    TickerUtil.tick(be, world, pos, state);
                }
            }
        }
    }

    private void speedUpRandomTicks(World world, Box box) {
        Optional<ServerWorld> optionalServerWorld = world.toServerWorld();
        if (!optionalServerWorld.isPresent()) return;
        ServerWorld serverWorld = optionalServerWorld.get();

        BlockPos min = box.getMinPos();
        BlockPos max = box.getMaxPos();

        for (BlockPos pos : BlockPos.iterate(min, max)) {
            BlockState state = world.getBlockState(pos);
            if (BlockStateUtil.hasRandomTicks(state)) {
                for (int i = 0; i < BONUS_TICKS; i++) {
                    state.randomTick(serverWorld, pos);
                }
            }
        }
    }

    private void slowMobs(World world, Box box) {
        List<EntityWrapper> mobs = world.getEntitiesByClassM(MobEntity.class, box);
        for (EntityWrapper mob : mobs) {
            Vector3d velocity = mob.getVelocity();
            mob.setVelocity( velocity.mul(MOB_SLOWDOWN, 1.0, MOB_SLOWDOWN));
        }
    }
}
