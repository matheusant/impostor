package com.game.impostor.domain.repository

import com.game.impostor.domain.model.DefaultCategory
import com.game.impostor.domain.model.RoundData
import com.game.impostor.domain.model.ThemeConfig
import kotlinx.coroutines.flow.Flow

/** Acesso aos temas padrão: origem Firestore + cache offline (Room) + fallback assets. */
interface ThemeRepository {
    fun temasPadrao(): List<DefaultCategory>
    suspend fun rodadasDoTemaPadrao(fileName: String): List<RoundData>

    /**
     * Observa os temas padrão de forma reativa. Estratégia:
     * busca no Firestore (best-effort) e atualiza o cache Room; emite sempre do cache;
     * se o cache estiver vazio (primeiro uso offline), emite os temas embarcados em assets.
     */
    fun observarTemas(): Flow<List<ThemeConfig>>
}
