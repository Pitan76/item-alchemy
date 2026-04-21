package net.pitan76.itemalchemy.item;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.pitan76.itemalchemy.util.IRechargeableFromKlein;
import net.pitan76.itemalchemy.util.ItemUtils;
import net.pitan76.itemalchemy.util.TooltipUtil;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.event.item.ItemAppendTooltipEvent;
import net.pitan76.mcpitanlib.api.event.item.ItemBarVisibleArgs;
import net.pitan76.mcpitanlib.api.event.item.PostMineEvent;
import net.pitan76.mcpitanlib.api.item.tool.CompatibleShovelItem;
import net.pitan76.mcpitanlib.api.item.tool.CompatibleToolMaterial;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.util.*;
import net.pitan76.mcpitanlib.midohra.util.math.Direction;
import net.pitan76.mcpitanlib.api.util.math.PosUtil;
import net.minecraft.util.math.BlockPos;
import net.pitan76.mcpitanlib.api.util.WorldUtil;

public class AlchemicalShovel extends CompatibleShovelItem implements IRechargeableFromKlein {
    public AlchemicalShovel(CompatibleToolMaterial toolMaterial, float attackDamage, float attackSpeed, CompatibleItemSettings settings) {
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

        int charge = ItemUtils.getCharge(stack);

        // Expand digging area based on charge level
        if (charge > 0) {
            // Mine adjacent blocks in horizontal plane (3x3 area)
            Player player = e.getPlayer();
            Direction facing = Direction.of(player.getHorizontalFacing());
            Direction left = facing.rotateYCounterclockwise();
            Direction right = facing.rotateYClockwise();
            
            mineAdjacentBlocks(e.getPlayer(), e.getWorld(), e.getPos(), left, 1);
            mineAdjacentBlocks(e.getPlayer(), e.getWorld(), e.getPos(), right, 1);
            mineAdjacentBlocks(e.getPlayer(), e.getWorld(), e.getPos(), facing.rotateYCounterclockwise().rotateYCounterclockwise(), 1);
            mineAdjacentBlocks(e.getPlayer(), e.getWorld(), e.getPos(), facing.rotateYClockwise().rotateYClockwise(), 1);
        }

        // Consume 1 charge per block mined if charge > 0
        if (charge > 0) {
            ItemUtils.setCharge(stack, charge - 1);
        }

        return super.postMine(e);
    }

    protected void mineAdjacentBlocks(Player player, net.minecraft.world.World world, BlockPos originPos, Direction direction, int distance) {
        for (int i = 1; i <= distance; i++) {
            BlockPos targetPos = PosUtil.offset(originPos, direction, i);
            BlockState targetState = WorldUtil.getBlockState(world, targetPos);
            if (overrideIsSuitableFor(targetState)) {
                WorldUtil.breakBlock(world, targetPos, true, player);
            }
        }
    }

    @Override
    public int getEmcCostPerCharge() {
        return 1000;
    }
}
