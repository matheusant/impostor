package com.game.impostor.domain.usecase

import com.game.impostor.domain.model.CredsValidationResult
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidarCredenciaisUseCaseTest {

    private val validar = ValidarCredenciaisUseCase()

    @Test
    fun `email vazio e senha valida retorna Invalido`() {
        val resultado = validar("", "123456")

        assertTrue(resultado is CredsValidationResult.Invalido)
        val mensagens = (resultado as CredsValidationResult.Invalido).mensagens
        assertTrue("esperava mensagem sobre e-mail", mensagens.any { it.contains("mail", ignoreCase = true) })
    }

    @Test
    fun `senha curta retorna Invalido`() {
        val resultado = validar("a@b.com", "123")

        assertTrue(resultado is CredsValidationResult.Invalido)
        val mensagens = (resultado as CredsValidationResult.Invalido).mensagens
        assertTrue("esperava mensagem sobre senha", mensagens.any { it.contains("senha", ignoreCase = true) })
    }

    @Test
    fun `email e senha validos retorna Valido`() {
        val resultado = validar("a@b.com", "123456")

        assertTrue(resultado is CredsValidationResult.Valido)
    }

    @Test
    fun `email sem dominio retorna Invalido`() {
        val resultado = validar("usuario@", "123456")

        assertTrue(resultado is CredsValidationResult.Invalido)
    }
}
