package gg.norisk.verbrecher.commands

import ac.grim.grimac.GrimAPI
import ac.grim.grimac.manager.init.start.SuperDebug
import ac.grim.grimac.utils.anticheat.LogUtil
import ac.grim.grimac.utils.anticheat.MessageUtil
import gg.norisk.verbrecher.utils.LuckPermsUtils.hasPermissionOrOp
import gg.norisk.verbrecher.utils.SchedulerUtils
import net.minecraft.server.command.ServerCommandSource
import net.silkmc.silk.commands.command
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.function.Consumer

object GrimLog {
    fun init() {
        command("grim") {
            literal("log") {
                requires { it.player?.hasPermissionOrOp("grim.log") == true }
                argument<Int>("flagId") { flagId ->
                    runs {
                        val builder = SuperDebug.getFlag(flagId())
                        if (builder == null) {
                            var failure = GrimAPI.INSTANCE.configManager.config
                                .getStringElse("upload-log-not-found", "%prefix% &cUnable to find that log")
                            failure = MessageUtil.replacePlaceholders(this.source, failure!!)
                            MessageUtil.sendMessage(this.source, MessageUtil.miniMessage(failure))
                            return@runs
                        }
                        sendLogAsync(
                            this.source, builder.toString(),
                            { }, "text/yaml"
                        )
                    }
                }
            }
        }
    }

    fun sendLogAsync(sender: ServerCommandSource, log: String, consumer: Consumer<String>, type: String) {
        val success = GrimAPI.INSTANCE.configManager.config
            .getStringElse("upload-log", "%prefix% &fUploaded debug to: %url%")
        val failure = GrimAPI.INSTANCE.configManager.config.getStringElse(
            "upload-log-upload-failure",
            "%prefix% &cSomething went wrong while uploading this log, see console for more information."
        )
        var uploading = GrimAPI.INSTANCE.configManager.config
            .getStringElse("upload-log-start", "%prefix% &fUploading log... please wait")
        uploading = MessageUtil.replacePlaceholders(sender, uploading!!)
        MessageUtil.sendMessage(sender, MessageUtil.miniMessage(uploading))
        SchedulerUtils.async({
            try {
                sendLog(sender, log, success, failure, consumer, type)
            } catch (e: Exception) {
                val message = MessageUtil.replacePlaceholders(sender, failure)
                MessageUtil.sendMessage(sender, MessageUtil.miniMessage(message))
                e.printStackTrace()
            }
        })
    }

    @Throws(IOException::class)
    private fun sendLog(
        sender: ServerCommandSource,
        log: String,
        success: String,
        failure: String,
        consumer: Consumer<String>,
        type: String
    ) {
        val mUrl = URL("https://paste.grim.ac/data/post")
        val urlConn = mUrl.openConnection() as HttpURLConnection
        try {
            urlConn.doOutput = true
            urlConn.requestMethod = "POST"
            urlConn.addRequestProperty("User-Agent", "GrimAC/" + GrimAPI.INSTANCE.externalAPI.grimVersion)
            urlConn.addRequestProperty("Content-Type", type) // Not really yaml, but looks nicer than plaintext
            urlConn.setRequestProperty("Content-Length", log.length.toString())
            urlConn.outputStream.use { stream ->
                stream.write(log.toByteArray(StandardCharsets.UTF_8))
            }
            val response = urlConn.responseCode
            if (response == HttpURLConnection.HTTP_CREATED) {
                val responseURL = urlConn.getHeaderField("Location")
                var message = success.replace("%url%", "https://paste.grim.ac/$responseURL")
                consumer.accept(message)
                message = MessageUtil.replacePlaceholders(sender, message)
                MessageUtil.sendMessage(sender, MessageUtil.miniMessage(message))
            } else {
                val message = MessageUtil.replacePlaceholders(sender, failure)
                MessageUtil.sendMessage(sender, MessageUtil.miniMessage(message))
                LogUtil.error("Returned response code " + response + ": " + urlConn.responseMessage)
            }
        } finally {
            urlConn.disconnect()
        }
    }
}