package net.pitan76.itemalchemy.item;

import net.pitan76.itemalchemy.block.pedestal.IPedestalItem;
import net.pitan76.itemalchemy.tile.DMPedestalTile;
import net.pitan76.itemalchemy.util.IRechargeableFromKlein;
import net.pitan76.itemalchemy.util.ItemUtils;
import net.pitan76.mcpitanlib.api.event.item.ItemBarColorArgs;
import net.pitan76.mcpitanlib.api.event.item.ItemBarStepArgs;
import net.pitan76.mcpitanlib.api.event.item.ItemBarVisibleArgs;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.util.CustomDataUtil;
import net.pitan76.mcpitanlib.api.registry.CompatRegistryLookup;
import net.pitan76.mcpitanlib.midohra.block.BlockState;
import net.pitan76.mcpitanlib.midohra.block.entity.BlockEntityWrapper;
import net.pitan76.mcpitanlib.midohra.entity.EntityWrapper;
import net.pitan76.mcpitanlib.midohra.item.ItemStack;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import net.pitan76.mcpitanlib.midohra.util.math.Box;
import net.pitan76.mcpitanlib.midohra.util.math.Vector3d;
import net.pitan76.mcpitanlib.midohra.world.ServerWorld;
import net.pitan76.mcpitanlib.midohra.world.World;

import java.util.List;
import java.util.Optional;

public class WatchOfFlowingTime extends AlchemicalItem implements IPedestalItem, IRechargeableFromKlein {

    private static final int BONUS_TICKS_PER_CHARGE = 4;
    private static final int BLOCK_UPDATE_INTERVAL = 4;
    private static final double MOB_SLOWDOWN = 0.25;

    public WatchOfFlowingTime(CompatibleItemSettings settings) {
        super(settings);
    }

    @Override
    public int getEmcCostPerCharge() {
        return 2000;
    }

    @Override
    public boolean isItemBarVisible(ItemBarVisibleArgs args) {
        return CustomDataUtil.contains(args.getStack(), "itemalchemy");
    }

    @Override
    public int getItemBarStep(ItemBarStepArgs args) {
        return ItemUtils.getChargeBarStep(args.getStack());
    }

    @Override
    public int getItemBarColor(ItemBarColorArgs args) {
        return ItemUtils.CHARGE_BAR_COLOR;
    }

    private static int getBonusTicks(ItemStack stack) {
        return BONUS_TICKS_PER_CHARGE * (ItemUtils.getCharge(stack) + 1);
    }

    @Override
    public boolean updateInPedestal(ItemStack stack, World world, BlockPos pos, CompatRegistryLookup registryLookup) {
        if (world.isClient()) return false;

        int bonusTicks = getBonusTicks(stack);

        Box effectBox = new Box(
                pos.subtract(DMPedestalTile.RANGE, DMPedestalTile.RANGE, DMPedestalTile.RANGE),
                pos.add(DMPedestalTile.RANGE + 1, DMPedestalTile.RANGE + 1, DMPedestalTile.RANGE + 1)
        );

        slowMobs(world, effectBox);

        if (world.getTime() % BLOCK_UPDATE_INTERVAL != 0) return false;

        BlockPos min = pos.subtract(DMPedestalTile.RANGE, DMPedestalTile.RANGE, DMPedestalTile.RANGE);
        BlockPos max = pos.add(DMPedestalTile.RANGE, DMPedestalTile.RANGE, DMPedestalTile.RANGE);

        speedUpBlockEntities(world, min, max, bonusTicks * BLOCK_UPDATE_INTERVAL);
        speedUpRandomTicks(world, min, max, bonusTicks * BLOCK_UPDATE_INTERVAL);

        return false;
    }

    private void speedUpBlockEntities(World world, BlockPos min, BlockPos max, int bonusTicks) {
        if (world.isClient()) return;

        for (BlockPos pos : BlockPos.iterate(min, max)) {
            BlockEntityWrapper blockEntity = world.getBlockEntity(pos);
            if (blockEntity.isPresent() && !blockEntity.isRemoved() && !blockEntity.instanceOf(DMPedestalTile.class)) {
                for (int i = 0; i < bonusTicks; i++) {
                    blockEntity.tick();
                }
            }
        }
    }

    private void speedUpRandomTicks(World world, BlockPos min, BlockPos max, int bonusTicks) {
        Optional<ServerWorld> optionalServerWorld = world.toServerWorld();
        if (!optionalServerWorld.isPresent()) return;
        ServerWorld serverWorld = optionalServerWorld.get();

        for (BlockPos pos : BlockPos.iterate(min, max)) {
            BlockState state = world.getBlockState(pos);
            if (state.hasRandomTicks()) {
                for (int i = 0; i < bonusTicks; i++) {
                    state.randomTick(serverWorld, pos);
                }
            }
        }
    }

    private void slowMobs(World world, Box box) {
        List<EntityWrapper> mobs = world.getMobs(box);
        for (EntityWrapper mob : mobs) {
            Vector3d velocity = mob.getVelocity();
            mob.setVelocity(velocity.mul(MOB_SLOWDOWN, 1.0, MOB_SLOWDOWN));
        }
    }
}
