package com.game.impostor.domain.usecase

import com.game.impostor.domain.model.ThemeConfig
import com.game.impostor.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Observa os temas padrão (Firestore → cache Room → assets). */
class ObservarTemasUseCase @Inject constructor(
    private val repository: ThemeRepository
) {
    operator fun invoke(): Flow<List<ThemeConfig>> = repository.observarTemas()
}
