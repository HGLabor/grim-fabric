package gg.norisk.verbrecher.commands

import ac.grim.grimac.GrimAPI
import ac.grim.grimac.utils.anticheat.MessageUtil
import gg.norisk.verbrecher.utils.LuckPermsUtils.hasPermissionOrOp
import net.minecraft.server.command.ServerCommandSource
import net.silkmc.silk.commands.command

object GrimHelp {
    fun init() {
        command("grim") {
            requires { it.player?.hasPermissionOrOp("grim.help") == true }
            runs {
                help(this.source)
            }
            literal("help") {
                requires { it.player?.hasPermissionOrOp("grim.help") == true }
                runs {
                    help(this.source)
                }
            }
        }
    }

    private fun help(source: ServerCommandSource) {
        for (string in GrimAPI.INSTANCE.configManager.config.getStringList("help")!!) {
            var string = string
            string = MessageUtil.replacePlaceholders(source, string!!)
            MessageUtil.sendMessage(source, MessageUtil.miniMessage(string))
        }
    }
}