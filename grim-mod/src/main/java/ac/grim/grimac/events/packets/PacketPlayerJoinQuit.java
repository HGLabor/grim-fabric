package ac.grim.grimac.events.packets;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.api.events.GrimQuitEvent;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.LogUtil;
import com.github.retrooper.packetevents.event.*;
import com.github.retrooper.packetevents.netty.channel.ChannelHelper;
import com.github.retrooper.packetevents.protocol.ConnectionState;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import gg.norisk.verbrecher.utils.AntiCheatEvents;
import gg.norisk.verbrecher.utils.LuckPermsUtils;
import net.minecraft.server.network.ServerPlayerEntity;


public class PacketPlayerJoinQuit extends PacketListenerAbstract {

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Login.Server.LOGIN_SUCCESS) {
            // Do this after send to avoid sending packets before the PLAY state
            event.getPostTasks().add(() -> {
                GrimAPI.INSTANCE.getPlayerDataManager().addUser(event.getUser());
            });
        }
    }

    @Override
    public void onUserConnect(UserConnectEvent event) {
        // ServerPlayerEntity connected too soon, perhaps late bind is off
        // Don't kick everyone on reload
        if (event.getUser().getConnectionState() == ConnectionState.PLAY && !GrimAPI.INSTANCE.getPlayerDataManager().exemptUsers.contains(event.getUser())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onUserLogin(UserLoginEvent event) {
        ServerPlayerEntity player = event.getPlayer();
        if (GrimAPI.INSTANCE.getConfigManager().getConfig().getBooleanElse("debug-pipeline-on-join", false)) {
            LogUtil.info("Pipeline: " + ChannelHelper.pipelineHandlerNamesAsString(event.getUser().getChannel()));
        }
        if (LuckPermsUtils.INSTANCE.hasPermission(player, "grim.alerts") && LuckPermsUtils.INSTANCE.hasPermission(player, "grim.alerts.enable-on-join")) {
            GrimAPI.INSTANCE.getAlertManager().toggleAlerts(player);
        }
        if (LuckPermsUtils.INSTANCE.hasPermission(player, "grim.verbose") && LuckPermsUtils.INSTANCE.hasPermission(player, "grim.verbose.enable-on-join")) {
            GrimAPI.INSTANCE.getAlertManager().toggleVerbose(player);
        }
        if (LuckPermsUtils.INSTANCE.hasPermission(player, "grim.brand") && LuckPermsUtils.INSTANCE.hasPermission(player, "grim.brand.enable-on-join")) {
            GrimAPI.INSTANCE.getAlertManager().toggleBrands(player);
        }
        if (LuckPermsUtils.INSTANCE.hasPermission(player, "grim.spectate") && GrimAPI.INSTANCE.getConfigManager().getConfig().getBooleanElse("spectators.hide-regardless", false)) {
            GrimAPI.INSTANCE.getSpectateManager().onLogin(player);
        }
    }

    @Override
    public void onUserDisconnect(UserDisconnectEvent event) {
        LogUtil.info("Disconnected: " + event.getUser().getName());
        GrimPlayer grimPlayer = GrimAPI.INSTANCE.getPlayerDataManager().remove(event.getUser());
        if (grimPlayer != null) AntiCheatEvents.INSTANCE.getGrimQuitEvent().invoke(new GrimQuitEvent(grimPlayer));
        GrimAPI.INSTANCE.getPlayerDataManager().exemptUsers.remove(event.getUser());
        //Check if calling async is safe
        if (event.getUser().getProfile().getUUID() == null) return; // folia doesn't like null getPlayer()
        ServerPlayerEntity player = GrimAPI.getPlayer(event.getUser().getProfile().getUUID());
        if (player != null) {
            GrimAPI.INSTANCE.getAlertManager().handlePlayerQuit(player);
            GrimAPI.INSTANCE.getSpectateManager().onQuit(player);
        }
    }
}
