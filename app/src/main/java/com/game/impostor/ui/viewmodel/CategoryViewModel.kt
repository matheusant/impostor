package com.game.impostor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.impostor.domain.model.CategoriaCustom
import com.game.impostor.domain.model.RoundData
import com.game.impostor.domain.usecase.AtualizarCategoriaUseCase
import com.game.impostor.domain.usecase.ExcluirCategoriaUseCase
import com.game.impostor.domain.usecase.IASuggestionUseCase
import com.game.impostor.domain.usecase.ObservarCategoriasUseCase
import com.game.impostor.domain.usecase.SalvarCategoriaUseCase
import com.game.impostor.ui.state.IASuggestionRounds
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
class CategoryViewModel @Inject constructor(
    observarCategorias: ObservarCategoriasUseCase,
    private val salvarCategoria: SalvarCategoriaUseCase,
    private val iaSuggestionRounds: IASuggestionUseCase,
    private val atualizarCategoria: AtualizarCategoriaUseCase,
    private val excluirCategoria: ExcluirCategoriaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(IASuggestionRounds())
    val uiState: StateFlow<IASuggestionRounds> = _uiState.asStateFlow()

    val categorias: StateFlow<List<CategoriaCustom>> = observarCategorias()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun salvar(nome: String, rodadas: List<Pair<String, String>>) {
        viewModelScope.launch {
            salvarCategoria(nome, rodadas.map { RoundData(it.first, it.second) })
        }
    }

    fun atualizar(id: Int, nome: String, rodadas: List<Pair<String, String>>) {
        viewModelScope.launch {
            atualizarCategoria(id, nome, rodadas.map { RoundData(it.first, it.second) })
        }
    }

    fun excluir(id: Int) {
        viewModelScope.launch {
            excluirCategoria(id)
        }
    }

    fun iaSuggestion(categoryName: String) =
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val response = iaSuggestionRounds(categoryName)
            if (response.isNotEmpty()) _uiState.update { it.copy(isLoading = false, rounds = response) }
        }

    fun clearIASuggestion() =
        viewModelScope.launch {
            _uiState.update { it.copy(rounds = emptyList()) }
        }
}
