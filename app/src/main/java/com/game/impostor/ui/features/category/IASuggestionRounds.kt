package com.game.impostor.ui.features.category

import com.game.impostor.domain.model.RoundData
import kotlinx.serialization.Serializable

@Serializable
data class IASuggestionRounds(
    val isLoading: Boolean = false,
    val rounds: List<RoundData> = emptyList()
)