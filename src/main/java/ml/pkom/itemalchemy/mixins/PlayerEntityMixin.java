package ml.pkom.itemalchemy.mixins;

import ml.pkom.itemalchemy.EMCManager;
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
        nbt.put("itemalchemy", itemAlchemy);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        itemAlchemy = nbt.getCompound("itemalchemy");
        if ((Object) this instanceof ServerPlayerEntity) {
            EMCManager.syncS2C((ServerPlayerEntity) (Object) this);
        }
    }
}
