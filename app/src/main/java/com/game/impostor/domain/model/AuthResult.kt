package com.game.impostor.domain.model

/** Resultado de uma operação de autenticação (login/cadastro). */
sealed class AuthResult {
    data class Sucesso(val uid: String) : AuthResult()
    data class Erro(val mensagem: String) : AuthResult()
}
