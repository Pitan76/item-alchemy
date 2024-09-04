package net.pitan76.itemalchemy.gui.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.api.PlayerRegisteredItemUtil;
import net.pitan76.itemalchemy.data.ModState;
import net.pitan76.itemalchemy.gui.inventory.ExtractInventory;
import net.pitan76.itemalchemy.gui.inventory.RegisterInventory;
import net.pitan76.itemalchemy.gui.slot.ExtractSlot;
import net.pitan76.itemalchemy.gui.slot.RegisterSlot;
import net.pitan76.itemalchemy.gui.slot.RemoveSlot;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.gui.SimpleScreenHandler;
import net.pitan76.mcpitanlib.api.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AlchemyTableScreenHandler extends SimpleScreenHandler {
    public Player player;

    public RegisterInventory registerInventory; // contains InputSlot(0)
    public ExtractInventory extractInventory;
    public Inventory otherInventory = InventoryUtil.createSimpleInventory(52);

    public AlchemyTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new RegisterInventory(64, new Player(playerInventory.player)));
    }

    public int index = 0;

    public void nextExtractSlots() {
        if (PlayerRegisteredItemUtil.count(player) < 13 * (index + 1) + 1) return;
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
        player = new Player(playerInventory.player);
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

    @Override
    public ItemStack quickMoveOverride(Player player, int index) {
        ItemStack newStack;
        Slot slot = ScreenHandlerUtil.getSlot(this, index);
        if (SlotUtil.hasStack(slot)) {
            ItemStack originalStack = SlotUtil.getStack(slot);
            newStack = originalStack.copy();

            if (index < 36) { // indexがRegisterサイズより小さい
                if (!this.callInsertItem(originalStack, 36, 37, true)) {
                    return ItemStackUtil.empty();
                }
            } else if (!this.callInsertItem(originalStack, 0, 36, false)) {
                return ItemStackUtil.empty();
            }

            if (originalStack.isEmpty()) {
                SlotUtil.setStack(slot, ItemStackUtil.empty());
            } else {
                SlotUtil.markDirty(slot);
            }
            return ItemStackUtil.empty();
        }
        return ItemStackUtil.empty();
    }

    public String searchText, searchNamespace = "";

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public NbtCompound translations = new NbtCompound();
    public void setTranslations(NbtCompound translations) {
        this.translations = translations;
    }

    public void sortBySearch() {
        if (searchText.isEmpty()) {
            extractInventory.placeExtractSlots();
            return;
        }

        List<String> ids = new ArrayList<>(ModState.getModState(player.getWorld().getServer()).getTeamByPlayer(player.getUUID()).get().registeredItems);
        List<String> sortedIds = new ArrayList<>();

        // Extract namespace from searchText [@(NAMESPACE)]
        Pattern pattern = Pattern.compile("@([a-zA-Z0-9_-]+)");
        Matcher matcher = pattern.matcher(searchText);
        if (matcher.find()) {
            searchNamespace = matcher.group(1);
            searchText = searchText.replaceFirst("@" + searchNamespace + " ?", "");
        }

        for (String id : ids) {
            String translatedName = "";

            Identifier itemIdentifier = IdentifierUtil.id(id);
            ItemStack itemStack = ItemStackUtil.create(ItemUtil.fromId(itemIdentifier));
            String itemTranslationKey = itemStack.getTranslationKey();

            // If the item has a translation, we should use that instead of the identifier.
            if (translations.contains(itemTranslationKey)) {
                translatedName = translations.getString(itemTranslationKey);
            }

            // Include only the name of the item in the id when searching
            String itemId = itemIdentifier.getPath();

            // Make sure everything is lower-case so capitalization doesn't matter for searching
            searchText = searchText.toLowerCase();
            translatedName = translatedName.toLowerCase();
            id = id.toLowerCase();

            String itemNamespace = itemIdentifier.getNamespace();

            // Display the item if the items id, translated name or custom name contains
            // the search term. Checking both the id and the translated name
            // makes sure that people can search in both their native language
            // and in English.
            if (
                    (searchNamespace.isEmpty() || itemNamespace.contains(searchNamespace)) &&
                            (itemId.contains(searchText) ||
                                    translatedName.contains(searchText) ||
                                    TextUtil.txt2str(itemStack.getName()).contains(searchText))
            ) {
                sortedIds.add(id);
            }
        }

        extractInventory.placeExtractSlots(sortedIds);
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
