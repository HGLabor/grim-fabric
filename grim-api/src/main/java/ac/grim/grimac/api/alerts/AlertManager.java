package ac.grim.grimac.api.alerts;

import net.minecraft.server.network.ServerPlayerEntity;

public interface AlertManager {

    /**
     * Checks if the player has alerts enabled.
     * @param player
     * @return boolean
     */
    boolean hasAlertsEnabled(ServerPlayerEntity player);

    /**
     * Toggles alerts for the player.
     * @param player
     */
    void toggleAlerts(ServerPlayerEntity player);

    /**
     * Checks if the player has verbose enabled.
     * @param player
     * @return boolean
     */
    boolean hasVerboseEnabled(ServerPlayerEntity player);

    /**
     * Toggles verbose for the player.
     * @param player
     */
    void toggleVerbose(ServerPlayerEntity player);

}
