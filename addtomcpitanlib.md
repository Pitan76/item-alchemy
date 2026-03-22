# Suggested additions to MCPitanLib

# !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!These additions made by AI!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

## Problem

`BlockEntityRenderEvent.getItemRenderer()` returns vanilla `ItemRenderer`, but calling
`renderItem(ItemStack, ModelTransformation.Mode, int, int, MatrixStack, VertexConsumerProvider, int)`
directly on it causes `NoSuchMethodError` at runtime on Minecraft 1.20.5+.

The method signature changed in 1.20.5:
- **1.18.2–1.20.4**: `renderItem(ItemStack, ModelTransformation.Mode, int, int, MatrixStack, VertexConsumerProvider, int)` (7 params)
- **1.20.5+**: `renderItem(ItemStack, ModelTransformationMode, int, int, MatrixStack, VertexConsumerProvider, World, int)` (8 params, added `World`, renamed enum)

## Suggested fix

Add a cross-version `renderItemFixed` helper to `BlockEntityRenderEvent`:

```java
// In BlockEntityRenderEvent<T>

/**
 * Renders an ItemStack in FIXED transform mode.
 */
public void renderItemFixed(ItemStack stack) {
    CompatItemRenderUtil.renderItemFixed(
        stack, light, overlay, matrices, vertexConsumers,
        blockEntity.callGetWorld()
    );
}
```

Where `CompatItemRenderUtil` detects the correct `renderItem` signature at runtime using
reflection (see `net.pitan76.mcpitanlib.api.client.render.CompatItemRenderUtil` for the
reference implementation).

## Workaround used in this project

`net.pitan76.mcpitanlib.api.client.render.CompatItemRenderUtil` — a reflection-based utility
that finds and caches the correct `renderItem` method at runtime, supporting both
7-param and 8-param signatures and both `ModelTransformation.Mode` / `ModelTransformationMode`.

---

## Problem: StateReplacedEvent.getBlockEntity() returns null on MC 1.21.x

In MC 1.21.x the block entity is removed from the world **before** `onStateReplaced` fires.
`StateReplacedEvent.getBlockEntity()` does a live `world.getBlockEntity(pos)` lookup, which
returns `null` at that point — making it impossible to read the BE's data (e.g. to drop items).

## Suggested fix

Capture the block entity at event construction time:

```java
// In StateReplacedEvent
private final BlockEntity cachedBlockEntity;

public StateReplacedEvent(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
    this.state = state;
    this.world = world;
    this.pos = pos;
    this.newState = newState;
    this.moved = moved;
    this.cachedBlockEntity = WorldUtil.getBlockEntity(world, pos); // capture before world removes it
}

public BlockEntity getBlockEntity() {
    if (cachedBlockEntity != null) return cachedBlockEntity;
    return WorldUtil.getBlockEntity(world, pos); // fallback for older versions
}
```

## Workaround used in this project

`net.pitan76.mcpitanlib.api.event.block.StateReplacedEvent` — a fixed copy placed in the
mod's source tree (same package as the MCPitanLib original) so it shadows the library class
at compile time. See that file for the full implementation.
