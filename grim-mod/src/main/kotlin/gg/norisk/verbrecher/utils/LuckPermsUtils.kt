package gg.norisk.verbrecher.utils

import net.fabricmc.loader.api.FabricLoader
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.model.user.User
import net.minecraft.server.network.ServerPlayerEntity
import net.silkmc.silk.commands.PermissionLevel

object LuckPermsUtils {
    val LuckPerms by lazy { LuckPermsProvider.get() }

    val ServerPlayerEntity.lpUser: User
        get() = LuckPerms.getPlayerAdapter(ServerPlayerEntity::class.java).getUser(this)

    val ServerPlayerEntity.permissionData
        get() = LuckPerms.getPlayerAdapter(ServerPlayerEntity::class.java).getPermissionData(this)

    fun ServerPlayerEntity.hasPermission(permission: String): Boolean {
        return runCatching { permissionData.checkPermission(permission).asBoolean() }.getOrElse { false }
    }

    fun ServerPlayerEntity.hasPermissionOrOp(permission: String): Boolean {
        return runCatching { permissionData.checkPermission(permission).asBoolean() }.getOrElse { hasPermissionLevel(PermissionLevel.BAN_RIGHTS.level) || FabricLoader.getInstance().isDevelopmentEnvironment }
    }
}
