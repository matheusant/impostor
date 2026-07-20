package com.game.impostor.domain.usecase

import com.game.impostor.domain.model.CategoriaCustom
import com.game.impostor.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservarCategoriasUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    operator fun invoke(): Flow<List<CategoriaCustom>> = repository.observar()
}
