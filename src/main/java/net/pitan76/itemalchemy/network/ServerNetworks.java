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
import net.pitan76.mcpitanlib.api.network.v2.ServerNetworking;
import net.pitan76.mcpitanlib.api.util.WorldUtil;

import java.util.Optional;

import static net.pitan76.itemalchemy.ItemAlchemy._id;

public class ServerNetworks {
    public static void init() {
        ServerNetworking.registerReceiver(_id("network"), (e) -> {
            NbtCompound nbt = PacketByteUtil.readNbt(e.buf);
            if (nbt.contains("control")) {
                Player player = e.player;
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
        });

        ServerNetworking.registerReceiver(_id("search"), (e) -> {
            String text = PacketByteUtil.readString(e.buf);
            NbtCompound translations = PacketByteUtil.readNbt(e.buf);
            Player player = e.player;
            AlchemyTableScreenHandler screenHandler = (AlchemyTableScreenHandler) player.getCurrentScreenHandler();

            // Sort
            screenHandler.setSearchText(text);
            screenHandler.setTranslations(translations);
            screenHandler.index = 0;
            screenHandler.sortBySearch();
        });

        ServerNetworking.registerReceiver(_id("tool_charge"), (e) -> {
            Player player = e.player;
            Optional<ItemStack> stackOptional = ItemUtils.getCurrentHandItem(player);

            if (!stackOptional.isPresent()) return;

            ItemStack stack = stackOptional.get();
            if (stack.getItem() instanceof ItemCharge) {
                int chargeLevel = ItemUtils.getCharge(stack);

                int afterChargeLevel = player.isSneaking() ? chargeLevel - 1 : chargeLevel + 1;
                ItemUtils.setCharge(stack, afterChargeLevel);

                if (ItemUtils.getCharge(stack) == afterChargeLevel) {
                    WorldUtil.playSound(player.getWorld(), null, player.getBlockPos(), player.isSneaking() ? Sounds.UNCHARGE_SOUND.getOrNull() : Sounds.CHARGE_SOUND.getOrNull(), SoundCategory.PLAYERS, 0.15f, 0.4f + afterChargeLevel / 5f);
                }
            }
        });
    }
}
