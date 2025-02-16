package ac.grim.grimac.manager;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.api.alerts.AlertManager;
import ac.grim.grimac.utils.anticheat.MessageUtil;
import gg.norisk.verbrecher.utils.LuckPermsUtils;
import lombok.Getter;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Getter
public class AlertManagerImpl implements AlertManager {
    public final Set<ServerPlayerEntity> enabledAlerts = new CopyOnWriteArraySet<>(new HashSet<>());
    public final Set<ServerPlayerEntity> enabledVerbose = new CopyOnWriteArraySet<>(new HashSet<>());
    public final Set<ServerPlayerEntity> enabledBrands = new CopyOnWriteArraySet<>(new HashSet<>());

    @Override
    public boolean hasAlertsEnabled(ServerPlayerEntity player) {
        return enabledAlerts.contains(player);
    }

    @Override
    public void toggleAlerts(ServerPlayerEntity player) {
        if (!enabledAlerts.remove(player)) {
            String alertString = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("alerts-enabled", "%prefix% &fAlerts enabled");
            alertString = MessageUtil.replacePlaceholders(player, alertString);
            MessageUtil.sendMessage(player.getCommandSource(), MessageUtil.miniMessage(alertString));
            enabledAlerts.add(player);
        } else {
            String alertString = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("alerts-disabled", "%prefix% &fAlerts disabled");
            alertString = MessageUtil.replacePlaceholders(player, alertString);
            MessageUtil.sendMessage(player.getCommandSource(), MessageUtil.miniMessage(alertString));
        }
    }

    @Override
    public boolean hasVerboseEnabled(ServerPlayerEntity player) {
        return enabledVerbose.contains(player);
    }

    public boolean hasBrandsEnabled(ServerPlayerEntity player) {
        return enabledBrands.contains(player) && LuckPermsUtils.INSTANCE.hasPermission(player, "grim.brand");
    }

    @Override
    public void toggleVerbose(ServerPlayerEntity player) {
        if (!enabledVerbose.remove(player)) {
            String alertString = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("verbose-enabled", "%prefix% &fVerbose enabled");
            alertString = MessageUtil.replacePlaceholders(player, alertString);
            MessageUtil.sendMessage(player.getCommandSource(), MessageUtil.miniMessage(alertString));
            enabledVerbose.add(player);
        } else {
            String alertString = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("verbose-disabled", "%prefix% &fVerbose disabled");
            alertString = MessageUtil.replacePlaceholders(player, alertString);
            MessageUtil.sendMessage(player.getCommandSource(), MessageUtil.miniMessage(alertString));
        }
    }

    public void toggleBrands(ServerPlayerEntity player) {
        if (!enabledBrands.remove(player)) {
            String alertString = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("brands-enabled", "%prefix% &fBrands enabled");
            alertString = MessageUtil.replacePlaceholders(player, alertString);
            MessageUtil.sendMessage(player.getCommandSource(), MessageUtil.miniMessage(alertString));
            enabledBrands.add(player);
        } else {
            String alertString = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("brands-disabled", "%prefix% &fBrands disabled");
            alertString = MessageUtil.replacePlaceholders(player, alertString);
            MessageUtil.sendMessage(player.getCommandSource(), MessageUtil.miniMessage(alertString));
        }
    }

    public void handlePlayerQuit(ServerPlayerEntity player) {
        enabledAlerts.remove(player);
        enabledVerbose.remove(player);
        enabledBrands.remove(player);
    }
}
