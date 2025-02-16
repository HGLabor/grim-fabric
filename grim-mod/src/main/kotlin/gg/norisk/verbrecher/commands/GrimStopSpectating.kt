package gg.norisk.verbrecher.commands

import ac.grim.grimac.GrimAPI
import ac.grim.grimac.utils.anticheat.MessageUtil
import gg.norisk.verbrecher.utils.LuckPermsUtils.hasPermissionOrOp
import net.minecraft.server.network.ServerPlayerEntity
import net.silkmc.silk.commands.command

object GrimStopSpectating {
    fun init() {
        command("grim") {
            literal("stopspectating") {
                requires { it.player?.hasPermissionOrOp("grim.spectate.stopspectating") == true }
                literal("here") {
                    runs {
                        stop(this.source.playerOrThrow, "here")
                    }
                }
                runs {
                    stop(this.source.playerOrThrow, null)
                }
            }
        }
    }

    private fun stop(sender: ServerPlayerEntity, string: String?) {
        if (GrimAPI.INSTANCE.spectateManager.isSpectating(sender.uuid)) {
            val teleportBack = string == null || !string.equals(
                "here",
                ignoreCase = true
            ) || !sender.hasPermissionOrOp("grim.spectate.stophere")
            GrimAPI.INSTANCE.spectateManager.disable(sender, teleportBack)
        } else {
            var message = GrimAPI.INSTANCE.configManager.config.getStringElse(
                "cannot-spectate-return",
                "%prefix% &cYou can only do this after spectating a player."
            )
            message = MessageUtil.replacePlaceholders(sender, message!!)
            MessageUtil.sendMessage(sender.commandSource, MessageUtil.miniMessage(message))
        }
    }
}