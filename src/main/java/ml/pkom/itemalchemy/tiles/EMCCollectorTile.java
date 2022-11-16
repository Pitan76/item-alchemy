package ml.pkom.itemalchemy.tiles;

import ml.pkom.mcpitanlibarch.api.event.block.TileCreateEvent;
import ml.pkom.mcpitanlibarch.api.tile.ExtendBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EMCCollectorTile extends ExtendBlockEntity implements BlockEntityTicker<EMCCollectorTile> {
    public long storedEMC = 0;
    public int coolDown = 0; // tick

    public int getMaxCoolDown() {
        return 60 * 1; // tick
    }

    public EMCCollectorTile(BlockEntityType<?> type, TileCreateEvent event) {
        super(type, event);

    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putLong("stored_emc", storedEMC);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        storedEMC = nbt.getLong("stored_emc");
    }

    public EMCCollectorTile(BlockPos pos, BlockState state) {
        this(new TileCreateEvent(pos, state));
    }

    public EMCCollectorTile(BlockView world) {
        this(new TileCreateEvent(world));
    }

    public EMCCollectorTile(TileCreateEvent event) {
        this(Tiles.EMC_COLLECTOR.getOrNull(), event);
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, EMCCollectorTile blockEntity) {
        if (coolDown == 0) {
            if ((!world.isRaining() && !world.isThundering() && world.isDay() && world.isSkyVisible(pos.up())) || world.getBlockState(pos.up()).getLuminance() > 10) {
                storedEMC++;
            }
        } else if (coolDown >= getMaxCoolDown()) {
            coolDown = 0;
        }
        coolDown++;
    }
}
