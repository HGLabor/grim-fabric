package ac.grim.grimac.manager;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.manager.init.Initable;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SpectateManager implements Initable {

    private final Map<UUID, PreviousState> spectatingPlayers = new ConcurrentHashMap<>();
    private final Set<UUID> hiddenPlayers = ConcurrentHashMap.newKeySet();
    private final Set<String> allowedWorlds = ConcurrentHashMap.newKeySet();

    private boolean checkWorld = false;

    @Override
    public void start() {
        allowedWorlds.clear();
        allowedWorlds.addAll(GrimAPI.INSTANCE.getConfigManager().getConfig().getStringListElse("spectators.allowed-worlds", new ArrayList<>()));
        checkWorld = !(allowedWorlds.isEmpty() || new ArrayList<>(allowedWorlds).get(0).isEmpty());
    }

    public boolean isSpectating(UUID uuid) {
        return spectatingPlayers.containsKey(uuid);
    }

    public boolean shouldHidePlayer(GrimPlayer receiver, WrapperPlayServerPlayerInfo.PlayerData playerData) {
        return playerData.getUser() != null
                && playerData.getUser().getUUID() != null
                && shouldHidePlayer(receiver, playerData.getUser().getUUID());
    }

    public boolean shouldHidePlayer(GrimPlayer receiver, UUID uuid) {
        return !Objects.equals(uuid, receiver.uuid) // don't hide to yourself
                && (spectatingPlayers.containsKey(uuid) || hiddenPlayers.contains(uuid)) //hide if you are a spectator
                && !(receiver.uuid != null && (spectatingPlayers.containsKey(receiver.uuid) || hiddenPlayers.contains(receiver.uuid))) // don't hide to other spectators
                && (!checkWorld || (receiver.bukkitPlayer != null /*TODO && allowedWorlds.contains(receiver.bukkitPlayer.getWorld().getName())*/)); // hide if you are in a specific world
    }

    public boolean enable(ServerPlayerEntity player) {
        if (spectatingPlayers.containsKey(player.getUuid())) return false;
        //TODO spectatingPlayers.put(player.getUuid(), new PreviousState(player.interactionManager.getGameMode(), player.getLocation()));
        return true;
    }

    public void onLogin(ServerPlayerEntity player) {
        hiddenPlayers.add(player.getUuid());
    }

    public void onQuit(ServerPlayerEntity player) {
        hiddenPlayers.remove(player.getUuid());
        handlePlayerStopSpectating(player.getUuid());
    }

    // only call this synchronously
    public void disable(ServerPlayerEntity player, boolean teleportBack) {
        PreviousState previousState = spectatingPlayers.get(player.getUuid());
        if (previousState != null) {
            /*TODO if (teleportBack && previousState.location.isWorldLoaded()) {
                PaperUtils.teleportAsync(player, previousState.location).thenAccept(bool -> {
                    if (bool) {
                        onDisable(previousState, player);
                    } else {
                        MessageUtil.sendMessage(player, Component.text("Teleport failed, please try again.", NamedTextColor.RED));
                    }
                });
            } else {
                onDisable(previousState, player);
            }*/
        }
    }

    private void onDisable(PreviousState previousState, ServerPlayerEntity player) {
        player.changeGameMode(previousState.gameMode);
        handlePlayerStopSpectating(player.getUuid());
    }

    public void handlePlayerStopSpectating(UUID uuid) {
        spectatingPlayers.remove(uuid);
    }

    private record PreviousState(GameMode gameMode) {
    }
}
