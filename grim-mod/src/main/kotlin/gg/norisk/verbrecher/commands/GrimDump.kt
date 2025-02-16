package gg.norisk.verbrecher.commands

import ac.grim.grimac.GrimAPI
import ac.grim.grimac.utils.anticheat.MessageUtil
import com.github.retrooper.packetevents.PacketEvents
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import gg.norisk.verbrecher.utils.LuckPermsUtils.hasPermissionOrOp
import io.github.retrooper.packetevents.util.viaversion.ViaVersionUtil
import net.silkmc.silk.commands.command

object GrimDump {
    private var link: String? = null // these links should not expire for a while
    fun init() {
        command("grim") {
            literal("dump") {
                requires { it.player?.hasPermissionOrOp("grim.dump") == true }
                runs {
                    if (link != null) {
                        MessageUtil.sendMessage(
                            this.source, MessageUtil.miniMessage(
                                GrimAPI.INSTANCE.configManager.config
                                    .getStringElse("upload-log", "%prefix% &fUploaded debug to: %url%")
                                    .replace("%url%", link!!)
                            )
                        )
                        return@runs
                    }
                    // TODO: change this back to application/json once allowed
                    GrimLog.sendLogAsync(this.source, generateDump(), { string -> link = string }, "text/yaml")
                }
            }
        }
    }

    // this will help for debugging & replicating issues
    private fun generateDump(): String {
        val base = JsonObject()
        base.addProperty("type", "dump")
        base.addProperty("timestamp", System.currentTimeMillis())
        // versions
        val versions = JsonObject()
        base.add("versions", versions)
        versions.addProperty("grim", GrimAPI.INSTANCE.externalAPI.grimVersion)
        versions.addProperty("packetevents", PacketEvents.getAPI().version.toString())
        versions.addProperty("server", PacketEvents.getAPI().serverManager.version.releaseName)
        versions.addProperty("implementation", PacketEvents.getAPI().serverManager.version.releaseName)
        // properties
        val properties = JsonArray()
        base.add("properties", properties)
        if (PAPER) properties.add("paper")
        if (ViaVersionUtil.isAvailable()) properties.add("viaversion")
        // system
        val system = JsonObject()
        base.add("system", system)
        system.addProperty("os", System.getProperty("os.name"))
        system.addProperty("java", System.getProperty("java.version"))
        // plugins
        val plugins = JsonArray()
        base.add("plugins", plugins)
        /*TODO ?for (plugin in Bukkit.getPluginManager().getPlugins()) {
            val pluginJson = JsonObject()
            pluginJson.addProperty("enabled", plugin.isEnabled())
            pluginJson.addProperty("name", plugin.getName())
            pluginJson.addProperty("version", plugin.getDescription().getVersion())
            plugins.add(pluginJson)
        }*/
        return gson.toJson(base)
    }

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    val PAPER: Boolean =
        hasClass("com.destroystokyo.paper.PaperConfig") || hasClass("io.papermc.paper.configuration.Configuration")

    private fun hasClass(className: String): Boolean {
        try {
            Class.forName(className)
            return true
        } catch (e: ClassNotFoundException) {
            return false
        }
    }
}