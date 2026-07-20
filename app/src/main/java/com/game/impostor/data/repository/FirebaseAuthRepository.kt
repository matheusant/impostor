package com.game.impostor.data.repository

import com.game.impostor.data.awaitResult
import com.game.impostor.di.IoDispatcher
import com.game.impostor.domain.model.AuthResult
import com.game.impostor.domain.repository.AuthRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Implementação de [AuthRepository] sobre [FirebaseAuth].
 * Converte os `Task` do Firebase em `suspend` sem depender de
 * `kotlinx-coroutines-play-services` (interop manual via [awaitResult]).
 */
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    @IoDispatcher private val io: CoroutineDispatcher
) : AuthRepository {

    override val usuarioAtual: String?
        get() = auth.currentUser?.uid

    override suspend fun entrarComEmail(email: String, senha: String): AuthResult =
        executar { auth.signInWithEmailAndPassword(email, senha) }

    override suspend fun cadastrarComEmail(email: String, senha: String): AuthResult =
        executar { auth.createUserWithEmailAndPassword(email, senha) }

    override suspend fun entrarComGoogle(idToken: String): AuthResult {
        val credencial = GoogleAuthProvider.getCredential(idToken, null)
        return executar { auth.signInWithCredential(credencial) }
    }

    override fun sair() {
        auth.signOut()
    }

    private suspend fun executar(
        acao: () -> Task<com.google.firebase.auth.AuthResult>
    ): AuthResult = withContext(io) {
        try {
            val resultado = acao().awaitResult()
            val uid = resultado.user?.uid
            if (uid != null) AuthResult.Sucesso(uid)
            else AuthResult.Erro("Não foi possível identificar o usuário.")
        } catch (e: Exception) {
            AuthResult.Erro(e.localizedMessage ?: "Falha na autenticação.")
        }
    }
}
