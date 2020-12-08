package me.blazingtide.nametag.listener

import me.blazingtide.nametag.NametagAPI
import me.blazingtide.nametag.tag.NameTag
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class NameTagListener(val api: NametagAPI) : Listener {

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player

        api.instances.remove(player.uniqueId)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val nameTag = NameTag(event.player, api)
        api.instances[event.player.uniqueId] = nameTag

        nameTag.attemptUpdateAll()
    }

}