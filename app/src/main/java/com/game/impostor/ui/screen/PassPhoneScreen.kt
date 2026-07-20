package com.game.impostor.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.impostor.domain.model.RoundData
import com.game.impostor.ui.theme.SpyBlack
import com.game.impostor.ui.theme.SpyGray
import com.game.impostor.ui.theme.SpyGreen
import com.game.impostor.ui.theme.SpyRed
import com.game.impostor.ui.theme.SpyTextWhite

@Composable
fun PassPhoneScreen(
    currentPlayerView: Int,
    impostorIndex: Int,
    currentRoundData: RoundData?,
    isTextVisible: Boolean,
    onReveal: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "INSPEÇÃO DE SEGURANÇA",
            fontFamily = FontFamily.Monospace,
            color = SpyRed,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "DISPOSITIVO DESTINADO AO:",
            fontFamily = FontFamily.Monospace,
            color = SpyTextWhite.copy(alpha = 0.6f),
            fontSize = 12.sp
        )
        Text(
            "AGENTE 0${currentPlayerView + 1}",
            fontFamily = FontFamily.Monospace,
            color = SpyGreen,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            "Certifique-se de que nenhum outro elemento esteja olhando para a tela antes de decodificar o arquivo.",
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = SpyTextWhite,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        if (isTextVisible && currentRoundData != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SpyGray),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(2.dp, if (currentPlayerView == impostorIndex) SpyRed else SpyGreen),
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        if (currentPlayerView == impostorIndex) "⚠️ INFILTRADO / INSTRUÇÕES" else "🔒 CANAL SEGURO / DIRETRIZ",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (currentPlayerView == impostorIndex) SpyRed else SpyGreen,
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        if (currentPlayerView == impostorIndex) currentRoundData.impostor else currentRoundData.grupo,
                        color = SpyTextWhite,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp
                    )
                }
            }
        }

        Button(
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (!isTextVisible) SpyGreen else SpyTextWhite.copy(alpha = 0.2f)
            ),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            onClick = { if (!isTextVisible) onReveal() else onNext() }
        ) {
            Text(
                if (!isTextVisible) "DECODIR DIRETRIZ" else "CRIPTOGRAFAR E PROSSEGUIR",
                fontFamily = FontFamily.Monospace,
                color = if (!isTextVisible) SpyBlack else SpyTextWhite,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
