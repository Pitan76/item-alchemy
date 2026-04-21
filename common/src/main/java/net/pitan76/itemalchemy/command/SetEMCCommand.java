package net.pitan76.itemalchemy.command;

import net.minecraft.item.Item;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.mcpitanlib.api.command.CommandSettings;
import net.pitan76.mcpitanlib.api.command.LiteralCommand;
import net.pitan76.mcpitanlib.api.command.argument.IntegerCommand;
import net.pitan76.mcpitanlib.api.command.argument.ItemCommand;
import net.pitan76.mcpitanlib.api.event.IntegerCommandEvent;
import net.pitan76.mcpitanlib.api.event.ItemCommandEvent;
import net.pitan76.mcpitanlib.api.event.ServerCommandEvent;
import net.pitan76.mcpitanlib.api.util.CommandUtil;
import net.pitan76.mcpitanlib.api.util.item.ItemUtil;

import java.util.Map;

public class SetEMCCommand extends LiteralCommand {
    @Override
    public void init(CommandSettings settings) {
        settings.permissionLevel(2);
        addArgumentCommand("item", new ItemCommand() {

            @Override
            public void init(CommandSettings settings) {
                addArgumentCommand("emc", new IntegerCommand() {
                    @Override
                    public void init(CommandSettings settings) {
                        settings.permissionLevel(2);
                    }

                    @Override
                    public void execute(IntegerCommandEvent e) {
                        Item item = CommandUtil.getItemArgument("item", e);
                        EMCManager.set(item, e.getValue());
                        for (Map.Entry<String, Long> entry : EMCManager.getMap().entrySet()) {
                            EMCManager.config.set(entry.getKey(), entry.getValue());
                        }
                        EMCManager.config.save(EMCManager.getConfigFile());
                    }

                    @Override
                    public String getArgumentName() {
                        return "emc";
                    }
                });
            }

            @Override
            public void execute(ItemCommandEvent e) {
                e.sendSuccess("[ItemAlchemy] " + ItemUtil.toId(e.getValue()) + ": " + EMCManager.get(e.getValue()) + "EMC");
            }

            @Override
            public String getArgumentName() {
                return "item";
            }
        });
    }

    @Override
    public void execute(ServerCommandEvent e) {
        if (!e.isClient()) {
            e.sendSuccess("[ItemAlchemy] Example: /itemalchemy setemc [Item] [EMC]");
        }
    }
}
