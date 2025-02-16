package gg.norisk.verbrecher.commands

import ac.grim.grimac.GrimAPI
import ac.grim.grimac.utils.anticheat.MessageUtil
import ac.grim.grimac.utils.anticheat.MultiLibUtil
import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.manager.server.ServerVersion
import gg.norisk.verbrecher.utils.LuckPermsUtils.hasPermissionOrOp
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.world.GameMode
import net.silkmc.silk.commands.command

object GrimSpectate {
    fun init() {
        command("grim") {
            literal("spectate") {
                requires { it.player?.hasPermissionOrOp("grim.spectate") == true }
                argument("player", EntityArgumentType.player()) {
                    runs {
                        val target = EntityArgumentType.getPlayer(this, "player")
                        if (!this.source.isExecutedByPlayer) return@runs
                        val sender = this.source.playerOrThrow
                        if (target != null && target.uuid.equals(sender.uuid)) {
                            var message = GrimAPI.INSTANCE.configManager.config.getStringElse(
                                "cannot-run-on-self",
                                "%prefix% &cYou cannot use this command on yourself!"
                            )
                            message = MessageUtil.replacePlaceholders(target, message!!)
                            MessageUtil.sendMessage(sender.commandSource, MessageUtil.miniMessage(message))
                            return@runs
                        }

                        if (target == null || (PacketEvents.getAPI().serverManager.version.isNewerThanOrEquals(
                                ServerVersion.V_1_18
                            ) && MultiLibUtil.isExternalPlayer(target))
                        ) {
                            var message = GrimAPI.INSTANCE.configManager.config
                                .getStringElse("player-not-this-server", "%prefix% &cThis player isn't on this server!")
                            message = MessageUtil.replacePlaceholders(target, message!!)
                            MessageUtil.sendMessage(sender.commandSource, MessageUtil.miniMessage(message))
                            return@runs
                        }


                        // hide player from tab list
                        if (GrimAPI.INSTANCE.spectateManager.enable(sender)) {
                            val grimPlayer = GrimAPI.INSTANCE.playerDataManager.getPlayer(sender)
                            if (grimPlayer != null) {
                                var message = GrimAPI.INSTANCE.configManager.config.getStringElse(
                                    "spectate-return",
                                    "<click:run_command:/grim stopspectating><hover:show_text:\"/grim stopspectating\">\n%prefix% &fClick here to return to previous location\n</hover></click>"
                                )
                                message = MessageUtil.replacePlaceholders(target, message!!)
                                grimPlayer.user.sendMessage(MessageUtil.miniMessage(message))
                            }
                        }

                        sender.changeGameMode(GameMode.SPECTATOR)
                        //TODO PaperUtils.teleportAsync(sender, target.getPlayer().getLocation())
                    }
                }
            }
        }
    }
}