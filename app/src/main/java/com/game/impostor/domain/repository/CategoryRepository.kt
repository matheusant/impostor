package com.game.impostor.domain.repository

import com.game.impostor.domain.model.CategoriaCustom
import com.game.impostor.domain.model.RoundData
import kotlinx.coroutines.flow.Flow

/** Acesso às categorias customizadas do usuário (persistência local). */
interface CategoryRepository {
    fun observar(): Flow<List<CategoriaCustom>>
    suspend fun salvar(nome: String, rodadas: List<RoundData>)
    suspend fun atualizar(id: Int, nome: String, rodadas: List<RoundData>)
    suspend fun excluir(id: Int)
}
