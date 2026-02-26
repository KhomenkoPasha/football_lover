package khom.pavlo.footballlover.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "standings")
data class StandingEntity(
    @PrimaryKey val id: String,
    val leagueId: String,
    val season: String,
    val rank: String?,
    val teamName: String,
    val played: String?,
    val win: String?,
    val draw: String?,
    val loss: String?,
    val goalsFor: String?,
    val goalsAgainst: String?,
    val goalsDifference: String?,
    val points: String?,
    val updatedAt: Long
)
