package ml.pkom.itemalchemy.item;

import com.google.common.collect.Lists;
import ml.pkom.itemalchemy.api.ItemCharge;
import ml.pkom.itemalchemy.sound.Sounds;
import ml.pkom.mcpitanlibarch.Dummy;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.event.item.ItemUseOnBlockEvent;
import ml.pkom.mcpitanlibarch.api.item.CompatibleItemSettings;
import ml.pkom.mcpitanlibarch.api.item.ExtendItem;
import ml.pkom.mcpitanlibarch.api.item.FixedRecipeRemainderItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PhilosopherStone extends ExtendItem implements FixedRecipeRemainderItem, ItemCharge {
    public static Map<Block, Block> exchange_map = new HashMap<>();
    public static Map<Block, Block> shift_exchange_map = new HashMap<>();

    static {
        exchange_map.put(Blocks.DIRT, Blocks.GRASS_BLOCK);

        exchange_map.put(Blocks.COBBLESTONE, Blocks.STONE);
        exchange_map.put(Blocks.STONE, Blocks.COBBLESTONE);
        exchange_map.put(Blocks.GRASS_BLOCK, Blocks.SAND);
        exchange_map.put(Blocks.SAND, Blocks.GRASS_BLOCK);
        exchange_map.put(Blocks.GLASS, Blocks.SAND);
        exchange_map.put(Blocks.NETHERRACK, Blocks.COBBLESTONE);
        exchange_map.put(Blocks.SANDSTONE, Blocks.GRAVEL);
        exchange_map.put(Blocks.PUMPKIN, Blocks.MELON);
        exchange_map.put(Blocks.MELON, Blocks.PUMPKIN);
        exchange_map.put(Blocks.TALL_GRASS, Blocks.DEAD_BUSH);
        exchange_map.put(Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM);
        exchange_map.put(Blocks.POPPY, Blocks.DANDELION);

        exchange_map.put(Blocks.OAK_LOG, Blocks.SPRUCE_LOG);
        exchange_map.put(Blocks.SPRUCE_LOG, Blocks.BIRCH_LOG);
        exchange_map.put(Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG);
        exchange_map.put(Blocks.JUNGLE_LOG, Blocks.ACACIA_LOG);
        exchange_map.put(Blocks.ACACIA_LOG, Blocks.DARK_OAK_LOG);
        exchange_map.put(Blocks.DARK_OAK_LOG, Blocks.OAK_LOG);

        exchange_map.put(Blocks.OAK_WOOD, Blocks.SPRUCE_WOOD);
        exchange_map.put(Blocks.SPRUCE_WOOD, Blocks.BIRCH_WOOD);
        exchange_map.put(Blocks.BIRCH_WOOD, Blocks.JUNGLE_WOOD);
        exchange_map.put(Blocks.JUNGLE_WOOD, Blocks.ACACIA_WOOD);
        exchange_map.put(Blocks.ACACIA_WOOD, Blocks.DARK_OAK_WOOD);
        exchange_map.put(Blocks.DARK_OAK_WOOD, Blocks.OAK_WOOD);

        exchange_map.put(Blocks.STRIPPED_OAK_LOG, Blocks.STRIPPED_SPRUCE_LOG);
        exchange_map.put(Blocks.STRIPPED_SPRUCE_LOG, Blocks.STRIPPED_BIRCH_LOG);
        exchange_map.put(Blocks.STRIPPED_BIRCH_LOG, Blocks.STRIPPED_JUNGLE_LOG);
        exchange_map.put(Blocks.STRIPPED_JUNGLE_LOG, Blocks.STRIPPED_ACACIA_LOG);
        exchange_map.put(Blocks.STRIPPED_ACACIA_LOG, Blocks.STRIPPED_DARK_OAK_LOG);
        exchange_map.put(Blocks.STRIPPED_DARK_OAK_LOG, Blocks.STRIPPED_OAK_LOG);

        exchange_map.put(Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES);
        exchange_map.put(Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES);
        exchange_map.put(Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES);
        exchange_map.put(Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES);
        exchange_map.put(Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES);
        exchange_map.put(Blocks.DARK_OAK_LEAVES, Blocks.OAK_LEAVES);

        exchange_map.put(Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS);
        exchange_map.put(Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS);
        exchange_map.put(Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS);
        exchange_map.put(Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS);
        exchange_map.put(Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS);
        exchange_map.put(Blocks.DARK_OAK_PLANKS, Blocks.OAK_PLANKS);

        exchange_map.put(Blocks.ANDESITE, Blocks.GRANITE);
        exchange_map.put(Blocks.GRANITE, Blocks.DIORITE);
        exchange_map.put(Blocks.DIORITE, Blocks.ANDESITE);

        shift_exchange_map.put(Blocks.STONE, Blocks.GRASS_BLOCK);
        shift_exchange_map.put(Blocks.COBBLESTONE, Blocks.GRASS_BLOCK);
        shift_exchange_map.put(Blocks.GRASS_BLOCK, Blocks.COBBLESTONE);
        shift_exchange_map.put(Blocks.SAND, Blocks.COBBLESTONE);
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

            List<BlockPos> blocks = getTargetBlocks(world, targetPos, ((ItemCharge)event.stack.getItem()).getCharge(event.stack));

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
        world.playSound(null, blockPos, Sounds.EXCHANGE_SOUND.getOrNull(), SoundCategory.PLAYERS, 0.15f, 1f);
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

    public static List<BlockPos> getTargetBlocks(World world, BlockPos pos, int range) {
        BlockState baseBlock = world.getBlockState(pos);

        if(!isExchange(baseBlock.getBlock())) {
            return Collections.emptyList();
        }

        if(range <= 0) {
            return Lists.newArrayList(pos);
        }

        List<BlockPos> blocks = new ArrayList<>();

        for (int y = 0; y < 1 + range * 2; y++) {
            for (int x = 0; x < 1 + range * 2; x++) {
                for (int z = 0; z < 1 + range * 2; z++) {
                    int offsetX = range - x;
                    int offsetY = range - y;
                    int offsetZ = range - z;

                    BlockPos targetPos = pos.add(offsetX, offsetY, offsetZ);

                    BlockState block = world.getBlockState(targetPos);

                    if(block.isAir()) {
                        continue;
                    }

                    if(!block.isOf(baseBlock.getBlock())) {
                        continue;
                    }

                    blocks.add(targetPos);
                }
            }
        }

        return blocks;
    }

    public static boolean isExchange(Block block) {
        return exchange_map.containsKey(block) || shift_exchange_map.containsKey(block);
    }
}
