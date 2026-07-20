package com.game.impostor.ui.state

import com.game.impostor.domain.model.DEFAULT_CATEGORIES
import com.game.impostor.domain.model.RoundData

/** Estado observável do fluxo de jogo (setup → passa-telefone → game-play). */
data class GameUiState(
    val totalPlayers: Int = 4,
    val selectedCategory: CategorySelection = CategorySelection.Default(DEFAULT_CATEGORIES.first()),
    val rodadasDisponiveis: List<RoundData> = emptyList(),
    val currentRoundData: RoundData? = null,
    val impostorIndex: Int = 0,
    val currentPlayerView: Int = 0,
    val isTextVisible: Boolean = false
)
