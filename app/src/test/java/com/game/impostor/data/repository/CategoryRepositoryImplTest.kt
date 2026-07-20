package com.game.impostor.data.repository

import com.game.impostor.data.CategoryDao
import com.game.impostor.data.CustomCategoryEntity
import com.game.impostor.data.CustomCategoryWithRounds
import com.game.impostor.data.CustomRoundEntity
import com.game.impostor.domain.model.RoundData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CategoryRepositoryImplTest {

    /** Fake em memória que registra as chamadas ao DAO. */
    private class FakeCategoryDao : CategoryDao {
        val categoriesFlow = MutableStateFlow<List<CustomCategoryWithRounds>>(emptyList())
        val insertedCategories = mutableListOf<CustomCategoryEntity>()
        val insertedRounds = mutableListOf<List<CustomRoundEntity>>()
        val deletedCategoryIds = mutableListOf<Int>()
        val updatedNames = mutableListOf<Pair<Int, String>>()
        val deletedRoundsForCategory = mutableListOf<Int>()
        var nextId = 1L

        override fun getAllWithRounds() = categoriesFlow
        override suspend fun insertCategory(category: CustomCategoryEntity): Long {
            insertedCategories.add(category)
            return nextId++
        }
        override suspend fun insertRounds(rounds: List<CustomRoundEntity>) {
            insertedRounds.add(rounds)
        }
        override suspend fun deleteCategory(id: Int) {
            deletedCategoryIds.add(id)
        }
        override suspend fun updateCategoryName(id: Int, name: String) {
            updatedNames.add(id to name)
        }
        override suspend fun deleteRoundsForCategory(categoryId: Int) {
            deletedRoundsForCategory.add(categoryId)
        }
    }

    private fun repo(dao: CategoryDao) =
        CategoryRepositoryImpl(dao, UnconfinedTestDispatcher())

    @Test
    fun `observar mapeia entidade Room para modelo de dominio`() = runTest {
        val dao = FakeCategoryDao()
        dao.categoriesFlow.value = listOf(
            CustomCategoryWithRounds(
                category = CustomCategoryEntity(id = 7, name = "Festa"),
                rounds = listOf(
                    CustomRoundEntity(id = 1, categoryId = 7, grupo = "g1", impostor = "i1"),
                    CustomRoundEntity(id = 2, categoryId = 7, grupo = "g2", impostor = "i2")
                )
            )
        )

        val categorias = repo(dao).observar().first()

        assertEquals(1, categorias.size)
        val categoria = categorias[0]
        assertEquals(7, categoria.id)
        assertEquals("Festa", categoria.nome)
        assertEquals(listOf(RoundData("g1", "i1"), RoundData("g2", "i2")), categoria.rodadas)
    }

    @Test
    fun `salvar insere categoria e rodadas com o id gerado`() = runTest {
        val dao = FakeCategoryDao()

        repo(dao).salvar("Festa", listOf(RoundData("g1", "i1"), RoundData("g2", "i2")))

        assertEquals(1, dao.insertedCategories.size)
        assertEquals("Festa", dao.insertedCategories[0].name)
        assertEquals(1, dao.insertedRounds.size)
        val rodadas = dao.insertedRounds[0]
        assertEquals(2, rodadas.size)
        assertEquals(1, rodadas[0].categoryId)
        assertEquals("g1", rodadas[0].grupo)
        assertEquals("i2", rodadas[1].impostor)
    }

    @Test
    fun `atualizar renomeia, apaga rodadas antigas e reinsere`() = runTest {
        val dao = FakeCategoryDao()

        repo(dao).atualizar(5, "Novo", listOf(RoundData("a", "b")))

        assertEquals(5 to "Novo", dao.updatedNames[0])
        assertEquals(5, dao.deletedRoundsForCategory[0])
        assertEquals(5, dao.insertedRounds[0][0].categoryId)
        assertEquals("a", dao.insertedRounds[0][0].grupo)
    }

    @Test
    fun `excluir remove a categoria pelo id`() = runTest {
        val dao = FakeCategoryDao()

        repo(dao).excluir(9)

        assertEquals(listOf(9), dao.deletedCategoryIds)
    }
}
