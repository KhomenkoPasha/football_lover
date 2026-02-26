package khom.pavlo.footballlover.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_queries")
data class SearchQueryEntity(
    @PrimaryKey val query: String,
    val teamIdsCsv: String,
    val updatedAt: Long
)
