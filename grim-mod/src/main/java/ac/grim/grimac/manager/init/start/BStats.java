package ac.grim.grimac.manager.init.start;

import ac.grim.grimac.manager.init.Initable;

public class BStats implements Initable {
    @Override
    public void start() {
        int pluginId = 12820; // <-- Replace with the id of your plugin!
        try {
            //new Metrics(GrimAPI.INSTANCE.getServer(), pluginId);
        } catch (Exception ignored) {
        }
    }
}
