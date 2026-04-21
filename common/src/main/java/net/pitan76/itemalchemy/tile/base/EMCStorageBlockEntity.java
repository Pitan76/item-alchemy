package net.pitan76.itemalchemy.tile.base;

import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.mcpitanlib.api.event.nbt.ReadNbtArgs;
import net.pitan76.mcpitanlib.api.event.nbt.WriteNbtArgs;
import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity;
import net.pitan76.mcpitanlib.api.util.NbtUtil;
import net.pitan76.mcpitanlib.midohra.block.entity.BlockEntityTypeWrapper;

public abstract class EMCStorageBlockEntity extends CompatBlockEntity {
    public long storedEMC = 0;
    public boolean isActive = false;

    public EMCStorageBlockEntity(BlockEntityTypeWrapper type, net.minecraft.util.math.BlockPos pos, net.minecraft.block.BlockState state) {
        super(type.get(), pos, state);
    }

    public EMCStorageBlockEntity(BlockEntityTypeWrapper type, TileCreateEvent event) {
        super(type, event);
    }

    @Override
    public void writeNbt(WriteNbtArgs args) {
        super.writeNbt(args);
        NbtUtil.putLong(args.getNbt(), "stored_emc", storedEMC);
        NbtUtil.putBoolean(args.getNbt(), "active", isActive);
    }

    @Override
    public void readNbt(ReadNbtArgs args) {
        super.readNbt(args);
        storedEMC = NbtUtil.getLong(args.getNbt(), "stored_emc");
        isActive = NbtUtil.getBoolean(args.getNbt(), "active");
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

    public boolean canExtract() {
        return storedEMC > 0;
    }
}
