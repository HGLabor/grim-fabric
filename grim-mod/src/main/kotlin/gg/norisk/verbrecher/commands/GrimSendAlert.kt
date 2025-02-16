package gg.norisk.verbrecher.commands

import ac.grim.grimac.GrimAPI
import ac.grim.grimac.utils.anticheat.LogUtil
import ac.grim.grimac.utils.anticheat.MessageUtil
import gg.norisk.verbrecher.utils.LuckPermsUtils.hasPermissionOrOp
import net.silkmc.silk.commands.command

object GrimSendAlert {
    fun init() {
        command("grim") {
            literal("sendalert") {
                requires { it.player?.hasPermissionOrOp("grim.sendalert") == true }
                argument<String>("text") { text ->
                    runs {
                        val string = MessageUtil.replacePlaceholders(null, text())
                        val message = MessageUtil.miniMessage(string)

                        for (bukkitPlayer in GrimAPI.INSTANCE.alertManager.enabledAlerts) {
                            MessageUtil.sendMessage(bukkitPlayer.commandSource, message)
                        }

                        if (GrimAPI.INSTANCE.configManager.printAlertsToConsole) {
                            LogUtil.console(message) // Print alert to console
                        }
                    }
                }
            }
        }
    }
}