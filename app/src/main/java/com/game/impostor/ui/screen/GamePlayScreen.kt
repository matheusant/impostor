package com.game.impostor.ui.screen

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
import com.game.impostor.ui.theme.SpyBlack
import com.game.impostor.ui.theme.SpyGreen
import com.game.impostor.ui.theme.SpyRed
import com.game.impostor.ui.theme.SpyTextWhite

@Composable
fun GamePlayScreen(onRestart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "🔴 DEBATE EM ANDAMENTO",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = SpyRed
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "PROTOCOLO DE INTERROGATÓRIO ATIVADO.\n\nCada elemento deve fornecer um relatório de apenas UMA palavra em ordem.\n\nLocalizem a linha cruzada na rede de comunicação.",
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            color = SpyTextWhite,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(48.dp))
        Button(
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SpyGreen),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            onClick = onRestart
        ) {
            Text(
                "FINALIZAR MISSÃO / REBOOT",
                color = SpyBlack,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
