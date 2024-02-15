package net.pitan76.itemalchemy;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.pitan76.easyapi.config.Config;
import net.pitan76.easyapi.config.JsonConfig;
import net.pitan76.itemalchemy.data.ModState;
import net.pitan76.itemalchemy.data.PlayerState;
import net.pitan76.itemalchemy.data.ServerState;
import net.pitan76.itemalchemy.data.TeamState;
import net.pitan76.itemalchemy.emcs.EMCDef;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.network.PacketByteUtil;
import net.pitan76.mcpitanlib.api.network.ServerNetworking;
import net.pitan76.mcpitanlib.api.tag.TagKey;
import net.pitan76.mcpitanlib.api.util.RecipeUtil;
import net.pitan76.mcpitanlib.api.util.ResourceUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.pitan76.mcpitanlib.api.util.ItemUtil;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class EMCManager {

    private static final List<EMCDef> defs = new ArrayList<>();

    private static Map<String, Long> map = new LinkedHashMap<>();

    public static String itemToId(Item item) {
        return ItemUtil.toID(item).toString();
    }

    public static void add(Item item, long emc) {
        add(itemToId(item), emc);
    }

    public static void add(String item, long emc) {
        if (contains(item)) return;
        map.put(item, emc);
    }

    public static void set(Item item, long emc) {
        set(itemToId(item), emc);
    }

    public static void set(String item, long emc) {
        if (contains(item))
            map.replace(item, emc);
        else
            map.put(item, emc);
    }

    public static void remove(Item item) {
        map.remove(itemToId(item));
    }

    public static long get(Item item) {
        if (!contains(item)) return 0;
        return map.get(itemToId(item));
    }

    public static long get(ItemStack stack) {
        if (!contains(stack.getItem())) return 0;
        return get(stack.getItem()) * stack.getCount();
    }

    public static Map<String, Long> getMap() {
        return map;
    }

    public static void setMap(Map<String, Long> map) {
        EMCManager.map = map;
    }

    public static boolean contains(Item item) {
        return contains(itemToId(item));
    }
    public static boolean contains(String item) {
        return map.containsKey(item);
    }

    public static void add2(TagKey<Item> tagKey, long emc) {
        for (Item item : ItemUtil.getAllItems()) {
            if (ItemUtil.isIn(item, tagKey)) {
                add(item, emc);
            }
        }
    }

    public static File getConfigFile() {
        File dir = new File(FabricLoader.getInstance().getConfigDir().toFile(), ItemAlchemy.MOD_ID);
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, "emc_config.json");
    }

    public static Config config;

    public static void init(MinecraftServer server) {
        System.out.println("init emc manager");
        if (!map.isEmpty()) map = new LinkedHashMap<>();
        config = new JsonConfig();

        File file = getConfigFile();

        if (file.exists() && config.load(file)) {
            for (Map.Entry<String, Object> entry : config.configMap.entrySet()) {
                if (entry.getValue() instanceof Long) {
                    add(entry.getKey(), (Long) entry.getValue());
                }
                if (entry.getValue() instanceof Integer) {
                    add(entry.getKey(), Long.valueOf((Integer) entry.getValue()));
                }
                if (entry.getValue() instanceof Double) {
                    add(entry.getKey(), (Math.round((Double) entry.getValue())));
                }
                if (entry.getValue() instanceof String) {
                    add(entry.getKey(), Long.parseLong((String) entry.getValue()));
                }
            }
        } else {
            defaultMap();
        }

        ServerWorld world = server.getOverworld();
        setEmcFromRecipes(world);
        for (Map.Entry<String, Long> entry : getMap().entrySet()) {
            config.set(entry.getKey(), entry.getValue());
        }
        config.save(file);

        if (!world.isClient()) {
            for (ServerPlayerEntity player : world.getPlayers()) {
                syncS2C_emc_map(player);
            }
        }
    }

    public static void exit(MinecraftServer server) {
        playerCache = new HashMap<>();
    }

    public static void defaultMap() {
        if (!defaultEMCMap.isEmpty()) {
            map.putAll(defaultEMCMap);
            return;
        }

        defs.forEach(EMCDef::addAll);
    }

    public static void setEmcFromRecipes(World world) {
        List<Recipe<?>> unsetRecipes = new ArrayList<>();
        List<Recipe<?>> recipes = RecipeUtil.getAllRecipes(world);

        for (Recipe<?> recipe : recipes) {
            try {
                ItemStack outStack = RecipeUtil.getOutput((Recipe<Inventory>) recipe, world);
                addEmcFromRecipe(outStack, recipe, unsetRecipes, false);
            } catch (NoClassDefFoundError | Exception ignore) {}
        }
        List<Recipe<?>> dummy = new ArrayList<>();
        for (Recipe<?> recipe : unsetRecipes) {
            try {
                ItemStack outStack = RecipeUtil.getOutput((Recipe<Inventory>) recipe, world);;
                addEmcFromRecipe(outStack, recipe, dummy, true);
            } catch (NoClassDefFoundError | Exception ignore) {}
        }
    }

    public static boolean addEmcFromRecipe(ItemStack outStack, Recipe<?> recipe, List<Recipe<?>> unsetRecipes, boolean last) {
        if (outStack == null) return false;
        if (!contains(outStack.getItem())) {
            long totalEmc = 0;
            for (Ingredient ingredient : recipe.getIngredients()) {
                if (ingredient.getMatchingStacks().length > 0) {
                    ItemStack stack = ingredient.getMatchingStacks()[0];
                    if (contains(stack.getItem())) {
                        if (outStack.getCount() == 0) {
                            totalEmc += get(stack.getItem());
                        } else {
                            totalEmc += get(stack.getItem()) / outStack.getCount();
                        }
                    } else if (!unsetRecipes.contains(recipe)) {
                        if (!last) {
                            unsetRecipes.add(recipe);
                            return false;
                        }
                        //totalEmc += 1 / outStack.getCount();
                    }
                }
            }
            if (totalEmc <= 0) return false;
            add(outStack.getItem(), totalEmc);
        }
        return true;
    }

    public static void writeEmcToPlayer(Player player, ItemStack stack) {
        incrementEmc(player, EMCManager.get(stack));
    }

    public static Map<String, NbtCompound> playerCache = new HashMap<>();

    public static void decrementEmc(Player player, long amount) {
        ServerState state = ServerState.getServerState(player.getWorld().getServer());

        if(!state.getPlayer(player.getUUID()).isPresent()) {
            return;
        }

        PlayerState playerState = state.getPlayer(player.getUUID()).get();
        TeamState teamState = state.getTeam(playerState.teamID).get();

        teamState.storedEMC -= amount;

        state.markDirty();
    }

    public static void setEMCtoPlayer(Player player, long emc) {
        ServerState state = ServerState.getServerState(player.getWorld().getServer());

        if(!state.getPlayer(player.getUUID()).isPresent()) {
            return;
        }

        PlayerState playerState = state.getPlayer(player.getUUID()).get();
        TeamState teamState = state.getTeam(playerState.teamID).get();

        teamState.storedEMC = emc;

        state.markDirty();
    }

    public static void incrementEmc(Player player, long amount) {
        ServerState state = ServerState.getServerState(player.getWorld().getServer());

        if(!state.getPlayer(player.getUUID()).isPresent()) {
            return;
        }

        PlayerState playerState = state.getPlayer(player.getUUID()).get();
        TeamState teamState = state.getTeam(playerState.teamID).get();

        teamState.storedEMC += amount;

        state.markDirty();
    }

    public static long getEmcFromPlayer(Player player) {
        Optional<TeamState> teamState = ModState.getModState(player.getWorld().getServer()).getTeamByPlayer(player.getUUID());

        return teamState.map(state -> state.storedEMC).orElse(0L);
    }

    public static void syncS2C(ServerPlayerEntity serverPlayer) {
        if (serverPlayer.networkHandler == null) {
            return;
        }

        Player player = new Player(serverPlayer);

        ServerState serverState = ServerState.getServerState(player.getWorld().getServer());
        PacketByteBuf buf = PacketByteUtil.create();

        TeamState teamState = serverState.getTeamByPlayer(player.getUUID()).get();

        NbtCompound nbt = new NbtCompound();
        NbtCompound teamNBT = new NbtCompound();

        teamState.writeNbt(teamNBT);

        nbt.put("team", teamNBT);

        PacketByteUtil.writeNbt(buf, nbt);

        ServerNetworking.send(serverPlayer, ItemAlchemy.id("sync_emc"), buf);
    }

    public static void syncS2C_emc_map(ServerPlayerEntity player) {
        if (player.networkHandler == null) {
            return;
        }
        if (map.isEmpty()) return;
        PacketByteBuf buf = PacketByteUtil.create();

        PacketByteUtil.writeMap(buf, map);
        //System.out.println("send emc map to " + player.getName().getString());
        ServerNetworking.send(player, ItemAlchemy.id("sync_emc_map"), buf);
    }

    public static Map<String, Long> defaultEMCMap = new LinkedHashMap<>();

    public static void loadDefaultEMCs(ResourceManager resourceManager) {
        Map<Identifier, Resource> resourceIds;
        try {
            resourceIds = ResourceUtil.findResources(resourceManager, "default_emcs", ".json");
        } catch (IOException e) {
            ItemAlchemy.LOGGER.error("Failed to read default emc", e);
            return;
        }

        Gson gson = new Gson();
        Type listType = new TypeToken<HashMap<String, Long>>(){}.getType();

        resourceIds.forEach((resourceId, resource) -> {
            try {
                String json = IOUtils.toString(ResourceUtil.getInputStream(resource), StandardCharsets.UTF_8);
                ResourceUtil.close(resource);
                HashMap<String, Long> map = gson.fromJson(json, listType);

                if (resourceId.toString().endsWith("/tags.json")) {
                    HashMap<String, Long> tempMap = gson.fromJson(json, listType);
                    for (Map.Entry<String, Long> entry : map.entrySet()) {
                        TagKey<Item> tagKey = (TagKey<Item>) TagKey.create(TagKey.Type.ITEM, new Identifier(entry.getKey()));
                        for (Item item : ItemUtil.getAllItems()) {
                            if (ItemUtil.isIn(item, tagKey)) {
                                if (tempMap.containsKey(ItemUtil.toID(item).toString())) continue;
                                tempMap.put(ItemUtil.toID(item).toString(), entry.getValue());
                            }
                        }
                    }
                    map = tempMap;
                }

                for (Map.Entry<String, Long> entry : map.entrySet()) {
                    if (ItemUtil.isExist(new Identifier(entry.getKey()))) {
                        defaultEMCMap.put(entry.getKey(), entry.getValue());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ItemAlchemy.LOGGER.error("Failed to read {}", resourceId.toString(), e);
            }
        });
    }

    public static List<EMCDef> getDefs() {
        return defs;
    }

    public static void addDef(EMCDef def) {
        defs.add(def);
    }
}
