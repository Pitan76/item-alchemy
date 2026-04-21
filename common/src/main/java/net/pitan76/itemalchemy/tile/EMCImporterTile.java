package net.pitan76.itemalchemy.tile;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
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
import net.pitan76.mcpitanlib.api.network.PacketByteUtil;
import net.pitan76.mcpitanlib.api.registry.CompatRegistryLookup;
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntityTicker;
import net.pitan76.mcpitanlib.api.util.*;
import net.pitan76.mcpitanlib.api.util.collection.ItemStackList;
import net.pitan76.mcpitanlib.api.util.inventory.InventoryWrapper;
import net.pitan76.mcpitanlib.midohra.block.entity.BlockEntityTypeWrapper;
import net.pitan76.mcpitanlib.midohra.item.ItemStack;
import net.pitan76.mcpitanlib.midohra.nbt.NbtCompound;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import net.pitan76.mcpitanlib.midohra.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

public class EMCImporterTile extends OwnedBlockEntity implements ExtendBlockEntityTicker<EMCImporterTile>, VanillaStyleSidedInventory, IInventory, ExtendedScreenHandlerFactory {

    public ItemStackList filter = ItemStackList.ofSize(9, ItemStackUtil.empty());
    public ItemStackList inv = ItemStackList.ofSize(1, ItemStackUtil.empty());
    public String ownerName = "";

    public EMCImporterTile(BlockEntityTypeWrapper type, TileCreateEvent e) {
        super(type, e);
    }

    public EMCImporterTile(TileCreateEvent e) {
        this(Tiles.EMC_IMPORTER, e);
    }

    @Override
    public net.minecraft.nbt.NbtCompound toInitialChunkDataNbt(CompatRegistryLookup registryLookup) {
        NbtCompound nbt = NbtCompound.of();
        NbtCompound filterNbt = NbtCompound.of();

        InventoryUtil.writeNbt(registryLookup, filterNbt.toMinecraft(), filter);
        nbt.put("filter", filterNbt);

        if (teamUUID != null)
            nbt.putUuid("team", teamUUID);

        if (ownerName != null && !ownerName.isEmpty())
            nbt.putString("ownerName", ownerName);

        return nbt.toMinecraft();
    }

    @Override
    public void writeNbt(WriteNbtArgs args) {
        super.writeNbt(args);
        NbtCompound nbt = args.getNbtM();
        NbtCompound filterNbt = NbtCompound.of();
        NbtCompound invNbt = NbtCompound.of();

        InventoryUtil.writeNbt(args.registryLookup, filterNbt.toMinecraft(), filter);
        InventoryUtil.writeNbt(args.registryLookup, invNbt.toMinecraft(), inv);

        nbt.put("filter", filterNbt);
        nbt.put("inv", invNbt);

        if (teamUUID != null)
            nbt.putUuid("team", teamUUID);

        if (ownerName != null && !ownerName.isEmpty())
            nbt.putString("ownerName", ownerName);
    }

    @Override
    public void readNbt(ReadNbtArgs args) {
        super.readNbt(args);
        NbtCompound nbt = args.getNbtM();

        if (nbt.has("filter")) {
            NbtCompound filterNbt = nbt.getCompound("filter");
            InventoryUtil.readNbt(args.registryLookup, filterNbt.toMinecraft(), filter);
        }

        if (nbt.has("inv")) {
            NbtCompound invNbt = nbt.getCompound("inv");
            InventoryUtil.readNbt(args.registryLookup, invNbt.toMinecraft(), inv);
        }

        if (nbt.has("team"))
            teamUUID = NbtUtil.getUuid(args.nbt, "team");

        if (nbt.has("ownerName"))
            ownerName = NbtUtil.getString(args.nbt, "ownerName");
    }

    @Nullable
    public ScreenHandler createMenu(CreateMenuEvent e) {
        IInventory filterInventory = () -> this.filter;
        return new EMCImporterScreenHandler(e.syncId, e.playerInventory, this, InventoryWrapper.of(this), InventoryWrapper.of(filterInventory));
    }

    @Override
    public void tick(TileTickEvent<EMCImporterTile> e) {
        if (e.isClient()) return;
        if (!hasTeam()) return;

        EMCStorageUtil.transferAllEMC(this);
        if (storedEMC > 0) {
            @SuppressWarnings("OptionalGetWithoutIsPresent")
            TeamState teamState = getTeamState().get();

            teamState.storedEMC += storedEMC;
            storedEMC = 0;
            BlockEntityUtil.markDirty(this);
        }

        if (inv.isEmpty(0)) return;

        ItemStack stack = inv.getAsMidohra(0);
        long emc = EMCManager.get(stack);
        if (emc <= 0) return;

        if (getFilterCount() > 0) {
            boolean isFiltered = false;
            for (ItemStack filterStack : filter.toMidohra()) {
                if (stack.getItem().equals(filterStack.getItem())) {
                    isFiltered = true;
                    break;
                }
            }
            if (!isFiltered) return;
        }

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        TeamState teamState = getTeamState().get();
        if (!teamState.registeredItems.contains(stack.getItem().getId().toString())) return;

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
        if (args.dir == null) return false;
        Direction dir = Direction.of(args.dir);

        if (dir.equals(Direction.DOWN)) return false;

        ItemStack stack = args.getStack_midohra();

        if (!hasTeam()) return false;
        if (!EMCManager.contains(stack.getItem())) return false;

        if (getFilterCount() > 0) {
            boolean isFiltered = false;
            for (ItemStack filterStack : filter.toMidohra()) {
                if (stack.getItem().equals(filterStack.getItem())) {
                    isFiltered = true;
                    break;
                }
            }
            if (!isFiltered) return false;
        }

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        TeamState teamState = getTeamState().get();

        return teamState.registeredItems.contains(stack.getItem().getId().toString());
    }

    @Override
    public Text getDisplayName(DisplayNameArgs args) {
        return TextUtil.translatable("block.itemalchemy.emc_importer");
    }

    @Override
    public void writeExtraData(ExtraDataArgs args) {
        BlockPos pos = getMidohraPos();

        PacketByteUtil.writeInt(args.buf, pos.getX());
        PacketByteUtil.writeInt(args.buf, pos.getY());
        PacketByteUtil.writeInt(args.buf, pos.getZ());

        boolean hasTeam = teamUUID != null;
        PacketByteUtil.writeBoolean(args.buf, hasTeam);

        if (hasTeam) {
            PacketByteUtil.writeUuid(args.buf, teamUUID);

            AtomicBoolean isWrittenOwnerName = new AtomicBoolean(false);

            getTeamState().ifPresent(teamState -> {
                if (ownerName == null || ownerName.isEmpty()) {
                    if (callGetWorld() == null) return;

                    Player player = PlayerManagerUtil.getPlayerByUUID(callGetWorld(), teamState.owner);
                    if (player.getEntity() == null) return;

                    ownerName = player.getName();
                }

                isWrittenOwnerName.set(true);
                PacketByteUtil.writeBoolean(args.buf, isWrittenOwnerName.get());
                PacketByteUtil.writeString(args.buf, ownerName);
            });

            if (!isWrittenOwnerName.get()) PacketByteUtil.writeBoolean(args.buf, false);
        }
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
