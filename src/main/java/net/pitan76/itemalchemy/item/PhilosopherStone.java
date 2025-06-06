package net.pitan76.itemalchemy.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.pitan76.itemalchemy.sound.Sounds;
import net.pitan76.itemalchemy.util.ItemCharge;
import net.pitan76.itemalchemy.util.ItemUtils;
import net.pitan76.itemalchemy.util.WorldUtils;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.event.item.EnchantableArgs;
import net.pitan76.mcpitanlib.api.event.item.ItemBarVisibleArgs;
import net.pitan76.mcpitanlib.api.event.item.ItemUseOnBlockEvent;
import net.pitan76.mcpitanlib.api.item.FixedRecipeRemainderItem;
import net.pitan76.mcpitanlib.api.item.v2.CompatItem;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.sound.CompatSoundCategory;
import net.pitan76.mcpitanlib.api.state.property.CompatProperties;
import net.pitan76.mcpitanlib.api.util.*;
import net.pitan76.mcpitanlib.api.util.block.BlockUtil;
import net.pitan76.mcpitanlib.core.Dummy;
import net.pitan76.mcpitanlib.midohra.block.BlockState;
import net.pitan76.mcpitanlib.midohra.block.BlockWrapper;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import net.pitan76.mcpitanlib.midohra.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhilosopherStone extends CompatItem implements FixedRecipeRemainderItem, ItemCharge {
    public static Map<Block, Block> exchange_map = new HashMap<>();
    public static Map<Block, Block> shift_exchange_map = new HashMap<>();

    public static void addExchangeInMap(Block target, Block replace) {
        exchange_map.put(target, replace);
    }

    // 賢者の石の等価変換に追加する
    public static boolean addExchangeInMap(CompatIdentifier target, CompatIdentifier replace) {
        if (BlockUtil.isExist(target) && BlockUtil.isExist(replace)) {
            addExchangeInMap(BlockUtil.fromId(target), BlockUtil.fromId(replace));
            return true;
        }

        return false;
    }

    public static boolean addExchangeInMap(Identifier target, Identifier replace) {
        return addExchangeInMap(CompatIdentifier.fromMinecraft(target), CompatIdentifier.fromMinecraft(replace));
    }

    public static boolean addExchangeInMap(String target, String replace) {
        return addExchangeInMap(CompatIdentifier.of(target), CompatIdentifier.of(replace));
    }

    public static boolean addShiftExchangeInMap(Block target, Block replace) {
        shift_exchange_map.put(target, replace);
        return true;
    }

    public static boolean addShiftExchangeInMap(CompatIdentifier target, CompatIdentifier replace) {
        if (BlockUtil.isExist(target) && BlockUtil.isExist(replace)) {
            addShiftExchangeInMap(BlockUtil.fromId(target), BlockUtil.fromId(replace));
            return true;
        }

        return false;
    }

    public static boolean addShiftExchangeInMap(String target, String replace) {
        return addShiftExchangeInMap(CompatIdentifier.of(target), CompatIdentifier.of(replace));
    }

    static {
        addExchangeInMap("minecraft:dirt", "minecraft:grass_block");
        addExchangeInMap("minecraft:cobblestone", "minecraft:stone");
        addExchangeInMap("minecraft:stone", "minecraft:cobblestone");
        addExchangeInMap("minecraft:grass_block", "minecraft:sand");
        addExchangeInMap("minecraft:sand", "minecraft:grass_block");
        addExchangeInMap("minecraft:glass", "minecraft:sand");

        addExchangeInMap("minecraft:nettherack", "minecraft:cobblestone");
        addExchangeInMap("minecraft:sandstone", "minecraft:gravel");
        addExchangeInMap("minecraft:pumpkin", "minecraft:melon");
        addExchangeInMap("minecraft:melon", "minecraft:pumpkin");
        addExchangeInMap("minecraft:tall_grass", "minecraft:dead_bush");
        addExchangeInMap("minecraft:red_mushroom", "minecraft:brown_mushroom");
        addExchangeInMap("minecraft:poppy", "minecraft:dandelion");

        addExchangeInMap("minecraft:oak_log", "minecraft:spruce_log");
        addExchangeInMap("minecraft:spruce_log", "minecraft:birch_log");
        addExchangeInMap("minecraft:birch_log", "minecraft:jungle_log");
        addExchangeInMap("minecraft:jungle_log", "minecraft:acacia_log");
        addExchangeInMap("minecraft:acacia_log", "minecraft:dark_oak_log");
        addExchangeInMap("minecraft:dark_oak_log", "minecraft:oak_log");

        addExchangeInMap("minecraft:oak_wood", "minecraft:spruce_wood");
        addExchangeInMap("minecraft:spruce_wood", "minecraft:birch_wood");
        addExchangeInMap("minecraft:birch_wood", "minecraft:jungle_wood");
        addExchangeInMap("minecraft:jungle_wood", "minecraft:acacia_wood");
        addExchangeInMap("minecraft:acacia_wood", "minecraft:dark_oak_wood");
        addExchangeInMap("minecraft:dark_oak_wood", "minecraft:oak_wood");

        addExchangeInMap("minecraft:stripped_oak_log", "minecraft:stripped_spruce_log");
        addExchangeInMap("minecraft:stripped_spruce_log", "minecraft:stripped_birch_log");
        addExchangeInMap("minecraft:stripped_birch_log", "minecraft:stripped_jungle_log");
        addExchangeInMap("minecraft:stripped_jungle_log", "minecraft:stripped_acacia_log");
        addExchangeInMap("minecraft:stripped_acacia_log", "minecraft:stripped_dark_oak_log");
        addExchangeInMap("minecraft:stripped_dark_oak_log", "minecraft:stripped_oak_log");

        addExchangeInMap("minecraft:oak_leaves", "minecraft:spruce_leaves");
        addExchangeInMap("minecraft:spruce_leaves", "minecraft:birch_leaves");
        addExchangeInMap("minecraft:birch_leaves", "minecraft:jungle_leaves");
        addExchangeInMap("minecraft:jungle_leaves", "minecraft:acacia_leaves");
        addExchangeInMap("minecraft:acacia_leaves", "minecraft:dark_oak_leaves");
        addExchangeInMap("minecraft:dark_oak_leaves", "minecraft:oak_leaves");

        addExchangeInMap("minecraft:oak_planks", "minecraft:spruce_planks");
        addExchangeInMap("minecraft:spruce_planks", "minecraft:birch_planks");
        addExchangeInMap("minecraft:birch_planks", "minecraft:jungle_planks");
        addExchangeInMap("minecraft:jungle_planks", "minecraft:acacia_planks");
        addExchangeInMap("minecraft:acacia_planks", "minecraft:dark_oak_planks");
        addExchangeInMap("minecraft:dark_oak_planks", "minecraft:oak_planks");

        addExchangeInMap("minecraft:andesite", "minecraft:granite");
        addExchangeInMap("minecraft:granite", "minecraft:diorite");
        addExchangeInMap("minecraft:diorite", "minecraft:andesite");

        addShiftExchangeInMap("minecraft:stone", "minecraft:grass_block");
        addShiftExchangeInMap("minecraft:cobblestone", "minecraft:grass_block");
        addShiftExchangeInMap("minecraft:grass_block", "minecraft:cobblestone");
        addShiftExchangeInMap("minecraft:sand", "minecraft:cobblestone");
    }

    public PhilosopherStone(CompatibleItemSettings settings) {
        super(settings); //.recipeRemainder(Items.PHILOSOPHER_STONE.getOrNull())
    }

    @Override
    public CompatActionResult onRightClickOnBlock(ItemUseOnBlockEvent e) {
        World world = e.getMidohraWorld();
        if (!e.isClient()) {
            BlockPos pos = e.getMidohraPos();
            BlockState state = e.getMidohraState();
            Player player = e.player;

            if (!isExchange(state.getBlock()))
                return e.success();

            List<BlockPos> blocks = WorldUtils.getTargetBlocks(world, pos, ItemUtils.getCharge(e.stack), true, true);

            Block replaceBlock = getExchangeBlock(state.getBlock(), player.isSneaking());
            BlockState replaceState = BlockState.of(replaceBlock);

            if (replaceBlock == null)
                return e.success();

            blocks.forEach(pos2 -> exchangeBlock(world, pos2, replaceState, world.getBlockState(pos)));

            world.playSound(null, pos, Sounds.EXCHANGE_SOUND, CompatSoundCategory.PLAYERS, 0.15f, 1f);
            return e.success();
        }

        return super.onRightClickOnBlock(e);
    }

    public void exchangeBlock(World world, BlockPos blockPos, BlockState newBlockState, BlockState blockState) {
        if (newBlockState.contains(CompatProperties.FACING) && blockState.contains(CompatProperties.FACING)) {
            newBlockState = newBlockState.with(CompatProperties.FACING, blockState.get(CompatProperties.FACING));
        }
        if (newBlockState.contains(CompatProperties.HORIZONTAL_FACING) && blockState.contains(CompatProperties.HORIZONTAL_FACING)) {
            newBlockState = newBlockState.with(CompatProperties.HORIZONTAL_FACING, blockState.get(CompatProperties.HORIZONTAL_FACING));
        }

        world.setBlockState(blockPos, newBlockState);
    }

    public void exchangeBlock(net.minecraft.world.World world, net.minecraft.util.math.BlockPos blockPos, net.minecraft.block.BlockState newBlockState, net.minecraft.block.BlockState blockState) {
        exchangeBlock(World.of(world), BlockPos.of(blockPos), BlockState.of(newBlockState), BlockState.of(blockState));
    }

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack) {
        return getFixedRecipeRemainder(stack);
    }

    @Override
    public boolean hasRecipeRemainder(Dummy dummy) {
        return true;
    }

    @Override
    public ItemStack getFixedRecipeRemainder(ItemStack stack) {
        return stack;
    }

    @Nullable
    public static Block getExchangeBlock(Block target, boolean isSneaking) {
        if (isSneaking) {
            if (shift_exchange_map.containsKey(target)) {
                return shift_exchange_map.get(target);
            }
        }

        if (exchange_map.containsKey(target)) {
            return exchange_map.get(target);
        }

        return null;
    }

    public static Block getExchangeBlock(BlockWrapper target, boolean isSneaking) {
        return getExchangeBlock(target.get(), isSneaking);
    }

    public static boolean isExchange(Block block) {
        return exchange_map.containsKey(block) || shift_exchange_map.containsKey(block);
    }

    public static boolean isExchange(BlockWrapper block) {
        return isExchange(block.get());
    }

    @Override
    public boolean isItemBarVisible(ItemBarVisibleArgs args) {
        return CustomDataUtil.contains(args.getStack(), "itemalchemy");
    }

    @Override
    public boolean isEnchantable(EnchantableArgs args) {
        return false;
    }
}
