package net.pitan76.itemalchemy.item;

import net.pitan76.itemalchemy.block.pedestal.IPedestalItem;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.event.item.InventoryTickEvent;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.registry.CompatRegistryLookup;
import net.pitan76.mcpitanlib.api.util.EntityUtil;
import net.pitan76.mcpitanlib.api.util.NbtUtil;
import net.pitan76.mcpitanlib.api.util.entity.ItemEntityUtil;
import net.pitan76.mcpitanlib.api.util.particle.CompatParticleTypes;
import net.pitan76.mcpitanlib.midohra.entity.ItemEntityWrapper;
import net.pitan76.mcpitanlib.midohra.item.ItemStack;
import net.pitan76.mcpitanlib.midohra.nbt.NbtCompound;
import net.pitan76.mcpitanlib.midohra.nbt.NbtElement;
import net.pitan76.mcpitanlib.midohra.nbt.NbtList;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import net.pitan76.mcpitanlib.midohra.util.math.Box;
import net.pitan76.mcpitanlib.midohra.util.math.Vector3d;
import net.pitan76.mcpitanlib.midohra.world.World;

import java.util.List;
import java.util.Optional;

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
        if (!e.isPlayer()) return;

        Player player = e.getPlayer();
        World world = e.getMidohraWorld();

        Vector3d playerPos = player.getPosM();

        List<ItemEntityWrapper> items = ItemEntityUtil.getEntityWrappers(world,
                new Box(playerPos).expand(PLAYER_RANGE));

        for (ItemEntityWrapper itemEntity : items) {
            Vector3d itemPos = itemEntity.getPos();
            double dx = playerPos.getX() - itemPos.getX();
            double dy = playerPos.getY() - itemPos.getY();
            double dz = playerPos.getZ() - itemPos.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (distance > 0.5) {
                double speed = 0.45;
                double nx = dx / distance * speed;
                double ny = dy / distance * speed;
                double nz = dz / distance * speed;
                itemEntity.setVelocity(nx, ny, nz);
                EntityUtil.setVelocityModified(itemEntity.get(), true);
            } else {
                itemEntity.setVelocity(0, 0, 0);
                itemEntity.setPos(playerPos);
                itemEntity.onPlayerCollision(player);
            }
        }
    }

    @Override
    public boolean updateInPedestal(ItemStack stack, World world, BlockPos pos, CompatRegistryLookup registryLookup) {
        if (world.isClient()) {
            spawnPedestalParticles(world, pos);
            return false;
        }

        Vector3d pedestalPos = pos.toCenterVector3d();
        List<ItemEntityWrapper> items = ItemEntityUtil.getEntityWrappers(world,
                new Box(pos).expand(PEDESTAL_RANGE));

        for (ItemEntityWrapper itemEntity : items) {
            Vector3d itemPos = itemEntity.getPos();
            double dx = pedestalPos.getX() - itemPos.getX();
            double dy = pedestalPos.getY() - itemPos.getY();
            double dz = pedestalPos.getZ() - itemPos.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (distance > 0.5) {
                double speed = 0.35;
                double nx = dx / distance * speed;
                double ny = dy / distance * speed;
                double nz = dz / distance * speed;
                itemEntity.setVelocity(nx, ny, nz);
                EntityUtil.setVelocityModified(itemEntity.get(), true);
            } else {
                if (addToInventory(stack, itemEntity.getStack(), registryLookup)) {
                    itemEntity.discard();
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

            world.addParticle(CompatParticleTypes.ENCHANT, x + offsetX, y + offsetY, z + offsetZ, 0.0, 0.08, 0.0);
        }

        for (int i = 0; i < 2; i++) {
            double offsetX = (Math.random() - 0.5) * 0.6;
            double offsetZ = (Math.random() - 0.5) * 0.6;
            double offsetY = 0.5 + Math.random() * 1.5;

            world.addParticle(CompatParticleTypes.PORTAL, x + offsetX, y + offsetY, z + offsetZ, 0.0, 0.05, 0.0);
        }
    }

    private NbtList getInventoryNbt(ItemStack stack) {
        NbtCompound nbt = stack.getCustomNbtM();
        if (!nbt.has(INVENTORY_NBT_KEY))
            nbt.put(INVENTORY_NBT_KEY, NbtList.of(NbtUtil.createNbtList()));

        Optional<NbtList> optionalNbtList = NbtList.ofOptional(nbt.get(INVENTORY_NBT_KEY));
        return optionalNbtList.orElseGet(() -> {
            NbtList newList = NbtList.of(NbtUtil.createNbtList());
            nbt.put(INVENTORY_NBT_KEY, newList);
            return newList;
        });
    }

    private boolean addToInventory(ItemStack stack, ItemStack itemStack, CompatRegistryLookup registryLookup) {
        NbtList inventory = getInventoryNbt(stack);
        
        if (inventory.size() >= INVENTORY_SIZE) {
            return false;
        }

        NbtCompound itemNbt = NbtCompound.of();
        if (registryLookup != null)
            itemNbt.putItemStack("item", itemStack, registryLookup);
        else
            itemNbt.putSimpleItemStack("item", itemStack);

        inventory.add(itemNbt.toElement());
        
        return true;
    }

    public int getInventoryCount(ItemStack stack) {
        NbtList inventory = getInventoryNbt(stack);
        return inventory.size();
    }

    public ItemStack removeFirstItem(ItemStack stack, CompatRegistryLookup registryLookup) {
        NbtList inventory = getInventoryNbt(stack);
        if (inventory.isEmpty()) {
            return ItemStack.EMPTY;
        }

        NbtElement itemNbtElement = inventory.get(0);
        inventory.remove(0);

        NbtCompound itemNbt = NbtCompound.of((net.minecraft.nbt.NbtCompound) itemNbtElement.toMinecraft());

        if (registryLookup != null)
            return itemNbt.getItemStack("item", registryLookup);
        else
            return itemNbt.getSimpleItemStack("item").orElse(ItemStack.EMPTY);
    }

    public void clearInventory(ItemStack stack) {
        NbtCompound nbt = stack.getCustomNbtM();
        if (nbt != null) {
            nbt.put(INVENTORY_NBT_KEY, NbtList.of(NbtUtil.createNbtList()));
            stack.setCustomNbt(nbt);
        }
    }
}
