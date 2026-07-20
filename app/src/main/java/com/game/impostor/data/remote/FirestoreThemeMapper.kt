package com.game.impostor.data.remote

import com.game.impostor.domain.model.RoundData
import com.game.impostor.domain.model.ThemeConfig
import javax.inject.Inject

/**
 * Mapeia um documento da collection `temas` do Firestore para [ThemeConfig] (puro, testável).
 *
 * Aplica as regras de §5 do SPEC:
 * - `tema` não pode ser vazio;
 * - deve haver ≥ 1 rodada com `grupo` e `impostor` não vazios.
 *
 * Rodadas malformadas (faltando/ vazias) são descartadas; se nenhuma rodada válida sobrar,
 * ou o `tema` for vazio/nulo, retorna `null` (documento inválido é ignorado pela camada de dados).
 */
class FirestoreThemeMapper @Inject constructor() {

    fun documentoParaThemeConfig(tema: String?, rodadas: List<Map<String, Any?>>?): ThemeConfig? {
        val nome = tema?.trim().orEmpty()
        if (nome.isEmpty()) return null
        if (rodadas.isNullOrEmpty()) return null

        val rodadasValidas = rodadas.mapNotNull { rodada ->
            val grupo = (rodada["grupo"] as? String)?.trim().orEmpty()
            val impostor = (rodada["impostor"] as? String)?.trim().orEmpty()
            if (grupo.isEmpty() || impostor.isEmpty()) null else RoundData(grupo, impostor)
        }
        if (rodadasValidas.isEmpty()) return null

        return ThemeConfig(tema = nome, rodadas = rodadasValidas)
    }
}
