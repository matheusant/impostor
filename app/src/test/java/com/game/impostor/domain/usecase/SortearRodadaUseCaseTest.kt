package com.game.impostor.domain.usecase

import com.game.impostor.domain.model.RoundData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import kotlin.random.Random

class SortearRodadaUseCaseTest {

    private val sortear = SortearRodadaUseCase()

    @Test
    fun `categoria com uma rodada sempre sorteia aquela`() {
        val unica = RoundData("grupo", "impostor")
        assertEquals(unica, sortear(listOf(unica), Random(1)))
    }

    @Test
    fun `lista vazia retorna null`() {
        assertNull(sortear(emptyList()))
    }

    @Test
    fun `deterministico com mesma seed`() {
        val lista = listOf(
            RoundData("a", "1"),
            RoundData("b", "2"),
            RoundData("c", "3")
        )
        assertEquals(sortear(lista, Random(7)), sortear(lista, Random(7)))
    }
}
