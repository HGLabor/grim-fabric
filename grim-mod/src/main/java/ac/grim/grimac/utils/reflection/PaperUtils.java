package ac.grim.grimac.utils.reflection;

public class PaperUtils {

    /*public static final boolean PAPER;
    public static final boolean TICK_END_EVENT;
    public static final boolean ASYNC_TELEPORT;

    static {
        PAPER = ReflectionUtils.hasClass("com.destroystokyo.paper.PaperConfig") || ReflectionUtils.hasClass("io.papermc.paper.configuration.Configuration");
        TICK_END_EVENT = ReflectionUtils.hasClass("com.destroystokyo.paper.event.server.ServerTickEndEvent");
        ASYNC_TELEPORT = ReflectionUtils.hasMethod(Entity.class, "teleportAsync", Location.class);
    }

    public static CompletableFuture<Boolean> teleportAsync(final Entity entity, final Location location) {
        if (PAPER) {
            return entity.teleportAsync(location);
        } else {
            return CompletableFuture.completedFuture(entity.teleport(location));
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean registerTickEndEvent(Listener listener, Runnable runnable) {
        if (TICK_END_EVENT) {
            try {
                Class<?> clazz = ReflectionUtils.getClass("com.destroystokyo.paper.event.server.ServerTickEndEvent");
                if (clazz == null) return false;
                GrimAPI.INSTANCE.getServer().getServer().getPluginManager().registerEvent((Class<? extends Event>) clazz,
                        listener,
                        EventPriority.NORMAL,
                        (l, event) -> runnable.run(), GrimAPI.INSTANCE.getServer());
                return true;
            } catch (Exception e) {
                LogUtil.exception("Failed to register tick end event", e);
            }
        }
        return false;
    }*/

}
