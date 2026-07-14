package com.game.impostor.data

import androidx.room.Embedded
import androidx.room.Relation

data class CustomCategoryWithRounds(
    @Embedded val category: CustomCategoryEntity,
    @Relation(parentColumn = "id", entityColumn = "categoryId")
    val rounds: List<CustomRoundEntity>
)
