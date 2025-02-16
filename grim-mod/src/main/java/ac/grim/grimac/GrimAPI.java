package ac.grim.grimac;

import ac.grim.grimac.manager.*;
import ac.grim.grimac.manager.config.BaseConfigManager;
import ac.grim.grimac.utils.anticheat.PlayerDataManager;
import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.UUID;

@Getter
public enum GrimAPI {
    INSTANCE;

    public final String MOD_ID = "grim";
    public final Logger logger = LoggerFactory.getLogger(MOD_ID);
    public final BaseConfigManager configManager = new BaseConfigManager();
    public final AlertManagerImpl alertManager = new AlertManagerImpl();
    public final SpectateManager spectateManager = new SpectateManager();
    public final DiscordManager discordManager = new DiscordManager();
    public final PlayerDataManager playerDataManager = new PlayerDataManager();
    public final TickManager tickManager = new TickManager();
    public final GrimExternalAPI externalAPI = new GrimExternalAPI(this);
    public InitManager initManager;
    public MinecraftServer server;

    public void load(final MinecraftServer plugin) {
        this.server = plugin;
        initManager = new InitManager();
        initManager.load();
    }

    public void start(final MinecraftServer plugin) {
        this.server = plugin;
        initManager.start();
    }

    public void stop(final MinecraftServer plugin) {
        this.server = plugin;
        initManager.stop();
    }

    public static ServerPlayerEntity getPlayer(UUID uuid) {
        return GrimAPI.INSTANCE.getServer().getPlayerManager().getPlayer(uuid);
    }

    public File getDataFolder() {
        return FabricLoader.getInstance().getConfigDir().resolve("grim").toFile();
    }
}
