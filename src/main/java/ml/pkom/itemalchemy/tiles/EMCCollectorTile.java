package ml.pkom.itemalchemy.tiles;

import ml.pkom.itemalchemy.ItemAlchemy;
import ml.pkom.itemalchemy.Items;
import ml.pkom.itemalchemy.blocks.EMCCollector;
import ml.pkom.itemalchemy.gui.inventory.IInventory;
import ml.pkom.itemalchemy.gui.screens.EMCCollectorScreenHandler;
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

public class EMCCollectorTile extends ExtendBlockEntity implements BlockEntityTicker<EMCCollectorTile>, SidedInventory, IInventory, ExtendedScreenHandlerFactory {
    public long storedEMC = 0;
    public int coolDown = 0; // tick

    public int getMaxCoolDown() {
        return 10 * 1; // tick
    }

    public DefaultedList<ItemStack> inventory = DefaultedList.ofSize(16 + 3, ItemStack.EMPTY);

    public EMCCollectorTile(BlockEntityType<?> type, TileCreateEvent event) {
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

    public EMCCollectorTile(BlockPos pos, BlockState state) {
        this(new TileCreateEvent(pos, state));
    }

    public EMCCollectorTile(BlockView world) {
        this(new TileCreateEvent(world));
    }

    public EMCCollectorTile(TileCreateEvent event) {
        this(Tiles.EMC_COLLECTOR.getOrNull(), event);
    }

    @Nullable
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new EMCCollectorScreenHandler(syncId, inv, this, this);
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, EMCCollectorTile blockEntity) {
        long maxEMC = ((EMCCollector) state.getBlock()).maxEMC;

        if (coolDown == 0) {
            if (maxEMC <= storedEMC) return;
            float skyAngle = world.getSkyAngle(0);
            if ((!world.isRaining() && !world.isThundering() && (world.getDimension().hasSkyLight() && skyAngle <= 0.25 || skyAngle >= 0.75) && world.isSkyVisible(pos.up())) || world.getBlockState(pos.up()).getLuminance() > 10) {
                storedEMC++;
            }
        }
        coolDown += 1 + Math.pow(maxEMC / 10000 - 1, 2);
        if (coolDown >= getMaxCoolDown()) {
            coolDown = 0;
        }

        if (!inventory.isEmpty()) {
            // 0, 1, 2は右スロット 2はinput 0はoutput
            for (int i = 0; i < inventory.size() - 3; i++) {
                // -1 < i < 16 0以上15以下
                int index = 15 + 3 - i;
                ItemStack stack = inventory.get(index).copy();
                if (!stack.isEmpty()) {
                    if (index > 2) {
                        int nextIndex = index - 1;

                        if (inventory.get(2).isEmpty()) {
                            nextIndex = 2;
                        }
                        if (nextIndex == 2) {
                            if (convertStack(stack, true) == ItemStack.EMPTY) continue;
                        }

                        if (inventory.get(nextIndex).isEmpty()) {
                            inventory.get(index).decrement(1);
                            stack.setCount(1);
                            inventory.set(nextIndex, stack);
                        } else if (inventory.get(nextIndex).getItem() == stack.getItem()) {
                            inventory.get(index).decrement(1);
                            inventory.get(nextIndex).increment(1);
                        }
                    }
                }
            }

            long oldStoredEMC = storedEMC;

            if (!inventory.get(2).isEmpty()) {
                BlockPos[] nearPoses = {pos.up(), pos.down(), pos.north(), pos.south(), pos.east(), pos.west()};
                for (BlockPos nearPos : nearPoses) {
                    BlockState nearState = world.getBlockState(nearPos);
                    if (nearState.getBlock() instanceof EMCCollector) {
                        BlockEntity nearTile = world.getBlockEntity(nearPos);
                        if (nearTile instanceof EMCCollectorTile) {
                            EMCCollectorTile nearCollectorTile = ((EMCCollectorTile) nearTile);
                            if (maxEMC > storedEMC && nearCollectorTile.storedEMC > 0) {
                                nearCollectorTile.storedEMC--;
                                storedEMC++;
                            }
                        }
                    }
                }

                if (inventory.get(0).isEmpty()) {
                    ItemStack stack = convertStack(inventory.get(2).copy());
                    if (!stack.isEmpty()) {
                        inventory.set(0, stack);
                        inventory.set(2, ItemStack.EMPTY);
                    }
                }
            }
            if (!inventory.get(0).isEmpty()) {
                for (int i = 0; i < 16; i++) {
                    if (inventory.get(3 + 15 - i).isEmpty()) {
                        inventory.set(3 + 15 - i, inventory.get(0).copy());
                        inventory.set(0, ItemStack.EMPTY);
                        break;
                    }
                }
            }

            if (oldStoredEMC != storedEMC) {
                if (!world.isClient) {
                    for (ServerPlayerEntity player : ((ServerWorld) world).getPlayers()) {
                        if (player.networkHandler != null && player.currentScreenHandler instanceof EMCCollectorScreenHandler && ((EMCCollectorScreenHandler) player.currentScreenHandler).tile == this ) {
                            PacketByteBuf buf = PacketByteBufs.create();
                            buf.writeLong(storedEMC);
                            ServerPlayNetworking.send(player, ItemAlchemy.id("itemalchemy_emc_collector"), buf);
                        }
                    }
                }
            }
        }
    }

    public ItemStack convertStack(ItemStack stack) {
        return convertStack(stack, false);
    }

    public ItemStack convertStack(ItemStack stack, boolean test) {
        if (!inventory.get(1).isEmpty() && inventory.get(1).getItem() == stack.getItem()) return ItemStack.EMPTY;

        if (net.minecraft.item.Items.COAL == stack.getItem()) {
            if (storedEMC >= 32 || test) {
                if (!test) storedEMC -= 32;
                return new ItemStack(net.minecraft.item.Items.REDSTONE, 1);
            }
        }
        if (net.minecraft.item.Items.REDSTONE == stack.getItem()) {
            if (storedEMC >= 64 || test) {
                if (!test) storedEMC -= 64;
                return new ItemStack(net.minecraft.item.Items.GUNPOWDER, 1);
            }
        }
        if (net.minecraft.item.Items.GUNPOWDER == stack.getItem()) {
            if (storedEMC >= 64 || test) {
                if (!test) storedEMC -= 64;
                return new ItemStack(Items.ALCHEMICAL_FUEL.getOrNull(), 1);
            }
        }
        if (Items.ALCHEMICAL_FUEL.getOrNull() == stack.getItem()) {
            if (storedEMC >= 256 || test) {
                if (!test) storedEMC -= 256;
                return new ItemStack(Items.MOBIUS_FUEL.getOrNull(), 1);
            }
        }
        if (Items.MOBIUS_FUEL.getOrNull() == stack.getItem()) {
            if (storedEMC >= 1024 || test) {
                if (!test) storedEMC -= 1024;
                return new ItemStack(Items.AETERNALIS_FUEL.getOrNull(), 1);
            }
        }
        return ItemStack.EMPTY;
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
        return TextUtil.translatable("block.itemalchemy.emc_collector");
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        NbtCompound data = new NbtCompound();
        data.putLong("x", pos.getX());
        data.putLong("y", pos.getY());
        data.putLong("z", pos.getZ());
        data.putLong("stored_emc", storedEMC);
        data.putLong("max_emc", ((EMCCollector) getCachedState().getBlock()).maxEMC);
        buf.writeNbt(data);
    }
}
