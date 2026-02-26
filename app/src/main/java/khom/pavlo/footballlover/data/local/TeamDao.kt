package khom.pavlo.footballlover.data.local

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

interface TeamDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTeams(teams: List<TeamEntity>)

    @Query("SELECT * FROM teams WHERE id IN (:ids)")
    suspend fun getTeamsByIds(ids: List<String>): List<TeamEntity>
}
