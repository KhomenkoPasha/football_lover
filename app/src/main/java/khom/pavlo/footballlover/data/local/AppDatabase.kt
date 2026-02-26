package khom.pavlo.footballlover.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        TeamEntity::class,
        SearchQueryEntity::class,
        EventEntity::class,
        LeagueEntity::class,
        StandingEntity::class,
        FavoriteLeagueEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): AppDao

    companion object {
        fun build(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "football_lover.db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
