package net.pitan76.itemalchemy.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction;
import net.pitan76.itemalchemy.item.Wrench;
import net.pitan76.itemalchemy.tile.AlchemyChestTile;
import net.pitan76.itemalchemy.tile.Tiles;
import net.pitan76.mcpitanlib.api.block.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.block.ExtendBlock;
import net.pitan76.mcpitanlib.api.block.ExtendBlockEntityProvider;
import net.pitan76.mcpitanlib.api.event.block.AppendPropertiesArgs;
import net.pitan76.mcpitanlib.api.event.block.BlockUseEvent;
import net.pitan76.mcpitanlib.api.event.block.PlacementStateArgs;
import net.pitan76.mcpitanlib.api.event.block.StateReplacedEvent;
import net.pitan76.mcpitanlib.api.util.BlockStateUtil;
import net.pitan76.mcpitanlib.api.util.TextUtil;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;
import org.jetbrains.annotations.Nullable;

public class AlchemyChest extends ExtendBlock implements ExtendBlockEntityProvider, IUseableWrench {
    public static DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    private static final Text TITLE = TextUtil.translatable("block.itemalchemy.alchemy_chest");

    protected CompatMapCodec<? extends Block> CODEC = CompatMapCodec.createCodecOfExtendBlock(AlchemyChest::new);

    @Override
    public CompatMapCodec<? extends Block> getCompatCodec() {
        return CODEC;
    }

    public AlchemyChest(CompatibleBlockSettings settings) {
        super(settings);
        setNewDefaultState(BlockStateUtil.getDefaultState(this).with(FACING, Direction.NORTH));
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
        return args.withBlockState(FACING, args.getHorizontalPlayerFacing().getOpposite());
    }

    public AlchemyChest() {
        this(CompatibleBlockSettings.copy(net.minecraft.block.Blocks.STONE).mapColor(MapColor.YELLOW).strength(2f, 7.0f));
    }

    @Override
    public ActionResult onRightClick(BlockUseEvent e) {
        if (e.isClient()) return ActionResult.SUCCESS;
        if (e.stack.getItem() instanceof Wrench)
            return ActionResult.PASS;

        BlockEntity blockEntity = e.getBlockEntity();
        if (blockEntity instanceof AlchemyChestTile) {
            AlchemyChestTile tile = (AlchemyChestTile)blockEntity;
            e.player.openMenu(tile);
            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
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
