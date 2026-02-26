package khom.pavlo.footballlover.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teams")
data class TeamEntity(
    @PrimaryKey val id: String,
    val name: String,
    val league: String?,
    val badgeUrl: String?,
    val stadium: String?,
    val country: String?
)
