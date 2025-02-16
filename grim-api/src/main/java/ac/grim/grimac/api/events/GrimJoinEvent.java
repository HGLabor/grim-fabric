package ac.grim.grimac.api.events;

import ac.grim.grimac.api.GrimUser;

public class GrimJoinEvent implements GrimUserEvent {
    private final GrimUser user;

    public GrimJoinEvent(GrimUser user) {
        this.user = user;
    }

    @Override
    public GrimUser getUser() {
        return user;
    }
}