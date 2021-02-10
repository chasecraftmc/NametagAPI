package me.blazingtide.nametag.tag

import me.blazingtide.nametag.NametagAPI
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team
import java.util.*
import kotlin.collections.HashSet
import kotlin.math.min


class NameTag(private val player: Player, private val api: NametagAPI) {

    companion object {
        const val PREFIX = "NAME_TAG-"
    }

    private val cachedTeams = HashMap<UUID, Team>()

    init {
        println("[Nametag api] Created nametag for ${player.name}")
    }

    fun attemptUpdateAll() {
        val scoreboard = getScoreboard()

        for (target in Bukkit.getOnlinePlayers()) {
            updateFor(target, scoreboard)
        }

        val toRemove = HashSet<UUID>()

        //Removes players that aren't online anymore
        cachedTeams.keys.forEach {
            if (Bukkit.getPlayer(it) == null) {
                toRemove.add(it)
            }
        }

        toRemove.forEach {
            val team = cachedTeams[it] ?: return
            team.unregister()
            cachedTeams.remove(it)
        }

        player.scoreboard = scoreboard
    }

    fun updateFor(target: Player, scoreboard: Scoreboard) {
        val prefix = api.adapter.getPrefix(player, target)
        val suffix = api.adapter.getSuffix(player, target)

        val suffixTranslated = if (suffix != null) ChatColor.translateAlternateColorCodes('&', suffix) else null
        val prefixTranslated = if (prefix != null) ChatColor.translateAlternateColorCodes('&', prefix) else null

        if (suffixTranslated == null && prefixTranslated == null) {
            return
        }

        val team = createTeam(scoreboard, target)

        if (api.sizeConstraints && prefixTranslated != null) {
            team.prefix = prefixTranslated.substring(0, min(16, prefixTranslated.length))
        } else if (prefixTranslated != null) {
            team.prefix = prefixTranslated
        }

        val color = api.adapter.getColor(player, target)
        if (color != null) {
            team.color = color
        }

        if (suffixTranslated != null && api.sizeConstraints) {
            team.suffix = suffixTranslated.substring(0, min(16, suffixTranslated.length))
        } else if (suffixTranslated != null) {
            team.suffix = suffixTranslated
        }

        if (!team.hasEntry(target.name)) {
            team.addEntry(target.name)
        }

        if (!cachedTeams.containsKey(target.uniqueId)) {
            cachedTeams[target.uniqueId] = team
        }
    }

    private fun createTeam(board: Scoreboard, player: Player): Team {
        val teamName = api.adapter.getName(player = this.player, target = player)

        if (cachedTeams.containsKey(player.uniqueId)) {
            val team = cachedTeams[player.uniqueId]!!

            if (team.name == teamName) {
                return team
            } else {
                cachedTeams.remove(player.uniqueId)
                team.unregister()
            }
        }
        val team = board.registerNewTeam(teamName)

        api.adapter.transform(player, team)

        return team
    }

    private fun getScoreboard(): Scoreboard {
        return if (player.scoreboard !== Bukkit.getScoreboardManager().mainScoreboard) player.scoreboard else Bukkit.getScoreboardManager().newScoreboard
    }
}
