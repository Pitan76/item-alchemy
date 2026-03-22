package net.pitan76.mcpitanlib.apiprototype.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.pitan76.mcpitanlib.api.client.render.block.entity.event.BlockEntityRenderEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Cross-version utility for rendering items in block entity renderers.
 *
 * 1.18.2 – 1.21.1: ItemRenderer.renderItem() — 7 or 8 params, instance method.
 * 1.21.11+:        Two-phase pipeline — ItemModelManager.clearAndUpdate() + ItemRenderState.render().
 */
public class CompatItemRenderUtil {

    private static volatile boolean initialized = false;

    // Old API (1.18.2 – 1.21.1)
    private static Method oldRenderItemMethod = null;
    private static Object fixedMode = null;
    private static boolean hasWorldParam = false;

    // New API (1.21.11+)
    private static boolean useNewApi = false;
    private static Object itemModelManager = null;
    private static Method clearAndUpdateMethod = null;
    private static Class<?> itemRenderStateClass = null;
    private static Method renderMethod = null;
    private static Object fixedDisplayContext = null;
    // Queue field found lazily by type (SubmitNodeCollector = second param of renderMethod)
    private static volatile Field queueField = null;

    /**
     * Must be called once from the renderer constructor.
     * Detects which rendering API is available and caches the required reflective handles.
     */
    public static synchronized void initFromContext(Object ctx) {
        if (initialized) return;
        initialized = true;

        // Try ctx.getItemModelManager() — MCPitanLib 1.21.11 exposes this
        try {
            Method getManager = ctx.getClass().getMethod("getItemModelManager");
            Object manager = getManager.invoke(ctx);
            if (manager != null && initNewApi(manager)) {
                itemModelManager = manager;
                useNewApi = true;
                return;
            }
        } catch (Exception ignored) {}

        // Fallback: scan MinecraftClient fields for the vanilla ItemModelManager
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            for (Field f : client.getClass().getDeclaredFields()) {
                try {
                    f.setAccessible(true);
                    Object value = f.get(client);
                    if (value == null || value == client) continue;
                    if (initNewApi(value)) {
                        itemModelManager = value;
                        useNewApi = true;
                        return;
                    }
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}

        initOldApi();
    }

    /**
     * Tries to set up the new (1.21.11+) rendering pipeline using the given manager object.
     * Only sets static fields if all required methods are found.
     */
    private static boolean initNewApi(Object manager) {
        try {
            // Find clearAndUpdate:
            // (ItemRenderState, ItemStack, ItemDisplayContext, World, ItemOwner, int) — 6 params
            for (Method m : manager.getClass().getMethods()) {
                if (Modifier.isStatic(m.getModifiers())) continue;
                Class<?>[] p = m.getParameterTypes();
                if (p.length != 6) continue;
                if (p[1] != ItemStack.class) continue;
                if (p[5] != int.class) continue;
                if (!p[2].isEnum()) continue;
                Object fixed;
                try {
                    //noinspection unchecked,rawtypes
                    fixed = Enum.valueOf((Class<Enum>) p[2], "FIXED");
                } catch (Exception e) {
                    continue;
                }

                Class<?> renderStateClass = p[0];

                // Find render on ItemRenderState:
                // (MatrixStack, SubmitNodeCollector, int, int, int) — 5 params
                Method foundRenderMethod = null;
                for (Method rm : renderStateClass.getMethods()) {
                    if (Modifier.isStatic(rm.getModifiers())) continue;
                    Class<?>[] rp = rm.getParameterTypes();
                    if (rp.length != 5) continue;
                    if (rp[0] != MatrixStack.class) continue;
                    if (rp[1].isPrimitive()) continue;
                    if (rp[2] != int.class || rp[3] != int.class || rp[4] != int.class) continue;
                    foundRenderMethod = rm;
                    break;
                }
                if (foundRenderMethod == null) continue;

                // All found — commit
                fixedDisplayContext = fixed;
                itemRenderStateClass = renderStateClass;
                renderMethod = foundRenderMethod;
                clearAndUpdateMethod = m;
                return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    private static void initOldApi() {
        try {
            ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();
            for (Method m : renderer.getClass().getMethods()) {
                if (Modifier.isStatic(m.getModifiers())) continue;
                Class<?>[] p = m.getParameterTypes();
                if ((p.length == 7 || p.length == 8)
                        && p[0] == ItemStack.class
                        && p[1].isEnum()
                        && p[2] == int.class
                        && p[3] == int.class
                        && p[4] == MatrixStack.class
                        && p[5] == VertexConsumerProvider.class
                        && p[p.length - 1] == int.class) {
                    try {
                        //noinspection unchecked,rawtypes
                        Object fixed = Enum.valueOf((Class<Enum>) p[1], "FIXED");
                        fixedMode = fixed;
                        oldRenderItemMethod = m;
                        hasWorldParam = p.length == 8;
                        break;
                    } catch (Exception ignored) {}
                }
            }
        } catch (Exception ignored) {}
    }

    /**
     * Renders an ItemStack in FIXED transform mode.
     * Works on both 1.18.2 and 1.21.11+ without NoSuchMethodError.
     */
    public static void renderItemFixed(ItemStack stack, BlockEntityRenderEvent<?> event, World world) {
        if (!initialized) {
            synchronized (CompatItemRenderUtil.class) {
                if (!initialized) initOldApi();
            }
        }
        if (useNewApi) {
            renderNewApi(stack, event, world);
        } else {
            renderOldApi(stack, event.getLight(), event.getOverlay(),
                    event.getMatrices(), event.getVertexConsumers(), world);
        }
    }

    private static void renderNewApi(ItemStack stack, BlockEntityRenderEvent<?> event, World world) {
        try {
            Object renderState = itemRenderStateClass.getDeclaredConstructor().newInstance();
            clearAndUpdateMethod.invoke(itemModelManager, renderState, stack, fixedDisplayContext, world, null, 0);

            int light = event.getLight();
            if (light == 0) light = 0xF000F0; // full-bright fallback if not populated by MCPitanLib
            int overlay = event.getOverlay();

            // event.getVertexConsumers() returns VertexConsumerProvider, but at runtime it IS
            // a SubmitNodeCollector — reflection checks runtime type, so the invoke succeeds.
            Object queue = resolveQueue(event);
            if (queue == null) return;

            renderMethod.invoke(renderState, event.getMatrices(), queue, light, overlay, 0);
        } catch (Exception ignored) {}
    }

    /**
     * Resolves the SubmitNodeCollector needed by ItemRenderState.render().
     * First tries event.getVertexConsumers() directly (fast path).
     * Falls back to finding the field in BlockEntityRenderEvent by type.
     */
    private static Object resolveQueue(BlockEntityRenderEvent<?> event) {
        Object vcp = event.getVertexConsumers();
        Class<?> expectedType = renderMethod.getParameterTypes()[1];
        if (expectedType.isInstance(vcp)) return vcp;

        // Fallback: find field in BlockEntityRenderEvent whose type matches SubmitNodeCollector
        if (queueField == null) {
            synchronized (CompatItemRenderUtil.class) {
                if (queueField == null) {
                    for (Field f : BlockEntityRenderEvent.class.getDeclaredFields()) {
                        if (expectedType.isAssignableFrom(f.getType())) {
                            try {
                                f.setAccessible(true);
                                queueField = f;
                            } catch (Exception ignored) {}
                            break;
                        }
                    }
                }
            }
        }
        if (queueField != null) {
            try {
                return queueField.get(event);
            } catch (Exception ignored) {}
        }
        return null;
    }

    private static void renderOldApi(ItemStack stack, int light, int overlay,
                                     MatrixStack matrices, VertexConsumerProvider vcp, World world) {
        if (oldRenderItemMethod == null || fixedMode == null) return;
        try {
            ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();
            if (hasWorldParam) {
                oldRenderItemMethod.invoke(renderer, stack, fixedMode, light, overlay, matrices, vcp, world, 0);
            } else {
                oldRenderItemMethod.invoke(renderer, stack, fixedMode, light, overlay, matrices, vcp, 0);
            }
        } catch (Exception ignored) {}
    }
}
