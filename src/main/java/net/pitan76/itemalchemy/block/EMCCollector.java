package net.pitan76.itemalchemy.block;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.text.Text;
import net.pitan76.itemalchemy.item.Wrench;
import net.pitan76.itemalchemy.tile.EMCCollectorTile;
import net.pitan76.itemalchemy.tile.Tiles;
import net.pitan76.mcpitanlib.api.block.ExtendBlockEntityProvider;
import net.pitan76.mcpitanlib.api.block.args.v2.PlacementStateArgs;
import net.pitan76.mcpitanlib.api.block.v2.CompatBlock;
import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.event.block.*;
import net.pitan76.mcpitanlib.api.state.property.CompatProperties;
import net.pitan76.mcpitanlib.api.state.property.DirectionProperty;
import net.pitan76.mcpitanlib.api.util.*;
import net.pitan76.mcpitanlib.api.util.color.CompatMapColor;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;
import net.pitan76.mcpitanlib.core.serialization.codecs.CompatBlockMapCodecUtil;
import net.pitan76.mcpitanlib.midohra.block.BlockState;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import net.pitan76.mcpitanlib.midohra.util.math.Direction;
import net.pitan76.mcpitanlib.midohra.world.World;
import org.jetbrains.annotations.Nullable;

public class EMCCollector extends CompatBlock implements ExtendBlockEntityProvider, IUseableWrench {
    public static DirectionProperty FACING = CompatProperties.HORIZONTAL_FACING;

    private static final Text TITLE = TextUtil.translatable("container.itemalchemy.emc_collector");

    public long maxEMC;

    protected CompatMapCodec<? extends EMCCollector> CODEC = CompatBlockMapCodecUtil.createCodec(EMCCollector::new);

    @Override
    public CompatMapCodec<? extends EMCCollector> getCompatCodec() {
        return CODEC;
    }

    public EMCCollector(CompatibleBlockSettings settings, long maxEMC) {
        super(settings);
        setDefaultState(getDefaultMidohraState().with(FACING, Direction.NORTH));
        this.maxEMC = maxEMC;
    }

    public EMCCollector(CompatibleBlockSettings settings) {
        this(settings, 10000);
    }

    public EMCCollector(CompatIdentifier id) {
        this(id, 10000);
    }

    public EMCCollector(CompatIdentifier id, long maxEMC) {
        this(CompatibleBlockSettings.copy(id, Blocks.STONE).mapColor(CompatMapColor.YELLOW).strength(2f, 7.0f), maxEMC);
    }

    @Override
    public void appendProperties(AppendPropertiesArgs args) {
        args.addProperty(FACING);
        super.appendProperties(args);
    }

    @Override
    public void onStateReplaced(StateReplacedEvent e) {
        World world = e.getMidohraWorld();
        BlockPos pos = e.getMidohraPos();
        if (e.isSameState()) return;

        BlockEntity blockEntity = e.getBlockEntity();
        if (blockEntity instanceof Inventory) {
            Inventory inventory = (Inventory) blockEntity;
            inventory.setStack(1, ItemStackUtil.empty());
            ItemScattererUtil.spawn(world, pos, inventory);
            e.updateComparators();
        }
        super.onStateReplaced(e);
    }

    @Override
    public @Nullable BlockState getPlacementState(PlacementStateArgs args) {
        return args.with(FACING, args.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public CompatActionResult onRightClick(BlockUseEvent e) {
        if (e.isClient()) return e.success();
        if (e.stack.getItem() instanceof Wrench)
            return e.pass();

        BlockEntity blockEntity = e.getBlockEntity();
        if (blockEntity instanceof EMCCollectorTile) {
            EMCCollectorTile tile = (EMCCollectorTile)blockEntity;
            e.player.openExtendedMenu(tile);
            return e.consume();
        }
        return e.pass();
    }

    @Nullable
    @Override
    public Text getScreenTitle() {
        return TITLE;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(TileCreateEvent event) {
        return new EMCCollectorTile(event);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityType<T> getBlockEntityType() {
        return (BlockEntityType<T>) Tiles.EMC_COLLECTOR.getOrNull();
    }

    @Override
    public boolean isTick() {
        return true;
    }
}
