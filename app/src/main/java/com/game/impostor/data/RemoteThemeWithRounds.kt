package com.game.impostor.data

import androidx.room.Embedded
import androidx.room.Relation

data class RemoteThemeWithRounds(
    @Embedded val theme: RemoteThemeEntity,
    @Relation(parentColumn = "tema", entityColumn = "temaId")
    val rounds: List<RemoteRoundEntity>
)
