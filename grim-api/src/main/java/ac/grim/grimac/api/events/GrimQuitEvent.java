package ac.grim.grimac.api.events;

import ac.grim.grimac.api.GrimUser;

public class GrimQuitEvent implements GrimUserEvent {

    private final GrimUser user;

    public GrimQuitEvent(GrimUser user) {
        this.user = user;
    }

    @Override
    public GrimUser getUser() {
        return user;
    }
}
