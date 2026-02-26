package khom.pavlo.footballlover.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteLeagueDaoInstrumentedTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: AppDao

    @Before
    fun setUp() {
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.dao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun upsert_and_getFavoriteLeagues_returnsSortedByName() = runBlocking {
        dao.upsertFavoriteLeague(
            FavoriteLeagueEntity(
                leagueId = "2",
                name = "Serie A",
                region = "Italy",
                addedAt = 2L
            )
        )
        dao.upsertFavoriteLeague(
            FavoriteLeagueEntity(
                leagueId = "1",
                name = "Premier League",
                region = "England",
                addedAt = 1L
            )
        )
        dao.upsertFavoriteLeague(
            FavoriteLeagueEntity(
                leagueId = "3",
                name = "Bundesliga",
                region = "Germany",
                addedAt = 3L
            )
        )

        val favorites = dao.getFavoriteLeagues()

        assertEquals(listOf("Bundesliga", "Premier League", "Serie A"), favorites.map { it.name })
    }

    @Test
    fun upsertFavoriteLeague_replacesByLeagueId() = runBlocking {
        dao.upsertFavoriteLeague(
            FavoriteLeagueEntity(
                leagueId = "4328",
                name = "Premier League",
                region = "England",
                addedAt = 1L
            )
        )
        dao.upsertFavoriteLeague(
            FavoriteLeagueEntity(
                leagueId = "4328",
                name = "EPL",
                region = "England",
                addedAt = 2L
            )
        )

        val favorites = dao.getFavoriteLeagues()

        assertEquals(1, favorites.size)
        assertEquals("EPL", favorites.first().name)
        assertEquals(2L, favorites.first().addedAt)
    }

    @Test
    fun deleteFavoriteLeague_removesRow() = runBlocking {
        dao.upsertFavoriteLeague(
            FavoriteLeagueEntity(
                leagueId = "4328",
                name = "Premier League",
                region = "England",
                addedAt = 1L
            )
        )

        dao.deleteFavoriteLeague("4328")

        assertTrue(dao.getFavoriteLeagues().isEmpty())
    }
}
