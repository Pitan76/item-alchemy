package net.pitan76.itemalchemy.config;

import net.pitan76.easyapi.config.JsonConfig;
import net.pitan76.mcpitanlib.api.util.PlatformUtil;

import java.io.File;

public class ItemAlchemyConfig {
    private static final File file = new File(PlatformUtil.getConfigFolderAsFile(), "itemalchemy/config.json");
    private static final JsonConfig config = new JsonConfig(file);

    public static boolean isChanged;

    private static boolean removeDataFromCopyStack = true;

    public static void init() {
        removeDataFromCopyStack = config.getBooleanOrCreate("remove_data_from_copy_stack", true);

        if (!file.exists() || !file.isFile()) {
            if (file.mkdirs())
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
        return true;
    }
}
