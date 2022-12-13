package ml.pkom.itemalchemy.gui.screens;

import ml.pkom.itemalchemy.EMCManager;
import ml.pkom.itemalchemy.ScreenHandlers;
import ml.pkom.itemalchemy.api.PlayerRegisteredItemUtil;
import ml.pkom.itemalchemy.gui.inventory.*;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.gui.SimpleScreenHandler;
import ml.pkom.mcpitanlibarch.api.nbt.NbtTag;
import ml.pkom.mcpitanlibarch.api.util.ItemUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class AlchemyTableScreenHandler extends SimpleScreenHandler {
    public RegisterInventory registerInventory; // contains InputSlot(0)
    public ExtractInventory extractInventory;
    public Inventory otherInventory = new SimpleInventory(52);

    public AlchemyTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new RegisterInventory(64, new Player(playerInventory.player)));
    }

    public int index = 0;

    public void nextExtractSlots() {
        if (PlayerRegisteredItemUtil.count(extractInventory.player) < 13 * (index + 1) + 1) return;
        index++;
        if (searchText.isEmpty())
            extractInventory.placeExtractSlots();
        else
            sortBySearch();
    }

    public void prevExtractSlots() {
        if (index <= 0) return;
        index--;
        if (searchText.isEmpty())
            extractInventory.placeExtractSlots();
        else
            sortBySearch();
    }

    public AlchemyTableScreenHandler(int syncId, PlayerInventory playerInventory, RegisterInventory inventory) {
        this(ScreenHandlers.ALCHEMY_TABLE, syncId, playerInventory, inventory);
        Player player = new Player(playerInventory.player);
        extractInventory = new ExtractInventory(13 + 80, player, this);
        registerInventory = inventory;
        addPlayerMainInventorySlots(playerInventory, 24, 140);
        addPlayerHotbarSlots(playerInventory, 24, 198);
        addRegisterSlot(registerInventory, 50, 42, 120); // Input
        addSlot(new RemoveSlot(otherInventory, 51, 24, 120, player)); // Remove?
        addRegisterSlot(registerInventory, 52, 41, 19);
        addRegisterSlot(registerInventory, 53, 15, 30);
        addRegisterSlot(registerInventory, 54, 66, 30);
        addRegisterSlot(registerInventory, 55, 28, 41);
        addRegisterSlot(registerInventory, 56, 51, 41);
        addRegisterSlot(registerInventory, 57, 7, 57);
        addRegisterSlot(registerInventory, 58, 29, 65);
        addRegisterSlot(registerInventory, 59, 52, 65);
        addRegisterSlot(registerInventory, 60, 74, 54);
        addRegisterSlot(registerInventory, 61, 15, 79);
        addRegisterSlot(registerInventory, 62, 66, 79);
        addRegisterSlot(registerInventory, 63, 41, 89);
        addExtractSlot(extractInventory, 64, 144, 13); // 13
        addExtractSlot(extractInventory, 65, 114, 25);
        addExtractSlot(extractInventory, 66, 174, 25);
        addExtractSlot(extractInventory, 67, 144, 33);
        addExtractSlot(extractInventory, 68, 102, 55);
        addExtractSlot(extractInventory, 69, 122, 55);
        addExtractSlot(extractInventory, 70, 144, 55); // Register ?
        addExtractSlot(extractInventory, 71, 166, 55);
        addExtractSlot(extractInventory, 72, 186, 55);
        addExtractSlot(extractInventory, 73, 144, 76);
        addExtractSlot(extractInventory, 74, 114, 85);
        addExtractSlot(extractInventory, 75, 174, 85);
        addExtractSlot(extractInventory, 76, 144, 97);
        setSearchText("");
    }

//API

    public Inventory inventory;

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public ScreenHandlerType<?> type = null;
    public boolean canUse = true;

    public AlchemyTableScreenHandler(ScreenHandlerType type, int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(type, syncId);
        this.type = type;
        this.inventory = inventory;
    }

    @Deprecated
    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(new Player(player));
    }

    public boolean canUse(Player player) {
        return this.canUse;
    }

    @Override
    protected Slot addSlot(Slot slot) {
        return super.addSlot(slot);
    }

    protected Slot addNormalSlot(Inventory inventory, int index, int x, int y) {
        Slot slot = new Slot(inventory, index, x, y);
        return this.addSlot(slot);
    }

    protected Slot addExtractSlot(ExtractInventory inventory, int index, int x, int y) {
        Slot slot = new ExtractSlot(inventory, index, x, y);
        return this.addSlot(slot);
    }

    protected Slot addRegisterSlot(RegisterInventory inventory, int index, int x, int y) {
        Slot slot = new RegisterSlot(inventory, index, x, y);
        return this.addSlot(slot);
    }

    public static final int DEFAULT_SLOT_SIZE = 18;

    /**
     * Add player main inventory slots
     * @param inventory target player inventory
     * @param x start x
     * @param y start y
     */
    protected List<Slot> addPlayerMainInventorySlots(PlayerInventory inventory, int x, int y) {
        return this.addSlots(inventory, 9, x, y, DEFAULT_SLOT_SIZE, 9, 3);
    }

    /**
     * Add player hotbar slots
     * @param inventory target player inventory
     * @param x start x
     * @param y start y
     */
    protected List<Slot> addPlayerHotbarSlots(PlayerInventory inventory, int x, int y) {
        return this.addSlotsX(inventory, 0, x, y, DEFAULT_SLOT_SIZE, 9);
    }

    /**
     * 一括でスロットを設置する
     * @param inventory target inventory
     * @param firstIndex fisrt index
     * @param firstX first x
     * @param firstY first y
     * @param size a slot size (if this is -1, set 18 to this)
     * @param maxAmountX x line slot max amount
     * @param maxAmountY y line slot max amount
     * @return Slot list
     */
    protected List<Slot> addSlots(Inventory inventory, int firstIndex, int firstX, int firstY, int size, int maxAmountX, int maxAmountY) {
        if (size < 0) size = DEFAULT_SLOT_SIZE;
        List<Slot> slots = new ArrayList<>();
        for (int y = 0; y < maxAmountY; ++y) {
            List<Slot> xSlots = this.addSlotsX(inventory, firstIndex + (y * maxAmountX), firstX, firstY + (y * size), size, maxAmountX);
            slots.addAll(xSlots);
        }
        return slots;
    }

    /**
     * 一括で横にスロットを設置する
     * @param inventory target inventory
     * @param firstIndex first index
     * @param firstX first x
     * @param y y
     * @param size a slot size (if this is -1, set 18 to this)
     * @param amount slot amount
     * @return Slot list
     */
    protected List<Slot> addSlotsX(Inventory inventory, int firstIndex, int firstX, int y, int size, int amount) {
        if (size < 0) size = DEFAULT_SLOT_SIZE;
        List<Slot> slots = new ArrayList<>();
        for (int x = 0; x < amount; ++x) {
            Slot slot = this.addNormalSlot(inventory, firstIndex + x, firstX + (x * size), y);
            slots.add(slot);
        }
        return slots;
    }

    /**
     * 一括で縦にスロットを設置する
     * @param inventory target inventory
     * @param firstIndex first index
     * @param x x
     * @param firstY first y
     * @param size a slot size (if this is -1, set 18 to this)
     * @param amount slot amount
     * @return Slot list
     */
    protected List<Slot> addSlotsY(Inventory inventory, int firstIndex, int x, int firstY, int size, int amount) {
        if (size < 0) size = DEFAULT_SLOT_SIZE;
        List<Slot> slots = new ArrayList<>();
        for (int y = 0; y < amount; ++y) {
            Slot slot = this.addNormalSlot(inventory, firstIndex + x, x, firstY + (y * size));
            slots.add(slot);
        }
        return slots;
    }

    int transferTime = 0;

    @Override
    public ItemStack quickMoveOverride(PlayerEntity playerEntity, int index) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            if (index < 36) { // indexがRegisterサイズより小さい
                if (!this.insertItem(originalStack, 36, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, 36, false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (index >= 50) {
                if (transferTime >= 63 || EMCManager.getEmcFromPlayer(new Player(playerEntity)) < EMCManager.get(newStack)) {
                    transferTime = 0;
                    return ItemStack.EMPTY;
                }
                transferTime++;
                return newStack;
            }
        }
        return ItemStack.EMPTY;
    }

    public String searchText = "";

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public void sortBySearch() {
        if (searchText.isEmpty()) return;
        NbtTag nbtTag = NbtTag.create();
        extractInventory.player.getPlayerEntity().writeCustomDataToNbt(nbtTag);

        if (nbtTag.contains("itemalchemy")) {

            NbtCompound copy = nbtTag.copy();
            NbtCompound items = NbtTag.create();

            NbtCompound itemAlchemyTag = nbtTag.getCompound("itemalchemy");
            if (itemAlchemyTag.contains("registered_items")) {
                items = itemAlchemyTag.getCompound("registered_items");
            }

            List<String> ids = new ArrayList<>(items.getKeys());
            for (String id : ids) {
                if (!id.contains(searchText) && !new ItemStack(ItemUtil.fromId(new Identifier(id))).getName().getString().contains(searchText)) {
                    items.remove(id);
                }
            }

            itemAlchemyTag.put("registered_items", items);
            nbtTag.put("itemalchemy", itemAlchemyTag);

            extractInventory.player.getPlayerEntity().readCustomDataFromNbt(nbtTag);

            extractInventory.placeExtractSlots();

            extractInventory.player.getPlayerEntity().readCustomDataFromNbt(copy);
        }
    }
}
