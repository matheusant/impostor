package com.game.impostor.domain.repository

import com.game.impostor.domain.model.AuthResult

/**
 * Autenticação do usuário (Firebase Auth). Contrato puro de domínio:
 * nenhuma dependência de Android/Firebase vaza para cá — a impl vive em `data/`.
 */
interface AuthRepository {
    /** UID do usuário autenticado, ou `null` se ninguém está logado. */
    val usuarioAtual: String?

    suspend fun entrarComEmail(email: String, senha: String): AuthResult
    suspend fun cadastrarComEmail(email: String, senha: String): AuthResult

    /** Autentica com o `idToken` obtido do Google Sign-In (play-services-auth). */
    suspend fun entrarComGoogle(idToken: String): AuthResult

    fun sair()
}
