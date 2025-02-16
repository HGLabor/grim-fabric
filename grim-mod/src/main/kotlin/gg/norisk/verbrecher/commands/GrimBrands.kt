package gg.norisk.verbrecher.commands

import ac.grim.grimac.GrimAPI
import gg.norisk.verbrecher.utils.LuckPermsUtils.hasPermissionOrOp
import net.silkmc.silk.commands.command

object GrimBrands {
    fun init() {
        command("grim") {
            literal("brands") {
                requires { it.player?.hasPermissionOrOp("grim.brand") == true }
                runs {
                    GrimAPI.INSTANCE.alertManager.toggleBrands(this.source.playerOrThrow)
                }
            }
        }
    }
}