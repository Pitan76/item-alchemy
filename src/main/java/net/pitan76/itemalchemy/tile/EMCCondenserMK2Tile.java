package net.pitan76.itemalchemy.tile;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.itemalchemy.api.EMCStorageUtil;
import net.pitan76.itemalchemy.config.ItemAlchemyConfig;
import net.pitan76.itemalchemy.gui.screen.EMCCondenserMK2ScreenHandler;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.mcpitanlib.api.event.tile.TileTickEvent;
import net.pitan76.mcpitanlib.api.network.PacketByteUtil;
import net.pitan76.mcpitanlib.api.network.v2.ServerNetworking;
import net.pitan76.mcpitanlib.api.util.*;
import net.pitan76.mcpitanlib.api.util.collection.ItemStackList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.pitan76.mcpitanlib.api.util.InventoryUtil.canMergeItems;

public class EMCCondenserMK2Tile extends EMCCondenserTile {

    public ItemStackList inventory = ItemStackList.ofSize(1 + 84, ItemStackUtil.empty());

    public EMCCondenserMK2Tile(BlockEntityType<?> type, TileCreateEvent e) {
        super(type, e);
    }

    public EMCCondenserMK2Tile(TileCreateEvent e) {
        this(Tiles.EMC_CONDENSER_MK2.getOrNull(), e);
    }

    @Override
    public int getMaxCoolDown() {
        return 1;
    }

    @Override
    public ItemStackList getItems() {
        return inventory;
    }

    @Override
    @Nullable
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new EMCCondenserMK2ScreenHandler(syncId, inv, this, this, getTargetStack());
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
            ItemStack targetStack = getItems().get(0);
            if (!ItemStackUtil.isEmpty(targetStack)) {
                if (coolDown == 0) {
                    List<ItemStack> storageInventory = new ArrayList<>(getItems());

                    if (!storageInventory.isEmpty()) {
                        for (int i = 1; i < 43; i++) {
                            ItemStack stack = storageInventory.get(i);

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

                        CustomDataUtil.setNbt(newStack, NbtUtil.create());
                        //newStack.setNbt(new NbtCompound());
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
        }

        if (oldStoredEMC != storedEMC || oldMaxEMC != maxEMC) {
            oldStoredEMC = storedEMC;
            oldMaxEMC = maxEMC;

            for (ServerPlayerEntity serverPlayerEntity : ((ServerWorld) world).getPlayers()) {
                Player player = new Player(serverPlayerEntity);
                if (player.hasNetworkHandler() && player.getCurrentScreenHandler() instanceof EMCCondenserMK2ScreenHandler && ((EMCCondenserMK2ScreenHandler) player.getCurrentScreenHandler()).tile == this ) {
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
        for (int i = 43; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (ItemStackUtil.isEmpty(stack)) {
                if (!test) inventory.set(i, insertStack);
                isInserted = true;
                break;
            } else if (canMergeItems(stack, insertStack)) {
                int j = ItemStackUtil.getCount(insertStack);
                if (!test) ItemStackUtil.incrementCount(stack, j);
                isInserted = j > 0;
                break;
            }
        }
        return isInserted;
    }
}
