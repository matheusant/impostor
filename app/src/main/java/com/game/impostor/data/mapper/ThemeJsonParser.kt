package com.game.impostor.data.mapper

import com.game.impostor.domain.model.RoundData
import com.game.impostor.domain.model.ThemeConfig
import org.json.JSONObject
import javax.inject.Inject

/**
 * Parser puro do JSON de tema (schema: { "tema": string, "rodadas": [ { "grupo", "impostor" } ] }).
 * Lança JSONException em JSON malformado — o I/O (leitura do asset) trata a exceção.
 */
class ThemeJsonParser @Inject constructor() {
    fun parse(json: String): ThemeConfig {
        val obj = JSONObject(json)
        val tema = obj.getString("tema")
        val arr = obj.getJSONArray("rodadas")
        val rodadas = (0 until arr.length()).map { i ->
            val item = arr.getJSONObject(i)
            RoundData(
                grupo = item.getString("grupo"),
                impostor = item.getString("impostor")
            )
        }
        return ThemeConfig(tema = tema, rodadas = rodadas)
    }
}
