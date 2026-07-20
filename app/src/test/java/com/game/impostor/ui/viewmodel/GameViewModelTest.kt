package com.game.impostor.ui.viewmodel

import com.game.impostor.MainDispatcherRule
import com.game.impostor.domain.model.DEFAULT_CATEGORIES
import com.game.impostor.domain.model.DefaultCategory
import com.game.impostor.domain.model.RoundData
import com.game.impostor.domain.model.ThemeConfig
import com.game.impostor.domain.repository.ThemeRepository
import com.game.impostor.domain.usecase.ObservarTemasUseCase
import com.game.impostor.domain.usecase.SortearImpostorUseCase
import com.game.impostor.domain.usecase.SortearRodadaUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private class FakeThemeRepository(private val rodadas: List<RoundData>) : ThemeRepository {
        override fun temasPadrao(): List<DefaultCategory> = DEFAULT_CATEGORIES
        override suspend fun rodadasDoTemaPadrao(fileName: String): List<RoundData> = rodadas
        override fun observarTemas(): Flow<List<ThemeConfig>> = flowOf(emptyList())
    }

    private fun buildViewModel(rodadas: List<RoundData> = listOf(RoundData("g", "i"))): GameViewModel {
        val repo = FakeThemeRepository(rodadas)
        return GameViewModel(repo, ObservarTemasUseCase(repo), SortearImpostorUseCase(), SortearRodadaUseCase())
    }

    @Test
    fun `iniciar sorteia rodada e impostor dentro do intervalo`() = runTest {
        val vm = buildViewModel(listOf(RoundData("g1", "i1")))
        advanceUntilIdle()

        assertTrue(vm.iniciar())

        val estado = vm.uiState.value
        assertEquals(RoundData("g1", "i1"), estado.currentRoundData)
        assertTrue(estado.impostorIndex in 0 until estado.totalPlayers)
    }

    @Test
    fun `iniciar retorna false quando nao ha rodadas`() = runTest {
        val vm = buildViewModel(emptyList())
        advanceUntilIdle()

        assertFalse(vm.iniciar())
    }

    @Test
    fun `revelar torna o texto visivel`() = runTest {
        val vm = buildViewModel()
        advanceUntilIdle()

        vm.revelar()

        assertTrue(vm.uiState.value.isTextVisible)
    }

    @Test
    fun `proximo avanca ate o ultimo agente e entao para`() = runTest {
        val vm = buildViewModel(listOf(RoundData("g", "i")))
        vm.definirTotalJogadores(3)
        advanceUntilIdle()
        vm.iniciar()

        assertEquals(0, vm.uiState.value.currentPlayerView)
        assertTrue(vm.proximo())   // 0 -> 1
        assertTrue(vm.proximo())   // 1 -> 2 (último agente para total = 3)
        assertFalse(vm.proximo())  // não avança além do último
        assertEquals(2, vm.uiState.value.currentPlayerView)
    }
}
