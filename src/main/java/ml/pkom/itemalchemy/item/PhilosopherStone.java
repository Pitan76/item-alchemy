package ml.pkom.itemalchemy.item;

import ml.pkom.itemalchemy.sound.Sounds;
import ml.pkom.itemalchemy.util.ItemCharge;
import ml.pkom.itemalchemy.util.ItemUtils;
import ml.pkom.itemalchemy.util.WorldUtils;
import ml.pkom.mcpitanlibarch.Dummy;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.event.item.ItemUseOnBlockEvent;
import ml.pkom.mcpitanlibarch.api.item.CompatibleItemSettings;
import ml.pkom.mcpitanlibarch.api.item.ExtendItem;
import ml.pkom.mcpitanlibarch.api.item.FixedRecipeRemainderItem;
import ml.pkom.mcpitanlibarch.api.util.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhilosopherStone extends ExtendItem implements FixedRecipeRemainderItem, ItemCharge {
    public static Map<Block, Block> exchange_map = new HashMap<>();
    public static Map<Block, Block> shift_exchange_map = new HashMap<>();

    public static void addExchangeInMap(Block target, Block replace) {
        exchange_map.put(target, replace);
    }

    // 賢者の石の等価変換に追加する
    public static boolean addExchangeInMap(Identifier target, Identifier replace) {
        if (BlockUtil.isExist(target) && BlockUtil.isExist(replace)) {
            addExchangeInMap(BlockUtil.block(target), BlockUtil.block(replace));
            return true;
        }

        return false;
    }

    public static boolean addExchangeInMap(String target, String replace) {
        return addExchangeInMap(new Identifier(target), new Identifier(replace));
    }

    public static boolean addShiftExchangeInMap(Block target, Block replace) {
        shift_exchange_map.put(target, replace);
        return true;
    }

    public static boolean addShiftExchangeInMap(Identifier target, Identifier replace) {
        if (BlockUtil.isExist(target) && BlockUtil.isExist(replace)) {
            addShiftExchangeInMap(BlockUtil.block(target), BlockUtil.block(replace));
            return true;
        }

        return false;
    }

    public static boolean addShiftExchangeInMap(String target, String replace) {
        return addShiftExchangeInMap(new Identifier(target), new Identifier(replace));
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
    public ActionResult onRightClickOnBlock(ItemUseOnBlockEvent event) {
        World world = event.world;
        if (!world.isClient()) {
            BlockPos targetPos = event.hit.getBlockPos();
            BlockState targetBlockState = world.getBlockState(targetPos);
            Player player = event.player;

            if(!isExchange(targetBlockState.getBlock())) {
                return ActionResult.SUCCESS;
            }

            List<BlockPos> blocks = WorldUtils.getTargetBlocks(world, targetPos, ItemUtils.getCharge(event.stack), true, true);

            Block replaceBlock = getExchangeBlock(targetBlockState.getBlock(), player.getPlayerEntity().isSneaking());

            if(replaceBlock == null) {
                return ActionResult.SUCCESS;
            }

            for (BlockPos pos : blocks) {
                exchangeBlock(world, pos, replaceBlock.getDefaultState(), world.getBlockState(pos));
            }

            world.playSound(null, targetPos, Sounds.EXCHANGE_SOUND.getOrNull(), SoundCategory.PLAYERS, 0.15f, 1f);

            return ActionResult.SUCCESS;
        }

        return super.onRightClickOnBlock(event);
    }

    public void exchangeBlock(World world, BlockPos blockPos, BlockState newBlockState, BlockState blockState) {
        if (newBlockState.contains(Properties.FACING) && blockState.contains(Properties.FACING)) {
            newBlockState = newBlockState.with(Properties.FACING, blockState.get(Properties.FACING));
        }
        if (newBlockState.contains(Properties.HORIZONTAL_FACING) && blockState.contains(Properties.HORIZONTAL_FACING)) {
            newBlockState = newBlockState.with(Properties.HORIZONTAL_FACING, blockState.get(Properties.HORIZONTAL_FACING));
        }
        //world.playSound(null, blockPos, Sounds.EXCHANGE_SOUND.getOrNull(), SoundCategory.PLAYERS, 0.15f, 1f);
        world.setBlockState(blockPos, newBlockState);
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
    public Rarity getRarity(ItemStack stack) {
        return Rarity.EPIC;
    }

    @Override
    public ItemStack getFixedRecipeRemainder(ItemStack stack) {
        return stack;
    }

    @Nullable
    public static Block getExchangeBlock(Block target, boolean isSneaking) {
        if(isSneaking) {
            if(shift_exchange_map.containsKey(target)) {
                return shift_exchange_map.get(target);
            }
        }

        if(exchange_map.containsKey(target)) {
            return exchange_map.get(target);
        }

        return null;
    }

    public static boolean isExchange(Block block) {
        return exchange_map.containsKey(block) || shift_exchange_map.containsKey(block);
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return stack.getSubNbt("itemalchemy") != null;
    }
}
