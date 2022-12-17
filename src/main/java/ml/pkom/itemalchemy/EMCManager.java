package ml.pkom.itemalchemy;

import ml.pkom.easyapi.config.Config;
import ml.pkom.easyapi.config.JsonConfig;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.nbt.NbtTag;
import ml.pkom.mcpitanlibarch.api.tag.TagKey;
import ml.pkom.mcpitanlibarch.api.util.ItemUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.io.File;
import java.util.*;

public class EMCManager {

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

    public static Config config = new JsonConfig();

    public static void init(MinecraftServer server, ServerWorld world) {
        System.out.println("init emc manager");
        if (!map.isEmpty()) map = new LinkedHashMap<>();

        File dir = new File(FabricLoader.getInstance().getConfigDir().toFile(), ItemAlchemy.MOD_ID);
        if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, "emc_config.json");

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
            for (Map.Entry<String, Long> entry : getMap().entrySet()) {
                config.set(entry.getKey(), entry.getValue());
            }
            config.save(file);
        }

        setEmcFromRecipes(world);

        if (!world.isClient()) {
            for (ServerPlayerEntity player : world.getPlayers()) {
                syncS2C_emc_map(player);
            }
        }
    }

    public static void defaultMap() {
        add(Items.COBBLESTONE, 1);
        add(Items.COARSE_DIRT, 2);
        add(Items.PODZOL, 2);
        add(Items.CRIMSON_NYLIUM, 2);
        add(Items.WARPED_NYLIUM, 2);
        add(Items.PODZOL, 2);
        add(Items.TUFF, 4);
        add(Items.CALCITE, 32);
        add(Items.ANDESITE, 16);
        add(Items.DIORITE, 16);
        add(Items.GRANITE, 16);
        add(Items.DRIPSTONE_BLOCK, 64);
        add(Items.COBBLED_DEEPSLATE, 2);
        add(Items.DEEPSLATE, 2);
        add(Items.COBBLESTONE_SLAB, 1);
        add(Items.DEAD_BUSH, 1);
        add(Items.FERN, 1);
        add(Items.TALL_GRASS, 1);
        add(Items.DIRT, 1);
        add(Items.GLASS, 1);
        add(Items.GRASS, 1);
        add(Items.GRASS_BLOCK, 1);
        add(Items.ICE, 1);
        add(Items.MYCELIUM, 2);
        add(Items.NETHERRACK, 1);
        add(Items.SNOW, 1);
        add(Items.STONE, 1);
        add(Items.STONE_BRICKS, 1);
        add(Items.STONE_BRICK_STAIRS, 1);
        add(Items.STONE_SLAB, 1);
        add(Items.STONE_STAIRS, 1);
        add(Items.BASALT, 1);
        add(Items.POLISHED_BASALT, 1);
        add(Items.SMOOTH_BASALT, 1);
        add(Items.END_STONE, 1);
        add(Items.MAGMA_BLOCK, 128);
        add2((TagKey<Item>) TagKey.create(TagKey.Type.ITEM, new Identifier("leaves")), 1);
        add2((TagKey<Item>) TagKey.create(TagKey.Type.ITEM, new Identifier("sand")), 1);

        add2((TagKey<Item>) TagKey.create(TagKey.Type.ITEM, new Identifier("buttons")), 2);
        add(Items.STONE_PRESSURE_PLATE, 2);
        add(Items.GRAVEL, 4);
        add(Items.NETHER_BRICK, 4);
        add(Items.SANDSTONE, 4);
        add(Items.SANDSTONE_SLAB, 4);
        add(Items.FLINT, 4);
        add(Items.STICK, 4);
        add(Items.ROOTED_DIRT, 5);
        add(Items.LEVER, 5);
        add(Items.CACTUS, 8);
        add2((TagKey<Item>) TagKey.create(TagKey.Type.ITEM, new Identifier("planks")), 8);
        add2((TagKey<Item>) TagKey.create(TagKey.Type.ITEM, new Identifier("wooden_slabs")), 8);
        add(Items.BLACK_DYE, 16);
        add(Items.WHITE_DYE, 16);
        add(Items.RED_DYE, 16);
        add(Items.BLUE_DYE, 16);
        add(Items.YELLOW_DYE, 16);
        add(Items.GREEN_DYE, 16);
        add(Items.BROWN_DYE, 16);
        add(Items.CYAN_DYE, 16);
        add(Items.MAGENTA_DYE, 16);
        add(Items.LIME_DYE, 16);
        add(Items.ORANGE_DYE, 16);
        add(Items.PINK_DYE, 16);
        add(Items.LIGHT_BLUE_DYE, 16);
        add(Items.LIGHT_GRAY_DYE, 16);
        add(Items.GRAY_DYE, 16);
        add(Items.PURPLE_DYE, 16);

        add(Items.BLACK_CONCRETE, 4);
        add(Items.WHITE_CONCRETE, 4);
        add(Items.RED_CONCRETE, 4);
        add(Items.BLUE_CONCRETE, 4);
        add(Items.YELLOW_CONCRETE, 4);
        add(Items.GREEN_CONCRETE, 4);
        add(Items.BROWN_CONCRETE, 4);
        add(Items.CYAN_CONCRETE, 4);
        add(Items.MAGENTA_CONCRETE, 4);
        add(Items.LIME_CONCRETE, 4);
        add(Items.ORANGE_CONCRETE, 4);
        add(Items.PINK_CONCRETE, 4);
        add(Items.LIGHT_BLUE_CONCRETE, 4);
        add(Items.LIGHT_GRAY_CONCRETE, 4);
        add(Items.GRAY_CONCRETE, 4);
        add(Items.PURPLE_CONCRETE, 4);
        add(Items.BLACK_CONCRETE_POWDER, 4);
        add(Items.WHITE_CONCRETE_POWDER, 4);
        add(Items.RED_CONCRETE_POWDER, 4);
        add(Items.BLUE_CONCRETE_POWDER, 4);
        add(Items.YELLOW_CONCRETE_POWDER, 4);
        add(Items.GREEN_CONCRETE_POWDER, 4);
        add(Items.BROWN_CONCRETE_POWDER, 4);
        add(Items.CYAN_CONCRETE_POWDER, 4);
        add(Items.MAGENTA_CONCRETE_POWDER, 4);
        add(Items.LIME_CONCRETE_POWDER, 4);
        add(Items.ORANGE_CONCRETE_POWDER, 4);
        add(Items.PINK_CONCRETE_POWDER, 4);
        add(Items.LIGHT_BLUE_CONCRETE_POWDER, 4);
        add(Items.LIGHT_GRAY_CONCRETE_POWDER, 4);
        add(Items.GRAY_CONCRETE_POWDER, 4);
        add(Items.PURPLE_CONCRETE_POWDER, 4);
        add(Items.TORCH, 9);

        add(Items.DEAD_BRAIN_CORAL_BLOCK, 4);
        add(Items.DEAD_BRAIN_CORAL, 1);
        add(Items.DEAD_BRAIN_CORAL_FAN, 1);
        add(Items.DEAD_BUBBLE_CORAL_BLOCK, 4);
        add(Items.DEAD_BUBBLE_CORAL, 1);
        add(Items.DEAD_BUBBLE_CORAL_FAN, 1);
        add(Items.DEAD_FIRE_CORAL_BLOCK, 4);
        add(Items.DEAD_FIRE_CORAL, 1);
        add(Items.DEAD_FIRE_CORAL_FAN, 1);
        add(Items.DEAD_HORN_CORAL_BLOCK, 4);
        add(Items.DEAD_HORN_CORAL, 1);
        add(Items.DEAD_HORN_CORAL_FAN, 1);
        add(Items.DEAD_TUBE_CORAL_BLOCK, 4);
        add(Items.DEAD_TUBE_CORAL, 1);
        add(Items.DEAD_TUBE_CORAL_FAN, 1);

        add(Items.BRAIN_CORAL_BLOCK, 64);
        add(Items.BRAIN_CORAL, 16);
        add(Items.BRAIN_CORAL_FAN, 16);
        add(Items.BUBBLE_CORAL_BLOCK, 64);
        add(Items.BUBBLE_CORAL, 16);
        add(Items.BUBBLE_CORAL_FAN, 16);
        add(Items.FIRE_CORAL_BLOCK, 64);
        add(Items.FIRE_CORAL, 16);
        add(Items.FIRE_CORAL_FAN, 16);
        add(Items.HORN_CORAL_BLOCK, 64);
        add(Items.HORN_CORAL, 16);
        add(Items.HORN_CORAL_FAN, 16);
        add(Items.TUBE_CORAL_BLOCK, 64);
        add(Items.TUBE_CORAL, 16);
        add(Items.TUBE_CORAL_FAN, 16);

        add(Items.COBWEB, 12);
        add2((TagKey<Item>) TagKey.create(TagKey.Type.ITEM, new Identifier("fences")), 32);
        add(Items.FISHING_ROD, 12);
        add(Items.STRING, 12);
        add2((TagKey<Item>) TagKey.create(TagKey.Type.ITEM, new Identifier("wooden_stairs")), 12);

        add(Items.LADDER, 14);
        add(Items.ARROW, 14);

        add2((TagKey<Item>) TagKey.create(TagKey.Type.ITEM, new Identifier("flowers")), 16);
        add(Items.ROSE_BUSH, 16);
        add(Items.LILY_PAD, 16);
        add2((TagKey<Item>) TagKey.create(TagKey.Type.ITEM, new Identifier("wooden_pressure_plates")), 16);
        add(Items.BEETROOT_SEEDS, 16);
        add(Items.MELON_SEEDS, 16);
        add(Items.PUMPKIN_SEEDS, 36);
        add(Items.WHEAT_SEEDS, 16);
        add(Items.WHEAT, 24);
        add2((TagKey<Item>) TagKey.create(TagKey.Type.ITEM, new Identifier("trapdoors")), 24);
        add(Items.NETHER_WART, 24);
        add(Items.ROTTEN_FLESH, 24);
        add(Items.SLIME_BALL, 32);

        add(Items.CHARCOAL, 32);
        add2((TagKey<Item>) TagKey.create(TagKey.Type.ITEM, new Identifier("saplings")), 32);
        add2((TagKey<Item>) TagKey.create(TagKey.Type.ITEM, new Identifier("logs")), 32);
        add(Items.PAPER, 32);
        add(Items.SUGAR, 15);
        add(Items.BONE, 144);
        add(Items.EGG, 32);

        add2((TagKey<Item>) TagKey.create(TagKey.Type.ITEM, new Identifier("wool")), 48);
        add(Items.BONE_MEAL, 48);
        add(Items.FEATHER, 48);

        add(Items.SOUL_SAND, 49);
        add(Items.OBSIDIAN, 64);
        add(Items.REDSTONE, 64);
        add(Items.PORKCHOP, 64);
        add(Items.BEEF, 64);
        add2((TagKey<Item>) TagKey.create(TagKey.Type.ITEM, new Identifier("fishes")), 64);
        add(Items.CHICKEN, 64);
        add(Items.LEATHER, 64);
        add(Items.CLAY, 64);
        add(Items.BREAD, 72);
        add(Items.PAINTING, 80);

        add(Items.GUNPOWDER, 192);
        add(Items.FERMENTED_SPIDER_EYE, 192);

        add(Items.GLOWSTONE_DUST, 384);

        add(Items.ENDER_PEARL, 1024);

        add(Items.COPPER_ORE, 128);
        add(Items.COPPER_BLOCK, 1152);
        add(Items.WEATHERED_COPPER, 1152);
        add(Items.EXPOSED_COPPER, 1152);
        add(Items.OXIDIZED_COPPER, 1152);
        add(Items.COAL_ORE, 128);
        add(Items.IRON_ORE, 256);
        add(Items.AMETHYST_SHARD, 32);
        add(Items.LAPIS_ORE, 864);
        add(Items.EMERALD_ORE, 1024);
        add(Items.GOLD_ORE, 2048);
        add(Items.DIAMOND_ORE, 8192);
        add(Items.NETHERITE_INGOT, 57344);

        add(Items.DEEPSLATE_COPPER_ORE, 85);
        add(Items.DEEPSLATE_COAL_ORE, 128);
        add(Items.DEEPSLATE_IRON_ORE, 256);
        add(Items.DEEPSLATE_LAPIS_ORE, 864);
        add(Items.DEEPSLATE_EMERALD_ORE, 1024);
        add(Items.DEEPSLATE_GOLD_ORE, 2048);
        add(Items.DEEPSLATE_DIAMOND_ORE, 8192);

        add(Items.RAW_COPPER, 128);
        add(Items.RAW_IRON, 256);
        add(Items.RAW_GOLD, 2048);

        add(Items.NETHER_GOLD_ORE, 2048);
        add(Items.NETHER_QUARTZ_ORE, 128);
        add(Items.QUARTZ, 256);

        add(Items.REDSTONE_ORE, 256);
        add(Items.DEEPSLATE_REDSTONE_ORE, 256);


        add(Items.NETHERITE_SCRAP, 12288);
        add(Items.ANCIENT_DEBRIS, 12288);
        add(Items.NETHER_STAR, 139264);
        add(Items.TURTLE_EGG, 192);
        add(Items.CONDUIT, 40960);
        add(Items.SCUTE, 96);

        add(Items.WATER_BUCKET, 768);
        add(Items.LAVA_BUCKET, 832);
        add(Items.POWDER_SNOW_BUCKET, 784);
        add(Items.SNOWBALL, 1);
        add(Items.MILK_BUCKET, 784);
        add(Items.PUFFERFISH_BUCKET, 832);
        add(Items.SALMON_BUCKET, 832);
        add(Items.COD_BUCKET, 832);
        add(Items.TROPICAL_FISH_BUCKET, 832);
        add(Items.AXOLOTL_BUCKET, 832);
        add(Items.BRICK, 16);
        add(Items.CLAY_BALL, 16);
        add(Items.INK_SAC, 16);
        add(Items.GLOW_INK_SAC, 400);
        add(Items.COCOA_BEANS, 64);
        add(Items.INK_SAC, 16);

        add(Items.END_ROD, 432);
        add(Items.CHORUS_PLANT, 64);
        add(Items.CHORUS_FLOWER, 96);
        add(Items.CHORUS_FRUIT, 192);
        add(Items.POPPED_CHORUS_FRUIT, 192);
        add(Items.SHULKER_SHELL, 2048);
        add(Items.IRON_NUGGET, 28);
        add(Items.NAUTILUS_SHELL, 1024);
        add(Items.HEART_OF_THE_SEA, 32768);
        add(Items.HONEYCOMB, 16);
        add(Items.PRISMARINE_SHARD, 256);
        add(Items.PRISMARINE_CRYSTALS, 512);
        add(Items.RABBIT_HIDE, 16);
        add(Items.IRON_HORSE_ARMOR, 2048);
        add(Items.GOLDEN_HORSE_ARMOR, 2048);
        add(Items.DIAMOND_HORSE_ARMOR, 2048);
        add(Items.WRITABLE_BOOK, 224);
        add(Items.MAP, 1344);
        add(Items.GOLD_NUGGET, 227);
        add(Items.ENDER_EYE, 1792);
        add(Items.BLAZE_ROD, 1536);
        add2((TagKey<Item>) TagKey.create(TagKey.Type.ITEM, new Identifier("music_discs")), 2048);
        add(Items.CRYING_OBSIDIAN, 768);
        add(Items.GHAST_TEAR, 4096);
        add(Items.PHANTOM_MEMBRANE, 192);
        add(Items.SADDLE, 192);
        add(Items.NAME_TAG, 192);
        add(Items.FISHING_ROD, 36);
        add(Items.ENCHANTED_BOOK, 160);
        add(Items.BROWN_MUSHROOM, 32);
        add(Items.RED_MUSHROOM, 32);
        add(Items.CRIMSON_FUNGUS, 32);
        add(Items.WARPED_FUNGUS, 32);
        add(Items.SUGAR_CANE, 32);
        add(Items.KELP, 1);
        add(Items.BAMBOO, 32);
        add(Items.MOSS_CARPET, 8);
        add(Items.MOSS_BLOCK, 12);
        add(Items.SEA_PICKLE, 16);
        add(Items.HANGING_ROOTS, 4);
        add(Items.VINE, 8);
        add(Items.GLOW_LICHEN, 8);
        add(Items.SHULKER_BOX, 4097);
        add(Items.CREEPER_HEAD, 256);
        add(Items.ZOMBIE_HEAD, 256);
        add(Items.BLACK_SHULKER_BOX, 4176);
        add(Items.WHITE_SHULKER_BOX, 4176);
        add(Items.RED_SHULKER_BOX, 4176);
        add(Items.BLUE_SHULKER_BOX, 4176);
        add(Items.YELLOW_SHULKER_BOX, 4176);
        add(Items.GREEN_SHULKER_BOX, 4176);
        add(Items.BROWN_SHULKER_BOX, 4176);
        add(Items.CYAN_SHULKER_BOX, 4176);
        add(Items.MAGENTA_SHULKER_BOX, 4176);
        add(Items.LIME_SHULKER_BOX, 4176);
        add(Items.ORANGE_SHULKER_BOX, 4176);
        add(Items.PINK_SHULKER_BOX, 4176);
        add(Items.LIGHT_BLUE_SHULKER_BOX, 4176);
        add(Items.LIGHT_GRAY_SHULKER_BOX, 4176);
        add(Items.GRAY_SHULKER_BOX, 4176);
        add(Items.PURPLE_SHULKER_BOX, 4176);
        add(Items.POTION, 1);
        add(Items.POINTED_DRIPSTONE, 16);
        add(Items.APPLE, 128);
        add(Items.POTATO, 64);
        add(Items.BAKED_POTATO, 64);
        add(Items.SPIDER_EYE, 128);
        add(Items.CARROT, 64);
        add(Items.SWEET_BERRIES, 16);
        add(Items.ENCHANTED_GOLDEN_APPLE, 147584);
        add(Items.MELON_SLICE, 16);
        add(Items.BEETROOT, 64);
        add(Items.RABBIT, 64);
        add(Items.GLOW_BERRIES, 16);
        add(Items.HONEY_BOTTLE, 48);
        add(Items.RABBIT_FOOT, 128);
        add(Items.TRIDENT, 16463);

        add(ml.pkom.itemalchemy.Items.ALCHEMICAL_FUEL.getOrNull(), 512);
        add(ml.pkom.itemalchemy.Items.MOBIUS_FUEL.getOrNull(), 2048);
        add(ml.pkom.itemalchemy.Items.AETERNALIS_FUEL.getOrNull(), 8192);
        add(ml.pkom.itemalchemy.Items.EMC_COLLECTOR_MK3.getOrNull(), 114697);

    }

    public static void setEmcFromRecipes(World world) {
        List<Recipe<?>> unsetRecipes = new ArrayList<>();
        Collection<Recipe<?>> recipes = world.getRecipeManager().values();

        for (Recipe<?> recipe : recipes) {
            ItemStack outStack = recipe.getOutput();
            addEmcFromRecipe(outStack, recipe, unsetRecipes, false);
        }
        List<Recipe<?>> dummy = new ArrayList<>();
        for (Recipe<?> recipe : unsetRecipes) {
            ItemStack outStack = recipe.getOutput();
            addEmcFromRecipe(outStack, recipe, dummy, true);
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

    public static void decrementEmc(Player player, long amount) {
        NbtCompound playerNbt = NbtTag.create();
        player.getPlayerEntity().writeCustomDataToNbt(playerNbt);
        long emc = 0;
        if (playerNbt.contains("itemalchemy")) {
            NbtCompound itemAlchemyTag = playerNbt.getCompound("itemalchemy");
            if (itemAlchemyTag.contains("emc")) {
                emc += itemAlchemyTag.getLong("emc");
            }
        }
        emc -= amount;

        if (playerNbt.contains("itemalchemy")) {
            NbtCompound itemAlchemyTag = playerNbt.getCompound("itemalchemy");
            itemAlchemyTag.putLong("emc", emc);
        } else {
            NbtCompound itemAlchemyTag = NbtTag.create();
            itemAlchemyTag.putLong("emc", emc);
            playerNbt.put("itemalchemy", itemAlchemyTag);
        }
        player.getPlayerEntity().readCustomDataFromNbt(playerNbt);
    }

    public static void setEMCtoPlayer(Player player, long emc) {
        NbtCompound playerNbt = NbtTag.create();
        player.getPlayerEntity().writeCustomDataToNbt(playerNbt);

        if (playerNbt.contains("itemalchemy")) {
            NbtCompound itemAlchemyTag = playerNbt.getCompound("itemalchemy");
            itemAlchemyTag.putLong("emc", emc);
        } else {
            NbtCompound itemAlchemyTag = NbtTag.create();
            itemAlchemyTag.putLong("emc", emc);
            playerNbt.put("itemalchemy", itemAlchemyTag);
        }
        player.getPlayerEntity().readCustomDataFromNbt(playerNbt);
    }

    public static void incrementEmc(Player player, long amount) {
        NbtCompound playerNbt = NbtTag.create();
        player.getPlayerEntity().writeCustomDataToNbt(playerNbt);
        long emc = 0;
        if (playerNbt.contains("itemalchemy")) {
            NbtCompound itemAlchemyTag = playerNbt.getCompound("itemalchemy");
            if (itemAlchemyTag.contains("emc")) {
                emc += itemAlchemyTag.getLong("emc");
            }
        }
        emc += amount;

        if (playerNbt.contains("itemalchemy")) {
            NbtCompound itemAlchemyTag = playerNbt.getCompound("itemalchemy");
            itemAlchemyTag.putLong("emc", emc);
        } else {
            NbtCompound itemAlchemyTag = NbtTag.create();
            itemAlchemyTag.putLong("emc", emc);
            playerNbt.put("itemalchemy", itemAlchemyTag);
        }
        player.getPlayerEntity().readCustomDataFromNbt(playerNbt);
    }

    public static long getEmcFromPlayer(Player player) {
        NbtTag playerNbt = new NbtTag();
        player.getPlayerEntity().writeCustomDataToNbt(playerNbt);
        long emc = 0;
        if (playerNbt.contains("itemalchemy")) {
            NbtCompound itemAlchemyTag = playerNbt.getCompound("itemalchemy");
            if (itemAlchemyTag.contains("emc")) {
                emc += itemAlchemyTag.getLong("emc");
            }
        }
        return emc;
    }

    public static void syncS2C(ServerPlayerEntity player) {
        if (player.networkHandler == null) {
            return;
        }
        PacketByteBuf buf = PacketByteBufs.create();
        NbtTag playerNbt = NbtTag.create();
        player.writeCustomDataToNbt(playerNbt);

        buf.writeNbt(playerNbt.getCompound("itemalchemy"));
        ServerPlayNetworking.send(player, ItemAlchemy.id("sync_emc"), buf);
    }

    public static void syncS2C_emc_map(ServerPlayerEntity player) {
        if (player.networkHandler == null) {
            return;
        }
        if (map.isEmpty()) return;
        PacketByteBuf buf = PacketByteBufs.create();
        NbtTag data = NbtTag.create();

        for (Map.Entry<String, Long> entry : map.entrySet()) {
            if (entry.getValue() == 0) continue;
            if (ItemUtil.isExist(new Identifier(entry.getKey()))) {
                data.putLong(entry.getKey(), entry.getValue());
            }
        }

        buf.writeNbt(data);
        ServerPlayNetworking.send(player, ItemAlchemy.id("sync_emc_map"), buf);
    }
}
