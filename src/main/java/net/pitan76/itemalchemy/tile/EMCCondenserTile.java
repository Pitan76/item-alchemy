package net.pitan76.itemalchemy.tile;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
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
import net.pitan76.mcpitanlib.api.gui.inventory.IInventory;
import net.pitan76.mcpitanlib.api.network.PacketByteUtil;
import net.pitan76.mcpitanlib.api.network.ServerNetworking;
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntity;
import net.pitan76.mcpitanlib.api.util.TextUtil;
import org.jetbrains.annotations.Nullable;

import static net.pitan76.mcpitanlib.api.util.InventoryUtil.canMergeItems;

public class EMCCondenserTile extends ExtendBlockEntity implements BlockEntityTicker<EMCCondenserTile>, SidedInventory, IInventory, ExtendedScreenHandlerFactory {
    public long storedEMC = 0;
    public long maxEMC = 0;
    public long oldStoredEMC = 0;
    public long oldMaxEMC = 0;
    public int coolDown = 0; // tick

    public ItemStack targetStack = ItemStack.EMPTY;

    public DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1 + 91, ItemStack.EMPTY);

    public EMCCondenserTile(BlockEntityType<?> type, TileCreateEvent event) {
        super(type, event);
    }
    public EMCCondenserTile(BlockPos pos, BlockState state) {
        this(new TileCreateEvent(pos, state));
    }
    public EMCCondenserTile(BlockView world) {
        this(new TileCreateEvent(world));
    }
    public EMCCondenserTile(TileCreateEvent event) {
        this(Tiles.EMC_CONDENSER.getOrNull(), event);
    }

    public int getMaxCoolDown() {
        return 2; // tick
    }

    @Override
    public void writeNbtOverride(NbtCompound nbt) {
        super.writeNbtOverride(nbt);

        Inventories.writeNbt(nbt, inventory);
        nbt.putLong("stored_emc", storedEMC);

        NbtCompound targetStackNbt = new NbtCompound();
        targetStack.writeNbt(targetStackNbt);
        nbt.put("target_item", targetStackNbt);
    }

    @Override
    public void readNbtOverride(NbtCompound nbt) {
        super.readNbtOverride(nbt);
        storedEMC = nbt.getLong("stored_emc");
        Inventories.readNbt(nbt, inventory);

        //旧仕様の対応
        if(!inventory.get(0).isEmpty() && !nbt.contains("target_item")) {
            targetStack = inventory.get(0);
        }

        if(nbt.contains("target_item")) {
            targetStack = ItemStack.fromNbt(nbt.getCompound("target_item"));
        }
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        NbtCompound data = new NbtCompound();
        data.putLong("x", pos.getX());
        data.putLong("y", pos.getY());
        data.putLong("z", pos.getZ());
        data.putLong("stored_emc", storedEMC);
        data.putLong("max_emc", maxEMC);

        NbtCompound targetStackNbt = new NbtCompound();

        targetStack.writeNbt(targetStackNbt);

        data.put("target_item", targetStackNbt);

        PacketByteUtil.writeNbt(buf, data);
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, EMCCondenserTile blockEntity) {
        if(world.isClient()) {
            return;
        }

        maxEMC = EMCManager.get(targetStack.getItem());

        BlockPos[] nearPoses = { pos.up(), pos.down(), pos.north(), pos.south(), pos.east(), pos.west() };

        for (BlockPos nearPos : EMCRepeater.getNearPoses(world, nearPoses)) {
            BlockState nearState = world.getBlockState(nearPos);

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

        if (coolDown == 0) {
            for (ItemStack stack : inventory) {
                if (targetStack.isEmpty()) {
                    break;
                }

                if (stack.isEmpty()) {
                    continue;
                }

                if (targetStack.getItem() == stack.getItem()) {
                    continue;
                }

                long emc = EMCManager.get(stack.getItem()) * stack.getCount();

                if (emc == 0) {
                    continue;
                }

                stack.decrement(stack.getCount());

                storedEMC += emc;

                break;
            }
        }

        if(targetStack.isEmpty()) {
            return;
        }

        long useEMC = EMCManager.get(targetStack.getItem());

        if (useEMC == 0) {
            return;
        }

        if (storedEMC >= useEMC) {
            ItemStack newStack = new ItemStack(targetStack.getItem());

            newStack.setCount(1);

            if (insertItem(newStack, inventory, true)) {
                insertItem(newStack, inventory);
                storedEMC -= useEMC;

                markDirty();
            }
        }

        coolDown++;
        if (coolDown >= getMaxCoolDown()) {
            coolDown = 0;
        }

        if (oldStoredEMC != storedEMC || oldMaxEMC != maxEMC) {
            oldStoredEMC = storedEMC;
            oldMaxEMC = maxEMC;

            for (ServerPlayerEntity player : ((ServerWorld) world).getPlayers()) {
                if (player.networkHandler != null && player.currentScreenHandler instanceof EMCCondenserScreenHandler && ((EMCCondenserScreenHandler) player.currentScreenHandler).tile == this ) {
                    PacketByteBuf buf = PacketByteUtil.create();
                    PacketByteUtil.writeLong(buf, storedEMC);
                    PacketByteUtil.writeLong(buf, maxEMC);

                    if(!targetStack.isEmpty()) {
                        PacketByteUtil.writeItemStack(buf, targetStack);
                    }
                    else {
                        PacketByteUtil.writeItemStack(buf, inventory.get(0));
                    }

                    ServerNetworking.send(player, ItemAlchemy.id("itemalchemy_emc_condenser"), buf);
                }
            }
        }
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

    @Override
    public Text getDisplayName() {
        return TextUtil.translatable("block.itemalchemy.emc_condenser");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new EMCCondenserScreenHandler(syncId, inv, this, this, targetStack);
    }

    public static boolean insertItem(ItemStack insertStack, DefaultedList<ItemStack> inventory, boolean test) {
        boolean isInserted = false;
        for (int slot = 0; slot < inventory.size(); slot++) {
            // EMC Condenser Target slot
            if (slot == 0) {
                continue;
            }

            ItemStack stack = inventory.get(slot);
            if (stack.isEmpty()) {
                if (!test) {
                    inventory.set(slot, insertStack);
                }

                isInserted = true;

                break;
            } else if (canMergeItems(stack, insertStack)) {
                int j = insertStack.getCount();

                if (!test){
                    stack.increment(j);
                }

                isInserted = j > 0;

                break;
            }
        }
        return isInserted;
    }

    public static boolean insertItem(ItemStack insertStack, DefaultedList<ItemStack> inventory) {
        return insertItem(insertStack, inventory, false);
    }

    public ItemStack getTargetStack() {
        return targetStack;
    }

    public void setTargetStack(ItemStack stack) {
        targetStack = stack;
        markDirty();
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }
}
