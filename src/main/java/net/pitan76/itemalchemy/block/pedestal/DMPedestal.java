package net.pitan76.itemalchemy.block.pedestal;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.shape.VoxelShape;
import net.pitan76.itemalchemy.tile.DMPedestalTile;
import net.pitan76.itemalchemy.tile.Tiles;
import net.pitan76.mcpitanlib.api.block.CompatibleMaterial;
import net.pitan76.mcpitanlib.api.block.ExtendBlockEntityProvider;
import net.pitan76.mcpitanlib.api.block.args.v2.OutlineShapeEvent;
import net.pitan76.mcpitanlib.api.block.v2.CompatBlock;
import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.event.block.BlockUseEvent;
import net.pitan76.mcpitanlib.api.event.block.StateReplacedEvent;
import net.pitan76.mcpitanlib.api.sound.CompatSoundCategory;
import net.pitan76.mcpitanlib.api.sound.CompatSoundEvents;
import net.pitan76.mcpitanlib.api.util.*;
import net.pitan76.mcpitanlib.api.util.color.CompatMapColor;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;
import net.pitan76.mcpitanlib.core.serialization.codecs.CompatBlockMapCodecUtil;
import net.pitan76.mcpitanlib.midohra.block.entity.BlockEntityWrapper;
import net.pitan76.mcpitanlib.midohra.item.ItemStack;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import net.pitan76.mcpitanlib.midohra.world.World;
import org.jetbrains.annotations.Nullable;

public class DMPedestal extends CompatBlock implements ExtendBlockEntityProvider {

    protected static final VoxelShape SHAPE = VoxelShapeUtil.union(
            VoxelShapeUtil.blockCuboid(3, 0, 3, 13, 2, 13),
            VoxelShapeUtil.blockCuboid(6, 2, 6, 10, 9, 10),
            VoxelShapeUtil.blockCuboid(5, 9, 5, 11, 10, 11)
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
        this(CompatibleBlockSettings.of(id, CompatibleMaterial.STONE)
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
        BlockEntityWrapper be = e.getBlockEntityWrapper();
        if (be.isEmpty() || !(be.get() instanceof DMPedestalTile)) return e.pass();
        DMPedestalTile pedestal = (DMPedestalTile) be.get();

        Player player = e.player;
        ItemStack heldStack = e.getStackM();
        ItemStack pedestalStack = pedestal.getStackM();

        if (e.isClient()) {
            if (e.isSneaking()) {
                // Client-side prediction: toggle active state immediately
                if (!pedestalStack.isEmpty() && pedestalStack.getRawItem() instanceof IPedestalItem) {
                    pedestal.setActiveFromPacket(!pedestal.getActive());
                }

                return e.success();
            }

            if (pedestalStack.isEmpty()) {
                if (!heldStack.isEmpty()) {
                    ItemStack toPlace = heldStack.copy();
                    toPlace.setCount(1);
                    pedestal.setStackFromPacket(toPlace);
                }
            } else {
                pedestal.setStackFromPacket(ItemStack.empty());
            }

            return e.success();
        }

        // Check if pedestal contains a Black Hole Band
        boolean isBlackHoleBand = !pedestalStack.isEmpty() && pedestalStack.getRawItem() instanceof net.pitan76.itemalchemy.item.BlackHoleBand;

        if (e.isSneaking()) {
            // Sneak + right-click: toggle active state OR extract all items from Black Hole Band
            if (!pedestalStack.isEmpty()) {
                if (isBlackHoleBand) {
                    // Extract all items from Black Hole Band inventory
                    net.pitan76.itemalchemy.item.BlackHoleBand band = (net.pitan76.itemalchemy.item.BlackHoleBand) pedestalStack.getRawItem();
                    net.minecraft.item.ItemStack pedestalVanilla = pedestalStack.toMinecraft();
                    int extractedCount = 0;
                    
                    while (true) {
                        net.minecraft.item.ItemStack extracted = band.removeFirstItem(pedestalVanilla);
                        if (extracted.isEmpty()) break;
                        
                        if (heldStack.isEmpty()) {
                            player.setStackInHand(e.getHand(), net.pitan76.mcpitanlib.midohra.item.ItemStack.of(extracted));
                            heldStack = e.getStackM();
                        } else {
                            player.offerOrDrop(extracted);
                        }
                        extractedCount++;
                    }
                    
                    BlockPos pos = e.getMidohraPos();
                    World world = e.getMidohraWorld();
                    world.playSound(null, pos, CompatSoundEvents.ENTITY_ITEM_PICKUP, CompatSoundCategory.BLOCKS, 0.5F, 1.5F);
                    return e.success();
                }
                
                boolean newActive = !pedestal.getActive();
                if (newActive && !(pedestalStack.getRawItem() instanceof IPedestalItem)) {
                    return e.pass();
                }
                pedestal.setActive(newActive);
                BlockPos pos = e.getMidohraPos();
                World world = e.getMidohraWorld();
                if (newActive) {
                    world.playSound(null, pos, CompatSoundEvents.BLOCK_BEACON_ACTIVATE, CompatSoundCategory.BLOCKS, 1.0F, 1.0F);
                } else {
                    world.playSound(null, pos, CompatSoundEvents.BLOCK_BEACON_DEACTIVATE, CompatSoundCategory.BLOCKS, 1.0F, 1.0F);
                }
                return e.success();
            }
        } else {
            // Right-click (not sneaking): place/remove items OR extract single item from Black Hole Band
            if (!pedestalStack.isEmpty()) {
                if (isBlackHoleBand) {
                    // Extract single item from Black Hole Band inventory
                    net.pitan76.itemalchemy.item.BlackHoleBand band = (net.pitan76.itemalchemy.item.BlackHoleBand) pedestalStack.getRawItem();
                    net.minecraft.item.ItemStack pedestalVanilla = pedestalStack.toMinecraft();
                    net.minecraft.item.ItemStack extracted = band.removeFirstItem(pedestalVanilla);
                    
                    if (!extracted.isEmpty()) {
                        if (heldStack.isEmpty()) {
                            player.setStackInHand(e.getHand(), net.pitan76.mcpitanlib.midohra.item.ItemStack.of(extracted));
                        } else {
                            player.offerOrDrop(extracted);
                        }
                        BlockPos pos = e.getMidohraPos();
                        World world = e.getMidohraWorld();
                        world.playSound(null, pos, CompatSoundEvents.ENTITY_ITEM_PICKUP, CompatSoundCategory.BLOCKS, 0.5F, 1.0F);
                        return e.success();
                    }
                }
                
                // Remove item from pedestal
                pedestal.setActive(false);
                if (heldStack.isEmpty()) {
                    player.setStackInHand(e.getHand(), pedestalStack.copy());
                } else {
                    player.offerOrDrop(pedestalStack.copy());
                }
                pedestal.setStack(ItemStack.empty());
                return e.success();
            } else if (!heldStack.isEmpty()) {
                // Place item on pedestal
                ItemStack toPlace = heldStack.copy();
                toPlace.setCount(1);
                pedestal.setStack(toPlace);
                heldStack.decrement();
                return e.success();
            }
        }

        return e.pass();
    }

    @Override
    public void onStateReplaced(StateReplacedEvent e) {
        if (e.isClient() || e.isSameState()) {
            super.onStateReplaced(e);
            return;
        }

        ItemStack toDrop = ItemStack.empty();
        BlockPos pos = e.getMidohraPos();

        // Direct lookup — works in older MC versions where BE is still accessible here.
        BlockEntity be = e.getBlockEntity();
        if (be instanceof DMPedestalTile) {
            toDrop = ((DMPedestalTile) be).getStackM();
        }

        // Fallback for MC 1.21.x: the BE is removed from the world before onStateReplaced
        // fires, so the direct lookup returns null. markRemoved() caches the item for us.
        if (toDrop.isEmpty()) {
            ItemStack pending = DMPedestalTile.takePendingDropM(pos);
            if (pending != null) toDrop = pending;
        } else {
            // Consume the pending entry so it doesn't accumulate.
            DMPedestalTile.takePendingDropM(pos);
        }

        if (!toDrop.isEmpty()) {
            e.getMidohraWorld().spawnStack(toDrop.toMinecraft(), pos);
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
