package com.game.impostor.domain.usecase

import com.game.impostor.domain.model.RoundData
import com.game.impostor.domain.repository.CategoryRepository
import javax.inject.Inject

class AtualizarCategoriaUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(id: Int, nome: String, rodadas: List<RoundData>) =
        repository.atualizar(id, nome, rodadas)
}
