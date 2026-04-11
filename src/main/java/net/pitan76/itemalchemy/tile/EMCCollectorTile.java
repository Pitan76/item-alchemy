package net.pitan76.itemalchemy.tile;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.itemalchemy.api.EMCStorageUtil;
import net.pitan76.itemalchemy.block.Blocks;
import net.pitan76.itemalchemy.block.EMCCollector;
import net.pitan76.itemalchemy.gui.screen.EMCCollectorScreenHandler;
import net.pitan76.itemalchemy.item.Items;
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
import net.pitan76.mcpitanlib.midohra.item.ItemStack;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import net.pitan76.mcpitanlib.midohra.world.World;
import org.jetbrains.annotations.Nullable;

public class EMCCollectorTile extends EMCStorageBlockEntity implements ExtendBlockEntityTicker<EMCCollectorTile>, VanillaStyleSidedInventory, IInventory, ExtendedScreenHandlerFactory {
    private long oldStoredEMC = 0;
    public long maxEMC = -1;
    public int coolDown = 0; // tick

    public int getMaxCoolDown() {
        return 10 * 1; // tick
    }

    public ItemStackList inventory = ItemStackList.ofSize(16 + 3, ItemStackUtil.empty());

    public EMCCollectorTile(BlockEntityType<?> type, TileCreateEvent e) {
        super(type, e);
    }

    public EMCCollectorTile(TileCreateEvent e) {
        this(Tiles.EMC_COLLECTOR.getOrNull(), e);
    }

    @Override
    public long getMaxEMC() {
        if (maxEMC == -1)
            return ((EMCCollector) BlockStateUtil.getBlock(callGetCachedState())).maxEMC;

        return maxEMC;
    }

    @Override
    public void writeNbt(WriteNbtArgs args) {
        super.writeNbt(args);
        InventoryUtil.writeNbt(args, inventory);
    }

    @Override
    public void readNbt(ReadNbtArgs args) {
        super.readNbt(args);
        InventoryUtil.readNbt(args, inventory);
    }

    @Nullable
    public ScreenHandler createMenu(CreateMenuEvent e) {
        return new EMCCollectorScreenHandler(e.syncId, e.playerInventory, this, this);
    }

    @Override
    public void tick(TileTickEvent<EMCCollectorTile> e) {
        World world = e.getMidohraWorld();
        if (e.isClient()) return;

        if (maxEMC == -1)
            maxEMC = ((EMCCollector) e.state.getBlock()).maxEMC;

        BlockPos pos = getMidohraPos();

        if (coolDown == 0) {
            if (isFull()) return;
            float skyAngle = world.getSkyAngle();
            if ((!world.isRaining() && !world.isThundering() && (world.hasSkyLight() && skyAngle <= 0.25 || skyAngle >= 0.75) && world.isSkyVisible(pos.up()))
                    || world.getLuminance(pos.up()) > 10
                    || world.getLuminance(pos.down()) > 10
                    || world.getLuminance(pos.north()) > 10
                    || world.getLuminance(pos.south()) > 10
                    || world.getLuminance(pos.east()) > 10
                    || world.getLuminance(pos.west()) > 10) {
                storedEMC++;
                if (maxEMC >= 100000) {
                    storedEMC += Math.round((float) maxEMC / 100000) + 2;
                }
            }
        }
        coolDown += (int) (1 + Math.pow((double) maxEMC / 10000 - 1, 2));
        if (coolDown >= getMaxCoolDown()) {
            coolDown = 0;
        }

        if (!inventory.isEmpty()) {
            // 0, 1, 2は右スロット 2はinput 0はoutput

            // アイテムの移動
            for (int i = 0; i < inventory.size() - 3; i++) {
                // -1 < i < 16 0以上15以下
                int index = 15 + 3 - i;
                ItemStack stack = inventory.getAsMidohra(index).copy();
                if (!stack.isEmpty()) {
                    if (index > 2) {
                        int nextIndex = index - 1;

                        if (inventory.getAsMidohra(2).isEmpty()) {
                            nextIndex = 2;
                        }
                        if (nextIndex == 2) {
                            if (convertStack(stack, true) == ItemStack.empty()) continue;
                        }

                        ItemStack nextStack = inventory.getAsMidohra(nextIndex);
                        if (nextStack.isEmpty()) {
                            inventory.getAsMidohra(index).decrement(1);
                            stack.setCount(1);
                            inventory.set(nextIndex, stack);
                        } else if (nextStack.getItem() == stack.getItem() && nextStack.getCount() < nextStack.getMaxCount()) {
                            inventory.getAsMidohra(index).decrement(1);
                            nextStack.increment(1);
                        }
                    }
                }
            }

            if (!inventory.isEmpty(2)) {
                setActive(true);

                EMCStorageUtil.transferAllEMC(this);

                if (inventory.isEmpty(0)) {
                    ItemStack stack = convertStack(inventory.getAsMidohra(2).copy());
                    if (!stack.isEmpty()) {
                        inventory.set(0, stack);
                        inventory.getAsMidohra(2).decrement(1);
                    }
                }
            }
            if (!inventory.isEmpty(0)) {
                for (int i = 0; i < 16; i++) {
                    if (inventory.isEmpty(3 + 15 - i)) {
                        inventory.set(3 + 15 - i, inventory.getAsMidohra(0).copy());
                        inventory.set(0, ItemStackUtil.empty());
                        break;
                    }
                }
            }

            if (oldStoredEMC != storedEMC) {
                oldStoredEMC = storedEMC;
                for (Player player : world.getPlayers()) {
                    if (player.hasNetworkHandler() && player.getCurrentScreenHandler() instanceof EMCCollectorScreenHandler && ((EMCCollectorScreenHandler) player.getCurrentScreenHandler()).tile == this) {
                        PacketByteBuf buf = PacketByteUtil.create();
                        PacketByteUtil.writeLong(buf, storedEMC);
                        ServerNetworking.send(player, ItemAlchemy._id("itemalchemy_emc_collector"), buf);
                    }
                }
            }
        } else {
            setActive(false);
        }
    }

    public ItemStack convertStack(ItemStack stack) {
        return convertStack(stack, false);
    }

    public ItemStack convertStack(ItemStack stack, boolean test) {
        net.pitan76.mcpitanlib.midohra.item.ItemStack inputStack = inventory.getAsMidohra(1);

        if (!inventory.getAsMidohra(1).isEmpty() && inputStack.getItem() == stack.getItem()) return ItemStack.EMPTY;

        if (net.pitan76.mcpitanlib.midohra.item.Items.COAL == stack.getItem()) {
            if (storedEMC >= 16 || test) {
                if (!test) storedEMC -= 16;
                return net.pitan76.mcpitanlib.midohra.item.Items.REDSTONE.createStack(1);
            }
        }
        if (net.pitan76.mcpitanlib.midohra.item.Items.REDSTONE == stack.getItem()) {
            if (storedEMC >= 32 || test) {
                if (!test) storedEMC -= 32;
                return net.pitan76.mcpitanlib.midohra.item.Items.GUNPOWDER.createStack(1);
            }
        }
        if (net.pitan76.mcpitanlib.midohra.item.Items.GUNPOWDER == stack.getItem()) {
            if (storedEMC >= 256 || test) {
                if (!test) storedEMC -= 256;
                return ItemStack.of(Items.ALCHEMICAL_FUEL.getOrNull(), 1);
            }
        }
        if (Items.ALCHEMICAL_FUEL.getOrNull() == stack.getRawItem()) {
            if (storedEMC >= 1024 || test) {
                if (!test) storedEMC -= 1024;
                return ItemStack.of(Items.MOBIUS_FUEL.getOrNull(), 1);
            }
        }
        if (Items.MOBIUS_FUEL.getOrNull() == stack.getRawItem()) {
            if (storedEMC >= 4096 || test) {
                if (!test) storedEMC -= 4096;
                return ItemStack.of(Items.AETERNALIS_FUEL.getOrNull(), 1);
            }
        }
        return ItemStack.EMPTY;
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
        return TextUtil.translatable("block.itemalchemy.emc_collector");
    }

    @Override
    public void writeExtraData(ExtraDataArgs args) {
//        NbtCompound nbt = NbtUtil.create();
//        NbtUtil.setBlockPosDirect(nbt, callGetPos());
//        NbtUtil.putLong(nbt, "stored_emc", storedEMC);
//        NbtUtil.putLong(nbt, "max_emc", ((EMCCollector) BlockStateUtil.getBlock(callGetCachedState())).maxEMC);
//        args.writeVar(nbt);
        PacketByteUtil.writeBlockPos(args.buf, callGetPos());
        PacketByteUtil.writeLong(args.buf, storedEMC);
        PacketByteUtil.writeLong(args.buf, ((EMCCollector) getMidohraCachedState().getBlock().getOrDefault(Blocks.EMC_COLLECTOR_MK1.get())).maxEMC);
    }
}
