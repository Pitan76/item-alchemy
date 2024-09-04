package net.pitan76.itemalchemy.tile;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.itemalchemy.block.EMCCollector;
import net.pitan76.itemalchemy.block.EMCRepeater;
import net.pitan76.itemalchemy.gui.screen.EMCCondenserScreenHandler;
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.mcpitanlib.api.event.container.factory.DisplayNameArgs;
import net.pitan76.mcpitanlib.api.event.container.factory.ExtraDataArgs;
import net.pitan76.mcpitanlib.api.event.nbt.ReadNbtArgs;
import net.pitan76.mcpitanlib.api.event.nbt.WriteNbtArgs;
import net.pitan76.mcpitanlib.api.event.tile.TileTickEvent;
import net.pitan76.mcpitanlib.api.gui.ExtendedScreenHandlerFactory;
import net.pitan76.mcpitanlib.api.gui.inventory.IInventory;
import net.pitan76.mcpitanlib.api.network.PacketByteUtil;
import net.pitan76.mcpitanlib.api.network.ServerNetworking;
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntity;
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntityTicker;
import net.pitan76.mcpitanlib.api.util.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.pitan76.mcpitanlib.api.util.InventoryUtil.canMergeItems;

public class EMCCondenserTile extends ExtendBlockEntity implements ExtendBlockEntityTicker<EMCCondenserTile>, SidedInventory, IInventory, ExtendedScreenHandlerFactory {
    public long storedEMC = 0;
    public long maxEMC = 0;
    public long oldStoredEMC = 0;
    public long oldMaxEMC = 0;
    public int coolDown = 0; // tick

    public DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1 + 91, ItemStackUtil.empty());

    public EMCCondenserTile(BlockEntityType<?> type, TileCreateEvent e) {
        super(type, e);
    }

    public EMCCondenserTile(BlockPos pos, BlockState state) {
        this(new TileCreateEvent(pos, state));
    }
    public EMCCondenserTile(BlockView world) {
        this(new TileCreateEvent(world));
    }
    public EMCCondenserTile(TileCreateEvent e) {
        this(Tiles.EMC_CONDENSER.getOrNull(), e);
    }

    public int getMaxCoolDown() {
        return 2; // tick
    }

    @Override
    public void writeNbt(WriteNbtArgs args) {
        NbtCompound nbt = args.getNbt();
        InventoryUtil.writeNbt(args, inventory);
        NbtUtil.set(nbt, "stored_emc", storedEMC);
    }

    @Override
    public void readNbt(ReadNbtArgs args) {
        NbtCompound nbt = args.getNbt();
        storedEMC = NbtUtil.get(nbt, "stored_emc", Long.class);
        InventoryUtil.readNbt(args, inventory);
    }

    @Override
    public void tick(TileTickEvent<EMCCondenserTile> e) {
        World world = e.world;
        if (world.isClient) return;

        if (!inventory.isEmpty()) {
            ItemStack targetStack = inventory.get(0);
            if (!targetStack.isEmpty()) {
                maxEMC = EMCManager.get(targetStack.getItem());
            } else {
                maxEMC = 0;
            }
        }

        BlockPos[] nearPoses = {pos.up(), pos.down(), pos.north(), pos.south(), pos.east(), pos.west()};

        for (BlockPos nearPos : EMCRepeater.getNearPoses(world, nearPoses)) {
            BlockState nearState = WorldUtil.getBlockState(world, nearPos);
            if (nearState.getBlock() instanceof EMCCollector) {
                BlockEntity nearTile = world.getBlockEntity(nearPos);
                if (nearTile instanceof EMCCollectorTile) {
                    EMCCollectorTile nearCollectorTile = ((EMCCollectorTile) nearTile);
                    if (nearCollectorTile.storedEMC > 0) {
                        long receiveEMC = nearCollectorTile.storedEMC;
                        nearCollectorTile.storedEMC -= receiveEMC;
                        storedEMC += receiveEMC;
                    }
                }
            }
        }

        if (!inventory.isEmpty()) {
            ItemStack targetStack = inventory.get(0);
            if (!targetStack.isEmpty()) {
                if (coolDown == 0) {
                    List<ItemStack> storageInventory = new ArrayList<>(inventory);

                    if (!storageInventory.isEmpty()) {
                        for (ItemStack stack : inventory) {
                            if (stack.isEmpty()) continue;
                            if (stack.getItem() == targetStack.getItem()) continue;

                            long emc = EMCManager.get(stack.getItem());
                            if (emc == 0) continue;
                            //if (emc + storedEMC <= maxEMC) {
                            storedEMC += emc;
                            stack.decrement(1);
                            break;
                            //}
                        }
                    }

                    long useEMC = EMCManager.get(targetStack.getItem());
                    if (useEMC == 0) useEMC = 1;
                    if (storedEMC >= useEMC) {
                        ItemStack newStack = targetStack.copy();
                        newStack.setCount(1);
                        // Remove NBT
                        CustomDataUtil.setNbt(newStack, new NbtCompound());
                        //newStack.setNbt(new NbtCompound());
                        if (insertItem(newStack, inventory, true)) {
                            insertItem(newStack, inventory);
                            storedEMC -= useEMC;

                            markDirty();
                        }

                    }
                }
                coolDown++;
                if (coolDown >= getMaxCoolDown()) {
                    coolDown = 0;
                }
            }
        }

        if (oldStoredEMC != storedEMC || oldMaxEMC != maxEMC) {
            oldStoredEMC = storedEMC;
            oldMaxEMC = maxEMC;

            for (ServerPlayerEntity player : ((ServerWorld) world).getPlayers()) {
                if (player.networkHandler != null && player.currentScreenHandler instanceof EMCCondenserScreenHandler && ((EMCCondenserScreenHandler) player.currentScreenHandler).tile == this ) {
                    PacketByteBuf buf = PacketByteUtil.create();
                    PacketByteUtil.writeLong(buf, storedEMC);
                    PacketByteUtil.writeLong(buf, maxEMC);
                    //if (!getTargetStack().isEmpty())
                    //    PacketByteUtil.writeItemStack(buf, getTargetStack());

                    ServerNetworking.send(player, ItemAlchemy._id("itemalchemy_emc_condenser").toMinecraft(), buf);
                }
            }
        }
    }

    public static boolean insertItem(ItemStack insertStack, DefaultedList<ItemStack> inventory) {
        return insertItem(insertStack, inventory, false);
    }

    public static boolean insertItem(ItemStack insertStack, DefaultedList<ItemStack> inventory, boolean test) {
        boolean isInserted = false;
        for (int i = 0; i < inventory.size(); i++) {
            // EMC Condenser Target slot
            if (i == 0) continue;
            //
            ItemStack stack = inventory.get(i);
            if (stack.isEmpty()) {
                if (!test) inventory.set(i, insertStack);
                isInserted = true;
                break;
            } else if (canMergeItems(stack, insertStack)) {
                int j = insertStack.getCount();
                if (!test) stack.increment(j);
                isInserted = j > 0;
                break;
            }
        }
        return isInserted;
    }

    public ItemStack getTargetStack() {
        return inventory.get(0);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        int[] result = new int[getItems().size() - 1];
        for (int i = 0; i < result.length; i++) {
            result[i] = i + 1;
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

    @Nullable
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new EMCCondenserScreenHandler(syncId, inv, this, this, getTargetStack());
    }

    @Override
    public Text getDisplayName(DisplayNameArgs args) {
        return TextUtil.translatable("block.itemalchemy.emc_condenser");
    }

    @Override
    public void writeExtraData(ExtraDataArgs args) {
        NbtCompound data = new NbtCompound();
        data.putLong("x", pos.getX());
        data.putLong("y", pos.getY());
        data.putLong("z", pos.getZ());
        data.putLong("stored_emc", storedEMC);
        data.putLong("max_emc", maxEMC);
        args.writeVar(data);
    }

    public void setTargetStack(ItemStack stack) {
        inventory.set(0, stack);
        markDirty();
    }
}
