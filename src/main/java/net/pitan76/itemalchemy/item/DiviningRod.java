package net.pitan76.itemalchemy.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.util.TooltipUtil;
import net.pitan76.mcpitanlib.api.event.item.ItemAppendTooltipEvent;
import net.pitan76.mcpitanlib.api.event.item.ItemUseEvent;
import net.pitan76.mcpitanlib.api.event.item.ItemUseOnBlockEvent;
import net.pitan76.mcpitanlib.api.item.v2.CompatItem;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.util.*;
import net.pitan76.mcpitanlib.api.util.block.BlockUtil;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import net.pitan76.mcpitanlib.midohra.util.math.Direction;
import net.pitan76.mcpitanlib.midohra.world.World;

public class DiviningRod extends CompatItem  {

    public final int maxLevel;

    public DiviningRod(int maxLevel, CompatibleItemSettings settings) {
        super(settings);
        this.maxLevel = maxLevel;
    }

    public DiviningRod(CompatibleItemSettings settings) {
        this(0, settings);
    }

    @Override
    public CompatActionResult onRightClickOnBlock(ItemUseOnBlockEvent e) {
        BlockPos center = e.getMidohraPos();
        World world = e.getMidohraWorld();

        // スニークしている場合、レベルを変更する、最大をこえるとレベル1に戻る
        if (e.isSneaking()) {
            ItemStack stack = e.getStack();
            int level = 1;
            if (CustomDataUtil.contains(stack, "divining_rod_level")) {
                level = CustomDataUtil.get(stack, "divining_rod_level", Integer.class) + 1;
                if (level > maxLevel) level = 1;
            }
            CustomDataUtil.put(stack, "divining_rod_level", level);
            e.getPlayer().sendMessage(TextUtil.translatable("message.itemalchemy.diving_rod_switch_mode", "3x3x" + (3 + getAdditionalDepth(level))));

            return CompatActionResult.SUCCESS;
        }

        int x = 1;
        int y = 1;
        int z = 1;

        int x1 = -x;
        int x2 = x;
        int y1 = -y;
        int y2 = y;
        int z1 = -z;
        int z2 = z;

        // 1より最大レベルが高い場合、プレイヤーの持っている杖のNBTにあるレベルに応じて、調査するブロックの範囲を広げる
        if (maxLevel > 1) {
            ItemStack stack = e.getStack();
            if (CustomDataUtil.contains(stack, "divining_rod_level")) {
                int level = CustomDataUtil.get(stack, "divining_rod_level", Integer.class);
                int depth = getAdditionalDepth(level);

                Direction dir = Direction.of(e.getSide());
                if (dir.equals(Direction.UP)) {
                    y2 += depth;
                } else if (dir.equals(Direction.DOWN)) {
                    y1 -= depth;
                } else if (dir.equals(Direction.NORTH)) {
                    z1 += depth;
                } else if (dir.equals(Direction.SOUTH)) {
                    z2 -= depth;
                } else if (dir.equals(Direction.WEST)) {
                    x1 += depth;
                } else if (dir.equals(Direction.EAST)) {
                    x2 -= depth;
                }
            }
        }

        long maxEMC = 0;
        long totalEMC = 0;
        int blockCount = 0;

        // 中心から1ブロックずつ、合計 3 x 3 x * ブロックを調べる
        for (int dx = x1; dx <= x2; dx++) {
            for (int dy = y1; dy <= y2; dy++) {
                for (int dz = z1; dz <= z2; dz++) {
                    BlockPos checkPos = center.add(dx, dy, dz);
                    if (world.isAir(checkPos)) continue;

                    Block block = world.getBlock(checkPos);
                    long emc = EMCManager.get(BlockUtil.toItem(block));

                    if (emc > maxEMC)
                        maxEMC = emc;

                    totalEMC += emc;

                    blockCount++;
                }
            }
        }

        e.getPlayer().sendMessage(TextUtil.translatable("message.itemalchemy.diving_rod_average_emc", "Average EMC for " + blockCount + " blocks: " + (blockCount > 0 ? totalEMC / blockCount : 0)));
        e.getPlayer().sendMessage(TextUtil.translatable("message.itemalchemy.diving_rod_max_emc", maxEMC));

        return super.onRightClickOnBlock(e);
    }

    @Override
    public StackActionResult onRightClick(ItemUseEvent e) {
        if (e.isSneaking()) {
            ItemStack stack = e.getStack();
            int level = 1;
            if (CustomDataUtil.contains(stack, "divining_rod_level")) {
                level = CustomDataUtil.get(stack, "divining_rod_level", Integer.class) + 1;
                if (level > maxLevel) level = 1;
            }
            CustomDataUtil.put(stack, "divining_rod_level", level);
            e.user.sendMessage("Divining Rod Level: " + level + " (3x3x" + (3 + getAdditionalDepth(level)) + ")");

            return e.success();
        }

        return super.onRightClick(e);
    }

    @Override
    public void appendTooltip(ItemAppendTooltipEvent e) {
        ItemStack stack = e.getStack();
        e.addTooltip(TooltipUtil.generateTooltipLines(ItemStackUtil.getItem(stack)));
        
        if (CustomDataUtil.contains(stack, "divining_rod_level")) {
            int level = CustomDataUtil.get(stack, "divining_rod_level", Integer.class);
            e.addTooltip(TextUtil.literal("Mode: §b3x3x" + (3 + getAdditionalDepth(level))));
        } else {
            e.addTooltip(TextUtil.literal("Mode: §b3x3x3"));
        }
    }

    // レベルに応じて、調査するブロックの深さを追加
    public int getAdditionalDepth(int level) {
        switch (level) {
            case 1:
                return 0;
            case 2:
                return 16 - 3;
            case 3:
                return 64 - 3;
            default:
                return 0;
        }
    }
}
