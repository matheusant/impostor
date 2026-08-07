package com.game.impostor.domain.usecase.category

import com.game.impostor.domain.repository.CategoryRepository
import javax.inject.Inject

class ExcluirCategoriaUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(id: Int) = repository.excluir(id)
}
