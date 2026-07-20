package com.game.impostor.domain.usecase

import com.game.impostor.domain.model.RoundData
import com.game.impostor.domain.repository.CategoryRepository
import javax.inject.Inject

class SalvarCategoriaUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(nome: String, rodadas: List<RoundData>) =
        repository.salvar(nome, rodadas)
}
