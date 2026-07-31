package com.game.impostor.ui.screen

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.impostor.R
import com.game.impostor.domain.model.CategoriaCustom
import com.game.impostor.ui.state.IASuggestionRounds
import com.game.impostor.ui.theme.SpyBlack
import com.game.impostor.ui.theme.SpyGray
import com.game.impostor.ui.theme.SpyGreen
import com.game.impostor.ui.theme.SpyRed
import com.game.impostor.ui.theme.SpyTextWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFormScreen(
    initial: CategoriaCustom? = null,
    iaSuggestion: (String) -> Unit,
    iaSuggestionState: IASuggestionRounds,
    onSave: (String, List<Pair<String, String>>) -> Unit,
    onBack: () -> Unit
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val focusManger = LocalFocusManager.current
    val isEditing = initial != null
    var categoryName by remember(initial?.id) {
        mutableStateOf(initial?.nome ?: "")
    }
    var rounds by remember(initial?.id) {
        mutableStateOf(
            initial?.rodadas?.map { Pair(it.grupo, it.impostor) } ?: listOf(Pair("", ""))
        )
    }
    var isFabExpanded by remember { mutableStateOf(true) }

    LaunchedEffect(iaSuggestionState.rounds) {
        if (iaSuggestionState.rounds.isNotEmpty()) rounds =
            iaSuggestionState.rounds.map { Pair(it.grupo, it.impostor) }
    }

    LaunchedEffect(Unit) {
        delay(2000.milliseconds)
        isFabExpanded = false
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) { data -> CategoryFormSB(data) } },
        floatingActionButton = { if (!isEditing)
            CategoryFormFab(
                text = "Sugestões de IA",
                isExpanded = isFabExpanded
            ) {
                focusManger.clearFocus()
                if (categoryName.isNotBlank()) {
                    iaSuggestion(categoryName)
                } else {
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = "Digite o nome da categoria",
                            withDismissAction = true
                        )
                    }
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(top = 10.dp, start = 20.dp, end = 20.dp)
            .statusBarsPadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "< VOLTAR",
                    fontFamily = FontFamily.Monospace,
                    color = SpyGreen,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .clickable { onBack() }
                        .padding(end = 16.dp)
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
                    Text(
                        "ex: TECNOLOGIA",
                        fontFamily = FontFamily.Monospace,
                        color = SpyTextWhite.copy(alpha = 0.3f)
                    )
                },
                textStyle = TextStyle(fontFamily = FontFamily.Monospace, color = SpyTextWhite),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SpyGreen,
                    unfocusedBorderColor = SpyGreen.copy(alpha = 0.4f),
                    cursorColor = SpyGreen
                ),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isEditing
            )

            Spacer(Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "DIRETRIZES DA MISSÃO:",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = SpyTextWhite.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            rounds.forEachIndexed { index, round ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = SpyGray),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, SpyGreen.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
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
                                        .clickable {
                                            rounds =
                                                rounds.toMutableList().also { it.removeAt(index) }
                                        }
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
                                rounds = rounds.toMutableList()
                                    .also { it[index] = Pair(new, round.second) }
                            },
                            placeholder = {
                                Text(
                                    "Pergunta para o grupo...",
                                    fontFamily = FontFamily.Monospace,
                                    color = SpyTextWhite.copy(alpha = 0.3f),
                                    fontSize = 12.sp
                                )
                            },
                            textStyle = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                color = SpyTextWhite,
                                fontSize = 13.sp
                            ),
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
                                rounds = rounds.toMutableList()
                                    .also { it[index] = Pair(round.first, new) }
                            },
                            placeholder = {
                                Text(
                                    "Pergunta para o impostor...",
                                    fontFamily = FontFamily.Monospace,
                                    color = SpyTextWhite.copy(alpha = 0.3f),
                                    fontSize = 12.sp
                                )
                            },
                            textStyle = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                color = SpyTextWhite,
                                fontSize = 13.sp
                            ),
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                onClick = { rounds = rounds + Pair("", "") }
            ) {
                Text("+ ADICIONAR RODADA", color = SpyGreen, fontFamily = FontFamily.Monospace)
            }

            Spacer(Modifier.height(16.dp))

            val canSave by remember {
                derivedStateOf {
                    categoryName.isNotBlank() && rounds.isNotEmpty() &&
                            rounds.all { it.first.isNotBlank() && it.second.isNotBlank() }
                }
            }

            Button(
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (canSave) SpyGreen else SpyGray,
                    disabledContainerColor = SpyGray
                ),
                enabled = canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                onClick = { onSave(categoryName.uppercase(), rounds) }
            ) {
                Text(
                    if (isEditing) "ATUALIZAR CANAL" else "SALVAR CANAL",
                    color = if (canSave) SpyBlack else SpyTextWhite.copy(alpha = 0.3f),
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            if (iaSuggestionState.isLoading) {
                Spacer(Modifier.height(20.dp))
                CircularProgressIndicator(color = SpyGreen)
            }
        }
    }
}

@Composable
fun CategoryFormSB(data: SnackbarData) {
    Snackbar(
        snackbarData = data,
        containerColor = SpyGray,
        contentColor = SpyTextWhite.copy(alpha = 0.3f)
    )
}

@Composable
fun CategoryFormFab(text: String, isExpanded: Boolean, onClicked: () -> Unit) {
    ExtendedFloatingActionButton(
        text = { Text(text = text) },
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_auto_awesome_24),
                contentDescription = "Inteligência Artificial"
            )
        },
        onClick = onClicked,
        expanded = isExpanded,
        containerColor = SpyGreen,
        contentColor = SpyGray,
        modifier = Modifier.animateContentSize()
    )
}


@Preview(showSystemUi = true)
@Composable
private fun CategoryFormScreenPrev() {
    CategoryFormScreen(
        initial = null,
        iaSuggestion = {},
        onSave = { _, _ -> },
        onBack = {},
        iaSuggestionState = IASuggestionRounds()
    )
}
