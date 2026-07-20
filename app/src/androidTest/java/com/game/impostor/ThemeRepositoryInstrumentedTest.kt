package com.game.impostor

import com.game.impostor.domain.model.DEFAULT_CATEGORIES
import com.game.impostor.domain.repository.ThemeRepository
import kotlinx.coroutines.runBlocking
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

/** Valida que os JSON de assets reais são lidos e parseados para rodadas não vazias. */
@HiltAndroidTest
class ThemeRepositoryInstrumentedTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var themeRepository: ThemeRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun cada_tema_padrao_tem_rodadas_validas() = runBlocking {
        for (tema in DEFAULT_CATEGORIES) {
            val rodadas = themeRepository.rodadasDoTemaPadrao(tema.fileName)
            assertTrue("tema ${tema.name} sem rodadas", rodadas.isNotEmpty())
            rodadas.forEach {
                assertTrue("grupo vazio em ${tema.name}", it.grupo.isNotBlank())
                assertTrue("impostor vazio em ${tema.name}", it.impostor.isNotBlank())
            }
        }
    }
}
