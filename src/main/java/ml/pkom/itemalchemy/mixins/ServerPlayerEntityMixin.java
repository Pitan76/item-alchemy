package ml.pkom.itemalchemy.mixins;

import ml.pkom.mcpitanlibarch.api.nbt.NbtTag;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Shadow public abstract void writeCustomDataToNbt(NbtCompound nbt);

    @Shadow public abstract void readCustomDataFromNbt(NbtCompound nbt);

    // リスポーン、ワールド移動時のコピー対応
    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        NbtCompound oldNbt = NbtTag.create();
        NbtCompound newNbt = NbtTag.create();
        oldPlayer.writeCustomDataToNbt(oldNbt);
        NbtCompound itemAlchemy = oldNbt.getCompound("itemalchemy");
        this.writeCustomDataToNbt(newNbt);
        newNbt.put("itemalchemy", itemAlchemy);
        this.readCustomDataFromNbt(newNbt);
    }
}
