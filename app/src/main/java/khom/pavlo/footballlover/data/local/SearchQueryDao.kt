package khom.pavlo.footballlover.data.local

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

interface SearchQueryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSearchQuery(searchQuery: SearchQueryEntity)

    @Query("SELECT * FROM search_queries WHERE `query` = :queryKey LIMIT 1")
    suspend fun getSearchQuery(queryKey: String): SearchQueryEntity?
}
