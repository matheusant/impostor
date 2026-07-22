package com.game.impostor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.impostor.domain.model.AuthResult
import com.game.impostor.domain.model.CredsValidationResult
import com.game.impostor.domain.repository.AuthRepository
import com.game.impostor.domain.usecase.ValidarCredenciaisUseCase
import com.game.impostor.ui.state.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val validarCredenciais: ValidarCredenciaisUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState(autenticado = authRepository.usuarioAtual != null))
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(valor: String) = _uiState.update { it.copy(email = valor, erros = emptyList()) }

    fun onSenhaChange(valor: String) = _uiState.update { it.copy(senha = valor, erros = emptyList()) }

    fun entrar() = autenticarComEmail { email, senha -> authRepository.entrarComEmail(email, senha) }

    fun sair() = sairDoApp()

    fun cadastrar() = autenticarComEmail { email, senha -> authRepository.cadastrarComEmail(email, senha) }

    fun entrarComGoogle(idToken: String) {
        _uiState.update { it.copy(carregando = true, erros = emptyList()) }
        viewModelScope.launch {
            aplicarResultado(authRepository.entrarComGoogle(idToken))
        }
    }

    /** Permite à camada de navegação reportar falhas do fluxo do Google Sign-In. */
    fun reportarErro(mensagem: String) = _uiState.update { it.copy(carregando = false, erros = listOf(mensagem)) }

    private fun autenticarComEmail(acao: suspend (email: String, senha: String) -> AuthResult) {
        val estado = _uiState.value
        val email = estado.email.trim()
        when (val validacao = validarCredenciais(email, estado.senha)) {
            is CredsValidationResult.Invalido ->
                _uiState.update { it.copy(erros = validacao.mensagens) }
            CredsValidationResult.Valido -> {
                _uiState.update { it.copy(carregando = true, erros = emptyList()) }
                viewModelScope.launch {
                    aplicarResultado(acao(email, estado.senha))
                }
            }
        }
    }

    private fun aplicarResultado(resultado: AuthResult) {
        when (resultado) {
            is AuthResult.Sucesso -> _uiState.update { it.copy(carregando = false, autenticado = true) }
            is AuthResult.Erro -> _uiState.update { it.copy(carregando = false, erros = listOf(resultado.mensagem)) }
        }
    }

    private fun sairDoApp() {
        authRepository.sair()
        _uiState.update { it.copy(email = "", senha = "", autenticado = false) }
    }
}
