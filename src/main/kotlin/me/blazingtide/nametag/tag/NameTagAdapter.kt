package me.blazingtide.nametag.tag

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Team

interface NameTagAdapter {

    fun getPrefix(player: Player, target: Player): String?

    fun getSuffix(player: Player, target: Player): String? {
        return null
    }

    /**
     * Called when a team is first created in order to set team
     * metadata
     */
    fun transform(player: Player, team: Team) {

    }

    fun getColor(player: Player, target: Player): ChatColor? {
        return null
    }

    /**
     * Returns the team name, meant for sorting player list name purposes
     *
     * Ensure the returned string is between 0 and 16 in character size
     */
    fun getName(player: Player, target: Player): String {
        return target.uniqueId.toString()
    }

}