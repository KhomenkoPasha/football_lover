package khom.pavlo.footballlover.data.local

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

interface StandingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStandings(rows: List<StandingEntity>)

    @Query("SELECT * FROM standings WHERE leagueId = :leagueId AND season = :season")
    suspend fun getStandings(leagueId: String, season: String): List<StandingEntity>
}
