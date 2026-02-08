package net.pitan76.itemalchemy.item;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pitan76.itemalchemy.util.ItemCharge;
import net.pitan76.itemalchemy.util.ItemUtils;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.event.item.ItemBarVisibleArgs;
import net.pitan76.mcpitanlib.api.event.item.PostMineEvent;
import net.pitan76.mcpitanlib.api.item.tool.CompatiblePickaxeItem;
import net.pitan76.mcpitanlib.api.item.tool.CompatibleToolMaterial;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.util.BlockStateUtil;
import net.pitan76.mcpitanlib.api.util.CustomDataUtil;
import net.pitan76.mcpitanlib.api.util.WorldUtil;
import net.pitan76.mcpitanlib.api.util.math.PosUtil;
import net.pitan76.mcpitanlib.midohra.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class AlchemicalPickaxe extends CompatiblePickaxeItem implements ItemCharge, AlchemicalToolMode {
    public AlchemicalPickaxe(CompatibleToolMaterial toolMaterial, int attackDamage, float attackSpeed, CompatibleItemSettings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean isDamageableOnDefault() {
        return false;
    }

    @Override
    public boolean isItemBarVisible(ItemBarVisibleArgs args, Options options) {
        return CustomDataUtil.contains(args.getStack(), "itemalchemy");
    }

    @Override
    public float overrideGetMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        if (!isSuitableFor(stack, state))
            return super.overrideGetMiningSpeedMultiplier(stack, state);

        return super.overrideGetMiningSpeedMultiplier(stack, state) * (ItemUtils.getCharge(stack) + 1);
    }

    @Override
    public boolean postMine(PostMineEvent e) {
        ItemStack stack = e.getStack();
        if (!isSuitableFor(stack, e.getState())) return super.postMine(e);

        int mode = getMode(stack);
        Direction forward = Direction.of(e.getPlayer().getHorizontalFacing());

        switch (mode) {
            case 0:
                // Normal mode
                break;
            case 1:
                // Vertical 3 mode
                mineAdjacentBlocks(e.getPlayer(), e.getWorld(), e.getPos(), e.getState(), stack, Direction.UP, 1);
                mineAdjacentBlocks(e.getPlayer(), e.getWorld(), e.getPos(), e.getState(), stack, Direction.DOWN, 1);
                break;
            case 2:
                // Horizontal 3 mode
                Direction left = forward.rotateYCounterclockwise();
                Direction right = forward.rotateYClockwise();
                mineAdjacentBlocks(e.getPlayer(), e.getWorld(), e.getPos(), e.getState(), stack, left, 1);
                mineAdjacentBlocks(e.getPlayer(), e.getWorld(), e.getPos(), e.getState(), stack, right, 1);
                break;
            case 3:
                // Depth 3 mode
                mineAdjacentBlocks(e.getPlayer(), e.getWorld(), e.getPos(), e.getState(), stack, forward, 2);
                break;
            default:
                break;
        }

        return super.postMine(e);
    }

    protected void mineAdjacentBlocks(Player player, World world, BlockPos originPos, BlockState originState, ItemStack stack, Direction direction, int distance) {
        for (int i = 1; i <= distance; i++) {
            BlockPos targetPos = PosUtil.offset(originPos, direction, i);
            BlockState targetState = world.getBlockState(targetPos);
            if (isSuitableFor(stack, targetState)) {
                WorldUtil.breakBlock(world, targetPos, true, player);
            }
        }
    }

    public List<BlockPos> getTargetBlocksFromMode(List<BlockPos> targetBlocks, World world, BlockPos blockPos, ItemStack stack, Direction direction, int mode) {

        switch (mode) {
            case 0:
                // Normal mode
                break;
            case 1:
                // Vertical 3 mode
                getTargetBlocks(targetBlocks, world, blockPos, stack, Direction.UP, 1);
                getTargetBlocks(targetBlocks, world, blockPos, stack, Direction.DOWN, 1);
                break;
            case 2:
                // Horizontal 3 mode
                Direction left = direction.rotateYCounterclockwise();
                Direction right = direction.rotateYClockwise();
                getTargetBlocks(targetBlocks, world, blockPos, stack, left, 1);
                getTargetBlocks(targetBlocks, world, blockPos, stack, right, 1);
                break;
            case 3:
                // Depth 3 mode
                getTargetBlocks(targetBlocks, world, blockPos, stack, direction, 2);
                break;
            default:
                break;
        }
        return targetBlocks;
    }

    public List<BlockPos> getTargetBlocks(List<BlockPos> targetBlocks, World world, BlockPos blockPos, ItemStack stack, Direction direction, int distance) {
        for (int i = 1; i <= distance; i++) {
            BlockPos targetPos = PosUtil.offset(blockPos, direction, i);
            BlockState targetState = world.getBlockState(targetPos);
            if (isSuitableFor(stack, targetState)) {
                targetBlocks.add(targetPos);
            }
        }
        return targetBlocks;
    }
}
