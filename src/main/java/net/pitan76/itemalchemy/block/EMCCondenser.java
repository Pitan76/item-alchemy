package net.pitan76.itemalchemy.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.pitan76.itemalchemy.item.Wrench;
import net.pitan76.itemalchemy.tile.EMCCondenserTile;
import net.pitan76.itemalchemy.tile.Tiles;
import net.pitan76.mcpitanlib.api.block.ExtendBlockEntityProvider;
import net.pitan76.mcpitanlib.api.block.v2.CompatBlock;
import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.event.block.*;
import net.pitan76.mcpitanlib.api.state.property.CompatProperties;
import net.pitan76.mcpitanlib.api.state.property.DirectionProperty;
import net.pitan76.mcpitanlib.api.util.*;
import net.pitan76.mcpitanlib.api.util.color.CompatMapColor;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;
import net.pitan76.mcpitanlib.core.serialization.codecs.CompatBlockMapCodecUtil;
import org.jetbrains.annotations.Nullable;

public class EMCCondenser extends CompatBlock implements ExtendBlockEntityProvider, IUseableWrench {
    public static DirectionProperty FACING = CompatProperties.HORIZONTAL_FACING;
    public static final Text TITLE = TextUtil.translatable("container.itemalchemy.emc_condenser");

    public long maxEMC = 100000;

    protected CompatMapCodec<? extends Block> CODEC = CompatBlockMapCodecUtil.createCodec(EMCCondenser::new);

    @Override
    public CompatMapCodec<? extends Block> getCompatCodec() {
        return CODEC;
    }

    public EMCCondenser(CompatibleBlockSettings settings) {
        super(settings);
        setNewDefaultState(BlockStateUtil.getDefaultState(this).with(FACING.getProperty(), Direction.NORTH));
    }

    public EMCCondenser(CompatIdentifier id) {
        this(CompatibleBlockSettings.copy(id, Blocks.STONE).mapColor(CompatMapColor.BLACK).strength(2f, 7.0f));
    }

    public long getMaxEMC() {
        return maxEMC;
    }

    @Override
    public void appendProperties(AppendPropertiesArgs args) {
        args.addProperty(FACING);
        super.appendProperties(args);
    }

    @Override
    public void onStateReplaced(StateReplacedEvent e) {
        World world = e.world;
        BlockPos pos = e.pos;

        if (e.isSameState()) return;

        BlockEntity blockEntity = WorldUtil.getBlockEntity(world, pos);
        if (blockEntity instanceof Inventory) {
            Inventory inventory = (Inventory) blockEntity;
            inventory.setStack(0, ItemStackUtil.empty());
            ItemScattererUtil.spawn(world, pos, inventory);
            e.updateComparators();
        }
        super.onStateReplaced(e);
    }

    @Override
    public @Nullable BlockState getPlacementState(PlacementStateArgs args) {
        return args.withBlockState(FACING.getProperty(), args.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public CompatActionResult onRightClick(BlockUseEvent e) {
        if (e.isClient()) return CompatActionResult.SUCCESS;
        if (e.stack.getItem() instanceof Wrench)
            return CompatActionResult.PASS;

        BlockEntity blockEntity = e.getBlockEntity();
        if (blockEntity instanceof EMCCondenserTile) {
            EMCCondenserTile tile = (EMCCondenserTile)blockEntity;
            e.player.openExtendedMenu(tile);
            return CompatActionResult.CONSUME;
        }
        return CompatActionResult.PASS;
    }

    @Nullable
    @Override
    public Text getScreenTitle() {
        return TITLE;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityType<T> getBlockEntityType() {
        return (BlockEntityType<T>) Tiles.EMC_CONDENSER.getOrNull();
    }

    @Override
    public boolean isTick() {
        return true;
    }
}
