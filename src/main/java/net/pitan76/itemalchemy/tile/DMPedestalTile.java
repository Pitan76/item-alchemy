package net.pitan76.itemalchemy.tile;

import net.pitan76.mcpitanlib.api.util.RegistryLookupUtil;
import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.itemalchemy.block.pedestal.IPedestalItem;
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.mcpitanlib.api.event.nbt.ReadNbtArgs;
import net.pitan76.mcpitanlib.api.event.nbt.WriteNbtArgs;
import net.pitan76.mcpitanlib.api.event.tile.TileTickEvent;
import net.pitan76.mcpitanlib.api.network.v2.ServerNetworking;
import net.pitan76.mcpitanlib.api.packet.UpdatePacketType;
import net.pitan76.mcpitanlib.api.registry.CompatRegistryLookup;
import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity;
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntityTicker;
import net.pitan76.mcpitanlib.api.util.particle.CompatParticleTypes;
import net.pitan76.mcpitanlib.midohra.block.entity.BlockEntityTypeWrapper;
import net.pitan76.mcpitanlib.midohra.item.ItemStack;
import net.pitan76.mcpitanlib.midohra.nbt.NbtCompound;
import net.pitan76.mcpitanlib.midohra.network.CompatPacketByteBuf;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import net.pitan76.mcpitanlib.midohra.util.math.Box;
import net.pitan76.mcpitanlib.midohra.world.World;
import org.jetbrains.annotations.Nullable;

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
    public void markRemovedOverride() {
        World world = getMidohraWorld();
        if (world != null && !world.isClient() && !storedStack.isEmpty()) {
            PENDING_DROPS.put(getMidohraPos().asLong(), storedStack.copy());
        }
        super.markRemovedOverride();
    }

    private ItemStack storedStack = ItemStack.EMPTY;
    private boolean isActive = false;
    private long tickCount = 0;
    // Counts down on server ticks; when it hits 0, forces a client sync.
    // After the initial sync, resets to 40 to periodically re-sync (every 2 s) as a safety net.
    private int syncCountdown = 5;

    public DMPedestalTile(BlockEntityTypeWrapper type, TileCreateEvent e) {
        super(type, e);
    }

    public DMPedestalTile(TileCreateEvent e) {
        this(Tiles.DM_PEDESTAL, e);
    }

    @Override
    public void tick(TileTickEvent<DMPedestalTile> e) {
        World world = e.getMidohraWorld();
        BlockPos blockPos = getMidohraPos();

        if (!e.isClient()) {
            syncCountdown--;
            if (syncCountdown <= 0) {
                sendSyncPacket(RegistryLookupUtil.getRegistryLookup(e.getWorld()));
                syncCountdown = 40;
            }
        }

        if (!isActive) return;

        if (e.isClient()) {
            // Don't deactivate from client side — server is authoritative
            if (!storedStack.isEmpty() && storedStack.getRawItem() instanceof IPedestalItem) {
                tickCount++;
                spawnActiveParticles(world, blockPos);
            }
            return;
        }

        if (storedStack.isEmpty()
                || !(storedStack.getRawItem() instanceof IPedestalItem)) {
            setActive(false, RegistryLookupUtil.getRegistryLookup(e.getWorld()));
            return;
        }

        IPedestalItem pedestalItem = (IPedestalItem) storedStack.getRawItem();
        pedestalItem.updateInPedestal(storedStack, world, blockPos, RegistryLookupUtil.getRegistryLookup(e.world));
    }

    private void spawnActiveParticles(World world, BlockPos pos) {
        if (tickCount % 10 == 0) {
            world.addParticle(CompatParticleTypes.FLAME, pos.getX() + 0.2, pos.getY() + 0.3, pos.getZ() + 0.2, 0, 0.01, 0);
            world.addParticle(CompatParticleTypes.FLAME, pos.getX() + 0.8, pos.getY() + 0.3, pos.getZ() + 0.2, 0, 0.01, 0);
            world.addParticle(CompatParticleTypes.FLAME, pos.getX() + 0.2, pos.getY() + 0.3, pos.getZ() + 0.8, 0, 0.01, 0);
            world.addParticle(CompatParticleTypes.FLAME, pos.getX() + 0.8, pos.getY() + 0.3, pos.getZ() + 0.8, 0, 0.01, 0);
        }
    }

    public Box getEffectBounds() {
        BlockPos pos = getMidohraPos();
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

    public void setStackFromPacket(net.minecraft.item.ItemStack stack) {
        this.storedStack = ItemStack.of(stack);
    }

    public void setActiveFromPacket(boolean active) {
        this.isActive = active;
    }

    public void setStack(ItemStack stack, @Nullable CompatRegistryLookup registryLookup) {
        this.storedStack = stack;
        callMarkDirty();
        sendSyncPacket(registryLookup);
    }

    public boolean getActive() {
        return isActive;
    }

    public void setActive(boolean active, @Nullable CompatRegistryLookup registryLookup) {
        if (this.isActive != active) {
            this.isActive = active;
            callMarkDirty();
            sendSyncPacket(registryLookup);
        }
    }

    @Override
    public UpdatePacketType getUpdatePacketType() {
        return UpdatePacketType.BLOCK_ENTITY_UPDATE_S2C;
    }

    @Override
    public net.minecraft.nbt.NbtCompound toInitialChunkDataNbt(CompatRegistryLookup registryLookup) {
        NbtCompound nbt = NbtCompound.of();
        writeNbtData(nbt, registryLookup);
        return nbt.toMinecraft();
    }

    @Override
    public void writeNbt(WriteNbtArgs args) {
        super.writeNbt(args);
        writeNbtData(args.getNbtM(), args.registryLookup);
    }

    @Override
    public void readNbt(ReadNbtArgs args) {
        super.readNbt(args);
        NbtCompound nbt = args.getNbtM();
        if (nbt.has("PedestalItem")) {
            storedStack = nbt.getItemStack("PedestalItem", args.registryLookup);
        }
        isActive = nbt.getBoolean("Active");
    }

    private void writeNbtData(NbtCompound nbt, @Nullable CompatRegistryLookup registryLookup) {
        if (!storedStack.isEmpty()) {
            if (registryLookup != null)
                nbt.putItemStack("PedestalItem", storedStack, registryLookup);
            else
                nbt.putSimpleItemStack("PedestalItem", storedStack);
        }
        nbt.putBoolean("Active", isActive);
    }

    public void sendSyncPacket(@Nullable CompatRegistryLookup registryLookup) {
        World world = getMidohraWorld();
        if (world == null || world.isClient()) return;

        NbtCompound nbt = NbtCompound.of();
        writeNbtData(nbt, registryLookup);

        CompatPacketByteBuf buf = CompatPacketByteBuf.create();
        buf.writeBlockPos(getMidohraPos());
        buf.writeNbt(nbt);

        ServerNetworking.sendAll(world, ItemAlchemy._id("pedestal_sync"), buf);
    }
}
