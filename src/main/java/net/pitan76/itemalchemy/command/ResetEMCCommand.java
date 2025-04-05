package net.pitan76.itemalchemy.command;

import net.pitan76.easyapi.FileControl;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.mcpitanlib.api.command.CommandSettings;
import net.pitan76.mcpitanlib.api.command.LiteralCommand;
import net.pitan76.mcpitanlib.api.event.ServerCommandEvent;
import net.pitan76.mcpitanlib.api.util.PlatformUtil;
import net.pitan76.mcpitanlib.midohra.world.ServerWorld;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResetEMCCommand extends LiteralCommand {
    @Override
    public void init(CommandSettings settings) {
        settings.permissionLevel(2);
    }

    @Override
    public void execute(ServerCommandEvent e) {
        if (!e.isClient()) {

            File dir = new File(PlatformUtil.getConfigFolderAsFile(), ItemAlchemy.MOD_ID);
            if (!dir.exists()) dir.mkdirs();

            File file = new File(dir, "emc_config.json");
            if (file.exists()) {
                String fileName = "emc_config_backup_" + DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").format(LocalDateTime.now()) + ".json";
                FileControl.fileRename(file, new File(dir, fileName));
                e.sendSuccess("[ItemAlchemy] Backup emc_config.json as " + fileName);
            }

            ItemAlchemy.logger.info("Reload EMCManager");

            if (!EMCManager.getMap().isEmpty()) EMCManager.setMap(new LinkedHashMap<>());

            if (file.exists() && EMCManager.config.load(file)) {
                for (Map.Entry<String, Object> entry : EMCManager.config.configMap.entrySet()) {
                    if (entry.getValue() instanceof Long) {
                        EMCManager.add(entry.getKey(), (Long) entry.getValue());
                    }
                    if (entry.getValue() instanceof Integer) {
                        EMCManager.add(entry.getKey(), Long.valueOf((Integer) entry.getValue()));
                    }
                    if (entry.getValue() instanceof Double) {
                        EMCManager.add(entry.getKey(), (Math.round((Double) entry.getValue())));
                    }
                    if (entry.getValue() instanceof String) {
                        EMCManager.add(entry.getKey(), Long.parseLong((String) entry.getValue()));
                    }
                }
            } else {
                EMCManager.defaultMap();
                for (Map.Entry<String, Long> entry : EMCManager.getMap().entrySet()) {
                    EMCManager.config.set(entry.getKey(), entry.getValue());
                }
                EMCManager.config.save(file);
            }

            if (e.getWorld() instanceof net.minecraft.server.world.ServerWorld) {
                ServerWorld serverWorld = ServerWorld.of((net.minecraft.server.world.ServerWorld) e.getWorld());
                EMCManager.setEmcFromRecipes(serverWorld);
            }

            e.sendSuccess("[ItemAlchemy] Set all emc to default emc");
        }
    }
}
