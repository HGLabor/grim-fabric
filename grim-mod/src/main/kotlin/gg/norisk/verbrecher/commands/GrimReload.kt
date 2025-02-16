package gg.norisk.verbrecher.commands

import ac.grim.grimac.GrimAPI
import ac.grim.grimac.utils.anticheat.MessageUtil
import gg.norisk.verbrecher.utils.LuckPermsUtils.hasPermissionOrOp
import net.silkmc.silk.commands.command

object GrimReload {
    fun init() {
        command("grim") {
            literal("reload") {
                requires { it.player?.hasPermissionOrOp("grim.reload") == true }
                runs {
                    // reload config
                    val reloading = GrimAPI.INSTANCE.configManager.config.getStringElse(
                        "reloading",
                        "%prefix% &7Reloading config..."
                    )
                    MessageUtil.sendMessage(
                        this.source,
                        MessageUtil.miniMessage(MessageUtil.replacePlaceholders(this.source, reloading))
                    )
                    GrimAPI.INSTANCE.externalAPI.reloadAsync().exceptionally { false }
                        .thenAccept { bool: Boolean ->
                            val message = if (bool)
                                GrimAPI.INSTANCE.configManager.config.getStringElse(
                                    "reloaded",
                                    "%prefix% &fConfig has been reloaded."
                                )
                            else
                                GrimAPI.INSTANCE.configManager.config.getStringElse(
                                    "reload-failed",
                                    "%prefix% &cFailed to reload config."
                                )
                            MessageUtil.sendMessage(
                                this.source,
                                MessageUtil.miniMessage(MessageUtil.replacePlaceholders(this.source, message))
                            )
                        }
                    GrimAPI.INSTANCE.alertManager.toggleAlerts(this.source.playerOrThrow);
                }
            }
        }
    }
}