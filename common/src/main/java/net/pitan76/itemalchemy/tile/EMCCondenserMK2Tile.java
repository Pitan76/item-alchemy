package net.pitan76.itemalchemy.tile;

import net.minecraft.screen.ScreenHandler;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.itemalchemy.api.EMCStorageUtil;
import net.pitan76.itemalchemy.config.ItemAlchemyConfig;
import net.pitan76.itemalchemy.gui.screen.EMCCondenserMK2ScreenHandler;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.mcpitanlib.api.event.tile.TileTickEvent;
import net.pitan76.mcpitanlib.api.gui.args.CreateMenuEvent;
import net.pitan76.mcpitanlib.api.gui.inventory.sided.args.CanExtractArgs;
import net.pitan76.mcpitanlib.api.network.PacketByteUtil;
import net.pitan76.mcpitanlib.api.network.v2.ServerNetworking;
import net.pitan76.mcpitanlib.api.util.*;
import net.pitan76.mcpitanlib.api.util.collection.ItemStackList;
import net.pitan76.mcpitanlib.api.util.inventory.InventoryWrapper;
import net.pitan76.mcpitanlib.midohra.block.entity.BlockEntityTypeWrapper;
import net.pitan76.mcpitanlib.midohra.item.ItemStack;
import net.pitan76.mcpitanlib.midohra.network.CompatPacketByteBuf;
import net.pitan76.mcpitanlib.midohra.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.pitan76.mcpitanlib.api.util.InventoryUtil.canMergeItems;

public class EMCCondenserMK2Tile extends EMCCondenserTile {

    public ItemStackList inventory = ItemStackList.ofSize(1 + 84, ItemStackUtil.empty());

    public EMCCondenserMK2Tile(BlockEntityTypeWrapper type, TileCreateEvent e) {
        super(type, e);
    }

    public EMCCondenserMK2Tile(TileCreateEvent e) {
        this(Tiles.EMC_CONDENSER_MK2, e);
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
    public ScreenHandler createMenu(CreateMenuEvent e) {
        return new EMCCondenserMK2ScreenHandler(e.syncId, e.playerInventory, this, InventoryWrapper.of(this), getTargetStack());
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
            ItemStack targetStack = getItems().getAsMidohra(0);
            if (!targetStack.isEmpty()) {
                if (coolDown == 0) {
                    List<ItemStack> storageInventory = new ArrayList<>(getItems().toMidohra());

                    if (!storageInventory.isEmpty()) {
                        for (int i = 1; i < 43; i++) {
                            ItemStack stack = storageInventory.get(i);

                            if (stack.isEmpty()) continue;
                            if (stack.getItem().equals(targetStack.getItem())) continue;

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
                        ItemStack newStack;
                        if (ItemAlchemyConfig.isRemoveDataFromCopyStack()) {
                            // Remove Data
                            newStack = targetStack.getItem().createStack();
                        } else {
                            newStack = targetStack.copy();
                        }

                        newStack.setCount(1);

                        newStack.setCustomNbt(NbtUtil.create());
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

            for (Player player : world.getPlayers()) {
                if (player.hasNetworkHandler() && player.getCurrentScreenHandler() instanceof EMCCondenserMK2ScreenHandler && ((EMCCondenserMK2ScreenHandler) player.getCurrentScreenHandler()).tile == this) {
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
        for (int i = 43; i < inventory.size(); i++) {
            ItemStack stack = inventory.getAsMidohra(i);
            if (stack.isEmpty()) {
                if (!test) inventory.set(i, insertStack);
                isInserted = true;
                break;
                // TODO: canMergeItemsでMidohraのItemStackを対応させる
            } else if (canMergeItems(stack.toMinecraft(), insertStack.toMinecraft())) {
                int j = insertStack.getCount();
                if (!test) stack.increment(j);
                isInserted = j > 0;
                break;
            }
        }
        return isInserted;
    }

    @Override
    public boolean canExtract(CanExtractArgs args) {
        return args.getSlot() >= 43;
    }
}
