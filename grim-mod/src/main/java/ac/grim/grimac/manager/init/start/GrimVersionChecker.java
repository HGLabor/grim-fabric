package ac.grim.grimac.manager.init.start;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.manager.init.Initable;
import gg.norisk.verbrecher.commands.GrimVersion;

public class GrimVersionChecker implements Initable {
    @Override
    public void start() {
        if (GrimAPI.INSTANCE.getConfigManager().getConfig().getBooleanElse("check-for-updates", true)) {
            GrimVersion.INSTANCE.checkForUpdatesAsync(GrimAPI.INSTANCE.getServer().getCommandSource());
        }
    }
}
