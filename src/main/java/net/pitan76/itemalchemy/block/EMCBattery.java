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
import net.pitan76.itemalchemy.tile.EMCBatteryTile;
import net.pitan76.itemalchemy.tile.Tiles;
import net.pitan76.mcpitanlib.api.block.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.block.ExtendBlockEntityProvider;
import net.pitan76.mcpitanlib.api.event.block.*;
import net.pitan76.mcpitanlib.api.util.TextUtil;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;
import org.jetbrains.annotations.Nullable;

public class EMCBattery extends EMCRepeater implements ExtendBlockEntityProvider, IUseableWrench {
    public static DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final Text TITLE = TextUtil.translatable("container.itemalchemy.emc_battery");

    public long maxEMC = 100_000;

    protected CompatMapCodec<? extends Block> CODEC = CompatMapCodec.createCodecOfExtendBlock(EMCBattery::new);

    @Override
    public CompatMapCodec<? extends Block> getCompatCodec() {
        return CODEC;
    }

    public EMCBattery(CompatibleBlockSettings settings, long maxEMC) {
        super(settings);
        this.maxEMC = maxEMC;
    }

    public EMCBattery(CompatibleBlockSettings settings) {
        super(settings);
    }

    public EMCBattery() {
        this(CompatibleBlockSettings.copy(net.minecraft.block.Blocks.STONE).mapColor(MapColor.DIAMOND_BLUE).strength(2f, 7.0f));
    }

    public EMCBattery(long maxEMC) {
        this();
        this.maxEMC = maxEMC;
    }

    public long getMaxEMC() {
        return maxEMC;
    }

    @Override
    public ActionResult onRightClick(BlockUseEvent e) {
        if (e.isClient()) return ActionResult.SUCCESS;

        BlockEntity blockEntity = e.getBlockEntity();
        if (blockEntity instanceof EMCBatteryTile) {
            e.player.openExtendedMenu((EMCBatteryTile)blockEntity);
            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
    }

    @Override
    public void appendProperties(AppendPropertiesArgs args) {
        args.addProperty(FACING);
        super.appendProperties(args);
    }

    @Override
    public void onStateReplaced(StateReplacedEvent e) {
        e.spawnDropsInContainer();
        super.onStateReplaced(e);
    }

    @Override
    public BlockState getPlacementState(PlacementStateArgs args) {
        return args.withBlockState(FACING, args.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public Text getScreenTitle() {
        return TITLE;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityType<T> getBlockEntityType() {
        return (BlockEntityType<T>) Tiles.EMC_BATTERY.getOrNull();
    }

    @Override
    public boolean isTick() {
        return true;
    }
}
