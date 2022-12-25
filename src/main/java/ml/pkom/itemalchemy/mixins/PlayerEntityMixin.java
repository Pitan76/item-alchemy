package ml.pkom.itemalchemy.mixins;

import ml.pkom.itemalchemy.EMCManager;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.nbt.NbtTag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    public NbtCompound itemAlchemy = NbtTag.create();

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        Player player = new Player((PlayerEntity) (Object) this);

        NbtCompound cachedPlayerNbt = EMCManager.writePlayerNbt(player);
        if (cachedPlayerNbt.contains("itemalchemy")) {
            itemAlchemy = cachedPlayerNbt.getCompound("itemalchemy");
            nbt.put("itemalchemy", itemAlchemy);
        }

    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        Player player = new Player((PlayerEntity) (Object) this);

        itemAlchemy = nbt.getCompound("itemalchemy");
        if (EMCManager.playerCache.containsKey(player.getName())) {
            EMCManager.playerCache.replace(player.getName(), nbt);
        } else {
            EMCManager.playerCache.put(player.getName(), nbt);
        }
        if ((Object) this instanceof ServerPlayerEntity) {
            EMCManager.syncS2C((ServerPlayerEntity) (Object) this);
        }
    }
}
