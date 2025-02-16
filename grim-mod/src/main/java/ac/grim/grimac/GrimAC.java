package ac.grim.grimac;

import ac.grim.grimac.manager.init.start.CommandRegister;
import gg.norisk.verbrecher.commands.GrimAlerts;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public final class GrimAC implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        new CommandRegister().start();
        /*ServerEvents.INSTANCE.getPreStart().listen(EventPriority.NORMAL, true, (mutableEventScope, serverEvent) -> {
            GrimAPI.INSTANCE.load(serverEvent.getServer());
            return Unit.INSTANCE;
        });
        ServerEvents.INSTANCE.getPostStart().listen(EventPriority.NORMAL, true, (mutableEventScope, serverEvent) -> {
            GrimAPI.INSTANCE.start(serverEvent.getServer());
            return Unit.INSTANCE;
        });
        ServerEvents.INSTANCE.getPostStop().listen(EventPriority.NORMAL, true, (mutableEventScope, serverEvent) -> {
            GrimAPI.INSTANCE.load(serverEvent.getServer());
            return Unit.INSTANCE;
        });*/
        ServerLifecycleEvents.SERVER_STARTING.register(GrimAPI.INSTANCE::load);
        ServerLifecycleEvents.SERVER_STOPPING.register(GrimAPI.INSTANCE::stop);
        ServerLifecycleEvents.SERVER_STARTED.register(GrimAPI.INSTANCE::start);
    }
}
