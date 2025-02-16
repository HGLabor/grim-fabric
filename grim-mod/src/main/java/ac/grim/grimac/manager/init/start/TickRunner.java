package ac.grim.grimac.manager.init.start;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.manager.init.Initable;
import ac.grim.grimac.utils.anticheat.LogUtil;
import gg.norisk.verbrecher.utils.SchedulerUtils;

public class TickRunner implements Initable {
    @Override
    public void start() {
        LogUtil.info("Registering tick schedulers...");
        SchedulerUtils.INSTANCE.infiniteAsync(() -> GrimAPI.INSTANCE.getTickManager().tickAsync(), 0, 1);
        SchedulerUtils.INSTANCE.infiniteSync(() -> GrimAPI.INSTANCE.getTickManager().tickSync(), 0, 1);
    }
}
