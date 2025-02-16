package ac.grim.grimac.utils.anticheat;

import ac.grim.grimac.GrimAPI;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.minecraft.text.Text;
import org.slf4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

@UtilityClass
public class LogUtil {
    public static void info(final String info) {
        getLogger().info(info);
    }

    public static void warn(final String warn) {
        getLogger().warn(warn);
    }

    public static void error(final String error) {
        getLogger().error(error);
    }

    public static void error(final String description, final Throwable throwable) {
        getLogger().error(description);
        throwable.printStackTrace();
    }

    public Logger getLogger() {
        return GrimAPI.INSTANCE.getLogger();
    }

    public void console(final String info) {
        GrimAPI.INSTANCE.getServer().sendMessage(Text.of(info));
    }

    public static void console(final Component info) {
        MessageUtil.sendMessage(GrimAPI.INSTANCE.getServer().getCommandSource(), info);
    }

    public static void exception(String description, Throwable throwable) {
        getLogger().error("{}: {}", description, getStackTrace(throwable));
    }

    private static String getStackTrace(Throwable throwable) {
        String message = throwable.getMessage();
        try (StringWriter sw = new StringWriter()) {
            try (PrintWriter pw = new PrintWriter(sw)) {
                throwable.printStackTrace(pw);
                message = sw.toString();
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return message;
    }

}
