package net.pitan76.itemalchemy.tile;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.pitan76.itemalchemy.gui.screen.AlchemyChestScreenHandler;
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.mcpitanlib.api.event.nbt.ReadNbtArgs;
import net.pitan76.mcpitanlib.api.event.nbt.WriteNbtArgs;
import net.pitan76.mcpitanlib.api.gui.inventory.IInventory;
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntity;
import net.pitan76.mcpitanlib.api.util.InventoryUtil;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;
import net.pitan76.mcpitanlib.api.util.TextUtil;
import org.jetbrains.annotations.Nullable;

public class AlchemyChestTile extends ExtendBlockEntity implements SidedInventory, IInventory, NamedScreenHandlerFactory {

    public DefaultedList<ItemStack> inventory = DefaultedList.ofSize(104, ItemStackUtil.empty());

    public AlchemyChestTile(BlockEntityType<?> type, TileCreateEvent e) {
        super(type, e);
    }

    @Override
    public void writeNbt(WriteNbtArgs args) {
        InventoryUtil.writeNbt(args, inventory);
    }

    @Override
    public void readNbt(ReadNbtArgs args) {
        InventoryUtil.readNbt(args, inventory);
    }

    public AlchemyChestTile(BlockPos pos, BlockState state) {
        this(new TileCreateEvent(pos, state));
    }

    public AlchemyChestTile(BlockView world) {
        this(new TileCreateEvent(world));
    }

    public AlchemyChestTile(TileCreateEvent e) {
        this(Tiles.ALCHEMY_CHEST.getOrNull(), e);
    }

    @Nullable
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new AlchemyChestScreenHandler(syncId, inv, this);
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

    @Override
    public Text getDisplayName() {
        return TextUtil.translatable("block.itemalchemy.alchemy_chest");
    }
}
