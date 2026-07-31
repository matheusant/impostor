package com.game.impostor.data.repository

import android.util.Log
import com.game.impostor.data.CategoryDao
import com.game.impostor.data.CustomCategoryEntity
import com.game.impostor.data.CustomCategoryWithRounds
import com.game.impostor.data.CustomRoundEntity
import com.game.impostor.di.IoDispatcher
import com.game.impostor.domain.model.CategoriaCustom
import com.game.impostor.domain.model.RoundData
import com.game.impostor.domain.repository.CategoryRepository
import com.game.impostor.ui.state.IASuggestionRounds
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val dao: CategoryDao,
    @IoDispatcher private val io: CoroutineDispatcher
) : CategoryRepository {

    override fun observar(): Flow<List<CategoriaCustom>> =
        dao.getAllWithRounds().map { list -> list.map { it.toDomain() } }

    override suspend fun salvar(nome: String, rodadas: List<RoundData>) = withContext(io) {
        val id = dao.insertCategory(CustomCategoryEntity(name = nome)).toInt()
        dao.insertRounds(rodadas.map { CustomRoundEntity(categoryId = id, grupo = it.grupo, impostor = it.impostor) })
    }

    override suspend fun atualizar(id: Int, nome: String, rodadas: List<RoundData>) = withContext(io) {
        dao.updateCategoryName(id, nome)
        dao.deleteRoundsForCategory(id)
        dao.insertRounds(rodadas.map { CustomRoundEntity(categoryId = id, grupo = it.grupo, impostor = it.impostor) })
    }

    override suspend fun excluir(id: Int) = withContext(io) {
        dao.deleteCategory(id)
    }

    override suspend fun iaSuggestion(categoryName: String) : List<RoundData> {
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

private fun CustomCategoryWithRounds.toDomain(): CategoriaCustom =
    CategoriaCustom(
        id = category.id,
        nome = category.name,
        rodadas = rounds.map { RoundData(it.grupo, it.impostor) }
    )
