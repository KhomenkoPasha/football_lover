package khom.pavlo.footballlover.data.local

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

interface FavoriteLeagueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFavoriteLeague(favoriteLeague: FavoriteLeagueEntity)

    @Query("SELECT * FROM favorite_leagues ORDER BY name")
    suspend fun getFavoriteLeagues(): List<FavoriteLeagueEntity>

    @Query("DELETE FROM favorite_leagues WHERE leagueId = :leagueId")
    suspend fun deleteFavoriteLeague(leagueId: String)
}
