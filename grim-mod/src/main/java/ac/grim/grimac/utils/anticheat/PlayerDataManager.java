package ac.grim.grimac.utils.anticheat;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.api.events.GrimJoinEvent;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.netty.channel.ChannelHelper;
import com.github.retrooper.packetevents.protocol.player.User;
import gg.norisk.verbrecher.utils.AntiCheatEvents;
import gg.norisk.verbrecher.utils.LuckPermsUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {
    private final ConcurrentHashMap<User, GrimPlayer> playerDataMap = new ConcurrentHashMap<>();
    public final Collection<User> exemptUsers = Collections.synchronizedCollection(new HashSet<>());

    public GrimPlayer getPlayer(final ServerPlayerEntity player) {
        if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_18) && MultiLibUtil.isExternalPlayer(player))
            return null;

        // Is it safe to interact with this, or is this internal PacketEvents code?
        User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
        return playerDataMap.get(user);
    }

    public boolean shouldCheck(User user) {
        if (exemptUsers.contains(user)) return false;
        if (!ChannelHelper.isOpen(user.getChannel())) return false;

        if (user.getUUID() != null) {
            // Geyser players don't have Java movement
            // Floodgate is the authentication system for Geyser on servers that use Geyser as a proxy instead of installing it as a plugin directly on the server
            /*if (GeyserUtil.isGeyserPlayer(user.getUUID()) || FloodgateUtil.isFloodgatePlayer(user.getUUID())) {
                exemptUsers.add(user);
                return false;
            }*/

            // Has exempt permission
            ServerPlayerEntity player = GrimAPI.getPlayer(user.getUUID());
            if (player != null && LuckPermsUtils.INSTANCE.hasPermission(player, "grim.exempt")) {
                LogUtil.info("User " + user.getName() + " has grim.exempt permissions");
                exemptUsers.add(user);
                return false;
            }

            // Geyser formatted player string
            // This will never happen for Java players, as the first character in the 3rd group is always 4 (xxxxxxxx-xxxx-4xxx-xxxx-xxxxxxxxxxxx)
            if (user.getUUID().toString().startsWith("00000000-0000-0000-0009")) {
                exemptUsers.add(user);
                return false;
            }
        }

        return true;
    }

    @Nullable
    public GrimPlayer getPlayer(final User user) {
        return playerDataMap.get(user);
    }

    public void addUser(final User user) {
        var shouldCheck = shouldCheck(user);
        LogUtil.info("Adding user " + user.getName() + " to anticheat: " + shouldCheck);
        if (shouldCheck) {
            GrimPlayer player = new GrimPlayer(user);
            playerDataMap.put(user, player);
            AntiCheatEvents.INSTANCE.getGrimJoinEvent().invoke(new GrimJoinEvent(player));
        }
    }

    public GrimPlayer remove(final User user) {
        LogUtil.info("Removing user " + user.getUUID() + " from anticheat");
        return playerDataMap.remove(user);
    }

    public Collection<GrimPlayer> getEntries() {
        return playerDataMap.values();
    }

    public int size() {
        return playerDataMap.size();
    }
}
