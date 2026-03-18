package net.pitan76.itemalchemy.tile;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.pitan76.itemalchemy.block.InterdictionTorch;
import net.minecraft.world.World;
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.mcpitanlib.api.event.tile.TileTickEvent;
import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity;
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntityTicker;
import net.pitan76.mcpitanlib.api.util.EntityUtil;
import net.pitan76.mcpitanlib.api.util.WorldUtil;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import net.pitan76.mcpitanlib.midohra.util.math.Box;
import net.pitan76.mcpitanlib.midohra.util.math.Direction;
import net.pitan76.mcpitanlib.midohra.util.math.Vector3d;

import java.util.List;

public class InterdictionTorchTile extends CompatBlockEntity implements ExtendBlockEntityTicker<InterdictionTorchTile> {

    public static final double RADIUS = 8.0;
    public static final double PUSH_STRENGTH = 0.15;

    public InterdictionTorchTile(BlockEntityType<?> type, TileCreateEvent e) {
        super(type, e);
    }

    public InterdictionTorchTile(TileCreateEvent e) {
        this(Tiles.INTERDICTION_TORCH.getOrNull(), e);
    }

    private int particleTick = 0;

    @Override
    public void tick(TileTickEvent<InterdictionTorchTile> e) {
        World world = e.world;
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
                WorldUtil.addParticle(world, ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0);
                WorldUtil.addParticle(world, ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 0.0, 0.0, 0.0);
            }
            return;
        }

        Box box = new Box(blockPos).expand(RADIUS);
        List<LivingEntity> entities = WorldUtil.getMonsters(world, box);

        Vector3d torchPos = Vector3d.of(blockPos.getX(), blockPos.getY(), blockPos.getZ()).ofCenter();

        for (LivingEntity entity : entities) {
            Vector3d entityPos = EntityUtil.getPosM(entity);
            double dx = entityPos.x - torchPos.x;
            double dy = entityPos.y - torchPos.y;
            double dz = entityPos.z - torchPos.z;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (distance == 0 || distance > RADIUS) continue;

            double strength = (RADIUS - distance) / RADIUS;
            double nx = dx / distance;
            double ny = dy / distance;
            double nz = dz / distance;

            EntityUtil.addVelocity(entity,
                    nx * strength * PUSH_STRENGTH,
                    ny * strength * PUSH_STRENGTH * 0.5,
                    nz * strength * PUSH_STRENGTH
            );
        }
    }
}
