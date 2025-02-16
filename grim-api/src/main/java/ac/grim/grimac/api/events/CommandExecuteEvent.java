package ac.grim.grimac.api.events;

import ac.grim.grimac.api.AbstractCheck;
import ac.grim.grimac.api.GrimUser;
import lombok.Getter;

public class CommandExecuteEvent extends FlagEvent {
    @Getter
    private final String command;

    public CommandExecuteEvent(GrimUser player, AbstractCheck check, String verbose, String command) {
        super(player, check, verbose); // Async!
        this.command = command;
    }
}
