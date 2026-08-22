package net.pitan76.itemalchemy.config;

import net.pitan76.easyapi.config.JsonConfig;
import net.pitan76.mcpitanlib.api.util.PlatformUtil;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class ItemAlchemyConfig {
    private static final File dir = new File(PlatformUtil.getConfigFolderAsFile(), "itemalchemy");
    private static final File file = new File(dir, "config.json");
    private static final JsonConfig config = new JsonConfig(file);

    public static boolean isChanged;

    private static boolean removeDataFromCopyStack = true;
    private static boolean showEmcInTooltip = true; // requires restart game to apply (ReloadCommand is not working)

    // Tome of Knowledge でのアイテムを一括解禁の有無
    private static boolean tomeOfKnowledgeEnabled = true;
    // Tome of Knowledge で解禁ブラックリスト、「,」カンマ区切りのID。"modid:*" で名前空間ごと指定可能
    private static String tomeOfKnowledgeBlacklist = "";
    private static Set<String> tomeOfKnowledgeBlacklistIds = Collections.emptySet();
    private static Set<String> tomeOfKnowledgeBlacklistNamespaces = Collections.emptySet();

    public static boolean initialized = false;

    public static void initOnce() {
        if (!initialized) {
            init();
            initialized = true;
        }
    }

    public static void init() {
        if (config.configMap == null) config.configMap = new LinkedHashMap<>();

        removeDataFromCopyStack = config.getBooleanOrCreate("remove_data_from_copy_stack", true);
        showEmcInTooltip = config.getBooleanOrCreate("show_emc_in_tooltip", true);
        tomeOfKnowledgeEnabled = config.getBooleanOrCreate("tome_of_knowledge_enabled", true);
        setTomeOfKnowledgeBlacklist(config.getStringOrCreate("tome_of_knowledge_blacklist", ""), false);

        if (!file.exists() || !file.isFile()) {
            if (dir.mkdirs())
                save();
        }
    }

    public static boolean isRemoveDataFromCopyStack() {
        return removeDataFromCopyStack;
    }

    public static void setRemoveDataFromCopyStack(boolean removeDataFromCopyStack) {
        ItemAlchemyConfig.removeDataFromCopyStack = removeDataFromCopyStack;
        isChanged = true;
    }

    public static boolean isShowEmcInTooltip() {
        return showEmcInTooltip;
    }

    public static void setShowEmcInTooltip(boolean showEmcInTooltip) {
        ItemAlchemyConfig.showEmcInTooltip = showEmcInTooltip;
        isChanged = true;
    }

    public static boolean isTomeOfKnowledgeEnabled() {
        return tomeOfKnowledgeEnabled;
    }

    public static void setTomeOfKnowledgeEnabled(boolean tomeOfKnowledgeEnabled) {
        ItemAlchemyConfig.tomeOfKnowledgeEnabled = tomeOfKnowledgeEnabled;
        isChanged = true;
    }

    public static String getTomeOfKnowledgeBlacklist() {
        return tomeOfKnowledgeBlacklist;
    }

    public static void setTomeOfKnowledgeBlacklist(String tomeOfKnowledgeBlacklist) {
        setTomeOfKnowledgeBlacklist(tomeOfKnowledgeBlacklist, true);
    }

    private static void setTomeOfKnowledgeBlacklist(String value, boolean changed) {
        ItemAlchemyConfig.tomeOfKnowledgeBlacklist = value == null ? "" : value;

        Set<String> ids = new LinkedHashSet<>();
        Set<String> namespaces = new LinkedHashSet<>();

        for (String entry : ItemAlchemyConfig.tomeOfKnowledgeBlacklist.split(",")) {
            String id = entry.trim();
            if (id.isEmpty()) continue;

            // 名前空間が省略された場合は minecraft 扱いにする (バニラのIDと同じ規則)
            if (id.indexOf(':') < 0) id = "minecraft:" + id;

            if (id.endsWith(":*"))
                namespaces.add(id.substring(0, id.length() - 2));
            else
                ids.add(id);
        }

        tomeOfKnowledgeBlacklistIds = ids;
        tomeOfKnowledgeBlacklistNamespaces = namespaces;

        if (changed) isChanged = true;
    }

    /**
     * Tome of Knowledge で解禁してはいけないアイテムかどうかを返す。
     *
     * @param itemId "modid:item" 形式のアイテムID
     */
    public static boolean isTomeOfKnowledgeBlacklisted(String itemId) {
        if (itemId == null) return true;
        if (tomeOfKnowledgeBlacklistIds.contains(itemId)) return true;
        if (tomeOfKnowledgeBlacklistNamespaces.isEmpty()) return false;

        int separator = itemId.indexOf(':');
        if (separator < 0) return false;

        return tomeOfKnowledgeBlacklistNamespaces.contains(itemId.substring(0, separator));
    }

    public static void reload() {
        if (file.exists() && file.isFile())
            config.load(file);

        init();
    }

    public static void saveIfChanged() {
        if (isChanged) {
            save();
            isChanged = false;
        }
    }

    public static void save() {
        config.save(file, true);
    }

    public static JsonConfig getConfig() {
        return config;
    }

    public static File getFile() {
        return file;
    }

    public static Boolean reset() {
        config.setBoolean("remove_data_from_copy_stack", true);
        config.setBoolean("show_emc_in_tooltip", true);
        config.setBoolean("tome_of_knowledge_enabled", true);
        config.setString("tome_of_knowledge_blacklist", "");
        return true;
    }
}
