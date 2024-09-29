package net.pitan76.itemalchemy.tile;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pitan76.itemalchemy.block.AEGUBlock;
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.mcpitanlib.api.event.tile.TileTickEvent;
import net.pitan76.mcpitanlib.api.gui.inventory.sided.CompatSidedInventory;
import net.pitan76.mcpitanlib.api.gui.inventory.sided.args.AvailableSlotsArgs;
import net.pitan76.mcpitanlib.api.gui.inventory.sided.args.CanExtractArgs;
import net.pitan76.mcpitanlib.api.gui.inventory.sided.args.CanInsertArgs;
import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity;
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntityTicker;
import net.pitan76.mcpitanlib.api.util.WorldUtil;

import java.util.Optional;

public class AEGUTile extends CompatBlockEntity implements ExtendBlockEntityTicker<AEGUTile>, CompatSidedInventory {
    public int coolDown = 0; // tick

    public int getMaxCoolDown() {
        return 5 * 1; // tick
    }

    public AEGUTile(BlockEntityType<?> type, TileCreateEvent e) {
        super(type, e);
    }

    public AEGUTile(TileCreateEvent e) {
        this(Tiles.AEGU.getOrNull(), e);
    }

    // TODO: Cache Target Condenser Tile

    @Override
    public void tick(TileTickEvent<AEGUTile> e) {
        World world = e.world;
        BlockState state = e.state;

        if (coolDown == 0) {
            BlockPos targetPos = getNearEMCCondenserPos(world, pos);
            if (targetPos != null) {
                WorldUtil.setBlockState(world, pos, AEGUBlock.setConnected(state, true));
                Optional<EMCCondenserTile> optional = getNearEMCCondenserByTargetPos(world, targetPos);
                if (!optional.isPresent()) return;
                EMCCondenserTile tile = optional.get();

                if (tile.storedEMC < tile.maxEMC)
                    tile.storedEMC += ((AEGUBlock) state.getBlock()).emc;
            } else {
                WorldUtil.setBlockState(world, pos, AEGUBlock.setConnected(state, false));
            }
        }

        if (coolDown >= getMaxCoolDown()) {
            coolDown = 0;
        }
    }

    public Optional<EMCCondenserTile> getNearEMCCondenser() {
        return getNearEMCCondenser(world, pos);
    }

    public static Optional<EMCCondenserTile> getNearEMCCondenser(World world, BlockPos pos) {
        if (world == null) return Optional.empty();

        BlockPos targetPos = getNearEMCCondenserPos(world, pos);
        if (targetPos == null) return Optional.empty();

        return getNearEMCCondenserByTargetPos(world, targetPos);
    }

    public static Optional<EMCCondenserTile> getNearEMCCondenserByTargetPos(World world, BlockPos targetPos) {
        if (world == null) return Optional.empty();

        if (targetPos == null) return Optional.empty();
        return Optional.ofNullable((EMCCondenserTile) WorldUtil.getBlockEntity(world, targetPos));
    }

    public static BlockPos getNearEMCCondenserPos(World world, BlockPos pos) {
        BlockPos blockPos = null;
        BlockPos[] nearPoses = {pos.up(), pos.down(), pos.north(), pos.south(), pos.east(), pos.west(),
                pos.up().north(), pos.up().south(), pos.up().east(), pos.up().west(), pos.up().north().east(), pos.up().south().east(), pos.up().north().west(), pos.up().south().west(),
                pos.down().north(), pos.down().south(), pos.down().east(), pos.down().west(), pos.down().north().east(), pos.down().south().east(), pos.down().north().west(), pos.down().south().west(),
                pos.north().west(), pos.north().east(), pos.south().west(), pos.south().east()
        };
        for (BlockPos nearPos : nearPoses) {
            if (WorldUtil.getBlockEntity(world, nearPos) instanceof EMCCondenserTile) {
                blockPos = nearPos;
                break;
            }
        }
        return blockPos;
    }

    @Override
    public int[] getAvailableSlots(AvailableSlotsArgs args) {
        return getNearEMCCondenser().map(tile -> tile.getAvailableSlots(args)).orElse(new int[0]);
    }

    @Override
    public boolean canInsert(CanInsertArgs args) {
        return getNearEMCCondenser().map(tile -> tile.canInsert(args)).orElse(false);

    }

    @Override
    public boolean canExtract(CanExtractArgs args) {
        return getNearEMCCondenser().map(tile -> tile.canExtract(args)).orElse(false);
    }

    @Override
    public int size() {
        return getNearEMCCondenser().map(EMCCondenserTile::size).orElse(0);
    }

    @Override
    public boolean isEmpty() {
        return getNearEMCCondenser().map(EMCCondenserTile::isEmpty).orElse(true);
    }

    @Override
    public ItemStack getStack(int slot) {
        return getNearEMCCondenser().map(tile -> tile.getStack(slot)).orElse(ItemStack.EMPTY);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return getNearEMCCondenser().map(tile -> tile.removeStack(slot, amount)).orElse(ItemStack.EMPTY);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return getNearEMCCondenser().map(tile -> tile.removeStack(slot)).orElse(ItemStack.EMPTY);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        getNearEMCCondenser().ifPresent(tile -> tile.setStack(slot, stack));
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return getNearEMCCondenser().map(tile -> tile.canPlayerUse(player)).orElse(false);
    }

    @Override
    public void clear() {
        getNearEMCCondenser().ifPresent(EMCCondenserTile::clear);
    }
}
