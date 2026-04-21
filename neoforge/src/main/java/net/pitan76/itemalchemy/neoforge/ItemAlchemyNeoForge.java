package net.pitan76.itemalchemy.neoforge;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.pitan76.itemalchemy.ItemAlchemy;

@Mod(ItemAlchemy.MOD_ID)
public class ItemAlchemyNeoForge {
    public ItemAlchemyNeoForge(ModContainer modContainer) {
        new ItemAlchemy();
    }
}