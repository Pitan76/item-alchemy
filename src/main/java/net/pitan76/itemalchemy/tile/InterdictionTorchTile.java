package net.pitan76.itemalchemy.tile;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.mcpitanlib.api.event.tile.TileTickEvent;
import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity;
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntityTicker;

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
        BlockPos blockPos = callGetPos();

        if (e.isClient()) {
            double x = blockPos.getX() + 0.5;
            double y = blockPos.getY() + 0.7;
            double z = blockPos.getZ() + 0.5;

            particleTick++;

            if (particleTick % 3 == 0) {
                world.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0);
                world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 0.0, 0.0, 0.0);
            }
            return;
        }

        Box box = new Box(blockPos).expand(RADIUS);
        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, box, entity -> entity instanceof Monster);

        Vec3d torchPos = Vec3d.ofCenter(blockPos);

        for (LivingEntity entity : entities) {
            double dx = entity.getX() - torchPos.x;
            double dy = entity.getY() - torchPos.y;
            double dz = entity.getZ() - torchPos.z;
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
