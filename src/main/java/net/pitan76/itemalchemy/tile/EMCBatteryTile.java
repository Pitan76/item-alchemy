package net.pitan76.itemalchemy.tile;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.itemalchemy.api.EMCStorageUtil;
import net.pitan76.itemalchemy.block.EMCBattery;
import net.pitan76.itemalchemy.gui.screen.EMCBatteryScreenHandler;
import net.pitan76.itemalchemy.tile.base.EMCStorageBlockEntity;
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
import net.pitan76.mcpitanlib.api.network.v2.ServerNetworking;
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntityTicker;
import net.pitan76.mcpitanlib.api.util.*;
import net.pitan76.mcpitanlib.api.util.collection.ItemStackList;
import net.pitan76.mcpitanlib.api.util.math.PosUtil;
import org.jetbrains.annotations.Nullable;

public class EMCBatteryTile extends EMCStorageBlockEntity implements ExtendBlockEntityTicker<EMCBatteryTile>, VanillaStyleSidedInventory, IInventory, ExtendedScreenHandlerFactory {
    public long maxEMC = -1;
    public int coolDown = 0; // tick

    private long oldStoredEMC = 0;

    public int getMaxCoolDown() {
        return 10;
    }

    public ItemStackList inventory = ItemStackList.ofSize(2, ItemStackUtil.empty());

    public EMCBatteryTile(BlockEntityType<?> type, TileCreateEvent e) {
        super(type, e);
    }

    public EMCBatteryTile(TileCreateEvent e) {
        this(Tiles.EMC_BATTERY.getOrNull(), e);
    }

    @Override
    public long getMaxEMC() {
        if (maxEMC == -1)
            return ((EMCBattery) BlockStateUtil.getBlock(getCachedState())).getMaxEMC();

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
    public ScreenHandler createMenu(CreateMenuEvent e) {
        return new EMCBatteryScreenHandler(e.syncId, e.playerInventory, this, this);
    }

    @Override
    public void tick(TileTickEvent<EMCBatteryTile> e) {
        if (e.isClient()) return;
        World world = e.world;

        if (maxEMC == -1)
            maxEMC = ((EMCBattery) BlockStateUtil.getBlock(e.state)).getMaxEMC();

        //if (maxEMC <= storedEMC) return;

        coolDown += (int) (1 + Math.pow((double) maxEMC / 10000 - 1, 2));
        if (coolDown >= getMaxCoolDown())
            coolDown = 0;

       // if (inventory.isEmpty()) return;

        //if (!inventory.get(2).isEmpty()) {

        EMCStorageUtil.transferAllEMC(this, true);

        if (oldStoredEMC != storedEMC) {
            oldStoredEMC = storedEMC;
            for (ServerPlayerEntity p : ((ServerWorld) world).getPlayers()) {
                Player player = new Player(p);
                if (player.hasNetworkHandler() && player.getCurrentScreenHandler() instanceof EMCBatteryScreenHandler && ((EMCBatteryScreenHandler) player.getCurrentScreenHandler()).tile == this) {
                    PacketByteBuf buf = PacketByteUtil.create();
                    PacketByteUtil.writeLong(buf, storedEMC);
                    ServerNetworking.send(player, ItemAlchemy._id("itemalchemy_emc_battery"), buf);
                }
            }
        }

    }

    @Override
    public ItemStackList getItems() {
        return inventory;
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
        return TextUtil.translatable("block.itemalchemy.emc_battery");
    }

    @Override
    public void writeExtraData(ExtraDataArgs args) {
        NbtCompound data = NbtUtil.create();
        BlockPos pos = callGetPos();

        NbtUtil.putInt(data, "x", PosUtil.x(pos));
        NbtUtil.putInt(data, "y", PosUtil.y(pos));
        NbtUtil.putInt(data, "z", PosUtil.z(pos));
        NbtUtil.putLong(data, "stored_emc", storedEMC);
        NbtUtil.putLong(data, "max_emc", getMaxEMC());
        args.writeVar(data);
    }
}
