package net.pitan76.itemalchemy.block;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.text.Text;
import net.pitan76.itemalchemy.item.Wrench;
import net.pitan76.itemalchemy.tile.AlchemyChestTile;
import net.pitan76.itemalchemy.tile.Tiles;
import net.pitan76.mcpitanlib.api.block.ExtendBlockEntityProvider;
import net.pitan76.mcpitanlib.api.block.args.v2.PlacementStateArgs;
import net.pitan76.mcpitanlib.api.block.v2.CompatBlock;
import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.event.block.AppendPropertiesArgs;
import net.pitan76.mcpitanlib.api.event.block.BlockUseEvent;
import net.pitan76.mcpitanlib.api.event.block.StateReplacedEvent;
import net.pitan76.mcpitanlib.api.state.property.CompatProperties;
import net.pitan76.mcpitanlib.api.state.property.DirectionProperty;
import net.pitan76.mcpitanlib.api.util.CompatActionResult;
import net.pitan76.mcpitanlib.api.util.CompatIdentifier;
import net.pitan76.mcpitanlib.api.util.TextUtil;
import net.pitan76.mcpitanlib.api.util.color.CompatMapColor;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;
import net.pitan76.mcpitanlib.core.serialization.codecs.CompatBlockMapCodecUtil;
import net.pitan76.mcpitanlib.midohra.block.BlockState;
import net.pitan76.mcpitanlib.midohra.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class AlchemyChest extends CompatBlock implements ExtendBlockEntityProvider, IUseableWrench {
    public static DirectionProperty FACING = CompatProperties.HORIZONTAL_FACING;

    private static final Text TITLE = TextUtil.translatable("block.itemalchemy.alchemy_chest");

    protected CompatMapCodec<? extends Block> CODEC = CompatBlockMapCodecUtil.createCodec(AlchemyChest::new);

    @Override
    public CompatMapCodec<? extends Block> getCompatCodec() {
        return CODEC;
    }

    public AlchemyChest(CompatibleBlockSettings settings) {
        super(settings);
        setDefaultState(getDefaultMidohraState().with(FACING, Direction.NORTH));
    }

    @Override
    public void appendProperties(AppendPropertiesArgs args) {
        args.addProperty(FACING);
        super.appendProperties(args);
    }

    @Override
    public void onStateReplaced(StateReplacedEvent e) {
        if (e.isSameState()) return;

        e.spawnDropsInContainer();
        super.onStateReplaced(e);
    }

    @Override
    public @Nullable BlockState getPlacementState(PlacementStateArgs args) {
        BlockState state = super.getPlacementState(args);

        return state.with(FACING, args.getHorizontalPlayerFacing().getOpposite());
    }

    public AlchemyChest(CompatIdentifier id) {
        this(CompatibleBlockSettings.copy(id, net.minecraft.block.Blocks.STONE).mapColor(CompatMapColor.YELLOW).strength(2f, 7.0f));
    }

    @Override
    public CompatActionResult onRightClick(BlockUseEvent e) {
        if (e.isClient()) return e.success();
        if (e.stack.getItem() instanceof Wrench)
            return e.pass();

        BlockEntity blockEntity = e.getBlockEntity();
        if (blockEntity instanceof AlchemyChestTile) {
            AlchemyChestTile tile = (AlchemyChestTile)blockEntity;
            e.player.openMenu(tile);
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
    public @Nullable <T extends BlockEntity> BlockEntityType<T> getBlockEntityType() {
        return (BlockEntityType<T>) Tiles.ALCHEMY_CHEST.getOrNull();
    }

    @Override
    public boolean isTick() {
        return true;
    }
}
