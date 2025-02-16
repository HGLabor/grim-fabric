package ac.grim.grimac.manager.init.load;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.manager.init.Initable;
import ac.grim.grimac.utils.anticheat.LogUtil;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.chat.ChatTypes;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.settings.PacketEventsSettings;
import io.github.retrooper.packetevents.factory.fabric.FabricPacketEventsAPI;
import net.fabricmc.api.EnvType;

import java.util.concurrent.Executors;

public class PacketEventsInit implements Initable {
    @Override
    public void start() {
        LogUtil.info("Loading PacketEvents...");
        PacketEvents.setAPI(new FabricPacketEventsAPI(GrimAPI.INSTANCE.getMOD_ID(), EnvType.SERVER, new PacketEventsSettings()
                .fullStackTrace(true)
                .kickOnPacketException(true)
                //.checkForUpdates(false)
                //.reEncodeByDefault(false)
                .debug(true)
        ));
        PacketEvents.getAPI().load();
        // This may seem useless, but it causes java to start loading stuff async before we need it
        Executors.defaultThreadFactory().newThread(() -> {
            StateTypes.AIR.getName();
            ItemTypes.AIR.getName();
            EntityTypes.PLAYER.getParent();
            EntityDataTypes.BOOLEAN.getName();
            ChatTypes.CHAT.getName();
            EnchantmentTypes.ALL_DAMAGE_PROTECTION.getName();
            ParticleTypes.DUST.getName();
        }).start();
    }
}
