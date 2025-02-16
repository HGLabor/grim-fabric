package gg.norisk.verbrecher.commands

import ac.grim.grimac.predictionengine.MovementCheckRunner
import gg.norisk.verbrecher.utils.LuckPermsUtils.hasPermissionOrOp
import net.minecraft.text.Text
import net.silkmc.silk.commands.command
import org.bukkit.ChatColor

object GrimPerf {
    fun init() {
        command("grim") {
            literal("performance") {
                requires { it.player?.hasPermissionOrOp("grim.performance") == true }
                runs {
                    val millis = MovementCheckRunner.predictionNanos / 1000000
                    val longMillis = MovementCheckRunner.longPredictionNanos / 1000000

                    this.source.sendMessage(Text.of(ChatColor.GRAY.toString() + "Milliseconds per prediction (avg. 500): " + ChatColor.WHITE + millis))
                    this.source.sendMessage(Text.of(ChatColor.GRAY.toString() + "Milliseconds per prediction (avg. 20k): " + ChatColor.WHITE + longMillis))
                }
            }
        }
    }
}