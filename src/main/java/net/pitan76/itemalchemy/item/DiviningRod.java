package net.pitan76.itemalchemy.item;

import net.minecraft.block.Block;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.mcpitanlib.api.event.item.ItemAppendTooltipEvent;
import net.pitan76.mcpitanlib.api.event.item.ItemUseEvent;
import net.pitan76.mcpitanlib.api.event.item.ItemUseOnBlockEvent;
import net.pitan76.mcpitanlib.api.item.v2.CompatItem;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.util.CompatActionResult;
import net.pitan76.mcpitanlib.api.util.StackActionResult;
import net.pitan76.mcpitanlib.api.util.TextUtil;
import net.pitan76.mcpitanlib.api.util.WorldUtil;
import net.pitan76.mcpitanlib.api.util.block.BlockUtil;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;

public class DiviningRod extends CompatItem  {
    public DiviningRod(int i, CompatibleItemSettings settings) {
        super(settings);
    }

    public DiviningRod(CompatibleItemSettings settings) {
        this(1, settings);
    }

    @Override
    public CompatActionResult onRightClickOnBlock(ItemUseOnBlockEvent e) {
        // TODO: 3x3x3以外に切り替えられるようにする、メッセージの多言語対応
        BlockPos center = e.getMidohraPos();

        int x = 1;
        int y = 1;
        int z = 1;

        long totalEMC = 0;
        int blockCount = 0;

        // 中心から1ブロックずつ、合計3x3x3=27ブロックを調べる
        for (int dx = -x; dx <= x; dx++) {
            for (int dy = -y; dy <= y; dy++) {
                for (int dz = -z; dz <= z; dz++) {
                    BlockPos checkPos = center.add(dx, dy, dz);
                    if (WorldUtil.isAir(e.world, checkPos.toRaw())) continue;

                    Block block = WorldUtil.getBlock(e.world, checkPos.toRaw());
                    totalEMC += EMCManager.get(BlockUtil.toItem(block));
                    blockCount++;
                }
            }
        }

        e.getPlayer().sendMessage(TextUtil.literal("Average EMC for " + blockCount + " blocks: " + (blockCount > 0 ? totalEMC / blockCount : 0)));
        e.getPlayer().sendMessage(TextUtil.literal("Max EMC: " + totalEMC));

        return super.onRightClickOnBlock(e);
    }

    @Override
    public StackActionResult onRightClick(ItemUseEvent e) {
        return super.onRightClick(e);
    }

    @Override
    public void appendTooltip(ItemAppendTooltipEvent e) {
        super.appendTooltip(e);
        e.addTooltip(TextUtil.literal("not implemented yet"));
    }
}
