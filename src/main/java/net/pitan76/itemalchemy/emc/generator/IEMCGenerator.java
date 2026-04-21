package net.pitan76.itemalchemy.emc.generator;

import net.pitan76.mcpitanlib.api.util.CompatIdentifier;
import net.pitan76.mcpitanlib.midohra.world.ServerWorld;

public interface IEMCGenerator {
    void generate(ServerWorld world);

    CompatIdentifier getId();
}