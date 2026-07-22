package com.game.impostor.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.impostor.domain.model.DefaultCategory
import com.game.impostor.ui.state.CategorySelection
import com.game.impostor.ui.theme.ImpostorTheme
import com.game.impostor.ui.theme.SpyBlack
import com.game.impostor.ui.theme.SpyGray
import com.game.impostor.ui.theme.SpyGreen
import com.game.impostor.ui.theme.SpyTextWhite

@Composable
fun SetupScreen(
    totalPlayers: Int,
    onTotalPlayersChange: (Int) -> Unit,
    selectedCategory: CategorySelection,
    onOpenCategories: () -> Unit,
    onStart: () -> Unit,
    onSair: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            .navigationBarsPadding()
    ) {
        Text(
            "x SAIR",
            fontFamily = FontFamily.Monospace,
            color = SpyGreen,
            fontSize = 13.sp,
            modifier = Modifier.clickable { onSair() }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "LINHA_CRUZADA.EXE",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = SpyGreen,
                letterSpacing = 2.sp
            )
            Text(
                "SISTEMA DE TRIAGEM DE AGENTES",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = SpyTextWhite.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(Modifier.height(48.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = SpyGray),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, SpyGreen.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "AGENTES NA ESCUTA: $totalPlayers",
                        fontFamily = FontFamily.Monospace,
                        color = SpyTextWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Slider(
                        value = totalPlayers.toFloat(),
                        onValueChange = { onTotalPlayersChange(it.toInt()) },
                        valueRange = 3f..8f,
                        steps = 4,
                        colors = SliderDefaults.colors(
                            thumbColor = SpyGreen,
                            activeTrackColor = SpyGreen,
                            inactiveTrackColor = SpyGray
                        )
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "CANAL SELECIONADO:",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = SpyTextWhite,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 8.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = SpyGray),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, SpyGreen.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenCategories() }
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        selectedCategory.displayName.uppercase(),
                        fontFamily = FontFamily.Monospace,
                        color = SpyGreen,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "  MUDAR >",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = SpyTextWhite.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

            Button(
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SpyGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                onClick = onStart
            ) {
                Text(
                    "INICIAR TRANSMISSÃO",
                    color = SpyBlack,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
