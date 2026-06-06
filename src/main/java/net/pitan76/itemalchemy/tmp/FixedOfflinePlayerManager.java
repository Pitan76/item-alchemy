package net.pitan76.itemalchemy.tmp;

import net.pitan76.easyapi.FileControl;
import net.pitan76.easyapi.config.JsonConfig;
import net.pitan76.mcpitanlib.api.offlineplayer.OfflinePlayer;
import net.pitan76.mcpitanlib.api.offlineplayer.OfflinePlayerManager;

public class FixedOfflinePlayerManager extends OfflinePlayerManager {

    public static FixedOfflinePlayerManager INSTANCE = new FixedOfflinePlayerManager();

    public FixedOfflinePlayerManager() {
        if (INSTANCE == null)
            INSTANCE = this;
        load();
    }

    @Override
    public void load() {
        if (FileControl.fileExists(DEFAULT_FILE)) {
            JsonConfig config = new JsonConfig(DEFAULT_FILE);
            config.configMap.forEach((key, value) -> addPlayer(new OfflinePlayer(key, (String) value)));
        }
    }
}
