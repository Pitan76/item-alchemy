package net.pitan76.itemalchemy.tile;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.pitan76.itemalchemy.gui.screen.AlchemyChestScreenHandler;
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.mcpitanlib.api.event.container.factory.DisplayNameArgs;
import net.pitan76.mcpitanlib.api.event.nbt.ReadNbtArgs;
import net.pitan76.mcpitanlib.api.event.nbt.WriteNbtArgs;
import net.pitan76.mcpitanlib.api.gui.args.CreateMenuEvent;
import net.pitan76.mcpitanlib.api.gui.inventory.IInventory;
import net.pitan76.mcpitanlib.api.gui.inventory.sided.VanillaStyleSidedInventory;
import net.pitan76.mcpitanlib.api.gui.inventory.sided.args.AvailableSlotsArgs;
import net.pitan76.mcpitanlib.api.gui.v2.SimpleScreenHandlerFactory;
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntity;
import net.pitan76.mcpitanlib.api.util.InventoryUtil;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;
import net.pitan76.mcpitanlib.api.util.TextUtil;
import net.pitan76.mcpitanlib.api.util.collection.ItemStackList;
import org.jetbrains.annotations.Nullable;

public class AlchemyChestTile extends ExtendBlockEntity implements VanillaStyleSidedInventory, IInventory, SimpleScreenHandlerFactory {

    public ItemStackList inventory = ItemStackList.ofSize(104, ItemStackUtil.empty());

    public AlchemyChestTile(BlockEntityType<?> type, TileCreateEvent e) {
        super(type, e);
    }

    public AlchemyChestTile(TileCreateEvent e) {
        this(Tiles.ALCHEMY_CHEST.getOrNull(), e);
    }

    @Override
    public void writeNbt(WriteNbtArgs args) {
        InventoryUtil.writeNbt(args, inventory);
    }

    @Override
    public void readNbt(ReadNbtArgs args) {
        InventoryUtil.readNbt(args, inventory);
    }

    @Nullable
    public ScreenHandler createMenu(CreateMenuEvent e) {
        return new AlchemyChestScreenHandler(e.syncId, e.playerInventory, this);
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
    public Text getDisplayName(DisplayNameArgs args) {
        return TextUtil.translatable("block.itemalchemy.alchemy_chest");
    }
}
