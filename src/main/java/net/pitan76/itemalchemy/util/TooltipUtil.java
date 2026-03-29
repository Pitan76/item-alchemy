package net.pitan76.itemalchemy.util;

import org.lwjgl.glfw.GLFW;

/**
 * Utility class for tooltip-related functions that provides cross-version compatibility.
 */
public class TooltipUtil {

    /**
     * Checks if the shift key is currently being held down.
     * Uses GLFW directly for cross-version compatibility.
     * 
     * @return true if either left or right shift key is pressed, false otherwise
     */
    public static boolean hasShiftDown() {
        long window = GLFW.glfwGetCurrentContext();
        if (window == 0L) {
            return false;
        }
        return GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS ||
               GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
    }
}
