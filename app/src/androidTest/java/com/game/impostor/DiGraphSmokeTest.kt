package com.game.impostor

import com.game.impostor.domain.repository.CategoryRepository
import com.game.impostor.domain.repository.ThemeRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

/** Smoke test: garante que o grafo de DI do Hilt monta e provê os repositories. */
@HiltAndroidTest
class DiGraphSmokeTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var categoryRepository: CategoryRepository

    @Inject
    lateinit var themeRepository: ThemeRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun grafo_de_di_prove_os_repositorios() {
        assertNotNull(categoryRepository)
        assertNotNull(themeRepository)
    }
}
