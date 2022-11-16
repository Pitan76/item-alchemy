package ml.pkom.itemalchemy.items;

import ml.pkom.itemalchemy.EMCManager;
import ml.pkom.itemalchemy.gui.screens.AlchemyTableScreenHandler;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.event.item.ItemUseEvent;
import ml.pkom.mcpitanlibarch.api.item.ExtendItem;
import ml.pkom.mcpitanlibarch.api.util.TextUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.TypedActionResult;

public class AlchemyPad extends ExtendItem {

    public AlchemyPad(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> onRightClick(ItemUseEvent e) {
        if (e.world.isClient()) {
            return TypedActionResult.consume(e.user.getPlayerEntity().getStackInHand(e.hand));
        }
        if (e.user.getPlayerEntity() instanceof ServerPlayerEntity) {
            EMCManager.syncS2C((ServerPlayerEntity) e.user.getPlayerEntity());
        }
        Player player = e.user;
        player.openGuiScreen(new NamedScreenHandlerFactory() {
            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                return new AlchemyTableScreenHandler(syncId, inv);
            }

            @Override
            public Text getDisplayName() {
                return TextUtil.translatable("container.itemalchemy.alchemy_table");
            }
        });
        return TypedActionResult.consume(e.user.getPlayerEntity().getStackInHand(e.hand));
    }
}
