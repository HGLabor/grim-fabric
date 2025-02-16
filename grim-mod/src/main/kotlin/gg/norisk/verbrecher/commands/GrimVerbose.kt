package gg.norisk.verbrecher.commands

import ac.grim.grimac.GrimAPI
import gg.norisk.verbrecher.utils.LuckPermsUtils.hasPermissionOrOp
import net.silkmc.silk.commands.command

object GrimVerbose {
    fun init() {
        command("grim") {
            literal("verbose") {
                requires { it.player?.hasPermissionOrOp("grim.verbose") == true }
                runs {
                    GrimAPI.INSTANCE.alertManager.toggleVerbose(this.source.playerOrThrow)
                }
            }
        }
    }
}