package ml.pkom.itemalchemy.tiles;

import ml.pkom.itemalchemy.ItemAlchemy;
import ml.pkom.itemalchemy.blocks.EMCCollector;
import ml.pkom.itemalchemy.blocks.EMCCondenser;
import ml.pkom.itemalchemy.gui.inventory.IInventory;
import ml.pkom.itemalchemy.gui.screens.EMCCondenserScreenHandler;
import ml.pkom.mcpitanlibarch.api.event.block.TileCreateEvent;
import ml.pkom.mcpitanlibarch.api.tile.ExtendBlockEntity;
import ml.pkom.mcpitanlibarch.api.util.TextUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
import org.jetbrains.annotations.Nullable;

public class EMCCondenserTile extends ExtendBlockEntity implements BlockEntityTicker<EMCCondenserTile>, SidedInventory, IInventory, ExtendedScreenHandlerFactory {
    public long storedEMC = 0;
    public int coolDown = 0; // tick

    public int getMaxCoolDown() {
        return 3 * 1; // tick
    }

    public DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1 + 91, ItemStack.EMPTY);

    public EMCCondenserTile(BlockEntityType<?> type, TileCreateEvent event) {
        super(type, event);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putLong("stored_emc", storedEMC);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        storedEMC = nbt.getLong("stored_emc");
        Inventories.readNbt(nbt, inventory);
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

    @Nullable
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new EMCCondenserScreenHandler(syncId, inv, this, this);
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, EMCCondenserTile blockEntity) {
        if (world.isClient) return;
        long oldStoredEMC = storedEMC;
        long maxEMC = ((EMCCondenser) state.getBlock()).maxEMC;

        if (maxEMC > storedEMC) {
            BlockPos[] nearPoses = {pos.up(), pos.down(), pos.north(), pos.south(), pos.east(), pos.west()};
            for (BlockPos nearPos : nearPoses) {
                BlockState nearState = world.getBlockState(nearPos);
                if (nearState.getBlock() instanceof EMCCollector) {
                    BlockEntity nearTile = world.getBlockEntity(nearPos);
                    if (nearTile instanceof EMCCollectorTile) {
                        EMCCollectorTile nearCollectorTile = ((EMCCollectorTile) nearTile);
                        if (maxEMC > storedEMC && nearCollectorTile.storedEMC > 0) {
                            long receiveEMC = nearCollectorTile.storedEMC;
                            if (storedEMC + receiveEMC > maxEMC) {
                                receiveEMC = maxEMC - storedEMC;
                            }
                            nearCollectorTile.storedEMC -= receiveEMC;
                            storedEMC += receiveEMC;
                        }
                    }
                }
            }
        }

        if (!inventory.isEmpty()) {
            if (!inventory.get(0).isEmpty()) {
                if (coolDown == 0) {

                }
                coolDown++;
                if (coolDown >= getMaxCoolDown()) {
                    coolDown = 0;
                }
            }
        }

        if (oldStoredEMC != storedEMC) {
            if (!world.isClient) {
                for (ServerPlayerEntity player : ((ServerWorld) world).getPlayers()) {
                    if (player.networkHandler != null && player.currentScreenHandler instanceof EMCCondenserScreenHandler && ((EMCCondenserScreenHandler) player.currentScreenHandler).tile == this ) {
                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeLong(storedEMC);
                        ServerPlayNetworking.send(player, ItemAlchemy.id("itemalchemy_emc_condenser"), buf);
                    }
                }
            }
        }
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
        return true;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    @Override
    public Text getDisplayName() {
        return TextUtil.translatable("block.itemalchemy.emc_condenser");
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        NbtCompound data = new NbtCompound();
        data.putLong("x", pos.getX());
        data.putLong("y", pos.getY());
        data.putLong("z", pos.getZ());
        data.putLong("stored_emc", storedEMC);
        data.putLong("max_emc", ((EMCCondenser) getCachedState().getBlock()).maxEMC);
        buf.writeNbt(data);
    }
}
