package gg.norisk.verbrecher.commands

import ac.grim.grimac.GrimAPI
import gg.norisk.verbrecher.utils.LuckPermsUtils.hasPermissionOrOp
import net.silkmc.silk.commands.command

object GrimAlerts {
    fun init() {
        command("grim") {
            literal("alerts") {
                requires { it.player?.hasPermissionOrOp("grim.alerts") == true }
                runs {
                    GrimAPI.INSTANCE.alertManager.toggleAlerts(this.source.playerOrThrow);
                }
            }
        }
    }
}