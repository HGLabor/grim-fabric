package gg.norisk.verbrecher.commands

import ac.grim.grimac.GrimAPI
import ac.grim.grimac.player.GrimPlayer
import com.github.retrooper.packetevents.PacketEvents
import gg.norisk.verbrecher.utils.LuckPermsUtils.hasPermissionOrOp
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.silkmc.silk.commands.command
import org.bukkit.ChatColor

object GrimDebug {
    fun init() {
        command("grim") {
            literal("debug") {
                requires { it.player?.hasPermissionOrOp("grim.debug") == true }
                argument("players", EntityArgumentType.players()) {
                    runs {
                        val player: ServerPlayerEntity? = this.source.player
                        for (target in EntityArgumentType.getPlayers(this, "players")) {
                            val grimPlayer = parseTarget(this.source, target)
                            if (grimPlayer == null) return@runs
                            if (player == null) { // Just debug to console to reduce complexity...
                                grimPlayer.checkManager.debugHandler.toggleConsoleOutput()
                            } else { // This sender is a player
                                grimPlayer.checkManager.debugHandler.toggleListener(player)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun parseTarget(
        sender: ServerCommandSource,
        target: ServerPlayerEntity
    ): GrimPlayer? {
        val targetPlayer = target
        val grimPlayer = GrimAPI.INSTANCE.playerDataManager.getPlayer(targetPlayer)
        if (grimPlayer == null) {
            val user = PacketEvents.getAPI().playerManager.getUser(targetPlayer)
            sender.sendMessage(Text.of(ChatColor.RED.toString() + "This player is exempt from all checks!"))

            if (user == null) {
                sender.sendMessage(Text.of(ChatColor.RED.toString() + "Unknown PacketEvents user"))
            } else {
                val isExempt = GrimAPI.INSTANCE.playerDataManager.shouldCheck(user)
                if (!isExempt) {
                    sender.sendMessage(Text.of(ChatColor.RED.toString() + "User connection state: " + user.connectionState))
                }
            }
        }

        return grimPlayer
    }
}