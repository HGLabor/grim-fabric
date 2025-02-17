package gg.norisk.verbrecher.utils

import ac.grim.grimac.api.events.*
import net.silkmc.silk.core.event.Event

object AntiCheatEvents {
    val flagEvent = Event.syncAsync<FlagEvent>(false)
    val grimReloadEvent = Event.syncAsync<GrimReloadEvent>(false)
    val grimJoinEvent = Event.syncAsync<GrimJoinEvent>(false)
    val grimQuitEvent = Event.syncAsync<GrimQuitEvent>(false)
    val commandExecuteEvent = Event.syncAsync<CommandExecuteEvent>(false)
    val completePredictionEvent = Event.syncAsync<CompletePredictionEvent>(false)
}
