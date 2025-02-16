package ac.grim.grimac.manager.init.start;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.manager.init.Initable;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.LogUtil;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

// Copied from: https://github.com/ThomasOM/Pledge/blob/master/src/main/java/dev/thomazz/pledge/inject/ServerInjector.java
@SuppressWarnings(value = {"unchecked", "deprecated"})
public class TickEndEvent implements Initable {

    @Override
    public void start() {
        if (!GrimAPI.INSTANCE.getConfigManager().getConfig().getBooleanElse("Reach.enable-post-packet", false)) {
            return;
        }
        if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThan(ServerVersion.V_1_11_2) && !Boolean.getBoolean("paper.explicit-flush")) {
            LogUtil.warn("Reach.enable-post-packet=true but paper.explicit-flush=false, add \"-Dpaper.explicit-flush=true\" to your server's startup flags for fully functional extra reach accuracy.");
        }
        // if it fails to inject, try to use paper's event
        ServerTickEvents.END_SERVER_TICK.register(minecraftServer -> {
            tickAllPlayers();
        });
    }

    private void onEndOfTick(GrimPlayer player) {
        player.checkManager.getEntityReplication().onEndOfTickEvent();
    }

    private void tickAllPlayers() {
        for (GrimPlayer player : GrimAPI.INSTANCE.getPlayerDataManager().getEntries()) {
            if (player.disableGrim) continue;
            onEndOfTick(player);
        }
    }
}
