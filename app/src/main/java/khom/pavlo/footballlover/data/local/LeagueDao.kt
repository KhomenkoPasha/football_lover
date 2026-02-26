package khom.pavlo.footballlover.data.local

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

interface LeagueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLeagues(leagues: List<LeagueEntity>)

    @Query("SELECT * FROM leagues WHERE sport = :sport")
    suspend fun getLeaguesBySport(sport: String): List<LeagueEntity>
}
