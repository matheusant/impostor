package com.game.impostor.domain.repository

import com.game.impostor.domain.model.RoundData

/** Conteúdo relacionadas a IA **/
interface AiDataSource {
    suspend fun iaSuggestion(categoryName: String) : List<RoundData>
}