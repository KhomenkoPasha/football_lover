package khom.pavlo.footballlover.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import khom.pavlo.footballlover.data.remote.model.EventsResponse
import khom.pavlo.footballlover.data.remote.model.LeaguesResponse
import khom.pavlo.footballlover.data.remote.model.TableResponse
import khom.pavlo.footballlover.data.remote.model.TeamsResponse

interface TheSportsDbApi {
    @GET("api/v1/json/123/searchteams.php")
    suspend fun searchTeams(
        @Query("t") teamName: String
    ): TeamsResponse

    @GET("api/v1/json/123/search_all_teams.php")
    suspend fun teamsByLeague(
        @Query("l") leagueName: String
    ): TeamsResponse

    @GET("api/v1/json/123/eventsnext.php")
    suspend fun eventsNext(
        @Query("id") teamId: String
    ): EventsResponse

    @GET("api/v1/json/123/eventslast.php")
    suspend fun eventsLast(
        @Query("id") teamId: String
    ): EventsResponse

    @GET("api/v1/json/123/lookupevent.php")
    suspend fun lookupEvent(
        @Query("id") eventId: String
    ): EventsResponse

    @GET("api/v1/json/123/eventsday.php")
    suspend fun eventsDay(
        @Query("d") date: String,
        @Query("s") sport: String? = null,
        @Query("l") league: String? = null
    ): EventsResponse

    @GET("api/v1/json/123/all_leagues.php")
    suspend fun allLeagues(): LeaguesResponse

    @GET("api/v1/json/123/lookupleague.php")
    suspend fun lookupLeague(
        @Query("id") leagueId: String
    ): LeaguesResponse

    @GET("api/v1/json/123/lookuptable.php")
    suspend fun lookupTable(
        @Query("l") leagueId: String,
        @Query("s") season: String? = null
    ): TableResponse
}

object TheSportsDbApiFactory {
    private const val BASE_URL = "https://www.thesportsdb.com/"

    fun create(): TheSportsDbApi {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()

        return retrofit.create(TheSportsDbApi::class.java)
    }
}
