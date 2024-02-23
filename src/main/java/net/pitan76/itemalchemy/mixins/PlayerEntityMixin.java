package net.pitan76.itemalchemy.mixins;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.pitan76.itemalchemy.data.ServerState;
import net.pitan76.itemalchemy.data.TeamState;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.util.ItemUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.stream.Collectors;

@Deprecated
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        Player player = new Player((PlayerEntity) (Object)this);

        if(player.isClient()) {
            return;
        }

        if(!nbt.contains("itemalchemy")) {
            return;
        }

        NbtCompound modNBT = nbt.getCompound("itemalchemy");

        ServerState serverState = ServerState.getServerState(player.getWorld().getServer());

        //ServerConnectionより早く呼ばれるのでModをアップデートしたユーザーはここでStateに記録
        serverState.createPlayer(player);

        TeamState teamState = serverState.getTeamByPlayer(player.getUUID()).get();

        if(modNBT.contains("emc")) {
            teamState.storedEMC = modNBT.getLong("emc");
        }

        if(modNBT.contains("registered_items")) {
            NbtCompound registeredItems = modNBT.getCompound("registered_items");

            List<String> keys = registeredItems.getKeys().stream().filter(key -> ItemUtil.isExist(new Identifier(key))).collect(Collectors.toList());

            teamState.registeredItems.clear();
            teamState.registeredItems.addAll(keys);
        }

        serverState.markDirty();
    }
}
