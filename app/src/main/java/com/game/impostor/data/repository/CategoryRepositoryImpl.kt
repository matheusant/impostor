package com.game.impostor.data.repository

import com.game.impostor.data.CategoryDao
import com.game.impostor.data.CustomCategoryEntity
import com.game.impostor.data.CustomCategoryWithRounds
import com.game.impostor.data.CustomRoundEntity
import com.game.impostor.di.IoDispatcher
import com.game.impostor.domain.model.CategoriaCustom
import com.game.impostor.domain.model.RoundData
import com.game.impostor.domain.repository.CategoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val dao: CategoryDao,
    @IoDispatcher private val io: CoroutineDispatcher
) : CategoryRepository {

    override fun observar(): Flow<List<CategoriaCustom>> =
        dao.getAllWithRounds().map { list -> list.map { it.toDomain() } }

    override suspend fun salvar(nome: String, rodadas: List<RoundData>) = withContext(io) {
        val id = dao.insertCategory(CustomCategoryEntity(name = nome)).toInt()
        dao.insertRounds(rodadas.map { CustomRoundEntity(categoryId = id, grupo = it.grupo, impostor = it.impostor) })
    }

    override suspend fun atualizar(id: Int, nome: String, rodadas: List<RoundData>) = withContext(io) {
        dao.updateCategoryName(id, nome)
        dao.deleteRoundsForCategory(id)
        dao.insertRounds(rodadas.map { CustomRoundEntity(categoryId = id, grupo = it.grupo, impostor = it.impostor) })
    }

    override suspend fun excluir(id: Int) = withContext(io) {
        dao.deleteCategory(id)
    }
}

private fun CustomCategoryWithRounds.toDomain(): CategoriaCustom =
    CategoriaCustom(
        id = category.id,
        nome = category.name,
        rodadas = rounds.map { RoundData(it.grupo, it.impostor) }
    )
