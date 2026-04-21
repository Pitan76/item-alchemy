package net.pitan76.itemalchemy.util;

import net.minecraft.SharedConstants;

public class VersionUtils {
    public static boolean isSupportedComponent() {
        return SharedConstants.getProtocolVersion() >= 766;
    }
}
