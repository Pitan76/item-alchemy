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
import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.itemalchemy.block.EMCCollector;
import net.pitan76.itemalchemy.block.EMCRepeater;
import net.pitan76.itemalchemy.gui.screen.EMCCollectorScreenHandler;
import net.pitan76.itemalchemy.item.Items;
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
import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity;
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntityTicker;
import net.pitan76.mcpitanlib.api.util.*;
import org.jetbrains.annotations.Nullable;

public class EMCCollectorTile extends CompatBlockEntity implements ExtendBlockEntityTicker<EMCCollectorTile>, SidedInventory, IInventory, ExtendedScreenHandlerFactory {
    private long oldStoredEMC = 0;
    public long storedEMC = 0;
    public int coolDown = 0; // tick

    public int getMaxCoolDown() {
        return 10 * 1; // tick
    }

    public DefaultedList<ItemStack> inventory = DefaultedList.ofSize(16 + 3, ItemStackUtil.empty());

    public EMCCollectorTile(BlockEntityType<?> type, TileCreateEvent event) {
        super(type, event);
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
    public void tick(TileTickEvent<EMCCollectorTile> e) {
        World world = e.world;

        if (world.isClient()) return;

        long maxEMC = ((EMCCollector) e.state.getBlock()).maxEMC;

        if (coolDown == 0) {
            if (maxEMC <= storedEMC) return;
            float skyAngle = world.getSkyAngle(0);
            if ((!WorldUtil.isRaining(world) && !WorldUtil.isThundering(world) && (WorldUtil.hasSkyLight(world) && skyAngle <= 0.25 || skyAngle >= 0.75) && WorldUtil.isSkyVisible(world, pos.up()))
                    || WorldUtil.getBlockState(world, pos.up()).getLuminance() > 10 || WorldUtil.getBlockState(world, pos.down()).getLuminance() > 10
                    || WorldUtil.getBlockState(world, pos.north()).getLuminance() > 10 || WorldUtil.getBlockState(world, pos.south()).getLuminance() > 10
                    || WorldUtil.getBlockState(world, pos.east()).getLuminance() > 10 || WorldUtil.getBlockState(world, pos.west()).getLuminance() > 10) {
                storedEMC++;
                if (maxEMC >= 100000) {
                    storedEMC += Math.round(maxEMC / 100000) + 2;
                }
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
                            if (convertStack(stack, true) == ItemStackUtil.empty()) continue;
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

            if (!inventory.get(2).isEmpty()) {
                BlockPos[] nearPoses = {pos.up(), pos.down(), pos.north(), pos.south(), pos.east(), pos.west()};
                for (BlockPos nearPos : EMCRepeater.getNearPoses(world, nearPoses)) {
                    BlockState nearState = WorldUtil.getBlockState(world, nearPos);
                    if (nearState.getBlock() instanceof EMCCollector) {
                        BlockEntity nearTile = WorldUtil.getBlockEntity(world, nearPos);
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
                        inventory.set(2, ItemStackUtil.empty());
                    }
                }
            }
            if (!inventory.get(0).isEmpty()) {
                for (int i = 0; i < 16; i++) {
                    if (inventory.get(3 + 15 - i).isEmpty()) {
                        inventory.set(3 + 15 - i, inventory.get(0).copy());
                        inventory.set(0, ItemStackUtil.empty());
                        break;
                    }
                }
            }

            if (oldStoredEMC != storedEMC) {
                oldStoredEMC = storedEMC;
                for (ServerPlayerEntity player : ((ServerWorld) world).getPlayers()) {
                    if (player.networkHandler != null && player.currentScreenHandler instanceof EMCCollectorScreenHandler && ((EMCCollectorScreenHandler) player.currentScreenHandler).tile == this) {
                        PacketByteBuf buf = PacketByteUtil.create();
                        PacketByteUtil.writeLong(buf, storedEMC);
                        ServerNetworking.send(player, ItemAlchemy._id("itemalchemy_emc_collector").toMinecraft(), buf);
                    }
                }
            }
        }
    }

    public ItemStack convertStack(ItemStack stack) {
        return convertStack(stack, false);
    }

    public ItemStack convertStack(ItemStack stack, boolean test) {
        if (!inventory.get(1).isEmpty() && inventory.get(1).getItem() == stack.getItem()) return ItemStackUtil.empty();

        if (net.minecraft.item.Items.COAL == stack.getItem()) {
            if (storedEMC >= 16 || test) {
                if (!test) storedEMC -= 16;
                return ItemStackUtil.create(net.minecraft.item.Items.REDSTONE, 1);
            }
        }
        if (net.minecraft.item.Items.REDSTONE == stack.getItem()) {
            if (storedEMC >= 32 || test) {
                if (!test) storedEMC -= 32;
                return ItemStackUtil.create(net.minecraft.item.Items.GUNPOWDER, 1);
            }
        }
        if (net.minecraft.item.Items.GUNPOWDER == stack.getItem()) {
            if (storedEMC >= 256 || test) {
                if (!test) storedEMC -= 256;
                return ItemStackUtil.create(Items.ALCHEMICAL_FUEL.getOrNull(), 1);
            }
        }
        if (Items.ALCHEMICAL_FUEL.getOrNull() == stack.getItem()) {
            if (storedEMC >= 1024 || test) {
                if (!test) storedEMC -= 1024;
                return ItemStackUtil.create(Items.MOBIUS_FUEL.getOrNull(), 1);
            }
        }
        if (Items.MOBIUS_FUEL.getOrNull() == stack.getItem()) {
            if (storedEMC >= 4096 || test) {
                if (!test) storedEMC -= 4096;
                return ItemStackUtil.create(Items.AETERNALIS_FUEL.getOrNull(), 1);
            }
        }
        return ItemStackUtil.empty();
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
        return dir != Direction.DOWN;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return dir == Direction.DOWN;
    }

    @Override
    public Text getDisplayName(DisplayNameArgs args) {
        return TextUtil.translatable("block.itemalchemy.emc_collector");
    }

    @Override
    public void writeExtraData(ExtraDataArgs args) {
        NbtCompound data = NbtUtil.create();
        data.putLong("x", pos.getX());
        data.putLong("y", pos.getY());
        data.putLong("z", pos.getZ());
        data.putLong("stored_emc", storedEMC);
        data.putLong("max_emc", ((EMCCollector) getCachedState().getBlock()).maxEMC);
        args.writeVar(data);
    }
}
