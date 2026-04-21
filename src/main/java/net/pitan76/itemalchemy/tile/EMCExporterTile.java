package net.pitan76.itemalchemy.tile;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.data.TeamState;
import net.pitan76.itemalchemy.gui.screen.EMCExporterScreenHandler;
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
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

public class EMCExporterTile extends OwnedBlockEntity implements ExtendBlockEntityTicker<EMCExporterTile>, VanillaStyleSidedInventory, IInventory, ExtendedScreenHandlerFactory {

    public static int MAX_STACK_COUNT = 4096;

    public ItemStackList filter = ItemStackList.ofSize(9, ItemStackUtil.empty());
    public String ownerName = "";

    public long oldStoredEMC = -1;

    public EMCExporterTile(BlockEntityTypeWrapper type, TileCreateEvent e) {
        super(type, e);
    }

    public EMCExporterTile(TileCreateEvent e) {
        this(Tiles.EMC_EXPORTER, e);
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

        InventoryUtil.writeNbt(args.registryLookup, filterNbt.toMinecraft(), filter);
        nbt.put("filter", filterNbt);

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

        if (nbt.has("team"))
            teamUUID = nbt.getUuid("team");

        if (nbt.has("ownerName"))
            ownerName = nbt.getString("ownerName");
    }

    @Nullable
    public ScreenHandler createMenu(CreateMenuEvent e) {
        IInventory filterInventory = () -> this.filter;
        return new EMCExporterScreenHandler(e.syncId, e.playerInventory, this, InventoryWrapper.of(filterInventory));
    }

    @Override
    public void tick(TileTickEvent<EMCExporterTile> e) {
        if (e.isClient()) return;

        if (oldStoredEMC != -1 && oldStoredEMC == storedEMC) return;
        if (!hasTeam()) return;

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        TeamState teamState = getTeamState().get();
        if (oldStoredEMC != -1)
            teamState.storedEMC -= oldStoredEMC - storedEMC;

        storedEMC = Math.min(teamState.storedEMC, getMaxEMC());
        oldStoredEMC = storedEMC;
        BlockEntityUtil.markDirty(this);
    }

    @Override
    public net.minecraft.item.ItemStack removeStack(int slot, int count) {
        long consumeEmc = EMCManager.get(CACHE.get(slot).getItem()) * count;

        getTeamState().ifPresent(teamState -> {
            if (teamState.storedEMC < consumeEmc) return;
            teamState.storedEMC -= consumeEmc;
        });

        return IInventory.super.removeStack(slot, count);
    }

    public ItemStackList CACHE = ItemStackList.ofSize(9, ItemStackUtil.empty());

    @Override
    public ItemStackList getItems() {
        ItemStackList result = ItemStackList.ofSize(filter.size(), ItemStackUtil.empty());

        if (!hasTeam()) return result;
        if (getFilterCount() <= 0) return result;

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        TeamState teamState = getTeamState().get();

        long emc = teamState.storedEMC;
        if (emc <= 0) return result;

        int filterCount = getFilterCount();

        long aveEMC = emc / filterCount;

        for (int i = 0; i < filter.size(); i++) {
            ItemStack filterStack = filter.getAsMidohra(i);
            if (filterStack.isEmpty()) continue;

            long neededEMC = EMCManager.get(filterStack);

            if (neededEMC <= 0) continue;
            if (aveEMC < neededEMC) continue;
            if (!teamState.registeredItems.contains(filterStack.getItem().getId().toString())) continue;

            ItemStack stack = filterStack.copy();
            stack.setCount(Math.min((int) Math.floorDiv(aveEMC, neededEMC), MAX_STACK_COUNT));

            result.set(i, stack);
        }

        CACHE = result;
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
    public int[] getAvailableSlots(AvailableSlotsArgs args) {
        int[] result = new int[getItems().size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = i;
        }
        return result;
    }

    @Override
    public Text getDisplayName(DisplayNameArgs args) {
        return TextUtil.translatable("block.itemalchemy.emc_exporter");
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
        return 500_000;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canInsert() {
        return false;
    }
}
