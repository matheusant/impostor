package com.game.impostor.data.repository

import android.content.Context
import com.game.impostor.data.RemoteRoundEntity
import com.game.impostor.data.RemoteThemeDao
import com.game.impostor.data.RemoteThemeEntity
import com.game.impostor.data.RemoteThemeWithRounds
import com.game.impostor.data.awaitResult
import com.game.impostor.data.mapper.ThemeJsonParser
import com.game.impostor.data.remote.FirestoreThemeMapper
import com.game.impostor.di.IoDispatcher
import com.game.impostor.domain.model.DEFAULT_CATEGORIES
import com.game.impostor.domain.model.DefaultCategory
import com.game.impostor.domain.model.RoundData
import com.game.impostor.domain.model.ThemeConfig
import com.game.impostor.domain.repository.ThemeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ThemeRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val parser: ThemeJsonParser,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val remoteThemeDao: RemoteThemeDao,
    private val firestoreMapper: FirestoreThemeMapper,
    @IoDispatcher private val io: CoroutineDispatcher
) : ThemeRepository {

    override fun temasPadrao(): List<DefaultCategory> = DEFAULT_CATEGORIES

    override suspend fun rodadasDoTemaPadrao(fileName: String): List<RoundData> = withContext(io) {
        try {
            val json = context.assets.open(fileName).bufferedReader().use { it.readText() }
            parser.parse(json).rodadas
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Reage ao estado de autenticação: sempre que o usuário logado muda (inclusive logo após
     * o login), re-sincroniza o cache com o Firestore e reemite o cache do Room. As regras do
     * Firestore exigem `request.auth != null`, então só faz sentido buscar quando há usuário.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observarTemas(): Flow<List<ThemeConfig>> =
        authUidFlow()
            .onEach { uid -> if (uid != null) atualizarCacheDoFirestore() }
            .flatMapLatest {
                remoteThemeDao.getAllWithRounds().map { cache ->
                    if (cache.isEmpty()) temasDeAssets()
                    else cache.map { it.toThemeConfig() }
                }
            }

    /** Emite o uid atual e a cada mudança de estado de autenticação (`null` = deslogado). */
    private fun authUidFlow(): Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser?.uid) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    /** Busca a collection `perguntas` no Firestore e regrava o cache. Offline/erro: mantém o cache. */
    private suspend fun atualizarCacheDoFirestore() = withContext(io) {
        try {
            val snapshot = firestore.collection(COLECAO_PERGUNTAS).get().awaitResult()
            val configs = snapshot.documents.mapNotNull { doc ->
                @Suppress("UNCHECKED_CAST")
                val rodadas = doc.get("rodadas") as? List<Map<String, Any?>>
                firestoreMapper.documentoParaThemeConfig(doc.getString("tema") ?: doc.id, rodadas)
            }
            if (configs.isNotEmpty()) {
                val themes = configs.map { RemoteThemeEntity(tema = it.tema) }
                val rounds = configs.flatMap { config ->
                    config.rodadas.map { RemoteRoundEntity(temaId = config.tema, grupo = it.grupo, impostor = it.impostor) }
                }
                remoteThemeDao.substituirTudo(themes, rounds)
            }
        } catch (e: Exception) {
            // Sem rede / falha do Firestore: preserva o cache existente (fallback offline).
        }
    }

    private suspend fun temasDeAssets(): List<ThemeConfig> =
        DEFAULT_CATEGORIES.mapNotNull { categoria ->
            val rodadas = rodadasDoTemaPadrao(categoria.fileName)
            if (rodadas.isEmpty()) null else ThemeConfig(tema = categoria.name, rodadas = rodadas)
        }

    private fun RemoteThemeWithRounds.toThemeConfig(): ThemeConfig =
        ThemeConfig(
            tema = theme.tema,
            rodadas = rounds.map { RoundData(it.grupo, it.impostor) }
        )

    companion object {
        private const val COLECAO_PERGUNTAS = "perguntas"
    }
}
