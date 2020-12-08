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


class NameTag(private val player: Player, private val API: NametagAPI) {

    companion object {
        const val PREFIX = "NAME_TAG-"
    }

    private val cachedTeams = HashMap<UUID, Team>()

    init {
        println("[Nametag API] Created nametag for ${player.name}")
    }

    fun attemptUpdateAll() {
        val scoreboard = getScoreboard()

        for (target in player.getNearbyEntities(30.0, 40.0, 30.0).filterIsInstance<Player>()) {
            updateFor(target, scoreboard)
        }
        updateFor(player, scoreboard)

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
        val prefix = API.adapter.getPrefix(player, target)
        val suffix = API.adapter.getSuffix(player, target)

        val suffixTranslated = if (suffix != null) ChatColor.translateAlternateColorCodes('&', suffix) else null
        val prefixTranslated = if (prefix != null) ChatColor.translateAlternateColorCodes('&', prefix) else null

        if (suffixTranslated == null && prefixTranslated == null) {
            return
        }

        val team = createTeam(scoreboard, target, prefixTranslated, suffixTranslated)

        if (API.sizeConstraints && prefixTranslated != null) {
            team.prefix = prefixTranslated.substring(0, min(16, prefixTranslated.length))
        } else if (prefixTranslated != null) {
            team.prefix = prefixTranslated
        }

        if (suffixTranslated != null && API.sizeConstraints) {
            team.suffix = suffixTranslated.substring(0, min(16, suffixTranslated.length))
        } else if (suffixTranslated != null) {
            team.suffix = suffixTranslated
        }

        if (!team.hasEntry(target.name)) {
            team.addEntry(target.name)        }

        if (!cachedTeams.containsKey(target.uniqueId)) {
            cachedTeams[target.uniqueId] = team
        }
    }

    private fun getTeamId(board: Scoreboard, player: Player): String {
        val uuidString = player.uniqueId.toString()
        val id = (PREFIX + uuidString).substring(0, 16)

        //Rarely happens but just incase to prevent players from getting kicked or having incorrect nametags
        if (board.getTeam(id) != null) {
            return getTeamId(board, player)
        }

        return id
    }

    private fun createTeam(board: Scoreboard, player: Player, prefix: String?, suffix: String?): Team {
        if (cachedTeams.containsKey(player.uniqueId)) {
            return cachedTeams[player.uniqueId]!!
        }
        val team = board.registerNewTeam(getTeamId(board, player))

        API.adapter.transform(player, team)

        return team
    }

    private fun getScoreboard(): Scoreboard {
        return if (player.scoreboard !== Bukkit.getScoreboardManager().mainScoreboard) player.scoreboard else Bukkit.getScoreboardManager().newScoreboard
    }
}