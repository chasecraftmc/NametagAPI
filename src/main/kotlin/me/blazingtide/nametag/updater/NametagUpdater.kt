package me.blazingtide.nametag.updater

import me.blazingtide.nametag.NametagAPI
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

class NametagUpdater(private val api: NametagAPI) : BukkitRunnable() {

    override fun run() {
        if (!api.shouldUpdate) {
            return
        }

        for (player in Bukkit.getOnlinePlayers()) {
            if (!api.instances.containsKey(player.uniqueId)) {
                continue
            }

            val instance = api.instances[player.uniqueId];

            instance?.attemptUpdateAll()
        }
    }

}