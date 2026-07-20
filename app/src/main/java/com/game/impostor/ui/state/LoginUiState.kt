package com.game.impostor.ui.state

/** Estado observável da tela de login. */
data class LoginUiState(
    val email: String = "",
    val senha: String = "",
    val carregando: Boolean = false,
    val erros: List<String> = emptyList(),
    val autenticado: Boolean = false
)
