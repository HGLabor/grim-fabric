package gg.norisk.verbrecher.utils

import net.minecraft.world.World

object AntiCheatUtils {
    fun getWorldName(world: World): String {
        return world.getRegistryKey().getValue().toString();
    }
}