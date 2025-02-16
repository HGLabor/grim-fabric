package gg.norisk.verbrecher.utils

import net.silkmc.silk.core.kotlin.ticks
import net.silkmc.silk.core.task.infiniteMcCoroutineTask
import net.silkmc.silk.core.task.mcCoroutineTask

object SchedulerUtils {
    fun async(runnable: Runnable) {
        mcCoroutineTask(sync = false, client = false) {
            runnable.run()
        }
    }

    fun sync(runnable: Runnable) {
        mcCoroutineTask(sync = true, client = false) {
            runnable.run()
        }
    }

    fun runLater(runnable: Runnable, delayTicks: Int) {
        mcCoroutineTask(sync = true, client = false, delay = delayTicks.ticks) {
            runnable.run()
        }
    }

    fun infiniteAsync(runnable: Runnable, delayTicks: Int, periodTicks: Int) {
        infiniteMcCoroutineTask(
            delay = delayTicks.ticks,
            period = periodTicks.ticks,
            sync = false,
            client = false
        ) { runnable.run() }
    }

    fun infiniteSync(runnable: Runnable, delayTicks: Int, periodTicks: Int) {
        infiniteMcCoroutineTask(
            delay = delayTicks.ticks,
            period = periodTicks.ticks,
            sync = true,
            client = false
        ) { runnable.run() }
    }
}