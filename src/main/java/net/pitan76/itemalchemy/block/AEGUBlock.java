package net.pitan76.itemalchemy.block;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.pitan76.itemalchemy.item.Wrench;
import net.pitan76.itemalchemy.tile.AEGUTile;
import net.pitan76.itemalchemy.tile.EMCCondenserTile;
import net.pitan76.itemalchemy.tile.Tiles;
import net.pitan76.mcpitanlib.api.block.ExtendBlockEntityProvider;
import net.pitan76.mcpitanlib.api.block.v2.CompatBlock;
import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.event.block.AppendPropertiesArgs;
import net.pitan76.mcpitanlib.api.event.block.BlockUseEvent;
import net.pitan76.mcpitanlib.api.state.property.BooleanProperty;
import net.pitan76.mcpitanlib.api.util.CompatActionResult;
import net.pitan76.mcpitanlib.api.util.CompatIdentifier;
import net.pitan76.mcpitanlib.api.util.WorldUtil;
import net.pitan76.mcpitanlib.api.util.color.CompatMapColor;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;
import net.pitan76.mcpitanlib.core.serialization.codecs.CompatBlockMapCodecUtil;
import net.pitan76.mcpitanlib.midohra.block.BlockState;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class AEGUBlock extends CompatBlock implements ExtendBlockEntityProvider, IUseableWrench {
    public static BooleanProperty CONNECTED = BooleanProperty.of("connected");
    public long emc;

    protected CompatMapCodec<? extends Block> CODEC = CompatBlockMapCodecUtil.createCodec(AEGUBlock::new);

    @Override
    public CompatMapCodec<? extends Block> getCompatCodec() {
        return CODEC;
    }

    public AEGUBlock(CompatibleBlockSettings settings, long emc) {
        super(settings);
        setDefaultState(getDefaultMidohraState().with(CONNECTED, false));
        this.emc = emc;
    }

    public AEGUBlock(CompatibleBlockSettings settings) {
        this(settings, 10000);
    }

    public AEGUBlock(CompatIdentifier id) {
        this(id, 10000);
    }

    public AEGUBlock(CompatIdentifier id, long emc) {
        this(CompatibleBlockSettings.copy(id, net.minecraft.block.Blocks.STONE).mapColor(CompatMapColor.YELLOW).strength(2f, 7.0f), emc);
    }

    @Override
    public void appendProperties(AppendPropertiesArgs args) {
        args.addProperty(CONNECTED);
        super.appendProperties(args);
    }

    public static BlockState setConnected(BlockState state, boolean isConnected) {
        return state.with(CONNECTED, isConnected);
    }

    public static net.minecraft.block.BlockState setConnected(net.minecraft.block.BlockState state, boolean isConnected) {
        return setConnected(BlockState.of(state), isConnected).toMinecraft();
    }

    public static boolean isConnected(BlockState state) {
        return state.get(CONNECTED);
    }

    public static boolean isConnected(net.minecraft.block.BlockState state) {
        return isConnected(BlockState.of(state));
    }

    @Override
    public CompatActionResult onRightClick(BlockUseEvent e) {
        if (e.stack.getItem() instanceof Wrench)
            return e.pass();

        net.minecraft.util.math.BlockPos rawPos = AEGUTile.getNearEMCCondenserPos(e.world, e.pos);
        if (rawPos == null) return e.fail();

        BlockPos blockPos = BlockPos.of(rawPos);
        BlockEntity blockEntity = e.getMidohraWorld().getBlockEntity(blockPos).get();

        if (blockEntity instanceof EMCCondenserTile) {
            EMCCondenserTile tile = (EMCCondenserTile) blockEntity;
            if (e.isClient()) return e.success();
            e.player.openExtendedMenu(tile);
            return e.consume();
        }
        return e.pass();
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
