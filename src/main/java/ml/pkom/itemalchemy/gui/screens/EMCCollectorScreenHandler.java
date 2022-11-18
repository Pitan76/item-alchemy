package ml.pkom.itemalchemy.gui.screens;

import ml.pkom.itemalchemy.ScreenHandlers;
import ml.pkom.itemalchemy.tiles.EMCCollectorTile;
import ml.pkom.mcpitanlibarch.api.gui.SimpleScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class EMCCollectorScreenHandler extends SimpleScreenHandler {
    private Inventory inventory;
    private PlayerInventory playerInventory;
    public EMCCollectorTile tile = null;

    public long storedEMC = 0;
    public long maxEMC = 0;

    public EMCCollectorScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, null, new SimpleInventory(16 + 3));
        NbtCompound data = buf.readNbt();
        if (data == null) return;
        int x, y, z;
        if (data.contains("x") && data.contains("y") && data.contains("z")) {
            x = data.getInt("x");
            y = data.getInt("y");
            z = data.getInt("z");

            tile = (EMCCollectorTile) playerInventory.player.world.getBlockEntity(new BlockPos(x, y, z));
            storedEMC = data.getInt("stored_emc") - tile.storedEMC;
            maxEMC = data.getInt("max_emc");
        }
    }

    public EMCCollectorScreenHandler(int syncId, PlayerInventory playerInventory, @Nullable EMCCollectorTile tile, Inventory inventory) {
        super(ScreenHandlers.EMC_COLLECTOR, syncId);

        this.inventory = inventory;
        this.playerInventory = playerInventory;
        this.tile = tile;
        addPlayerMainInventorySlots(playerInventory, 24, 84);
        addPlayerHotbarSlots(playerInventory, 24, 142);
        addNormalSlot(inventory, 0, 149, 12);
        addNormalSlot(inventory, 1, 177, 35);
        addNormalSlot(inventory, 2, 149, 58);
        addSlots(inventory, 3, 14, 8, -1, 4, 4);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            if (index < 36) {
                if (!this.insertItem(originalStack, 36 + 3, 36 + 16 + 3, false)) {
                    if (!this.insertItem(originalStack, 36, 36 + 3, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.insertItem(originalStack, 0, 36, false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return ItemStack.EMPTY;
    }
}
