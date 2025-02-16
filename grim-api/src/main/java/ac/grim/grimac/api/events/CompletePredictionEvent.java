package ac.grim.grimac.api.events;

import ac.grim.grimac.api.AbstractCheck;
import ac.grim.grimac.api.GrimUser;
import lombok.Getter;

public class CompletePredictionEvent extends FlagEvent {
    @Getter
    private final double offset;

    public CompletePredictionEvent(GrimUser player, AbstractCheck check, String verbose, double offset) {
        super(player, check, verbose);
        this.offset = offset;
    }
}
