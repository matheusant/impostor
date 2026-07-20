package com.game.impostor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.impostor.domain.model.CategoriaCustom
import com.game.impostor.domain.model.RoundData
import com.game.impostor.domain.usecase.AtualizarCategoriaUseCase
import com.game.impostor.domain.usecase.ExcluirCategoriaUseCase
import com.game.impostor.domain.usecase.ObservarCategoriasUseCase
import com.game.impostor.domain.usecase.SalvarCategoriaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    observarCategorias: ObservarCategoriasUseCase,
    private val salvarCategoria: SalvarCategoriaUseCase,
    private val atualizarCategoria: AtualizarCategoriaUseCase,
    private val excluirCategoria: ExcluirCategoriaUseCase
) : ViewModel() {

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
}
