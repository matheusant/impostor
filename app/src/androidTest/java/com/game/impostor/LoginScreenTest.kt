package com.game.impostor

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.game.impostor.ui.screen.LoginScreen
import com.game.impostor.ui.screen.LoginTestTags
import com.game.impostor.ui.state.LoginUiState
import org.junit.Rule
import org.junit.Test

/** Verifica que a LoginScreen expõe os três botões de autenticação e o campo de e-mail. */
class LoginScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun exibe_tres_botoes_e_campo_email() {
        composeRule.setContent {
            LoginScreen(
                state = LoginUiState(),
                onEmailChange = {},
                onSenhaChange = {},
                onEntrar = {},
                onCadastrar = {},
                onEntrarComGoogle = {}
            )
        }

        composeRule.onNodeWithText("ENTRAR").assertIsDisplayed()
        composeRule.onNodeWithText("CADASTRAR").assertIsDisplayed()
        composeRule.onNodeWithText("ENTRAR COM GOOGLE").assertIsDisplayed()
        composeRule.onNodeWithTag(LoginTestTags.CAMPO_EMAIL).assertIsDisplayed()
    }
}
