package com.game.impostor.domain.usecase

import com.game.impostor.domain.model.RoundData
import javax.inject.Inject
import kotlin.random.Random

/** Sorteia uma rodada da lista. Retorna null se vazia. Puro e determinístico com Random(seed). */
class SortearRodadaUseCase @Inject constructor() {
    operator fun invoke(rodadas: List<RoundData>, random: Random = Random.Default): RoundData? {
        if (rodadas.isEmpty()) return null
        return rodadas[random.nextInt(rodadas.size)]
    }
}
