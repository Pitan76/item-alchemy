package net.pitan76.itemalchemy.tile;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.itemalchemy.block.pedestal.IPedestalItem;
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.mcpitanlib.api.event.nbt.ReadNbtArgs;
import net.pitan76.mcpitanlib.api.event.nbt.WriteNbtArgs;
import net.pitan76.mcpitanlib.api.event.tile.TileTickEvent;
import net.pitan76.mcpitanlib.api.network.PacketByteUtil;
import net.pitan76.mcpitanlib.api.network.v2.ServerNetworking;
import net.pitan76.mcpitanlib.api.packet.UpdatePacketType;
import net.pitan76.mcpitanlib.api.registry.CompatRegistryLookup;
import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity;
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntityTicker;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;
import net.pitan76.mcpitanlib.api.util.NbtUtil;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DMPedestalTile extends CompatBlockEntity implements ExtendBlockEntityTicker<DMPedestalTile> {

    public static final int RANGE = 4;

    // In MC 1.21.x the block entity is removed from the world BEFORE onStateReplaced fires,
    // so world.getBlockEntity(pos) returns null at that point.
    // markRemoved() fires during block removal (after block state is already changed to the new state),
    // so we cache the item here and DMPedestal.onStateReplaced reads it.
    private static final ConcurrentHashMap<Long, ItemStack> PENDING_DROPS = new ConcurrentHashMap<>();

    public static ItemStack takePendingDrop(BlockPos pos) {
        return PENDING_DROPS.remove(pos.asLong());
    }

    @Override
    public void markRemoved() {
        World world = callGetWorld();
        if (world != null && !world.isClient() && !storedStack.isEmpty()) {
            PENDING_DROPS.put(callGetPos().asLong(), storedStack.copy());
        }
        super.markRemoved();
    }

    private ItemStack storedStack = ItemStack.EMPTY;
    private boolean isActive = false;
    private long tickCount = 0;
    // Counts down on server ticks; when it hits 0, forces a client sync.
    // After the initial sync, resets to 40 to periodically re-sync (every 2 s) as a safety net.
    private int syncCountdown = 5;

    public DMPedestalTile(BlockEntityType<?> type, TileCreateEvent e) {
        super(type, e);
    }

    public DMPedestalTile(TileCreateEvent e) {
        this(Tiles.DM_PEDESTAL.getOrNull(), e);
    }

    @Override
    public void tick(TileTickEvent<DMPedestalTile> e) {
        World world = e.world;
        BlockPos blockPos = callGetPos();

        if (!e.isClient()) {
            syncCountdown--;
            if (syncCountdown <= 0) {
                sendSyncPacket();
                syncCountdown = 40;
            }
        }

        if (!isActive) return;

        if (e.isClient()) {
            // Don't deactivate from client side — server is authoritative
            if (!storedStack.isEmpty() && storedStack.getItem() instanceof IPedestalItem) {
                tickCount++;
                spawnActiveParticles(world, blockPos);
            }
            return;
        }

        if (storedStack.isEmpty()) {
            setActive(false);
            return;
        }

        if (!(storedStack.getItem() instanceof IPedestalItem)) {
            setActive(false);
            return;
        }

        IPedestalItem pedestalItem = (IPedestalItem) storedStack.getItem();
        pedestalItem.updateInPedestal(storedStack, world, blockPos);
    }

    private void spawnActiveParticles(World world, BlockPos pos) {
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.9;
        double cz = pos.getZ() + 0.5;

        double angle = (tickCount % 40) * (Math.PI * 2.0 / 40.0);
        double radius = 0.4;
        double px = cx + Math.cos(angle) * radius;
        double pz = cz + Math.sin(angle) * radius;
        world.addParticle(ParticleTypes.ENCHANT, px, cy + 0.3, pz, 0, -0.1, 0);

        double angle2 = angle + Math.PI;
        double px2 = cx + Math.cos(angle2) * radius;
        double pz2 = cz + Math.sin(angle2) * radius;
        world.addParticle(ParticleTypes.ENCHANT, px2, cy + 0.3, pz2, 0, -0.1, 0);

        double witchAngle = (tickCount % 20) * (Math.PI * 2.0 / 20.0);
        world.addParticle(ParticleTypes.WITCH,
                cx + Math.cos(witchAngle) * 0.25, cy + 0.1, cz + Math.sin(witchAngle) * 0.25,
                0, 0.05, 0);

        if (tickCount % 15 == 0) {
            for (int i = 0; i < 3; i++) {
                double a = Math.random() * Math.PI * 2;
                double r = 0.15 + Math.random() * 0.3;
                world.addParticle(ParticleTypes.PORTAL,
                        cx + Math.cos(a) * r, cy + 0.05, cz + Math.sin(a) * r,
                        (Math.random() - 0.5) * 0.05, 0.04 + Math.random() * 0.04, (Math.random() - 0.5) * 0.05);
            }
        }

        if (tickCount % 10 == 0) {
            world.addParticle(ParticleTypes.FLAME, pos.getX() + 0.2, pos.getY() + 0.3, pos.getZ() + 0.2, 0, 0.01, 0);
            world.addParticle(ParticleTypes.FLAME, pos.getX() + 0.8, pos.getY() + 0.3, pos.getZ() + 0.2, 0, 0.01, 0);
            world.addParticle(ParticleTypes.FLAME, pos.getX() + 0.2, pos.getY() + 0.3, pos.getZ() + 0.8, 0, 0.01, 0);
            world.addParticle(ParticleTypes.FLAME, pos.getX() + 0.8, pos.getY() + 0.3, pos.getZ() + 0.8, 0, 0.01, 0);
        }
    }

    public Box getEffectBounds() {
        BlockPos pos = callGetPos();
        return new Box(
                pos.getX() - RANGE, pos.getY() - RANGE, pos.getZ() - RANGE,
                pos.getX() + RANGE + 1, pos.getY() + RANGE + 1, pos.getZ() + RANGE + 1
        );
    }

    public ItemStack getStack() {
        return storedStack;
    }

    public void setStackFromPacket(ItemStack stack) {
        this.storedStack = stack;
    }

    public void setActiveFromPacket(boolean active) {
        this.isActive = active;
    }

    public void setStack(ItemStack stack) {
        this.storedStack = stack;
        markDirty();
        sendSyncPacket();
    }

    public boolean getActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        if (this.isActive != active) {
            this.isActive = active;
            markDirty();
            sendSyncPacket();
        }
    }

    @Override
    public UpdatePacketType getUpdatePacketType() {
        return UpdatePacketType.BLOCK_ENTITY_UPDATE_S2C;
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(CompatRegistryLookup registryLookup) {
        NbtCompound nbt = NbtUtil.create();
        writeNbtData(nbt);
        return nbt;
    }

    @Override
    public void writeNbt(WriteNbtArgs args) {
        super.writeNbt(args);
        writeNbtData(args.getNbt());
    }

    @Override
    public void readNbt(ReadNbtArgs args) {
        super.readNbt(args);
        NbtCompound nbt = args.getNbt();
        if (NbtUtil.has(nbt, "PedestalItem")) {
            Optional<ItemStack> opt = NbtUtil.getSimpleItemStack(nbt, "PedestalItem");
            storedStack = opt.orElse(ItemStackUtil.empty());
        }
        isActive = NbtUtil.getBoolean(nbt, "Active");
    }

    private void writeNbtData(NbtCompound nbt) {
        if (!ItemStackUtil.isEmpty(storedStack)) {
            NbtUtil.putSimpleItemStack(nbt, "PedestalItem", storedStack);
        }
        NbtUtil.putBoolean(nbt, "Active", isActive);
    }

    public void sendSyncPacket() {
        World world = callGetWorld();
        if (world == null || world.isClient()) return;
        PacketByteBuf buf = PacketByteUtil.create();
        PacketByteUtil.writeBlockPos(buf, callGetPos());
        NbtCompound nbt = NbtUtil.create();
        writeNbtData(nbt);
        PacketByteUtil.writeNbt(buf, nbt);
        ServerNetworking.sendAll(world, ItemAlchemy._id("pedestal_sync"), buf);
    }
}
