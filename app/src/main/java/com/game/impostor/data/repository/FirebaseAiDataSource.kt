package com.game.impostor.data.repository

import android.util.Log
import com.game.impostor.domain.model.RoundData
import com.game.impostor.domain.repository.AiDataSource
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.serialization.json.Json
import javax.inject.Inject

class FirebaseAiDataSource @Inject constructor() : AiDataSource {
    override suspend fun iaSuggestion(categoryName: String): List<RoundData> {
        val esquemaResposta = Schema.array(
            Schema.obj(
                properties = mapOf(
                    "grupo" to Schema.string(),
                    "impostor" to Schema.string()
                )
            )
        )

        val model = Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = "gemini-3.1-flash-lite",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = esquemaResposta
            }
        )

        val jsonFormatter = Json {
            ignoreUnknownKeys = true
        }

        try {
            Log.d("AI", "Enviando requisição ao Gemini...")
            val prompt = """
                    Você é um gerador de perguntas para um jogo de festa no estilo Impostor.
                    O usuário criou a seguinte categoria customizada: "$categoryName".
                    
                    Gere um objeto JSON contendo 5 pares de perguntas baseados nessa categoria.
                    
                    REGRAS OBRIGATÓRIAS DE CONTEÚDO:
                    1. "grupo": Deve ser uma pergunta ou instrução direta sobre a categoria.
                    2. "impostor": Deve ser uma variação MUITO parecida com a do grupo, mas com uma nuance, restrição ou objetivo ligeiramente diferente. A diferença deve ser sutil o suficiente para que o impostor consiga dar uma resposta plausível, mas diferente o bastante para gerar desconfiança.
                    3. Não use títulos como "Pergunta do Grupo:", responda apenas com a instrução direta.
                    
                    REGRAS DE FORMATO:
                    - Retorne EXATAMENTE um JSON contendo objetos com as chaves "grupo" e "impostor".
                    - Não inclua marcadores de código como ```json, nem textos de introdução ou conclusão. Retorne apenas o JSON puro.
                    
                    Exemplo de formato de saída esperado:
                    [
                        {
                          "grupo": "Diga o nome de uma criatura mágica de Harry Potter considerada perigosa ou assustadora.",
                          "impostor": "Diga o nome de uma criatura mágica de Harry Potter que possa ser mantida como animal de estimação."
                        }
                    ]
                """.trimIndent()


            val response = model.generateContent(prompt)

            val jsonString = response.text ?: emptyList<String>()
            val resposta = jsonFormatter.decodeFromString<List<RoundData>>(jsonString as String)
            Log.d("AI", "Resposta recebida: $jsonString")

            return resposta
        } catch (e: Exception) {
            Log.e("AI", "Erro ao gerar conteúdo", e)
            return emptyList()
        }
    }
}