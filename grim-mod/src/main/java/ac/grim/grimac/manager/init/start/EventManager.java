package ac.grim.grimac.manager.init.start;

import ac.grim.grimac.manager.init.Initable;
import ac.grim.grimac.utils.anticheat.LogUtil;

public class EventManager implements Initable {
    public void start() {
        LogUtil.info("Registering singular bukkit event... (PistonEvent)");

        //TODO Bukkit.getPluginManager().registerEvents(new PistonEvent(), GrimAPI.INSTANCE.getServer());
    }
}
