package khom.pavlo.footballlover.di

import android.content.Context
import khom.pavlo.footballlover.data.local.AppDatabase
import khom.pavlo.footballlover.data.remote.TheSportsDbApiFactory
import khom.pavlo.footballlover.data.repository.TheSportsRepositoryImpl
import khom.pavlo.footballlover.domain.repository.TheSportsRepository
import khom.pavlo.footballlover.domain.usecase.GetEventDetailsUseCase
import khom.pavlo.footballlover.domain.usecase.GetEventsByDayUseCase
import khom.pavlo.footballlover.domain.usecase.GetFavoriteLeaguesUseCase
import khom.pavlo.footballlover.domain.usecase.GetLastEventsUseCase
import khom.pavlo.footballlover.domain.usecase.GetLeagueBadgeUseCase
import khom.pavlo.footballlover.domain.usecase.GetLeaguesUseCase
import khom.pavlo.footballlover.domain.usecase.GetNextEventsUseCase
import khom.pavlo.footballlover.domain.usecase.AddFavoriteLeagueUseCase
import khom.pavlo.footballlover.domain.usecase.RemoveFavoriteLeagueUseCase
import khom.pavlo.footballlover.domain.usecase.GetStandingsUseCase
import khom.pavlo.footballlover.domain.usecase.GetTeamsByLeagueUseCase
import khom.pavlo.footballlover.domain.usecase.SearchTeamsUseCase

object AppContainer {
    @Volatile
    private var initialized = false

    lateinit var repository: TheSportsRepository
        private set
    lateinit var searchTeamsUseCase: SearchTeamsUseCase
        private set
    lateinit var getNextEventsUseCase: GetNextEventsUseCase
        private set
    lateinit var getLastEventsUseCase: GetLastEventsUseCase
        private set
    lateinit var getEventDetailsUseCase: GetEventDetailsUseCase
        private set
    lateinit var getLeaguesUseCase: GetLeaguesUseCase
        private set
    lateinit var getStandingsUseCase: GetStandingsUseCase
        private set
    lateinit var getEventsByDayUseCase: GetEventsByDayUseCase
        private set
    lateinit var getTeamsByLeagueUseCase: GetTeamsByLeagueUseCase
        private set
    lateinit var getFavoriteLeaguesUseCase: GetFavoriteLeaguesUseCase
        private set
    lateinit var addFavoriteLeagueUseCase: AddFavoriteLeagueUseCase
        private set
    lateinit var removeFavoriteLeagueUseCase: RemoveFavoriteLeagueUseCase
        private set
    lateinit var getLeagueBadgeUseCase: GetLeagueBadgeUseCase
        private set

    fun init(context: Context) {
        if (initialized) return
        synchronized(this) {
            if (initialized) return
            val api = TheSportsDbApiFactory.create()
            val db = AppDatabase.build(context)
            repository = TheSportsRepositoryImpl(api, db.dao())
            searchTeamsUseCase = SearchTeamsUseCase(repository)
            getNextEventsUseCase = GetNextEventsUseCase(repository)
            getLastEventsUseCase = GetLastEventsUseCase(repository)
            getEventDetailsUseCase = GetEventDetailsUseCase(repository)
            getLeaguesUseCase = GetLeaguesUseCase(repository)
            getStandingsUseCase = GetStandingsUseCase(repository)
            getEventsByDayUseCase = GetEventsByDayUseCase(repository)
            getTeamsByLeagueUseCase = GetTeamsByLeagueUseCase(repository)
            getFavoriteLeaguesUseCase = GetFavoriteLeaguesUseCase(repository)
            addFavoriteLeagueUseCase = AddFavoriteLeagueUseCase(repository)
            removeFavoriteLeagueUseCase = RemoveFavoriteLeagueUseCase(repository)
            getLeagueBadgeUseCase = GetLeagueBadgeUseCase(repository)
            initialized = true
        }
    }
}
