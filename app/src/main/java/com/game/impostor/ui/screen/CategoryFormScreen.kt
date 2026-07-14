package com.game.impostor.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.impostor.data.CustomCategoryWithRounds
import com.game.impostor.ui.theme.SpyBlack
import com.game.impostor.ui.theme.SpyGray
import com.game.impostor.ui.theme.SpyGreen
import com.game.impostor.ui.theme.SpyRed
import com.game.impostor.ui.theme.SpyTextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFormScreen(
    initial: CustomCategoryWithRounds? = null,
    onSave: (String, List<Pair<String, String>>) -> Unit,
    onBack: () -> Unit
) {
    val isEditing = initial != null
    var categoryName by remember(initial?.category?.id) {
        mutableStateOf(initial?.category?.name ?: "")
    }
    var rounds by remember(initial?.category?.id) {
        mutableStateOf(
            if (initial != null)
                initial.rounds.map { Pair(it.grupo, it.impostor) }
            else
                listOf(Pair("", ""))
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 56.dp, bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "< VOLTAR",
                fontFamily = FontFamily.Monospace,
                color = SpyGreen,
                fontSize = 13.sp,
                modifier = Modifier.clickable { onBack() }.padding(end = 16.dp)
            )
            Text(
                if (isEditing) "EDITAR CANAL" else "NOVO CANAL",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = SpyTextWhite,
                fontSize = 16.sp
            )
        }

        Text(
            "IDENTIFICADOR DO CANAL:",
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = SpyTextWhite.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = categoryName,
            onValueChange = { categoryName = it },
            placeholder = {
                Text("ex: TECNOLOGIA", fontFamily = FontFamily.Monospace, color = SpyTextWhite.copy(alpha = 0.3f))
            },
            textStyle = TextStyle(fontFamily = FontFamily.Monospace, color = SpyTextWhite),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SpyGreen,
                unfocusedBorderColor = SpyGreen.copy(alpha = 0.4f),
                cursorColor = SpyGreen
            ),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(28.dp))

        Text(
            "DIRETRIZES DA MISSÃO:",
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = SpyTextWhite.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        rounds.forEachIndexed { index, round ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SpyGray),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, SpyGreen.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "RODADA ${index + 1}",
                            fontFamily = FontFamily.Monospace,
                            color = SpyGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        if (rounds.size > 1) {
                            Text(
                                "[−]",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SpyRed,
                                modifier = Modifier
                                    .clickable { rounds = rounds.toMutableList().also { it.removeAt(index) } }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "AGENTES:",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = SpyTextWhite.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = round.first,
                        onValueChange = { new ->
                            rounds = rounds.toMutableList().also { it[index] = Pair(new, round.second) }
                        },
                        placeholder = {
                            Text(
                                "Pergunta para o grupo...",
                                fontFamily = FontFamily.Monospace,
                                color = SpyTextWhite.copy(alpha = 0.3f),
                                fontSize = 12.sp
                            )
                        },
                        textStyle = TextStyle(fontFamily = FontFamily.Monospace, color = SpyTextWhite, fontSize = 13.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SpyGreen,
                            unfocusedBorderColor = SpyGreen.copy(alpha = 0.3f),
                            cursorColor = SpyGreen
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        "INFILTRADO:",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = SpyRed.copy(alpha = 0.8f),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = round.second,
                        onValueChange = { new ->
                            rounds = rounds.toMutableList().also { it[index] = Pair(round.first, new) }
                        },
                        placeholder = {
                            Text(
                                "Pergunta para o impostor...",
                                fontFamily = FontFamily.Monospace,
                                color = SpyTextWhite.copy(alpha = 0.3f),
                                fontSize = 12.sp
                            )
                        },
                        textStyle = TextStyle(fontFamily = FontFamily.Monospace, color = SpyTextWhite, fontSize = 13.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SpyRed,
                            unfocusedBorderColor = SpyRed.copy(alpha = 0.3f),
                            cursorColor = SpyRed
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Button(
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            border = BorderStroke(1.dp, SpyGreen.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth().height(45.dp),
            onClick = { rounds = rounds + Pair("", "") }
        ) {
            Text("+ ADICIONAR RODADA", color = SpyGreen, fontFamily = FontFamily.Monospace)
        }

        Spacer(Modifier.height(16.dp))

        val canSave = categoryName.isNotBlank() && rounds.isNotEmpty() &&
                rounds.all { it.first.isNotBlank() && it.second.isNotBlank() }
        Button(
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (canSave) SpyGreen else SpyGray,
                disabledContainerColor = SpyGray
            ),
            enabled = canSave,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            onClick = { onSave(categoryName, rounds) }
        ) {
            Text(
                if (isEditing) "ATUALIZAR CANAL" else "SALVAR CANAL",
                color = if (canSave) SpyBlack else SpyTextWhite.copy(alpha = 0.3f),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}
