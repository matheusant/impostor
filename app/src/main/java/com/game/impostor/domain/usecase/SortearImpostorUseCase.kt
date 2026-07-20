package com.game.impostor.domain.usecase

import javax.inject.Inject
import kotlin.random.Random

/** Sorteia o índice do impostor em [0, totalPlayers). Puro e determinístico com Random(seed). */
class SortearImpostorUseCase @Inject constructor() {
    operator fun invoke(totalPlayers: Int, random: Random = Random.Default): Int {
        require(totalPlayers > 0) { "totalPlayers deve ser > 0" }
        return random.nextInt(totalPlayers)
    }
}
