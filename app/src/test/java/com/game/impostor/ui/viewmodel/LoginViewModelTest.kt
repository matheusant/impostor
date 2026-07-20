package com.game.impostor.ui.viewmodel

import com.game.impostor.MainDispatcherRule
import com.game.impostor.domain.model.AuthResult
import com.game.impostor.domain.repository.AuthRepository
import com.game.impostor.domain.usecase.ValidarCredenciaisUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private class FakeAuthRepository(
        var proximoResultado: AuthResult = AuthResult.Sucesso("uid-123"),
        override var usuarioAtual: String? = null
    ) : AuthRepository {
        var chamouEntrarEmail = false
        var chamouCadastrar = false
        override suspend fun entrarComEmail(email: String, senha: String): AuthResult {
            chamouEntrarEmail = true
            return proximoResultado
        }
        override suspend fun cadastrarComEmail(email: String, senha: String): AuthResult {
            chamouCadastrar = true
            return proximoResultado
        }
        override suspend fun entrarComGoogle(idToken: String): AuthResult = proximoResultado
        override fun sair() { usuarioAtual = null }
    }

    private fun buildViewModel(repo: AuthRepository = FakeAuthRepository()) =
        LoginViewModel(ValidarCredenciaisUseCase(), repo)

    @Test
    fun `credenciais invalidas nao chamam o repositorio e expoem erros`() = runTest {
        val repo = FakeAuthRepository()
        val vm = buildViewModel(repo)
        vm.onEmailChange("invalido")
        vm.onSenhaChange("123")

        vm.entrar()
        advanceUntilIdle()

        assertFalse(repo.chamouEntrarEmail)
        assertTrue(vm.uiState.value.erros.isNotEmpty())
        assertFalse(vm.uiState.value.autenticado)
    }

    @Test
    fun `login valido com sucesso marca autenticado`() = runTest {
        val repo = FakeAuthRepository(proximoResultado = AuthResult.Sucesso("uid-9"))
        val vm = buildViewModel(repo)
        vm.onEmailChange("a@b.com")
        vm.onSenhaChange("123456")

        vm.entrar()
        advanceUntilIdle()

        assertTrue(repo.chamouEntrarEmail)
        assertTrue(vm.uiState.value.autenticado)
        assertFalse(vm.uiState.value.carregando)
    }

    @Test
    fun `login valido com erro expoe mensagem e nao autentica`() = runTest {
        val repo = FakeAuthRepository(proximoResultado = AuthResult.Erro("Credenciais inválidas."))
        val vm = buildViewModel(repo)
        vm.onEmailChange("a@b.com")
        vm.onSenhaChange("123456")

        vm.entrar()
        advanceUntilIdle()

        assertFalse(vm.uiState.value.autenticado)
        assertEquals(listOf("Credenciais inválidas."), vm.uiState.value.erros)
    }

    @Test
    fun `estado inicial reflete usuario ja autenticado`() {
        val repo = FakeAuthRepository(usuarioAtual = "uid-existente")
        val vm = buildViewModel(repo)

        assertTrue(vm.uiState.value.autenticado)
    }
}
