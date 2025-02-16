package ac.grim.grimac.api.events;

import ac.grim.grimac.api.AbstractCheck;
import ac.grim.grimac.api.GrimUser;
import lombok.Getter;
import net.silkmc.silk.core.event.Cancellable;
import net.silkmc.silk.core.event.EventScopeProperty;
import org.jetbrains.annotations.NotNull;

public class FlagEvent implements GrimUserEvent, Cancellable {
    @Getter
    private final GrimUser user;
    @Getter
    private final AbstractCheck check;
    @Getter
    private final String verbose;
    private final EventScopeProperty<Boolean> eventScope = new EventScopeProperty<>(false);

    public FlagEvent(GrimUser user, AbstractCheck check, String verbose) {
        this.user = user;
        this.check = check;
        this.verbose = verbose;
    }

    public double getViolations() {
        return check.getViolations();
    }

    public boolean isSetback() {
        return check.getViolations() > check.getSetbackVL();
    }

    @Override
    public @NotNull EventScopeProperty<Boolean> isCancelled() {
        return eventScope;
    }
}
