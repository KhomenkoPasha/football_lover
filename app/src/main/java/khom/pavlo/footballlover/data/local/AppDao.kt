package khom.pavlo.footballlover.data.local

import androidx.room.Dao

@Dao
interface AppDao :
    TeamDao,
    SearchQueryDao,
    EventDao,
    LeagueDao,
    StandingDao,
    FavoriteLeagueDao
