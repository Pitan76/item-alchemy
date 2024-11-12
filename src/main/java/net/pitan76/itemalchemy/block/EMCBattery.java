package net.pitan76.itemalchemy.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.text.Text;
import net.pitan76.itemalchemy.item.Wrench;
import net.pitan76.itemalchemy.tile.EMCBatteryTile;
import net.pitan76.itemalchemy.tile.Tiles;
import net.pitan76.mcpitanlib.api.block.ExtendBlockEntityProvider;
import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.event.block.AppendPropertiesArgs;
import net.pitan76.mcpitanlib.api.event.block.BlockUseEvent;
import net.pitan76.mcpitanlib.api.event.block.PlacementStateArgs;
import net.pitan76.mcpitanlib.api.event.block.StateReplacedEvent;
import net.pitan76.mcpitanlib.api.state.property.CompatProperties;
import net.pitan76.mcpitanlib.api.state.property.DirectionProperty;
import net.pitan76.mcpitanlib.api.util.CompatActionResult;
import net.pitan76.mcpitanlib.api.util.CompatIdentifier;
import net.pitan76.mcpitanlib.api.util.TextUtil;
import net.pitan76.mcpitanlib.api.util.color.CompatMapColor;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;
import net.pitan76.mcpitanlib.core.serialization.codecs.CompatBlockMapCodecUtil;
import org.jetbrains.annotations.Nullable;

public class EMCBattery extends EMCRepeater implements ExtendBlockEntityProvider, IUseableWrench {
    public static DirectionProperty FACING = CompatProperties.HORIZONTAL_FACING;
    public static final Text TITLE = TextUtil.translatable("container.itemalchemy.emc_battery");

    public long maxEMC = 100_000;

    protected CompatMapCodec<? extends Block> CODEC = CompatBlockMapCodecUtil.createCodec(EMCBattery::new);

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

    public EMCBattery(CompatIdentifier id) {
        this(CompatibleBlockSettings.copy(id, net.minecraft.block.Blocks.STONE).mapColor(CompatMapColor.DIAMOND_BLUE).strength(2f, 7.0f));
    }

    public EMCBattery(CompatIdentifier id, long maxEMC) {
        this(id);
        this.maxEMC = maxEMC;
    }

    public long getMaxEMC() {
        return maxEMC;
    }

    @Override
    public CompatActionResult onRightClick(BlockUseEvent e) {
        if (e.isClient()) return CompatActionResult.SUCCESS;
        if (e.stack.getItem() instanceof Wrench)
            return CompatActionResult.PASS;

        BlockEntity blockEntity = e.getBlockEntity();
        if (blockEntity instanceof EMCBatteryTile) {
            e.player.openExtendedMenu((EMCBatteryTile)blockEntity);
            return CompatActionResult.CONSUME;
        }
        return CompatActionResult.PASS;
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
        return args.withBlockState(FACING.getProperty(), args.getHorizontalPlayerFacing().getOpposite());
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
