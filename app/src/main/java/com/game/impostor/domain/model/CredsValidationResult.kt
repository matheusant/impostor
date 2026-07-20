package com.game.impostor.domain.model

/** Resultado da validação de credenciais de login (puro, testável). */
sealed class CredsValidationResult {
    data object Valido : CredsValidationResult()
    data class Invalido(val mensagens: List<String>) : CredsValidationResult()
}
