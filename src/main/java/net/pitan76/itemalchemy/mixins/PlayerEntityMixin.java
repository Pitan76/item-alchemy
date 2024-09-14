package net.pitan76.itemalchemy.mixins;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.pitan76.itemalchemy.data.ServerState;
import net.pitan76.itemalchemy.data.TeamState;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.util.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Deprecated
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        Player player = new Player((PlayerEntity) (Object)this);

        if (player.isClient()) return;
        if (!NbtUtil.has(nbt, "itemalchemy")) return;
        
        NbtCompound modNBT = NbtUtil.get(nbt, "itemalchemy");

        Optional<MinecraftServer> serverOptional = WorldUtil.getServer(player.getWorld());
        if (!serverOptional.isPresent()) return;

        ServerState serverState = ServerState.getServerState(serverOptional.get());

        //ServerConnectionより早く呼ばれるのでModをアップデートしたユーザーはここでStateに記録
        serverState.createPlayer(player);

        TeamState teamState = serverState.getTeamByPlayer(player.getUUID()).get();

        if (NbtUtil.has(modNBT, "emc"))
            teamState.storedEMC = NbtUtil.get(modNBT, "emc", Long.class);

        if (NbtUtil.has(modNBT, "registered_items")) {
            NbtCompound registeredItems = NbtUtil.get(modNBT, "registered_items");

            List<String> keys = registeredItems.getKeys().stream().filter(key -> ItemUtil.isExist(IdentifierUtil.id(key))).collect(Collectors.toList());

            teamState.registeredItems.clear();
            teamState.registeredItems.addAll(keys);
        }

        PersistentStateUtil.markDirty(serverState);
    }
}
