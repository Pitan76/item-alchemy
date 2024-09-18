package net.pitan76.itemalchemy.tile;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.pitan76.itemalchemy.api.EMCStorageUtil;
import net.pitan76.itemalchemy.block.EMCBattery;
import net.pitan76.itemalchemy.gui.screen.EMCBatteryScreenHandler;
import net.pitan76.itemalchemy.tile.base.EMCStorageBlockEntity;
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.mcpitanlib.api.event.container.factory.DisplayNameArgs;
import net.pitan76.mcpitanlib.api.event.container.factory.ExtraDataArgs;
import net.pitan76.mcpitanlib.api.event.nbt.ReadNbtArgs;
import net.pitan76.mcpitanlib.api.event.nbt.WriteNbtArgs;
import net.pitan76.mcpitanlib.api.event.tile.TileTickEvent;
import net.pitan76.mcpitanlib.api.gui.ExtendedScreenHandlerFactory;
import net.pitan76.mcpitanlib.api.gui.inventory.IInventory;
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntityTicker;
import net.pitan76.mcpitanlib.api.util.*;
import org.jetbrains.annotations.Nullable;

public class EMCBatteryTile extends EMCStorageBlockEntity implements ExtendBlockEntityTicker<EMCBatteryTile>, SidedInventory, IInventory, ExtendedScreenHandlerFactory {
    public long maxEMC = -1;
    public int coolDown = 0; // tick

    public int getMaxCoolDown() {
        return 10;
    }

    public DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStackUtil.empty());

    public EMCBatteryTile(BlockEntityType<?> type, TileCreateEvent e) {
        super(type, e);
    }

    public EMCBatteryTile(TileCreateEvent e) {
        this(Tiles.EMC_BATTERY.getOrNull(), e);
    }

    @Override
    public long getMaxEMC() {
        if (maxEMC == -1)
            return ((EMCBattery) getCachedState().getBlock()).getMaxEMC();

        return maxEMC;
    }

    @Override
    public void writeNbt(WriteNbtArgs args) {
        super.writeNbt(args);
        InventoryUtil.writeNbt(args, getItems());
    }

    @Override
    public void readNbt(ReadNbtArgs args) {
        super.readNbt(args);
        InventoryUtil.readNbt(args, getItems());
    }

    @Nullable
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new EMCBatteryScreenHandler(syncId, inv, this, this);
    }

    @Override
    public void tick(TileTickEvent<EMCBatteryTile> e) {
        World world = e.world;

        if (WorldUtil.isClient(world)) return;

        if (maxEMC == -1)
            maxEMC = ((EMCBattery) e.state.getBlock()).getMaxEMC();

        //if (maxEMC <= storedEMC) return;

        coolDown += (int) (1 + Math.pow(maxEMC / 10000 - 1, 2));
        if (coolDown >= getMaxCoolDown())
            coolDown = 0;

       // if (inventory.isEmpty()) return;

        //if (!inventory.get(2).isEmpty()) {

        EMCStorageUtil.transferAllEMC(this, true);

    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        int[] result = new int[getItems().size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = i;
        }
        return result;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return dir != Direction.DOWN;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return dir == Direction.DOWN;
    }

    @Override
    public Text getDisplayName(DisplayNameArgs args) {
        return TextUtil.translatable("block.itemalchemy.emc_battery");
    }

    @Override
    public void writeExtraData(ExtraDataArgs args) {
        NbtCompound data = NbtUtil.create();
        NbtUtil.putInt(data, "x", pos.getX());
        NbtUtil.putInt(data, "y", pos.getY());
        NbtUtil.putInt(data, "z", pos.getZ());
        NbtUtil.putLong(data, "stored_emc", storedEMC);
        NbtUtil.putLong(data, "max_emc", ((EMCBattery) getCachedState().getBlock()).getMaxEMC());
        args.writeVar(data);
    }
}
