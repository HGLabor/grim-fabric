package gg.norisk.verbrecher.commands

import ac.grim.grimac.GrimAPI
import ac.grim.grimac.utils.anticheat.MessageUtil
import ac.grim.grimac.utils.anticheat.MultiLibUtil
import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.manager.server.ServerVersion
import gg.norisk.verbrecher.utils.LuckPermsUtils.hasPermissionOrOp
import net.minecraft.command.argument.EntityArgumentType
import net.silkmc.silk.commands.command

object GrimProfile {
    fun init() {
        command("grim") {
            literal("profile") {
                requires { it.player?.hasPermissionOrOp("grim.profile") == true }
                argument("players", EntityArgumentType.players()) {
                    runs {
                        val sender = this.source
                        for (target in EntityArgumentType.getPlayers(this, "players")) {
                            // Short circuit due to minimum java requirements for MultiLib
                            if (PacketEvents.getAPI().serverManager.version.isNewerThanOrEquals(ServerVersion.V_1_18) && MultiLibUtil.isExternalPlayer(
                                    target
                                )
                            ) {
                                var alertString = GrimAPI.INSTANCE.configManager.config
                                    .getStringElse(
                                        "player-not-this-server",
                                        "%prefix% &cThis player isn't on this server!"
                                    )
                                alertString = MessageUtil.replacePlaceholders(sender, alertString!!)
                                MessageUtil.sendMessage(sender, MessageUtil.miniMessage(alertString))
                                return@runs
                            }

                            val grimPlayer = GrimAPI.INSTANCE.playerDataManager.getPlayer(target)
                            if (grimPlayer == null) {
                                var message = GrimAPI.INSTANCE.configManager.config
                                    .getStringElse("player-not-found", "%prefix% &cPlayer is exempt or offline!")
                                message = MessageUtil.replacePlaceholders(sender, message!!)
                                MessageUtil.sendMessage(sender, MessageUtil.miniMessage(message))
                                return@runs
                            }
                            for (message in GrimAPI.INSTANCE.configManager.config
                                .getStringList("profile")!!) {
                                val component = MessageUtil.miniMessage(message)
                                MessageUtil.sendMessage(
                                    sender,
                                    MessageUtil.replacePlaceholders(grimPlayer, component)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}