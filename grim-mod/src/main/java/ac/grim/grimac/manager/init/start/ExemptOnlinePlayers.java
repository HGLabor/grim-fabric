package ac.grim.grimac.manager.init.start;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.manager.init.Initable;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import net.minecraft.server.network.ServerPlayerEntity;


public class ExemptOnlinePlayers implements Initable {
    @Override
    public void start() {
        for (ServerPlayerEntity player : GrimAPI.INSTANCE.getServer().getPlayerManager().getPlayerList()) {
            User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
            GrimAPI.INSTANCE.getPlayerDataManager().exemptUsers.add(user);
        }
    }
}
