package me.blazingtide.nametag

import me.blazingtide.nametag.listener.NameTagListener
import me.blazingtide.nametag.tag.NameTag
import me.blazingtide.nametag.tag.NameTagAdapter
import me.blazingtide.nametag.updater.NametagUpdater
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.collections.HashMap

class NametagAPI(plugin: JavaPlugin, val adapter: NameTagAdapter) {

    val instances = HashMap<UUID, NameTag>()

    var shouldUpdate = false

    //Constraints on the sizes so prefixes & suffixes will be maxed to 16 characters
    var sizeConstraints = false

    init {
        NametagUpdater(this).runTaskTimer(plugin, 20L, 20L)

        Bukkit.getPluginManager().registerEvents(NameTagListener(this), plugin)
    }

    fun setAutoUpdating(bool: Boolean) {
        shouldUpdate = bool
    }

    fun update(player: Player) {
        val nameTag = instances[player.uniqueId]
        nameTag!!.attemptUpdateAll()
    }

}