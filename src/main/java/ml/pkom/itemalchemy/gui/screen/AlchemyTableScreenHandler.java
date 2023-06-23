package ml.pkom.itemalchemy.gui.screen;

import ml.pkom.itemalchemy.EMCManager;
import ml.pkom.itemalchemy.api.PlayerRegisteredItemUtil;
import ml.pkom.itemalchemy.gui.inventory.*;
import ml.pkom.itemalchemy.gui.slot.ExtractSlot;
import ml.pkom.itemalchemy.gui.slot.RegisterSlot;
import ml.pkom.itemalchemy.gui.slot.RemoveSlot;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.gui.SimpleScreenHandler;
import ml.pkom.mcpitanlibarch.api.util.ItemUtil;
import ml.pkom.mcpitanlibarch.api.util.SlotUtil;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
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
        callAddSlot(new RemoveSlot(otherInventory, 51, 24, 120, player)); // Remove?
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

    public boolean canUse(Player player) {
        return this.canUse;
    }

    public Slot addNormalSlot(Inventory inventory, int index, int x, int y) {
        Slot slot = new Slot(inventory, index, x, y);
        return this.callAddSlot(slot);
    }

    protected Slot addExtractSlot(ExtractInventory inventory, int index, int x, int y) {
        Slot slot = new ExtractSlot(inventory, index, x, y);
        return this.callAddSlot(slot);
    }

    protected Slot addRegisterSlot(RegisterInventory inventory, int index, int x, int y) {
        Slot slot = new RegisterSlot(inventory, index, x, y);
        return this.callAddSlot(slot);
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

    @Override
    public ItemStack quickMoveOverride(Player player, int index) {
        ItemStack newStack;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack originalStack = SlotUtil.getStack(slot);
            newStack = originalStack.copy();

            if (index < 36) { // indexがRegisterサイズより小さい
                if (!this.callInsertItem(originalStack, 36, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.callInsertItem(originalStack, 0, 36, false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                SlotUtil.setStack(slot, ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
            return ItemStack.EMPTY;
        }
        return ItemStack.EMPTY;
    }

    public String searchText = "";

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public void sortBySearch() {
        if (searchText.isEmpty()) {
            extractInventory.placeExtractSlots();
            return;
        }
        NbtCompound nbtTag = EMCManager.writePlayerNbt(extractInventory.player).copy();

        if (nbtTag.contains("itemalchemy")) {

            NbtCompound items = new NbtCompound();

            NbtCompound itemAlchemyTag = nbtTag.getCompound("itemalchemy");
            if (itemAlchemyTag.contains("registered_items")) {
                items = itemAlchemyTag.getCompound("registered_items");
            }

            List<String> ids = new ArrayList<>(items.getKeys());
            for (String id : ids) {
                String translatedName = "";

                ItemStack itemStack = new ItemStack(ItemUtil.fromId(new Identifier(id)));
                String itemTranslationKey = itemStack.getTranslationKey();

                // If the item has a translation, we should use that instead of the identifier.
                if (I18n.hasTranslation(itemTranslationKey)) {
                    translatedName = I18n.translate(itemTranslationKey);
                }

                // Display the item if the items id, translated name or custom name contains
                // the search term. Checking both the id and the translated name
                // makes sure that people can search in both their native language
                // and in English.
                if (id.contains(searchText) || translatedName.contains(searchText) || itemStack.getName().asString().contains(searchText)) continue;

                items.remove(id);
            }

            itemAlchemyTag.put("registered_items", items);

            nbtTag.put("itemalchemy", itemAlchemyTag);

            extractInventory.placeExtractSlots(nbtTag);

        }
    }

    @Override
    public void overrideOnSlotClick(int slotIndex, int button, SlotActionType actionType, Player player) {
        super.overrideOnSlotClick(slotIndex, button, actionType, player);

        //System.out.println("index: " + slotIndex + ", action: " + actionType.name());

        if (slotIndex >= 50 && !player.getWorld().isClient && (actionType == SlotActionType.SWAP || actionType == SlotActionType.PICKUP || actionType == SlotActionType.QUICK_MOVE || actionType == SlotActionType.THROW)) {

            Slot slot = callGetSlot(slotIndex);
            if (!(slot instanceof ExtractSlot)) return;
            ExtractSlot extractSlot = (ExtractSlot) slot;
            ItemStack definedStack = extractSlot.inventory.definedStacks.get(slotIndex + 14);
            ItemStack stack = SlotUtil.getStack(extractSlot);

            int receivable = 1;
            if (actionType == SlotActionType.QUICK_MOVE) {
                receivable = (int) Math.min(Math.floorDiv(EMCManager.getEmcFromPlayer(player), EMCManager.get(definedStack)), definedStack.getMaxCount());
            }


            if (definedStack != null && stack.isEmpty() && EMCManager.getEmcFromPlayer(player) >= EMCManager.get(definedStack) * receivable) {
                EMCManager.decrementEmc(player, EMCManager.get(definedStack) * receivable);
                SlotUtil.setStack(extractSlot, definedStack.copy());

                if (receivable > 1) {
                    ItemStack addedStack = definedStack.copy();
                    addedStack.setCount(receivable - 1);
                    player.offerOrDrop(addedStack);
                }

                // sync emc
                if (player.getEntity() instanceof ServerPlayerEntity) {
                    ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player.getEntity();
                    EMCManager.syncS2C(serverPlayer);
                }

            }
        }
    }
}
