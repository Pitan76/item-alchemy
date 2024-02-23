package net.pitan76.itemalchemy.network;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.pitan76.itemalchemy.gui.screen.AlchemyTableScreenHandler;
import net.pitan76.itemalchemy.sound.Sounds;
import net.pitan76.itemalchemy.util.ItemCharge;
import net.pitan76.itemalchemy.util.ItemUtils;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.network.PacketByteUtil;
import net.pitan76.mcpitanlib.api.network.ServerNetworking;

import static net.pitan76.itemalchemy.ItemAlchemy.id;

public class ServerNetworks {
    public static void init() {
        ServerNetworking.registerReceiver(id("network"), ((server, p, buf) -> {
            NbtCompound nbt = PacketByteUtil.readNbt(buf);
            if (nbt.contains("control")) {
                Player player = new Player(p);
                int ctrl = nbt.getInt("control");
                if (ctrl == 0) {
                    if (!(player.getCurrentScreenHandler() instanceof AlchemyTableScreenHandler)) return;
                    AlchemyTableScreenHandler screenHandler = (AlchemyTableScreenHandler) player.getCurrentScreenHandler();
                    screenHandler.prevExtractSlots();
                }
                if (ctrl == 1) {
                    if (!(player.getCurrentScreenHandler() instanceof AlchemyTableScreenHandler)) return;
                    AlchemyTableScreenHandler screenHandler = (AlchemyTableScreenHandler) player.getCurrentScreenHandler();
                    screenHandler.nextExtractSlots();
                }
            }
        }));

        ServerNetworking.registerReceiver(id("search"), ((server, p, buf) -> {
            String text = PacketByteUtil.readString(buf);
            NbtCompound translations = PacketByteUtil.readNbt(buf);
            Player player = new Player(p);
            AlchemyTableScreenHandler screenHandler = (AlchemyTableScreenHandler) player.getCurrentScreenHandler();

            // Sort
            screenHandler.setSearchText(text);
            screenHandler.setTranslations(translations);
            screenHandler.index = 0;
            screenHandler.sortBySearch();
        }));

        ServerNetworking.registerReceiver(id("tool_charge"), (server, p, buf) -> {
            Player player = new Player(p);
            ItemStack itemStack = ItemUtils.getCurrentHandItem(p);

            if(itemStack == null) {
                return;
            }

            if(itemStack.getItem() instanceof ItemCharge) {
                int chargeLevel = ItemUtils.getCharge(itemStack);

                int afterChargeLevel = player.isSneaking() ? chargeLevel - 1 : chargeLevel + 1;
                ItemUtils.setCharge(itemStack, afterChargeLevel);

                if (ItemUtils.getCharge(itemStack) == afterChargeLevel) {
                    player.getWorld().playSound(null, player.getBlockPos(), player.isSneaking() ? Sounds.UNCHARGE_SOUND.getOrNull() : Sounds.CHARGE_SOUND.getOrNull(), SoundCategory.PLAYERS, 0.15f, 0.4f + chargeLevel / 5f);
                }
            }
        });
    }
}
