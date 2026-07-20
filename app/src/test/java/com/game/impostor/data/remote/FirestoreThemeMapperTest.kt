package com.game.impostor.data.remote

import com.game.impostor.domain.model.RoundData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FirestoreThemeMapperTest {

    private val mapper = FirestoreThemeMapper()

    private fun rodadaValida() = mapOf<String, Any?>(
        "grupo" to "Diga algo que você faz de manhã.",
        "impostor" to "Diga algo que você faz à noite."
    )

    @Test
    fun `documento valido vira ThemeConfig com tema e uma rodada`() {
        val config = mapper.documentoParaThemeConfig("Cotidiano", listOf(rodadaValida()))

        assertEquals("Cotidiano", config?.tema)
        assertEquals(
            listOf(RoundData("Diga algo que você faz de manhã.", "Diga algo que você faz à noite.")),
            config?.rodadas
        )
    }

    @Test
    fun `documento sem rodadas retorna null`() {
        assertNull(mapper.documentoParaThemeConfig("Cotidiano", null))
    }

    @Test
    fun `documento com tema vazio retorna null`() {
        assertNull(mapper.documentoParaThemeConfig("", listOf(rodadaValida())))
    }

    @Test
    fun `rodada com impostor vazio e descartada, sem rodada valida retorna null`() {
        val rodadaInvalida = mapOf<String, Any?>("grupo" to "g", "impostor" to "")

        assertNull(mapper.documentoParaThemeConfig("Cotidiano", listOf(rodadaInvalida)))
    }

    @Test
    fun `mistura de rodadas mantem apenas as validas`() {
        val invalida = mapOf<String, Any?>("grupo" to "", "impostor" to "i")
        val config = mapper.documentoParaThemeConfig("Mix", listOf(rodadaValida(), invalida))

        assertEquals(1, config?.rodadas?.size)
    }
}
