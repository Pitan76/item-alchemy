package net.pitan76.itemalchemy.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.pitan76.itemalchemy.item.Wrench;
import net.pitan76.itemalchemy.tile.AEGUTile;
import net.pitan76.itemalchemy.tile.EMCCondenserTile;
import net.pitan76.itemalchemy.tile.Tiles;
import net.pitan76.mcpitanlib.api.block.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.block.ExtendBlock;
import net.pitan76.mcpitanlib.api.block.ExtendBlockEntityProvider;
import net.pitan76.mcpitanlib.api.event.block.AppendPropertiesArgs;
import net.pitan76.mcpitanlib.api.event.block.BlockUseEvent;
import net.pitan76.mcpitanlib.api.util.BlockStateUtil;
import net.pitan76.mcpitanlib.api.util.WorldUtil;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;
import org.jetbrains.annotations.Nullable;

public class AEGUBlock extends ExtendBlock implements ExtendBlockEntityProvider, IUseableWrench {
    public static BooleanProperty CONNECTED = BooleanProperty.of("connected");
    public long emc;

    protected CompatMapCodec<? extends Block> CODEC = CompatMapCodec.createCodecOfExtendBlock(AEGUBlock::new);

    @Override
    public CompatMapCodec<? extends Block> getCompatCodec() {
        return CODEC;
    }

    public AEGUBlock(CompatibleBlockSettings settings, long emc) {
        super(settings);
        setNewDefaultState(BlockStateUtil.getDefaultState(this).with(CONNECTED, false));
        this.emc = emc;
    }

    public AEGUBlock(CompatibleBlockSettings settings) {
        this(settings, 10000);
    }

    public AEGUBlock() {
        this(10000);
    }

    public AEGUBlock(long emc) {
        this(CompatibleBlockSettings.copy(net.minecraft.block.Blocks.STONE).mapColor(MapColor.YELLOW).strength(2f, 7.0f), emc);
    }

    @Override
    public void appendProperties(AppendPropertiesArgs args) {
        args.addProperty(CONNECTED);
        super.appendProperties(args);
    }

    public static BlockState setConnected(BlockState state, boolean isConnected) {
        return BlockStateUtil.with(state, CONNECTED, isConnected);
    }

    public static boolean isConnected(BlockState state) {
        return state.get(CONNECTED);
    }

    @Override
    public ActionResult onRightClick(BlockUseEvent e) {
        if (e.stack.getItem() instanceof Wrench)
            return ActionResult.PASS;

        BlockPos blockPos = AEGUTile.getNearEMCCondenserPos(e.world, e.pos);
        if (blockPos == null) return ActionResult.FAIL;
        BlockEntity blockEntity = WorldUtil.getBlockEntity(e.world, blockPos);

        if (blockEntity instanceof EMCCondenserTile) {
            EMCCondenserTile tile = (EMCCondenserTile) blockEntity;
            if (e.isClient()) return ActionResult.SUCCESS;
            e.player.openExtendedMenu(tile);
            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityType<T> getBlockEntityType() {
        return (BlockEntityType<T>) Tiles.AEGU.getOrNull();
    }

    @Override
    public boolean isTick() {
        return true;
    }
}
