package net.pitan76.itemalchemy.tile.base;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.mcpitanlib.api.event.nbt.ReadNbtArgs;
import net.pitan76.mcpitanlib.api.event.nbt.WriteNbtArgs;
import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity;

public abstract class EMCStorageBlockEntity extends CompatBlockEntity {
    public long storedEMC = 0;
    public boolean isActive = false;

    public EMCStorageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public EMCStorageBlockEntity(BlockEntityType<?> type, TileCreateEvent event) {
        super(type, event);
    }

    @Override
    public void writeNbt(WriteNbtArgs args) {
        super.writeNbt(args);
        args.getNbt().putLong("stored_emc", storedEMC);
        args.getNbt().putBoolean("active", isActive);
    }

    @Override
    public void readNbt(ReadNbtArgs args) {
        super.readNbt(args);
        storedEMC = args.getNbt().getLong("stored_emc");
        isActive = args.getNbt().getBoolean("active");
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isActive() {
        return isActive;
    }

    public long getStoredEMC() {
        return storedEMC;
    }

    public void setStoredEMC(long emc) {
        storedEMC = emc;
    }

    public void addEMC(long emc) {
        storedEMC += emc;
    }

    public void removeEMC(long emc) {
        storedEMC -= emc;
    }

    public abstract long getMaxEMC();

    public boolean isFull() {
        return storedEMC >= getMaxEMC();
    }

    public boolean canInsert() {
        return storedEMC < getMaxEMC();
    }

    public boolean isEmpty() {
        return storedEMC <= 0;
    }
}
