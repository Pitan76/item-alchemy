package net.pitan76.itemalchemy.tile;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
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

    public EMCCondenserTile(BlockEntityType<?> type, TileCreateEvent e) {
        super(type, e);
    }

    public EMCCondenserTile(TileCreateEvent e) {
        this(Tiles.EMC_CONDENSER.getOrNull(), e);
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
        World world = e.world;
        if (!getItems().isEmpty()) {
            ItemStack targetStack = getItems().get(0);
            if (!ItemStackUtil.isEmpty(targetStack)) {
                maxEMC = EMCManager.get(targetStack.getItem());
            } else {
                maxEMC = 0;
            }
        }

        EMCStorageUtil.transferAllEMC(this);

        if (!getItems().isEmpty()) {
            setActive(true);

            ItemStack targetStack = getItems().get(0);
            if (!ItemStackUtil.isEmpty(targetStack)) {
                if (coolDown == 0) {
                    List<ItemStack> storageInventory = new ArrayList<>(getItems());

                    if (!storageInventory.isEmpty()) {
                        for (ItemStack stack : getItems()) {
                            if (ItemStackUtil.isEmpty(stack)) continue;
                            if (stack.getItem() == targetStack.getItem()) continue;

                            long emc = EMCManager.get(stack.getItem());
                            if (emc == 0) continue;
                            //if (emc + storedEMC <= maxEMC) {
                            storedEMC += emc;
                            ItemStackUtil.decrementCount(stack, 1);
                            break;
                            //}
                        }
                    }

                    long useEMC = EMCManager.get(targetStack.getItem());
                    if (useEMC == 0) useEMC = 1;
                    if (storedEMC >= useEMC) {
                        ItemStack newStack;
                        if (ItemAlchemyConfig.isRemoveDataFromCopyStack()) {
                            // Remove Data
                            newStack = new ItemStack(targetStack.getItem());
                        } else {
                            newStack = targetStack.copy();
                        }

                        ItemStackUtil.setCount(newStack, 1);

                        if (insertItem(newStack, getItems(), true)) {
                            insertItem(newStack, getItems());
                            storedEMC -= useEMC;

                            BlockEntityUtil.markDirty(this);
                        }

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

            for (ServerPlayerEntity serverPlayerEntity : ((ServerWorld) world).getPlayers()) {
                Player player = new Player(serverPlayerEntity);
                if (player.hasNetworkHandler() && player.getCurrentScreenHandler() instanceof EMCCondenserScreenHandler && ((EMCCondenserScreenHandler) player.getCurrentScreenHandler()).tile == this ) {
                    PacketByteBuf buf = PacketByteUtil.create();
                    PacketByteUtil.writeLong(buf, storedEMC);
                    PacketByteUtil.writeLong(buf, maxEMC);
                    //if (!getTargetStack().isEmpty())
                    //    PacketByteUtil.writeItemStack(buf, getTargetStack());

                    ServerNetworking.send(serverPlayerEntity, ItemAlchemy._id("itemalchemy_emc_condenser"), buf);
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
            ItemStack stack = inventory.get(i);
            if (ItemStackUtil.isEmpty(stack)) {
                if (!test) inventory.set(i, insertStack);
                isInserted = true;
                break;
            } else if (canMergeItems(stack, insertStack)) {
                int j = insertStack.getCount();
                if (!test) ItemStackUtil.incrementCount(stack, j);
                isInserted = j > 0;
                break;
            }
        }
        return isInserted;
    }

    public ItemStack getTargetStack() {
        return getItems().get(0);
    }

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
        return new EMCCondenserScreenHandler(e.syncId, e.playerInventory, this, this, getTargetStack());
    }

    @Override
    public Text getDisplayName(DisplayNameArgs args) {
        return TextUtil.translatable("block.itemalchemy.emc_condenser");
    }

    @Override
    public void writeExtraData(ExtraDataArgs args) {
        NbtCompound data = NbtUtil.create();
        NbtUtil.putInt(data, "x", pos.getX());
        NbtUtil.putInt(data, "y", pos.getY());
        NbtUtil.putInt(data, "z", pos.getZ());
        NbtUtil.putLong(data, "stored_emc", storedEMC);
        NbtUtil.putLong(data, "max_emc", maxEMC);
        args.writeVar(data);
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
