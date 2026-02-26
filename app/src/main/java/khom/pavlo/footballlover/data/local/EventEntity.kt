package khom.pavlo.footballlover.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val id: String,
    val teamId: String,
    val type: String,
    val name: String,
    val date: String?,
    val time: String?,
    val homeTeam: String?,
    val awayTeam: String?,
    val homeTeamBadgeUrl: String?,
    val awayTeamBadgeUrl: String?,
    val homeScore: String?,
    val awayScore: String?,
    val thumbUrl: String?,
    val updatedAt: Long
)
