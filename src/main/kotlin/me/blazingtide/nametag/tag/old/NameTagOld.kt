package me.blazingtide.nametag.tag.old

import me.blazingtide.nametag.NametagAPI
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team
import java.util.*
import kotlin.math.min


class NameTagOld(private val player: Player, private val API: NametagAPI) {

    companion object {
        const val PREFIX = "NAME_TAG-"
    }

    private val cachedTeams = HashMap<String, Team>()

    init {
        println("[Nametag API] Created nametag for ${player.name}")
    }

    fun attemptUpdateAll() {
        val scoreboard = getScoreboard()

        for (target in player.getNearbyEntities(30.0, 40.0, 30.0).filterIsInstance<Player>()) {
            updateFor(target, scoreboard)
        }
        println("Updated self scoreboard")
        updateFor(player, scoreboard)

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

        if (checkRemoveTeam(target, prefixTranslated, suffixTranslated)) {
            val team = createTeam(scoreboard, prefixTranslated, suffixTranslated)

            cachedTeams[target.name] = team

            if (!team.hasEntry(target.name)) {
                team.addEntry(target.name)
            }
        }
    }

    /**
     * Returns false if no team needs to be created or returns false if not.
     * If a team needs to be created then it will also remove this entry off all other teams.
     */
    private fun checkRemoveTeam(target: Player, prefix: String?, suffix: String?): Boolean {
        val team = cachedTeams[target.name]

        if (team != null) {
            if (team.prefix == prefix && team.suffix == suffix) {
                return false
            }

            team.removeEntry(target.name)
            cachedTeams.remove(target.name)

            if (team.entries.isEmpty()) {
                team.unregister()
            }
        }

        return true
    }

    private fun getTeamId(board: Scoreboard): String {
        val uuidString = UUID.randomUUID().toString()
        val id = (PREFIX + uuidString).substring(0, 16)

        if (board.getTeam(id) != null) {
            return getTeamId(board)
        }

        return id
    }

    private fun createTeam(board: Scoreboard, prefix: String?, suffix: String?): Team {
        for (team in cachedTeams.values) {
            if (team.prefix == prefix && team.suffix == suffix) {
                if (!team.name.startsWith(PREFIX)) {
                    continue
                }
                return team
            }
        }

        val team = board.registerNewTeam(getTeamId(board))
        if (API.sizeConstraints && prefix != null) {
            team.prefix = prefix.substring(0, min(16, prefix.length))
        }

        if (suffix != null && API.sizeConstraints) {
            team.suffix = suffix.substring(0, min(16, suffix.length))
        }

        return team
    }

    private fun getScoreboard(): Scoreboard {
        return if (player.scoreboard !== Bukkit.getScoreboardManager().mainScoreboard) player.scoreboard else Bukkit.getScoreboardManager().newScoreboard
    }
}