package net.pitan76.itemalchemy.item;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.pitan76.itemalchemy.util.TooltipUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pitan76.itemalchemy.util.IRechargeableFromKlein;
import net.pitan76.itemalchemy.util.ItemUtils;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.event.item.ItemAppendTooltipEvent;
import net.pitan76.mcpitanlib.api.event.item.ItemBarVisibleArgs;
import net.pitan76.mcpitanlib.api.event.item.PostMineEvent;
import net.pitan76.mcpitanlib.api.item.tool.CompatiblePickaxeItem;
import net.pitan76.mcpitanlib.api.item.tool.CompatibleToolMaterial;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.util.CustomDataUtil;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;
import net.pitan76.mcpitanlib.api.util.WorldUtil;
import net.pitan76.mcpitanlib.api.util.math.PosUtil;
import net.pitan76.mcpitanlib.midohra.util.math.Direction;

import java.util.List;

public class AlchemicalPickaxe extends CompatiblePickaxeItem implements IRechargeableFromKlein, AlchemicalToolMode {
    public AlchemicalPickaxe(CompatibleToolMaterial toolMaterial, int attackDamage, float attackSpeed, CompatibleItemSettings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public void appendTooltip(ItemAppendTooltipEvent e, Options options) {
        ItemStack stack = e.getStack();
        e.addTooltip(TooltipUtil.generateTooltipLines(ItemStackUtil.getItem(stack)));
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
        if (!overrideIsSuitableFor(state))
            return super.overrideGetMiningSpeedMultiplier(stack, state);

        return super.overrideGetMiningSpeedMultiplier(stack, state) * (ItemUtils.getCharge(stack) + 1);
    }

    @Override
    public boolean postMine(PostMineEvent e) {
        ItemStack stack = e.getStack();
        if (!overrideIsSuitableFor(e.getState())) return super.postMine(e);

        int mode = getMode(stack);
        Direction forward = Direction.of(e.getPlayer().getHorizontalFacing());
        int charge = ItemUtils.getCharge(stack);

        // Expand mining area based on charge level when in normal mode (mode 0)
        if (mode == 0 && charge > 0) {
            // Mine additional blocks based on charge level
            // Charge 1: +1 block each direction (3x3 area)
            // Charge 2+: same as charge 1
            mineAdjacentBlocks(e.getPlayer(), e.getWorld(), e.getPos(), Direction.UP, 1);
            mineAdjacentBlocks(e.getPlayer(), e.getWorld(), e.getPos(), Direction.DOWN, 1);
            Direction left = forward.rotateYCounterclockwise();
            Direction right = forward.rotateYClockwise();
            mineAdjacentBlocks(e.getPlayer(), e.getWorld(), e.getPos(), left, 1);
            mineAdjacentBlocks(e.getPlayer(), e.getWorld(), e.getPos(), right, 1);
        } else {
            // Use existing mode system
            switch (mode) {
                case 0:
                    // Normal mode
                    break;
                case 1:
                    // Vertical 3 mode
                    mineAdjacentBlocks(e.getPlayer(), e.getWorld(), e.getPos(), Direction.UP, 1);
                    mineAdjacentBlocks(e.getPlayer(), e.getWorld(), e.getPos(), Direction.DOWN, 1);
                    break;
                case 2:
                    // Horizontal 3 mode
                    Direction left = forward.rotateYCounterclockwise();
                    Direction right = forward.rotateYClockwise();
                    mineAdjacentBlocks(e.getPlayer(), e.getWorld(), e.getPos(), left, 1);
                    mineAdjacentBlocks(e.getPlayer(), e.getWorld(), e.getPos(), right, 1);
                    break;
                case 3:
                    // Depth 3 mode
                    mineAdjacentBlocks(e.getPlayer(), e.getWorld(), e.getPos(), forward, 2);
                    break;
                default:
                    break;
            }
        }

        // Consume 1 charge per block mined if charge > 0
        if (charge > 0) {
            ItemUtils.setCharge(stack, charge - 1);
        }

        return super.postMine(e);
    }

    protected void mineAdjacentBlocks(Player player, World world, BlockPos originPos, Direction direction, int distance) {
        for (int i = 1; i <= distance; i++) {
            BlockPos targetPos = PosUtil.offset(originPos, direction, i);
            BlockState targetState = WorldUtil.getBlockState(world, targetPos);
            if (overrideIsSuitableFor(targetState)) {
                WorldUtil.breakBlock(world, targetPos, true, player);
            }
        }
    }

    public void getTargetBlocksFromMode(List<BlockPos> targetBlocks, World world, BlockPos blockPos, ItemStack stack, Direction direction, int mode) {
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
    }

    public void getTargetBlocks(List<BlockPos> targetBlocks, World world, BlockPos blockPos, ItemStack stack, Direction direction, int distance) {
        for (int i = 1; i <= distance; i++) {
            BlockPos targetPos = PosUtil.offset(blockPos, direction, i);
            BlockState targetState = WorldUtil.getBlockState(world, targetPos);
            if (overrideIsSuitableFor(targetState)) {
                targetBlocks.add(targetPos);
            }
        }
    }

    @Override
    public int getEmcCostPerCharge() {
        return 1200;
    }
}
