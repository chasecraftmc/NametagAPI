package me.blazingtide.nametag.tag

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

}