package ac.grim.grimac.manager.init.start;

import ac.grim.grimac.manager.init.Initable;
import ac.grim.grimac.utils.anticheat.LogUtil;
import gg.norisk.verbrecher.commands.*;

public class CommandRegister implements Initable {
    @Override
    public void start() {
        LogUtil.info("Registering commands...");

        GrimAlerts.INSTANCE.init();
        GrimBrands.INSTANCE.init();
        GrimDebug.INSTANCE.init();
        GrimDump.INSTANCE.init();
        GrimHelp.INSTANCE.init();
        GrimLog.INSTANCE.init();
        GrimPerf.INSTANCE.init();
        GrimProfile.INSTANCE.init();
        GrimSpectate.INSTANCE.init();
        GrimStopSpectating.INSTANCE.init();
        GrimVersion.INSTANCE.init();
        GrimReload.INSTANCE.init();
        GrimSendAlert.INSTANCE.init();
        GrimVerbose.INSTANCE.init();
    }
}
