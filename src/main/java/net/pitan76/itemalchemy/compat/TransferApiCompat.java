package net.pitan76.itemalchemy.compat;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.pitan76.itemalchemy.tile.Tiles;

public class TransferApiCompat {
    private static boolean registered = false;

    /**
     * The EMC Exporter's stacks are virtual and only removeStack charges EMC. Fabric's fallback storage for plain
     * inventories extracts through getStack/setStack, so pipes (Tom's Simple Storage, Tesseract...) took items for
     * free. Expose no storage instead; hoppers still extract through removeStack and pay.
     */
    public static void register() {
        if (registered) return;
        registered = true;
        ItemStorage.SIDED.registerForBlockEntity((tile, side) -> Storage.empty(), Tiles.EMC_EXPORTER.get());
    }
}
