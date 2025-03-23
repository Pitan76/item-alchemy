package net.pitan76.itemalchemy.command;

import net.pitan76.itemalchemy.config.ItemAlchemyConfig;
import net.pitan76.mcpitanlib.api.command.ConfigCommand;
import net.pitan76.mcpitanlib.api.command.LiteralCommand;
import net.pitan76.mcpitanlib.api.event.*;

public class ItemAlchemyCommand extends LiteralCommand {

    @Override
    public void init() {
        addArgumentCommand("reloademc", new ReloadCommand());
        addArgumentCommand("opentable", new OpenTableCommand());
        addArgumentCommand("resetemc", new ResetEMCCommand());
        addArgumentCommand("setemc", new SetEMCCommand());
        addArgumentCommand("team", new TeamCommand());
        addArgumentCommand("ranking", new RankingCommand());
        addArgumentCommand("config", new ConfigCommand(ItemAlchemyConfig.getConfig(), ItemAlchemyConfig.getFile(), "[Item Alchemy]", ItemAlchemyConfig::reset));
    }

    @Override
    public void execute(ServerCommandEvent e) {
        e.sendSuccess("[ItemAlchemy]"
                + "\n- /itemalchemy reloademc...Reload emc_config.json"
                + "\n- /itemalchemy opentable...Reload emc_config.json"
                + "\n- /itemalchemy resetemc...Set all emc to default emc"
                + "\n- /itemalchemy setemc [Item] [EMC]...Set emc of the item"
                + "\n- /itemalchemy team [create | join | kick | leave | list | members] ([Team Name/Player Name])"
                + "\n- /itemalchemy ranking...Show EMC ranking"
                + "\n- /itemalchemy config [set | get | reset | list] ([key])...Config command"
        );
    }
}
