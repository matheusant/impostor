package com.game.impostor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.impostor.domain.model.DEFAULT_CATEGORIES
import com.game.impostor.domain.model.ThemeConfig
import com.game.impostor.domain.repository.ThemeRepository
import com.game.impostor.domain.usecase.ObservarTemasUseCase
import com.game.impostor.domain.usecase.SortearImpostorUseCase
import com.game.impostor.domain.usecase.SortearRodadaUseCase
import com.game.impostor.ui.state.CategorySelection
import com.game.impostor.ui.state.GameUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
    observarTemas: ObservarTemasUseCase,
    private val sortearImpostor: SortearImpostorUseCase,
    private val sortearRodada: SortearRodadaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    /** Temas padrão vindos do Firestore (com cache offline e fallback em assets). */
    val temasRemotos: StateFlow<List<ThemeConfig>> = observarTemas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        selecionarCategoria(CategorySelection.Default(DEFAULT_CATEGORIES.first()))
    }

    fun definirTotalJogadores(total: Int) {
        _uiState.update { it.copy(totalPlayers = total) }
    }

    fun selecionarCategoria(selecao: CategorySelection) {
        _uiState.update { it.copy(selectedCategory = selecao) }
        viewModelScope.launch {
            val rodadas = when (selecao) {
                is CategorySelection.Default -> themeRepository.rodadasDoTemaPadrao(selecao.category.fileName)
                is CategorySelection.Remote -> selecao.rounds
                is CategorySelection.Custom -> selecao.rounds
            }
            _uiState.update { it.copy(rodadasDisponiveis = rodadas) }
        }
    }

    /** Inicia a partida sorteando rodada e impostor. Retorna false se não há rodadas (não avança). */
    fun iniciar(): Boolean {
        val estado = _uiState.value
        val rodada = sortearRodada(estado.rodadasDisponiveis) ?: return false
        val impostor = sortearImpostor(estado.totalPlayers)
        _uiState.update {
            it.copy(
                currentRoundData = rodada,
                impostorIndex = impostor,
                currentPlayerView = 0,
                isTextVisible = false
            )
        }
        return true
    }

    fun revelar() {
        _uiState.update { it.copy(isTextVisible = true) }
    }

    /** Avança para o próximo agente. Retorna false quando o último já viu (ir para game-play). */
    fun proximo(): Boolean {
        val estado = _uiState.value
        return if (estado.currentPlayerView < estado.totalPlayers - 1) {
            _uiState.update { it.copy(currentPlayerView = it.currentPlayerView + 1, isTextVisible = false) }
            true
        } else {
            false
        }
    }

    fun reiniciar() {
        _uiState.update {
            GameUiState(
                totalPlayers = it.totalPlayers,
                selectedCategory = it.selectedCategory,
                rodadasDisponiveis = it.rodadasDisponiveis
            )
        }
    }
}
