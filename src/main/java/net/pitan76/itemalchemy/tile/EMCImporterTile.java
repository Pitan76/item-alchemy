package net.pitan76.itemalchemy.tile;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.api.EMCStorageUtil;
import net.pitan76.itemalchemy.data.TeamState;
import net.pitan76.itemalchemy.gui.screen.EMCImporterScreenHandler;
import net.pitan76.itemalchemy.tile.base.OwnedBlockEntity;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.mcpitanlib.api.event.container.factory.DisplayNameArgs;
import net.pitan76.mcpitanlib.api.event.container.factory.ExtraDataArgs;
import net.pitan76.mcpitanlib.api.event.nbt.ReadNbtArgs;
import net.pitan76.mcpitanlib.api.event.nbt.WriteNbtArgs;
import net.pitan76.mcpitanlib.api.event.tile.TileTickEvent;
import net.pitan76.mcpitanlib.api.gui.args.CreateMenuEvent;
import net.pitan76.mcpitanlib.api.gui.inventory.IInventory;
import net.pitan76.mcpitanlib.api.gui.inventory.sided.VanillaStyleSidedInventory;
import net.pitan76.mcpitanlib.api.gui.inventory.sided.args.AvailableSlotsArgs;
import net.pitan76.mcpitanlib.api.gui.inventory.sided.args.CanInsertArgs;
import net.pitan76.mcpitanlib.api.gui.v2.ExtendedScreenHandlerFactory;
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntityTicker;
import net.pitan76.mcpitanlib.api.util.*;
import net.pitan76.mcpitanlib.api.util.collection.ItemStackList;
import net.pitan76.mcpitanlib.api.util.item.ItemUtil;
import org.jetbrains.annotations.Nullable;

public class EMCImporterTile extends OwnedBlockEntity implements ExtendBlockEntityTicker<EMCImporterTile>, VanillaStyleSidedInventory, IInventory, ExtendedScreenHandlerFactory {

    public ItemStackList filter = ItemStackList.ofSize(9, ItemStackUtil.empty());
    public ItemStackList inv = ItemStackList.ofSize(1, ItemStackUtil.empty());
    public String ownerName = "";

    public EMCImporterTile(BlockEntityType<?> type, TileCreateEvent e) {
        super(type, e);
    }

    public EMCImporterTile(TileCreateEvent e) {
        this(Tiles.EMC_IMPORTER.getOrNull(), e);
    }

    @Override
    public void writeNbt(WriteNbtArgs args) {
        super.writeNbt(args);

        NbtCompound filterNbt = NbtUtil.create();
        InventoryUtil.writeNbt(args.registryLookup, filterNbt, filter);

        NbtCompound invNbt = NbtUtil.create();
        InventoryUtil.writeNbt(args.registryLookup, invNbt, inv);

        NbtUtil.put(args.nbt, "filter", filterNbt);
        NbtUtil.put(args.nbt, "inv", invNbt);

        if (teamUUID != null)
            NbtUtil.putUuid(args.nbt, "team", teamUUID);

        if (ownerName != null && !ownerName.isEmpty())
            NbtUtil.putString(args.nbt, "ownerName", ownerName);
    }

    @Override
    public void readNbt(ReadNbtArgs args) {
        super.readNbt(args);

        if (NbtUtil.has(args.nbt, "filter")) {
            NbtCompound filterNbt = NbtUtil.get(args.nbt, "filter");
            InventoryUtil.readNbt(args.registryLookup, filterNbt, filter);
        }

        if (NbtUtil.has(args.nbt, "inv")) {
            NbtCompound invNbt = NbtUtil.get(args.nbt, "inv");
            InventoryUtil.readNbt(args.registryLookup, invNbt, inv);
        }

        if (NbtUtil.has(args.nbt, "team"))
            teamUUID = NbtUtil.getUuid(args.nbt, "team");

        if (NbtUtil.has(args.nbt, "ownerName"))
            ownerName = NbtUtil.getString(args.nbt, "ownerName");
    }

    @Nullable
    public ScreenHandler createMenu(CreateMenuEvent e) {
        IInventory filterInventory = () -> this.filter;
        return new EMCImporterScreenHandler(e.syncId, e.playerInventory, this, this, filterInventory);
    }

    @Override
    public void tick(TileTickEvent<EMCImporterTile> e) {
        World world = e.world;
        if (WorldUtil.isClient(world)) return;
        if (!hasTeam()) return;

        EMCStorageUtil.transferAllEMC(this);
        if (storedEMC > 0) {
            @SuppressWarnings("OptionalGetWithoutIsPresent")
            TeamState teamState = getTeamState().get();

            teamState.storedEMC += storedEMC;
            storedEMC = 0;
            BlockEntityUtil.markDirty(this);
        }

        if (inv.get(0).isEmpty()) return;

        ItemStack stack = inv.get(0);
        long emc = EMCManager.get(stack);
        if (emc <= 0) return;

        if (getFilterCount() > 0) {
            boolean isFiltered = false;
            for (ItemStack filterStack : filter) {
                if (ItemUtil.isEqual(stack.getItem(), filterStack.getItem())) {
                    isFiltered = true;
                    break;
                }
            }
            if (!isFiltered) return;
        }

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        TeamState teamState = getTeamState().get();
        if (!teamState.registeredItems.contains(ItemUtil.toIdAsString(stack.getItem()))) return;

        teamState.storedEMC += emc;
        inv.set(0, ItemStackUtil.empty());
    }

    @Override
    public ItemStackList getItems() {
        return inv;
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
    public int[] getAvailableSlots(AvailableSlotsArgs args) {
        int[] result = new int[getItems().size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = i;
        }
        return result;
    }

    @Override
    public boolean canInsert(CanInsertArgs args) {
        if (args.dir == Direction.DOWN) return false;

        ItemStack stack = args.stack;

        if (!hasTeam()) return false;
        if (!EMCManager.contains(stack.getItem())) return false;

        if (getFilterCount() > 0) {
            boolean isFiltered = false;
            for (ItemStack filterStack : filter) {
                if (ItemUtil.isEqual(stack.getItem(), filterStack.getItem())) {
                    isFiltered = true;
                    break;
                }
            }
            if (!isFiltered) return false;
        }

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        TeamState teamState = getTeamState().get();

        return teamState.registeredItems.contains(ItemUtil.toIdAsString(stack.getItem()));
    }

    @Override
    public Text getDisplayName(DisplayNameArgs args) {
        return TextUtil.translatable("block.itemalchemy.emc_importer");
    }

    @Override
    public void writeExtraData(ExtraDataArgs args) {
        NbtCompound data = NbtUtil.create();
        BlockPos pos = callGetPos();

        NbtUtil.putInt(data, "x", pos.getX());
        NbtUtil.putInt(data, "y", pos.getY());
        NbtUtil.putInt(data, "z", pos.getZ());
        if (teamUUID != null) {
            NbtUtil.putUuid(data, "team", teamUUID);

            getTeamState().ifPresent(teamState -> {

                if (ownerName == null || ownerName.isEmpty()) {
                    if (callGetWorld() == null) return;

                    Player player = PlayerManagerUtil.getPlayerByUUID(callGetWorld(), teamState.owner);
                    if (player.getEntity() == null) return;

                    ownerName = player.getName();
                }
                NbtUtil.putString(data, "ownerName", ownerName);
            });
        }

        args.writeVar(data);
    }

    @Override
    public long getMaxEMC() {
        return 30_000;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canInsert() {
        return true;
    }
}
