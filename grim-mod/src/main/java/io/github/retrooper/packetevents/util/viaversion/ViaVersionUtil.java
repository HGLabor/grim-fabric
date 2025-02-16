package io.github.retrooper.packetevents.util.viaversion;

import net.fabricmc.loader.api.FabricLoader;

public class ViaVersionUtil {
    public static boolean isAvailable() {
        return FabricLoader.getInstance().isModLoaded("viaversion");
    }
}
