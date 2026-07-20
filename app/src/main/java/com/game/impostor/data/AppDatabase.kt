package com.game.impostor.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        CustomCategoryEntity::class,
        CustomRoundEntity::class,
        RemoteThemeEntity::class,
        RemoteRoundEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun remoteThemeDao(): RemoteThemeDao

    companion object {
        /** v1 → v2: adiciona o cache de temas remotos (Firestore). Não toca em custom_*. */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `remote_themes` " +
                        "(`tema` TEXT NOT NULL, PRIMARY KEY(`tema`))"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `remote_rounds` " +
                        "(`id` INTEGER NOT NULL, `temaId` TEXT NOT NULL, `grupo` TEXT NOT NULL, " +
                        "`impostor` TEXT NOT NULL, PRIMARY KEY(`id`), " +
                        "FOREIGN KEY(`temaId`) REFERENCES `remote_themes`(`tema`) " +
                        "ON UPDATE NO ACTION ON DELETE CASCADE )"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_remote_rounds_temaId` " +
                        "ON `remote_rounds` (`temaId`)"
                )
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "impostor_db"
                ).addMigrations(MIGRATION_1_2).build().also { INSTANCE = it }
            }
    }
}
