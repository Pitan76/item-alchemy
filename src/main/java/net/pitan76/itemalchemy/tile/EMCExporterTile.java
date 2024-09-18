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
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.gui.screen.EMCExporterScreenHandler;
import net.pitan76.itemalchemy.tile.base.OwnedBlockEntity;
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

public class EMCExporterTile extends OwnedBlockEntity implements ExtendBlockEntityTicker<EMCExporterTile>, SidedInventory, IInventory, ExtendedScreenHandlerFactory {

    public DefaultedList<ItemStack> filter = DefaultedList.ofSize(9, ItemStackUtil.empty());

    public EMCExporterTile(BlockEntityType<?> type, TileCreateEvent e) {
        super(type, e);
    }

    public EMCExporterTile(TileCreateEvent e) {
        this(Tiles.EMC_EXPORTER.getOrNull(), e);
    }

    @Override
    public void writeNbt(WriteNbtArgs args) {
        NbtCompound filterNbt = NbtUtil.create();
        InventoryUtil.writeNbt(args.registryLookup, filterNbt, filter);
        NbtUtil.put(args.nbt, "filter", filterNbt);

        if (teamUUID != null)
            NbtUtil.putUuid(args.nbt, "team", teamUUID);
    }

    @Override
    public void readNbt(ReadNbtArgs args) {
        if (NbtUtil.has(args.nbt, "filter")) {
            NbtCompound filterNbt = NbtUtil.get(args.nbt, "filter");
            InventoryUtil.readNbt(args.registryLookup, filterNbt, filter);
        }

        if (NbtUtil.has(args.nbt, "team"))
            teamUUID = NbtUtil.getUuid(args.nbt, "team");
    }

    @Nullable
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new EMCExporterScreenHandler(syncId, inv, this, this);
    }

    @Override
    public void tick(TileTickEvent<EMCExporterTile> e) {

    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        DefaultedList<ItemStack> result = DefaultedList.ofSize(filter.size(), ItemStackUtil.empty());

        if (!hasTeam()) return result;
        if (filter.get(0).isEmpty()) return result;

        long emc = getTeamState().get().storedEMC;
        if (emc <= 0) return result;

        int filterCount = getFilterCount();

        long aveEMC = emc / filterCount;

        for (ItemStack filterStack : filter) {
            if (filterStack.isEmpty()) continue;

            long neededEMC = EMCManager.get(filterStack);
            if (neededEMC <= 0) continue;

            if (aveEMC < neededEMC) continue;

            ItemStack stack = filterStack.copy();
            stack.setCount((int) Math.floorDiv(aveEMC, neededEMC));
            result.add(stack);
        }

        return result;
    }

    public int getFilterCount() {
        int count = 0;
        for (int i = filter.size() - 1; i >= 0; i--) {
            if (!filter.get(i).isEmpty())
                count++;
        }
        return count;
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
        return TextUtil.translatable("block.itemalchemy.emc_exporter");
    }

    @Override
    public void writeExtraData(ExtraDataArgs args) {
        NbtCompound data = NbtUtil.create();
        NbtUtil.putInt(data, "x", pos.getX());
        NbtUtil.putInt(data, "y", pos.getY());
        NbtUtil.putInt(data, "z", pos.getZ());
        if (teamUUID != null)
            NbtUtil.putUuid(data, "team", teamUUID);

        args.writeVar(data);
    }
}
