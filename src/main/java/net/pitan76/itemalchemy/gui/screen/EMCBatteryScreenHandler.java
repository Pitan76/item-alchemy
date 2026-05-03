package net.pitan76.itemalchemy.gui.screen;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.pitan76.itemalchemy.tile.EMCBatteryTile;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.gui.ExtendedScreenHandler;
import net.pitan76.mcpitanlib.api.gui.args.CreateMenuEvent;
import net.pitan76.mcpitanlib.api.network.PacketByteUtil;
import net.pitan76.mcpitanlib.api.util.*;
import net.pitan76.mcpitanlib.api.util.inventory.CompatInventory;
import net.pitan76.mcpitanlib.api.util.inventory.CompatPlayerInventory;
import net.pitan76.mcpitanlib.api.util.inventory.ICompatInventory;
import net.pitan76.mcpitanlib.midohra.block.entity.BlockEntityWrapper;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class EMCBatteryScreenHandler extends ExtendedScreenHandler {
    public ICompatInventory inventory;
    public CompatPlayerInventory playerInventory;
    public EMCBatteryTile tile = null;

    public long storedEMC = 0;
    public long maxEMC = 0;

    public EMCBatteryScreenHandler(CreateMenuEvent e, PacketByteBuf buf) {
        this(e, null, new CompatInventory(2));

        BlockPos pos = PacketByteUtil.readBlockPosM(buf);
        BlockEntityWrapper blockEntity = e.getWorldM().getBlockEntity(pos);

        if (blockEntity.isPresent()) {
            tile = blockEntity.getCompatBlockEntity(EMCBatteryTile.class);
            storedEMC = PacketByteUtil.readLong(buf) - tile.storedEMC;
            maxEMC = PacketByteUtil.readLong(buf);
        }
    }

    public EMCBatteryScreenHandler(CreateMenuEvent e, @Nullable EMCBatteryTile tile, ICompatInventory inventory) {
        this(ScreenHandlers.EMC_BATTERY, e, tile, inventory);
    }

    public EMCBatteryScreenHandler(ScreenHandlerType<?> type, CreateMenuEvent e, @Nullable EMCBatteryTile tile, ICompatInventory inventory) {
        super(type, e.syncId);

        this.inventory = inventory;
        this.playerInventory = e.getCompatPlayerInventory();
        this.tile = tile;

        initSlots();
    }

    public void initSlots() {
        addPlayerMainInventorySlots(playerInventory.getRaw(), 24, 84);
        addPlayerHotbarSlots(playerInventory.getRaw(), 24, 142);
        addNormalSlot(inventory, 0, 24, 33);
        addNormalSlot(inventory, 1, 168, 33);
        //addSlots(inventory, 3, 14, 8, -1, 4, 4);
    }

    @Override
    public ItemStack quickMoveOverride(Player player, int index) {
        Slot slot = ScreenHandlerUtil.getSlot(this, index);
        if (SlotUtil.hasStack(slot)) {
            ItemStack originalStack = SlotUtil.getStack(slot);

            if (index < 36) {
                if (!this.callInsertItem(originalStack, 36, 38, false))
                    return ItemStackUtil.empty();

            } else if (!this.callInsertItem(originalStack, 0, 35, false)) {
                return ItemStackUtil.empty();
            }

            if (ItemStackUtil.isEmpty(originalStack)) {
                SlotUtil.setStack(slot, ItemStackUtil.empty());
            } else {
                SlotUtil.markDirty(slot);
            }
        }
        return ItemStackUtil.empty();
    }
}
