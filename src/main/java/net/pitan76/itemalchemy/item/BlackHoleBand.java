package net.pitan76.itemalchemy.item;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.pitan76.itemalchemy.block.pedestal.IPedestalItem;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.event.item.InventoryTickEvent;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.util.EntityUtil;
import net.pitan76.mcpitanlib.api.util.WorldUtil;
import net.pitan76.mcpitanlib.api.util.entity.ItemEntityUtil;
import net.pitan76.mcpitanlib.api.util.particle.CompatParticleTypes;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import net.pitan76.mcpitanlib.midohra.util.math.Box;
import net.pitan76.mcpitanlib.midohra.util.math.Vector3d;
import net.pitan76.mcpitanlib.midohra.world.World;

import java.util.List;

public class BlackHoleBand extends Ring implements IPedestalItem {

    private static final double PLAYER_RANGE = 7.0;
    private static final double PEDESTAL_RANGE = 16.0;
    private static final int INVENTORY_SIZE = 16;
    private static final String INVENTORY_NBT_KEY = "BlackHoleBandInventory";

    public BlackHoleBand(CompatibleItemSettings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(InventoryTickEvent e) {
        super.inventoryTick(e);
        if (e.isClient()) return;
        if (!(e.entity instanceof PlayerEntity)) return;

        Player player = new Player((PlayerEntity) e.entity);

        // TODO: PlayerのgetPosを直接Vector3dとして返すようにする
        Vector3d playerPos = Vector3d.of(player.getPos());

        Box box = new Box(playerPos);
        box.expand(PLAYER_RANGE);
        List<ItemEntity> items = ItemEntityUtil.getEntities(e.world, box.toMinecraft());

        for (ItemEntity itemEntity : items) {
            Vector3d itemPos = EntityUtil.getPosM(itemEntity);
            double dx = playerPos.getX() - itemPos.getX();
            double dy = playerPos.getY() - itemPos.getY();
            double dz = playerPos.getZ() - itemPos.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (distance > 0.5) {
                double speed = 0.45;
                double nx = dx / distance * speed;
                double ny = dy / distance * speed;
                double nz = dz / distance * speed;
                EntityUtil.setVelocity(itemEntity, nx, ny, nz);
                EntityUtil.setVelocityModified(itemEntity, true);
            } else {
                EntityUtil.setVelocity(itemEntity, 0, 0, 0);
                EntityUtil.setPos(itemEntity, playerPos.getX(), playerPos.getY(), playerPos.getZ());
                itemEntity.onPlayerCollision(player.getEntity());
            }
        }
    }

    @Override
    public boolean updateInPedestal(ItemStack stack, World world, BlockPos pos) {
        if (world.isClient()) {
            spawnPedestalParticles(world, pos);
            return false;
        }

        Vector3d pedestalPos = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        List<ItemEntity> items = WorldUtil.getEntitiesByClass(
                world.toMinecraft(), ItemEntity.class, pedestalPos, PEDESTAL_RANGE
        );

        for (ItemEntity itemEntity : items) {
            Vector3d itemPos = EntityUtil.getPosM(itemEntity);
            double dx = pedestalPos.getX() - itemPos.getX();
            double dy = pedestalPos.getY() - itemPos.getY();
            double dz = pedestalPos.getZ() - itemPos.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (distance > 0.5) {
                double speed = 0.35;
                double nx = dx / distance * speed;
                double ny = dy / distance * speed;
                double nz = dz / distance * speed;
                EntityUtil.setVelocity(itemEntity, nx, ny, nz);
                EntityUtil.setVelocityModified(itemEntity, true);
            } else {
                if (addToInventory(stack, itemEntity.getStack())) {
                    EntityUtil.discard(itemEntity);
                }
            }
        }

        return false;
    }

    private void spawnPedestalParticles(World world, BlockPos pos) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;

        for (int i = 0; i < 3; i++) {
            double offsetX = (Math.random() - 0.5) * 0.8;
            double offsetZ = (Math.random() - 0.5) * 0.8;
            double offsetY = 0.3 + Math.random() * 0.5;
            
            WorldUtil.addParticle(world.toMinecraft(), CompatParticleTypes.ENCHANT, x + offsetX, y + offsetY, z + offsetZ, 0.0, 0.08, 0.0);
        }

        for (int i = 0; i < 2; i++) {
            double offsetX = (Math.random() - 0.5) * 0.6;
            double offsetZ = (Math.random() - 0.5) * 0.6;
            double offsetY = 0.5 + Math.random() * 1.5;
            
            WorldUtil.addParticle(world.toMinecraft(), CompatParticleTypes.PORTAL, x + offsetX, y + offsetY, z + offsetZ, 0.0, 0.05, 0.0);
        }
    }

    private NbtList getInventoryNbt(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) {
            nbt = new NbtCompound();
            stack.setNbt(nbt);
        }
        if (!nbt.contains(INVENTORY_NBT_KEY)) {
            nbt.put(INVENTORY_NBT_KEY, new NbtList());
        }
        return nbt.getList(INVENTORY_NBT_KEY, 10);
    }

    private boolean addToInventory(ItemStack stack, ItemStack itemStack) {
        NbtList inventory = getInventoryNbt(stack);
        
        if (inventory.size() >= INVENTORY_SIZE) {
            return false;
        }

        NbtCompound itemNbt = new NbtCompound();
        itemStack.writeNbt(itemNbt);
        inventory.add(itemNbt);
        
        return true;
    }

    public int getInventoryCount(ItemStack stack) {
        NbtList inventory = getInventoryNbt(stack);
        return inventory.size();
    }

    public ItemStack removeFirstItem(ItemStack stack) {
        NbtList inventory = getInventoryNbt(stack);
        if (inventory.isEmpty()) {
            return ItemStack.EMPTY;
        }

        NbtCompound itemNbt = inventory.getCompound(0);
        inventory.remove(0);
        
        return ItemStack.fromNbt(itemNbt);
    }

    public void clearInventory(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt != null) {
            nbt.put(INVENTORY_NBT_KEY, new NbtList());
            stack.setNbt(nbt);
        }
    }
}
