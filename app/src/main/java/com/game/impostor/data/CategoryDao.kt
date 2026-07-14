package com.game.impostor.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Transaction
    @Query("SELECT * FROM custom_categories ORDER BY name ASC")
    fun getAllWithRounds(): Flow<List<CustomCategoryWithRounds>>

    @Insert
    suspend fun insertCategory(category: CustomCategoryEntity): Long

    @Insert
    suspend fun insertRounds(rounds: List<CustomRoundEntity>)

    @Query("DELETE FROM custom_categories WHERE id = :id")
    suspend fun deleteCategory(id: Int)

    @Query("UPDATE custom_categories SET name = :name WHERE id = :id")
    suspend fun updateCategoryName(id: Int, name: String)

    @Query("DELETE FROM custom_rounds WHERE categoryId = :categoryId")
    suspend fun deleteRoundsForCategory(categoryId: Int)
}
