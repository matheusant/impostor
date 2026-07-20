package com.game.impostor.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface RemoteThemeDao {
    @Transaction
    @Query("SELECT * FROM remote_themes ORDER BY tema ASC")
    fun getAllWithRounds(): Flow<List<RemoteThemeWithRounds>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThemes(themes: List<RemoteThemeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRounds(rounds: List<RemoteRoundEntity>)

    @Query("DELETE FROM remote_themes")
    suspend fun clearThemes()

    /** Substitui o cache inteiro atomicamente (apaga e regrava). */
    @Transaction
    suspend fun substituirTudo(themes: List<RemoteThemeEntity>, rounds: List<RemoteRoundEntity>) {
        clearThemes()
        insertThemes(themes)
        insertRounds(rounds)
    }
}
