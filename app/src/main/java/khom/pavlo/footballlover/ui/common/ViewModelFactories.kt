package khom.pavlo.footballlover.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import khom.pavlo.footballlover.ui.details.EventDetailViewModel
import khom.pavlo.footballlover.ui.home.HomeViewModel
import khom.pavlo.footballlover.ui.league.LeagueTeamsViewModel
import khom.pavlo.footballlover.ui.matches.MatchesViewModel
import khom.pavlo.footballlover.ui.search.SearchViewModel
import khom.pavlo.footballlover.ui.standings.StandingsViewModel

class SearchViewModelFactory(
    private val searchTeamsUseCase: SearchTeamsUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(searchTeamsUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MatchesViewModelFactory(
    private val getNextEventsUseCase: GetNextEventsUseCase,
    private val getLastEventsUseCase: GetLastEventsUseCase,
    private val teamId: String,
    private val teamName: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MatchesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MatchesViewModel(getNextEventsUseCase, getLastEventsUseCase, teamId, teamName) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class EventDetailViewModelFactory(
    private val getEventDetailsUseCase: GetEventDetailsUseCase,
    private val eventId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventDetailViewModel(getEventDetailsUseCase, eventId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class StandingsViewModelFactory(
    private val getLeaguesUseCase: GetLeaguesUseCase,
    private val getStandingsUseCase: GetStandingsUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StandingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StandingsViewModel(getLeaguesUseCase, getStandingsUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class HomeViewModelFactory(
    private val getEventsByDayUseCase: GetEventsByDayUseCase,
    private val getLeaguesUseCase: GetLeaguesUseCase,
    private val getFavoriteLeaguesUseCase: GetFavoriteLeaguesUseCase,
    private val addFavoriteLeagueUseCase: AddFavoriteLeagueUseCase,
    private val removeFavoriteLeagueUseCase: RemoveFavoriteLeagueUseCase,
    private val getLeagueBadgeUseCase: GetLeagueBadgeUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(
                getEventsByDayUseCase,
                getLeaguesUseCase,
                getFavoriteLeaguesUseCase,
                addFavoriteLeagueUseCase,
                removeFavoriteLeagueUseCase,
                getLeagueBadgeUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class LeagueTeamsViewModelFactory(
    private val getTeamsByLeagueUseCase: GetTeamsByLeagueUseCase,
    private val leagueName: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LeagueTeamsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LeagueTeamsViewModel(getTeamsByLeagueUseCase, leagueName) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
