package ac.grim.grimac.api.events;

import lombok.Getter;

public class GrimReloadEvent {
    @Getter
    private final boolean success;

    public GrimReloadEvent(boolean success) {
        this.success = success;
    }
}
