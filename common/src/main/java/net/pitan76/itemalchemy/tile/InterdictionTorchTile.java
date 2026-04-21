package net.pitan76.itemalchemy.tile;

import net.pitan76.itemalchemy.block.InterdictionTorch;
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.mcpitanlib.api.event.tile.TileTickEvent;
import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity;
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntityTicker;
import net.pitan76.mcpitanlib.api.util.particle.CompatParticleTypes;
import net.pitan76.mcpitanlib.midohra.block.entity.BlockEntityTypeWrapper;
import net.pitan76.mcpitanlib.midohra.entity.EntityWrapper;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import net.pitan76.mcpitanlib.midohra.util.math.Box;
import net.pitan76.mcpitanlib.midohra.util.math.Direction;
import net.pitan76.mcpitanlib.midohra.util.math.Vector3d;
import net.pitan76.mcpitanlib.midohra.world.World;

import java.util.List;

public class InterdictionTorchTile extends CompatBlockEntity implements ExtendBlockEntityTicker<InterdictionTorchTile> {

    public static final double RADIUS = 8.0;
    public static final double PUSH_STRENGTH = 0.15;

    public InterdictionTorchTile(BlockEntityTypeWrapper type, TileCreateEvent e) {
        super(type, e);
    }

    public InterdictionTorchTile(TileCreateEvent e) {
        this(Tiles.INTERDICTION_TORCH, e);
    }

    private int particleTick = 0;

    @Override
    public void tick(TileTickEvent<InterdictionTorchTile> e) {
        World world = e.getMidohraWorld();
        BlockPos blockPos = e.getMidohraPos();

        if (e.isClient()) {
            Direction facing = e.get(InterdictionTorch.FACING);

            double x, y, z;
            if (facing == Direction.UP) {
                x = blockPos.getX() + 0.5;
                y = blockPos.getY() + 0.7;
                z = blockPos.getZ() + 0.5;
            } else {
                Direction wallDir = facing.getOpposite();
                x = blockPos.getX() + 0.5 + 0.27 * wallDir.getOffsetX();
                y = blockPos.getY() + 0.92;
                z = blockPos.getZ() + 0.5 + 0.27 * wallDir.getOffsetZ();
            }

            particleTick++;

            if (particleTick % 3 == 0) {
                world.addParticle(CompatParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0);
                world.addParticle(CompatParticleTypes.SOUL_FIRE_FLAME, x, y, z, 0.0, 0.0, 0.0);
            }
            return;
        }

        Box box = new Box(blockPos).expand(RADIUS);
        List<EntityWrapper> entities = world.getMonsters(box);

        Vector3d torchPos = blockPos.toCenterVector3d();

        for (EntityWrapper entity : entities) {
            Vector3d entityPos = entity.getPos();
            double dx = entityPos.x - torchPos.x;
            double dy = entityPos.y - torchPos.y;
            double dz = entityPos.z - torchPos.z;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (distance == 0 || distance > RADIUS) continue;

            double strength = (RADIUS - distance) / RADIUS;
            double nx = dx / distance;
            double ny = dy / distance;
            double nz = dz / distance;

            entity.addVelocity(
                    nx * strength * PUSH_STRENGTH,
                    ny * strength * PUSH_STRENGTH * 0.5,
                    nz * strength * PUSH_STRENGTH
            );
        }
    }
}
