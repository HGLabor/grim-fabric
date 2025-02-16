package gg.norisk.verbrecher.commands

import ac.grim.grimac.GrimAPI
import ac.grim.grimac.utils.anticheat.LogUtil
import ac.grim.grimac.utils.anticheat.MessageUtil
import com.google.gson.JsonParser
import gg.norisk.verbrecher.utils.LuckPermsUtils.hasPermissionOrOp
import gg.norisk.verbrecher.utils.SchedulerUtils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minecraft.server.command.ServerCommandSource
import net.silkmc.silk.commands.command
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.max

object GrimVersion {
    fun init() {
        command("grim") {
            literal("version") {
                requires { it.player?.hasPermissionOrOp("grim.version") == true }
                runs {
                    checkForUpdatesAsync(this.source)
                }
            }
        }
    }

    var lastCheck: Long = 0
    val updateMessage: AtomicReference<Component> = AtomicReference()
    val HTTP_CLIENT: HttpClient = HttpClient.newHttpClient()

    fun checkForUpdatesAsync(sender: ServerCommandSource) {
        val current = GrimAPI.INSTANCE.externalAPI.grimVersion
        MessageUtil.sendMessage(
            sender, Component.text()
                .append(Component.text("Grim Version: ").color(NamedTextColor.GRAY))
                .append(Component.text(current).color(NamedTextColor.AQUA))
                .build()
        )
        // use cached message if last check was less than 1 minute ago
        val now = System.currentTimeMillis()
        if (now - lastCheck < 60000) {
            val message = updateMessage.get()
            if (message != null) MessageUtil.sendMessage(sender, message)
            return
        }
        lastCheck = now
        SchedulerUtils.async({
            checkForUpdates(sender)
        })
    }

    // Using UserAgent format recommended by https://docs.modrinth.com/api/
    @Suppress("deprecation")
    private fun checkForUpdates(sender: ServerCommandSource) {
        val current = GrimAPI.INSTANCE.externalAPI.grimVersion
        try {
            val request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.modrinth.com/v2/project/LJNGWSvH/version"))
                .GET()
                .header("User-Agent", "GrimAnticheat/Grim/" + GrimAPI.INSTANCE.externalAPI.grimVersion)
                .header("Content-Type", "application/json")
                .timeout(Duration.of(5, ChronoUnit.SECONDS))
                .build()

            val response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() != 200) {
                val msg = updateMessage.get()
                MessageUtil.sendMessage(
                    sender, Objects.requireNonNullElseGet(
                        msg
                    ) {
                        Component.text()
                            .append(
                                Component.text("Failed to check latest version.")
                                    .color(NamedTextColor.RED)
                            )
                            .build()
                    })
                LogUtil.error("Failed to check latest GrimAC version. Response code: " + response.statusCode())
                return
            }
            // Using old JsonParser method, as old versions of Gson don't include the static one
            val `object` = JsonParser().parse(response.body()).asJsonArray[0].asJsonObject
            val latest = `object`["version_number"].asString
            val status = compareVersions(current, latest)
            val msg: Component = when (status) {
                Status.AHEAD -> Component.text("You are using a development version of GrimAC")
                    .color(NamedTextColor.LIGHT_PURPLE)

                Status.UPDATED -> Component.text("You are using the latest version of GrimAC")
                    .color(NamedTextColor.GREEN)

                Status.OUTDATED -> Component.text()
                    .append(Component.text("New GrimAC version found!").color(NamedTextColor.AQUA))
                    .append(Component.text(" Version ").color(NamedTextColor.GRAY))
                    .append(Component.text(latest).color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC))
                    .append(Component.text(" is available to be downloaded here: ").color(NamedTextColor.GRAY))
                    .append(
                        Component.text("https://modrinth.com/plugin/grimac").color(NamedTextColor.GRAY)
                            .decorate(TextDecoration.UNDERLINED)
                            .clickEvent(ClickEvent.openUrl("https://modrinth.com/plugin/grimac"))
                    )
                    .build()
            }
            updateMessage.set(msg)
            MessageUtil.sendMessage(sender, msg)
        } catch (ignored: Exception) {
            MessageUtil.sendMessage(sender, Component.text("Failed to check latest version.").color(NamedTextColor.RED))
            LogUtil.error("Failed to check latest GrimAC version.", ignored)
        }
    }

    private enum class Status {
        AHEAD,
        UPDATED,
        OUTDATED
    }

    private fun compareVersions(local: String, latest: String): Status {
        if (local == latest) return Status.UPDATED
        val localParts = local.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val latestParts = latest.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val length = max(localParts.size.toDouble(), latestParts.size.toDouble()).toInt()
        for (i in 0..<length) {
            val localPart = if (i < localParts.size) localParts[i].toInt() else 0
            val latestPart = if (i < latestParts.size) latestParts[i].toInt() else 0
            if (localPart < latestPart) {
                return Status.OUTDATED
            } else if (localPart > latestPart) {
                return Status.AHEAD
            }
        }
        return Status.UPDATED
    }
}