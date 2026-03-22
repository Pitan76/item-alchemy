package net.pitan76.mcpitanlib.apiprototype.event.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pitan76.mcpitanlib.api.event.BaseEvent;
import net.pitan76.mcpitanlib.api.event.block.ItemScattererUtil;
import net.pitan76.mcpitanlib.api.util.WorldUtil;
import net.pitan76.mcpitanlib.midohra.block.BlockWrapper;
import net.pitan76.mcpitanlib.midohra.block.entity.BlockEntityWrapper;
import net.pitan76.mcpitanlib.midohra.world.IWorldView;

public class StateReplacedEvent extends BaseEvent {

    public BlockState state;
    public World world;
    public BlockPos pos;
    public BlockState newState;
    public boolean moved;

    // Captured at construction time so getBlockEntity() works even after the world
    // has removed the block entity (MC 1.21.x removes BEs before onStateReplaced fires).
    private final BlockEntity cachedBlockEntity;

    public StateReplacedEvent(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        this.state = state;
        this.world = world;
        this.pos = pos;
        this.newState = newState;
        this.moved = moved;
        this.cachedBlockEntity = WorldUtil.getBlockEntity(world, pos);
    }

    public BlockState getState() {
        return state;
    }

    public World getWorld() {
        return world;
    }

    public BlockPos getPos() {
        return pos;
    }

    public BlockState getNewState() {
        return newState;
    }

    public boolean isMoved() {
        return moved;
    }

    public boolean isClient() {
        return world.isClient();
    }

    /**
     * check if the block is the same state
     * @return boolean
     */
    public boolean isSameState() {
        return state.isOf(newState.getBlock());
    }

    /**
     * check if the block has a block entity
     * @return boolean
     */
    public boolean hasBlockEntity() {
        return getBlockEntity() != null;
    }

    /**
     * get the block entity
     * <p>
     * The block entity is captured at event creation time, so this returns a valid
     * reference even in MC 1.21.x where the world removes the BE before
     * onStateReplaced is invoked.
     * @return BlockEntity
     */
    public BlockEntity getBlockEntity() {
        if (cachedBlockEntity != null) return cachedBlockEntity;
        // Fallback to live world lookup for older versions / edge cases.
        return WorldUtil.getBlockEntity(world, pos);
    }

    /**
     * spawn the drops in the container
     */
    public void spawnDropsInContainer() {
        if (isSameState() || !hasInventory()) return;

        ItemScattererUtil.spawn(getWorld(), getPos(), getBlockEntity());
        updateComparators();
    }

    public boolean hasInventory() {
        return getBlockEntity() instanceof Inventory;
    }

    /**
     * update the comparators
     */
    public void updateComparators() {
        WorldUtil.updateComparators(getWorld(), getPos(), getState().getBlock());
    }

    public net.pitan76.mcpitanlib.midohra.world.World getMidohraWorld() {
        return net.pitan76.mcpitanlib.midohra.world.World.of(world);
    }

    public IWorldView getWorldView() {
        return getMidohraWorld();
    }

    public net.pitan76.mcpitanlib.midohra.block.BlockState getMidohraState() {
        return net.pitan76.mcpitanlib.midohra.block.BlockState.of(state);
    }

    public net.pitan76.mcpitanlib.midohra.util.math.BlockPos getMidohraPos() {
        return net.pitan76.mcpitanlib.midohra.util.math.BlockPos.of(pos);
    }

    public BlockWrapper getBlockWrapper() {
        return BlockWrapper.of(state.getBlock());
    }

    public BlockEntityWrapper getBlockEntityWrapper() {
        return BlockEntityWrapper.of(getBlockEntity());
    }
}
