package com.game.impostor.data.mapper

import org.json.JSONException
import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeJsonParserTest {

    private val parser = ThemeJsonParser()

    @Test
    fun `parse de json valido preenche tema e rodadas`() {
        val json = """
            {
              "tema": "Cotidiano",
              "rodadas": [
                { "grupo": "g1", "impostor": "i1" },
                { "grupo": "g2", "impostor": "i2" }
              ]
            }
        """.trimIndent()

        val tema = parser.parse(json)

        assertEquals("Cotidiano", tema.tema)
        assertEquals(2, tema.rodadas.size)
        assertEquals("g1", tema.rodadas[0].grupo)
        assertEquals("i2", tema.rodadas[1].impostor)
    }

    @Test(expected = JSONException::class)
    fun `json malformado lanca excecao`() {
        parser.parse("{ isto nao e json valido")
    }

    @Test(expected = JSONException::class)
    fun `json sem rodadas lanca excecao`() {
        parser.parse("""{ "tema": "X" }""")
    }
}
