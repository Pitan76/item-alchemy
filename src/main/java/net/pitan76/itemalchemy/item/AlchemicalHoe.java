package net.pitan76.itemalchemy.item;

import net.pitan76.itemalchemy.util.IRechargeableFromKlein;
import net.pitan76.itemalchemy.util.ItemUtils;
import net.pitan76.itemalchemy.util.TooltipUtil;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.event.item.ItemAppendTooltipEvent;
import net.pitan76.mcpitanlib.api.event.item.ItemBarVisibleArgs;
import net.pitan76.mcpitanlib.api.event.item.PostMineEvent;
import net.pitan76.mcpitanlib.api.item.args.tool.MiningSpeedMultiplierArgs;
import net.pitan76.mcpitanlib.api.item.args.tool.SuitableForArgs;
import net.pitan76.mcpitanlib.api.item.tool.CompatibleToolMaterial;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.item.v3.tool.CompatHoeItem;
import net.pitan76.mcpitanlib.api.text.TextComponent;
import net.pitan76.mcpitanlib.api.util.CustomDataUtil;
import net.pitan76.mcpitanlib.midohra.block.BlockState;
import net.pitan76.mcpitanlib.midohra.item.ItemStack;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import net.pitan76.mcpitanlib.midohra.util.math.Direction;
import net.pitan76.mcpitanlib.midohra.world.World;

import java.util.stream.Collectors;

public class AlchemicalHoe extends CompatHoeItem implements IRechargeableFromKlein {
    public AlchemicalHoe(CompatibleToolMaterial toolMaterial, int attackDamage, float attackSpeed, CompatibleItemSettings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public void appendTooltip(ItemAppendTooltipEvent e, Options options) {
        ItemStack stack = e.getStackM();
        e.addTooltip(TooltipUtil.generateTooltipLines(stack.getItem())
                .stream().map(TextComponent::getText).collect(Collectors.toList()));
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
    public float getMiningSpeedMultiplier(MiningSpeedMultiplierArgs args) {
        if (!isSuitableFor(new SuitableForArgs(args.getState())))
            return super.getMiningSpeedMultiplier(args);

        return super.getMiningSpeedMultiplier(args) * (ItemUtils.getCharge(args.getStack()) + 1);
    }

    @Override
    public boolean postMine(PostMineEvent e) {
        ItemStack stack = e.getStackM();

        if (!isSuitableFor(new SuitableForArgs(e.getStateM()))) return super.postMine(e);

        int charge = ItemUtils.getCharge(stack);

        // Expand tilling area based on charge level
        if (charge > 0) {
            World world = e.getWorldM();
            BlockPos originPos = e.getPosM();

            // Till adjacent blocks in horizontal plane (3x3 area)
            Player player = e.getPlayer();
            Direction facing = player.getHorizontalFacingM();
            Direction left = facing.rotateYCounterclockwise();
            Direction right = facing.rotateYClockwise();
            
            tillAdjacentBlocks(e.getPlayer(), world, originPos, left, 1);
            tillAdjacentBlocks(e.getPlayer(), world, originPos, right, 1);
            tillAdjacentBlocks(e.getPlayer(), world, originPos, facing.rotateYCounterclockwise().rotateYCounterclockwise(), 1);
            tillAdjacentBlocks(e.getPlayer(), world, originPos, facing.rotateYClockwise().rotateYClockwise(), 1);
        }

        // Consume 1 charge per block tilled if charge > 0
        if (charge > 0) {
            ItemUtils.setCharge(stack, charge - 1);
        }

        return super.postMine(e);
    }

    protected void tillAdjacentBlocks(Player player, World world, BlockPos originPos, Direction direction, int distance) {
        for (int i = 1; i <= distance; i++) {
            BlockPos targetPos = originPos.offset(direction, i);
            BlockState targetState = world.getBlockState(targetPos);
            if (isSuitableFor(new SuitableForArgs(targetState))) {
                world.breakBlock(targetPos, true, player);
            }
        }
    }

    @Override
    public int getEmcCostPerCharge() {
        return 1000;
    }
}
