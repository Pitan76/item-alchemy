package net.pitan76.itemalchemy.block.pedestal;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.pitan76.itemalchemy.tile.DMPedestalTile;
import net.pitan76.itemalchemy.tile.Tiles;
import net.pitan76.mcpitanlib.api.block.ExtendBlockEntityProvider;
import net.pitan76.mcpitanlib.api.block.args.v2.OutlineShapeEvent;
import net.pitan76.mcpitanlib.api.block.v2.CompatBlock;
import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.event.block.BlockUseEvent;
import net.pitan76.mcpitanlib.api.event.block.StateReplacedEvent;
import net.pitan76.mcpitanlib.api.sound.CompatSoundCategory;
import net.pitan76.mcpitanlib.api.util.CompatActionResult;
import net.pitan76.mcpitanlib.api.util.CompatIdentifier;
import net.pitan76.mcpitanlib.api.util.WorldUtil;
import net.pitan76.mcpitanlib.api.util.color.CompatMapColor;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;
import net.pitan76.mcpitanlib.core.serialization.codecs.CompatBlockMapCodecUtil;
import org.jetbrains.annotations.Nullable;

public class DMPedestal extends CompatBlock implements ExtendBlockEntityProvider {

    protected static final VoxelShape SHAPE = VoxelShapes.union(
            Block.createCuboidShape(3, 0, 3, 13, 2, 13),
            Block.createCuboidShape(6, 2, 6, 10, 9, 10),
            Block.createCuboidShape(5, 9, 5, 11, 10, 11)
    );

    protected CompatMapCodec<? extends Block> CODEC = CompatBlockMapCodecUtil.createCodec(DMPedestal::new);

    @Override
    public CompatMapCodec<? extends Block> getCompatCodec() {
        return CODEC;
    }

    public DMPedestal(CompatibleBlockSettings settings) {
        super(settings);
    }

    public DMPedestal(CompatIdentifier id) {
        this(CompatibleBlockSettings.of(id, net.pitan76.mcpitanlib.api.block.CompatibleMaterial.STONE)
                .mapColor(CompatMapColor.BLACK)
                .strength(3.5f, 7.0f)
                .nonOpaque());
    }

    @Override
    public VoxelShape getOutlineShape(OutlineShapeEvent e) {
        return SHAPE;
    }

    @Override
    public CompatActionResult onRightClick(BlockUseEvent e) {
        BlockEntity be = e.getBlockEntity();
        if (!(be instanceof DMPedestalTile)) return e.pass();
        DMPedestalTile pedestal = (DMPedestalTile) be;

        Player player = e.player;
        ItemStack heldStack = e.stack;

        if (e.isClient()) {
            if (!player.isSneaking()) {
                if (!pedestal.getStack().isEmpty()) {
                    pedestal.setStackFromPacket(ItemStack.EMPTY);
                } else if (!heldStack.isEmpty()) {
                    ItemStack toPlace = heldStack.copy();
                    toPlace.setCount(1);
                    pedestal.setStackFromPacket(toPlace);
                }
            } else {
                // Client-side prediction: toggle active state immediately
                if (!pedestal.getStack().isEmpty() && pedestal.getStack().getItem() instanceof IPedestalItem) {
                    pedestal.setActiveFromPacket(!pedestal.getActive());
                }
            }
            return e.success();
        }

        if (player.isSneaking()) {
            // Sneak + right-click: toggle active state
            if (!pedestal.getStack().isEmpty()) {
                boolean newActive = !pedestal.getActive();
                if (newActive && !(pedestal.getStack().getItem() instanceof IPedestalItem)) {
                    return e.pass();
                }
                pedestal.setActive(newActive);
                BlockPos pos = e.pos;
                if (newActive) {
                    WorldUtil.playSound(e.world, null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE, net.minecraft.sound.SoundCategory.BLOCKS, 1.0F, 1.0F);
                } else {
                    WorldUtil.playSound(e.world, null, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE, net.minecraft.sound.SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
                return e.success();
            }
        } else {
            // Right-click (not sneaking): place/remove items
            ItemStack pedestalStack = pedestal.getStack();
            if (!pedestalStack.isEmpty()) {
                // Remove item from pedestal
                pedestal.setActive(false);
                if (heldStack.isEmpty()) {
                    player.setStackInHand(e.getHand(), pedestalStack.copy());
                } else {
                    player.getInventory().offerOrDrop(pedestalStack.copy());
                }
                pedestal.setStack(ItemStack.EMPTY);
                return e.success();
            } else if (!heldStack.isEmpty()) {
                // Place item on pedestal
                ItemStack toPlace = heldStack.copy();
                toPlace.setCount(1);
                pedestal.setStack(toPlace);
                heldStack.decrement(1);
                return e.success();
            }
        }

        return e.pass();
    }

    @Override
    public void onStateReplaced(StateReplacedEvent e) {
        if (e.isSameState()) {
            super.onStateReplaced(e);
            return;
        }

        if (!e.isClient()) {
            ItemStack toDrop = ItemStack.EMPTY;

            // Direct lookup — works in older MC versions where BE is still accessible here.
            BlockEntity be = e.getBlockEntity();
            if (be instanceof DMPedestalTile) {
                toDrop = ((DMPedestalTile) be).getStack();
            }

            // Fallback for MC 1.21.x: the BE is removed from the world before onStateReplaced
            // fires, so the direct lookup returns null. markRemoved() caches the item for us.
            if (toDrop.isEmpty()) {
                ItemStack pending = DMPedestalTile.takePendingDrop(e.pos);
                if (pending != null) toDrop = pending;
            } else {
                // Consume the pending entry so it doesn't accumulate.
                DMPedestalTile.takePendingDrop(e.pos);
            }

            if (!toDrop.isEmpty()) {
                e.world.spawnEntity(new ItemEntity(e.world,
                        e.pos.getX() + 0.5, e.pos.getY() + 0.5, e.pos.getZ() + 0.5, toDrop));
            }
        }
        super.onStateReplaced(e);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityType<T> getBlockEntityType() {
        return (BlockEntityType<T>) Tiles.DM_PEDESTAL.getOrNull();
    }

    @Override
    public boolean isTick() {
        return true;
    }
}
