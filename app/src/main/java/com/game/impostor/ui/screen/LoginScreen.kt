package com.game.impostor.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import com.game.impostor.ui.state.LoginUiState
import com.game.impostor.ui.theme.SpyBlack
import com.game.impostor.ui.theme.SpyGray
import com.game.impostor.ui.theme.SpyGreen
import com.game.impostor.ui.theme.SpyRed
import com.game.impostor.ui.theme.SpyTextWhite

object LoginTestTags {
    const val CAMPO_EMAIL = "campo_email"
    const val CAMPO_SENHA = "campo_senha"
}

@Composable
fun LoginScreen(
    state: LoginUiState,
    onEmailChange: (String) -> Unit,
    onSenhaChange: (String) -> Unit,
    onEntrar: () -> Unit,
    onCadastrar: () -> Unit,
    onEntrarComGoogle: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "CANAL SEGURO",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = SpyGreen,
            letterSpacing = 2.sp
        )
        Text(
            "AUTENTICAÇÃO DE AGENTE",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = SpyTextWhite.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(Modifier.height(40.dp))

        val fieldColors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SpyGreen,
            unfocusedBorderColor = SpyGreen.copy(alpha = 0.3f),
            focusedTextColor = SpyTextWhite,
            unfocusedTextColor = SpyTextWhite,
            cursorColor = SpyGreen,
            focusedLabelColor = SpyGreen,
            unfocusedLabelColor = SpyTextWhite.copy(alpha = 0.6f),
            focusedContainerColor = SpyGray,
            unfocusedContainerColor = SpyGray
        )

        OutlinedTextField(
            value = state.email,
            onValueChange = onEmailChange,
            singleLine = true,
            label = { Text("E-MAIL", fontFamily = FontFamily.Monospace) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(4.dp),
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth().testTag(LoginTestTags.CAMPO_EMAIL)
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = state.senha,
            onValueChange = onSenhaChange,
            singleLine = true,
            label = { Text("SENHA", fontFamily = FontFamily.Monospace) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(4.dp),
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth().testTag(LoginTestTags.CAMPO_SENHA)
        )

        if (state.erros.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                state.erros.forEach { erro ->
                    Text(
                        "! $erro",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = SpyRed
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        Button(
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SpyGreen),
            enabled = !state.carregando,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            onClick = onEntrar
        ) {
            Text("ENTRAR", color = SpyBlack, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, SpyGreen),
            enabled = !state.carregando,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            onClick = onCadastrar
        ) {
            Text("CADASTRAR", color = SpyGreen, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, SpyTextWhite.copy(alpha = 0.5f)),
            enabled = !state.carregando,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            onClick = onEntrarComGoogle
        ) {
            Text("ENTRAR COM GOOGLE", color = SpyTextWhite, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }

        if (state.carregando) {
            Spacer(Modifier.height(20.dp))
            CircularProgressIndicator(color = SpyGreen)
        }
    }
}
