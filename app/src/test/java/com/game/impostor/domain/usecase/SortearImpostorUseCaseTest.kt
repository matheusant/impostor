package com.game.impostor.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class SortearImpostorUseCaseTest {

    private val sortear = SortearImpostorUseCase()

    @Test
    fun `indice sempre dentro do intervalo para 4 jogadores`() {
        repeat(1000) {
            val indice = sortear(4)
            assertTrue("índice $indice fora de [0,4)", indice in 0..3)
        }
    }

    @Test
    fun `deterministico com mesma seed`() {
        val a = sortear(6, Random(42))
        val b = sortear(6, Random(42))
        assertEquals(a, b)
    }

    @Test
    fun `com um jogador sempre retorna zero`() {
        assertEquals(0, sortear(1))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `total zero lanca excecao`() {
        sortear(0)
    }
}
