package khom.pavlo.footballlover.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_leagues")
data class FavoriteLeagueEntity(
    @PrimaryKey val leagueId: String,
    val name: String,
    val region: String?,
    val addedAt: Long
)
