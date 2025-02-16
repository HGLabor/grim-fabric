package ac.grim.grimac;

import ac.grim.grimac.api.GrimAbstractAPI;
import ac.grim.grimac.api.GrimUser;
import ac.grim.grimac.api.alerts.AlertManager;
import ac.grim.grimac.api.config.ConfigManager;
import ac.grim.grimac.api.events.GrimReloadEvent;
import ac.grim.grimac.manager.config.ConfigManagerFileImpl;
import ac.grim.grimac.manager.init.Initable;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.LogUtil;
import ac.grim.grimac.utils.common.ConfigReloadObserver;
import com.github.retrooper.packetevents.netty.channel.ChannelHelper;
import gg.norisk.verbrecher.utils.AntiCheatEvents;
import gg.norisk.verbrecher.utils.SchedulerUtils;
import lombok.Getter;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow;
import net.minecraft.server.network.ServerPlayerEntity;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

//This is used for grim's external API. It has its own class just for organization.

public class GrimExternalAPI implements GrimAbstractAPI, ConfigReloadObserver, Initable {

    private final GrimAPI api;

    public GrimExternalAPI(GrimAPI api) {
        this.api = api;
    }

    @Nullable
    @Override
    public GrimUser getGrimUser(ServerPlayerEntity player) {
        return api.getPlayerDataManager().getPlayer(player);
    }

    @Override
    public @Nullable GrimUser getGrimUser(UUID uuid) {
        ServerPlayerEntity player = GrimAPI.getPlayer(uuid);
        if (player == null) return null;
        return getGrimUser(player);
    }

    @Getter
    private final Map<String, Function<GrimUser, String>> variableReplacements = new ConcurrentHashMap<>();

    @Getter
    private final Map<String, String> staticReplacements = new ConcurrentHashMap<>();

    public String replaceVariables(GrimUser user, String content) {
        for (Map.Entry<String, String> entry : staticReplacements.entrySet()) {
            content = content.replace(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Function<GrimUser, String>> entry : variableReplacements.entrySet()) {
            content = content.replace(entry.getKey(), entry.getValue().apply(user));
        }
        return content;
    }

    @Override
    public void registerVariable(String string, Function<GrimUser, String> replacement) {
        if (replacement == null) {
            variableReplacements.remove(string);
        } else {
            variableReplacements.put(string, replacement);
        }
    }

    @Override
    public void registerVariable(String variable, String replacement) {
        if (replacement == null) {
            staticReplacements.remove(variable);
        } else {
            staticReplacements.put(variable, replacement);
        }
    }

    @Override
    public String getGrimVersion() {
        return "2.3.71";
    }

    private final Map<String, Function<Object, Object>> functions = new ConcurrentHashMap<>();

    @Override
    public void registerFunction(String key, Function<Object, Object> function) {
        if (function == null) {
            functions.remove(key);
        } else {
            functions.put(key, function);
        }
    }

    @Override
    public Function<Object, Object> getFunction(String key) {
        return functions.get(key);
    }

    @Override
    public AlertManager getAlertManager() {
        return GrimAPI.INSTANCE.getAlertManager();
    }

    @Override
    public ConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public boolean hasStarted() {
        return started;
    }

    @Override
    public int getCurrentTick() {
        return GrimAPI.INSTANCE.getTickManager().currentTick;
    }

    private ConfigManager configManager = null;
    private final ConfigManagerFileImpl configManagerFile = new ConfigManagerFileImpl();
    private boolean started = false;

    // on load, load the config & register the service
    public void load() {
        reload(configManagerFile);
        //TODO ? Bukkit.getServicesManager().register(GrimAbstractAPI.class, this, GrimAPI.INSTANCE.getServer(), ServicePriority.Normal);
    }

    // handles any config loading that's needed to be done after load
    @Override
    public void start() {
        started = true;
        try {
            GrimAPI.INSTANCE.getConfigManager().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reload(ConfigManager config) {
        if (config.isLoadedAsync() && started) {
            SchedulerUtils.INSTANCE.async(() -> successfulReload(config));
        } else {
            successfulReload(config);
        }
    }

    @Override
    public CompletableFuture<Boolean> reloadAsync(ConfigManager config) {
        if (config.isLoadedAsync() && started) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            SchedulerUtils.INSTANCE.async(() -> future.complete(successfulReload(config)));
            return future;
        }
        return CompletableFuture.completedFuture(successfulReload(config));
    }

    private boolean successfulReload(ConfigManager config) {
        try {
            config.reload();
            GrimAPI.INSTANCE.getConfigManager().load(config);
            if (started) GrimAPI.INSTANCE.getConfigManager().start();
            onReload(config);
            if (started)
                SchedulerUtils.INSTANCE.async(() -> AntiCheatEvents.INSTANCE.getGrimReloadEvent().invoke(new GrimReloadEvent(true)));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (started)
            SchedulerUtils.INSTANCE.async(() -> AntiCheatEvents.INSTANCE.getGrimReloadEvent().invoke(new GrimReloadEvent(false)));
        return false;
    }

    @Override
    public void onReload(ConfigManager newConfig) {
        if (newConfig == null) {
            LogUtil.warn("ConfigManager not set. Using default config file manager.");
            configManager = configManagerFile;
        } else {
            configManager = newConfig;
        }
        // Update variables
        updateVariables();
        // Restart
        GrimAPI.INSTANCE.getDiscordManager().start();
        GrimAPI.INSTANCE.getSpectateManager().start();
        // Don't reload players if the plugin hasn't started yet
        if (!started) return;
        // Reload checks for all players
        for (GrimPlayer grimPlayer : GrimAPI.INSTANCE.getPlayerDataManager().getEntries()) {
            ChannelHelper.runInEventLoop(grimPlayer.user.getChannel(), () -> {
                grimPlayer.updatePermissions();
                grimPlayer.reload(configManager);
            });
        }
    }

    private void updateVariables() {
        variableReplacements.putIfAbsent("%player%", GrimUser::getName);
        variableReplacements.putIfAbsent("%uuid%", user -> user.getUniqueId().toString());
        variableReplacements.putIfAbsent("%ping%", user -> user.getTransactionPing() + "");
        variableReplacements.putIfAbsent("%brand%", GrimUser::getBrand);
        variableReplacements.putIfAbsent("%h_sensitivity%", user -> ((int) Math.round(user.getHorizontalSensitivity() * 200)) + "");
        variableReplacements.putIfAbsent("%v_sensitivity%", user -> ((int) Math.round(user.getVerticalSensitivity() * 200)) + "");
        variableReplacements.putIfAbsent("%fast_math%", user -> !user.isVanillaMath() + "");
        variableReplacements.putIfAbsent("%tps%", user -> String.format("%.2f", SparkProvider.get().tps().poll(StatisticWindow.TicksPerSecond.SECONDS_10)));
        variableReplacements.putIfAbsent("%version%", GrimUser::getVersionName);
        // static variables
        staticReplacements.put("%prefix%", ChatColor.translateAlternateColorCodes('&', GrimAPI.INSTANCE.getConfigManager().getPrefix()));
        staticReplacements.putIfAbsent("%grim_version%", getGrimVersion());
    }

}
