package net.pitan76.itemalchemy.tile;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.itemalchemy.api.EMCStorageUtil;
import net.pitan76.itemalchemy.config.ItemAlchemyConfig;
import net.pitan76.itemalchemy.gui.screen.EMCCondenserScreenHandler;
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
import net.pitan76.mcpitanlib.api.util.inventory.InventoryWrapper;
import net.pitan76.mcpitanlib.midohra.block.entity.BlockEntityTypeWrapper;
import net.pitan76.mcpitanlib.midohra.item.ItemStack;
import net.pitan76.mcpitanlib.midohra.network.CompatPacketByteBuf;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import net.pitan76.mcpitanlib.midohra.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.pitan76.mcpitanlib.api.util.InventoryUtil.canMergeItems;

public class EMCCondenserTile extends EMCStorageBlockEntity implements ExtendBlockEntityTicker<EMCCondenserTile>, VanillaStyleSidedInventory, IInventory, ExtendedScreenHandlerFactory {
    public long maxEMC = 0;
    public long oldStoredEMC = 0;
    public long oldMaxEMC = 0;
    public int coolDown = 0; // tick

    public ItemStackList inventory = ItemStackList.ofSize(1 + 91, ItemStackUtil.empty());

    public EMCCondenserTile(BlockEntityTypeWrapper type, TileCreateEvent e) {
        super(type, e);
    }

    public EMCCondenserTile(TileCreateEvent e) {
        this(Tiles.EMC_CONDENSER, e);
    }

    public int getMaxCoolDown() {
        return 3; // tick
    }

    @Override
    public long getMaxEMC() {
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

    @Override
    public void tick(TileTickEvent<EMCCondenserTile> e) {
        if (e.isClient()) return;
        World world = e.getMidohraWorld();
        if (!getItems().isEmpty()) {
            ItemStack targetStack = getItems().getAsMidohra(0);
            if (!targetStack.isEmpty()) {
                maxEMC = EMCManager.get(targetStack.getItem());
            } else {
                maxEMC = 0;
            }
        }

        EMCStorageUtil.transferAllEMC(this);

        if (!getItems().isEmpty()) {
            setActive(true);

            ItemStack targetStack = getItems().getAsMidohra(0);
            if (!targetStack.isEmpty()) {
                if (coolDown == 0) {
                    List<ItemStack> storageInventory = new ArrayList<>(getItems().toMidohra());

                    if (!storageInventory.isEmpty()) {
                        for (ItemStack stack : getItems().toMidohra()) {
                            if (stack.isEmpty()) continue;
                            if (stack.getItem().equals(targetStack.getItem())) continue;

                            long emc = EMCManager.get(stack.getItem());
                            if (emc == 0) continue;
                            storedEMC += emc;
                            stack.decrement(1);
                            break;
                        }
                    }
                }

                long useEMC = EMCManager.get(targetStack.getItem());
                if (useEMC == 0) useEMC = 1;
                if (storedEMC >= useEMC) {
                    ItemStack newStack;
                    if (ItemAlchemyConfig.isRemoveDataFromCopyStack()) {
                        // Remove Data
                        newStack = targetStack.getItem().createStack();
                    } else {
                        newStack = targetStack.copy();
                    }

                    newStack.setCount(1);

                    if (insertItem(newStack, getItems(), true)) {
                        insertItem(newStack, getItems());
                        storedEMC -= useEMC;

                        BlockEntityUtil.markDirty(this);
                    }

                }

                coolDown++;
                if (coolDown >= getMaxCoolDown()) {
                    coolDown = 0;
                }
            }
        } else {
            setActive(false);
        }

        if (oldStoredEMC != storedEMC || oldMaxEMC != maxEMC) {
            oldStoredEMC = storedEMC;
            oldMaxEMC = maxEMC;

            for (Player player : world.getPlayers()) {
                if (player.hasNetworkHandler() && player.getCurrentScreenHandler() instanceof EMCCondenserScreenHandler && ((EMCCondenserScreenHandler) player.getCurrentScreenHandler()).tile == this) {
                    CompatPacketByteBuf buf = CompatPacketByteBuf.create();
                    PacketByteUtil.writeLong(buf, storedEMC);
                    PacketByteUtil.writeLong(buf, maxEMC);
                    //if (!getTargetStack().isEmpty())
                    //    PacketByteUtil.writeItemStack(buf, getTargetStack());

                    ServerNetworking.send(player, ItemAlchemy._id("itemalchemy_emc_condenser"), buf);
                }
            }
        }
    }

    public static boolean insertItem(ItemStack insertStack, ItemStackList inventory) {
        return insertItem(insertStack, inventory, false);
    }

    public static boolean insertItem(ItemStack insertStack, ItemStackList inventory, boolean test) {
        boolean isInserted = false;
        for (int i = 0; i < inventory.size(); i++) {
            // EMC Condenser Target slot
            if (i == 0) continue;
            //
            ItemStack stack = inventory.getAsMidohra(i);
            if (stack.isEmpty()) {
                if (!test) inventory.set(i, insertStack);
                isInserted = true;
                break;
            } else if (canMergeItems(stack.toMinecraft(), insertStack.toMinecraft()) && stack.getCount() < stack.getMaxCount()) {
                int j = insertStack.getCount();
                if (!test) stack.increment(j);
                isInserted = j > 0;
                break;
            }
        }
        return isInserted;
    }

    public ItemStack getTargetStack() {
        return getItems().getAsMidohra(0);
    }

    // TODO: List<ItemStack> getItemsM の実装
    @Override
    public ItemStackList getItems() {
        return inventory;
    }

    @Override
    public int[] getAvailableSlots(AvailableSlotsArgs args) {
        int[] result = new int[getItems().size() - 1];
        for (int i = 0; i < result.length; i++) {
            result[i] = i + 1;
        }
        return result;
    }

    @Override
    @Nullable
    public ScreenHandler createMenu(CreateMenuEvent e) {
        return new EMCCondenserScreenHandler(e.syncId, e.playerInventory, this, InventoryWrapper.of(this), getTargetStack());
    }

    @Override
    public Text getDisplayName(DisplayNameArgs args) {
        return TextUtil.translatable("block.itemalchemy.emc_condenser");
    }

    @Override
    public void writeExtraData(ExtraDataArgs args) {
        BlockPos pos = getMidohraPos();

        PacketByteUtil.writeInt(args.buf, pos.getX());
        PacketByteUtil.writeInt(args.buf, pos.getY());
        PacketByteUtil.writeInt(args.buf, pos.getZ());
        PacketByteUtil.writeLong(args.buf, storedEMC);
        PacketByteUtil.writeLong(args.buf, maxEMC);
        PacketByteUtil.writeItemStack(args.buf, getTargetStack().toMinecraft());
    }

    public void setTargetStack(ItemStack stack) {
        getItems().set(0, stack);
        BlockEntityUtil.markDirty(this);
    }

    @Override
    public boolean canExtract() {
        return false;
    }
}
