package com.game.impostor.domain.usecase.game

import com.game.impostor.domain.repository.CategoryRepository
import javax.inject.Inject

class IASuggestionUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(categoryName: String) = repository.iaSuggestion(categoryName)
}