package com.game.impostor.domain.usecase

import com.game.impostor.domain.model.CredsValidationResult
import javax.inject.Inject

/**
 * Valida e-mail e senha antes de tentar autenticar (regra pura, sem Android).
 * - e-mail: formato válido (algo@algo.dominio), não vazio;
 * - senha: pelo menos [MIN_SENHA] caracteres.
 * Não usa android.util.Patterns para permanecer testável em src/test.
 */
class ValidarCredenciaisUseCase @Inject constructor() {

    operator fun invoke(email: String, senha: String): CredsValidationResult {
        val mensagens = mutableListOf<String>()

        if (!EMAIL_REGEX.matches(email.trim())) {
            mensagens.add("Informe um e-mail válido.")
        }
        if (senha.length < MIN_SENHA) {
            mensagens.add("A senha deve ter ao menos $MIN_SENHA caracteres.")
        }

        return if (mensagens.isEmpty()) {
            CredsValidationResult.Valido
        } else {
            CredsValidationResult.Invalido(mensagens)
        }
    }

    companion object {
        const val MIN_SENHA = 6
        private val EMAIL_REGEX = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    }
}
