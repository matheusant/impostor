package com.game.impostor.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.impostor.domain.model.CategoriaCustom
import com.game.impostor.domain.model.ThemeConfig
import com.game.impostor.ui.state.CategorySelection
import com.game.impostor.ui.theme.SpyGray
import com.game.impostor.ui.theme.SpyGreen
import com.game.impostor.ui.theme.SpyRed
import com.game.impostor.ui.theme.SpyTextWhite

@Composable
fun CategorySelectScreen(
    remoteThemes: List<ThemeConfig>,
    customCategories: List<CategoriaCustom>,
    selectedCategory: CategorySelection,
    onSelectRemote: (ThemeConfig) -> Unit,
    onSelectCustom: (CategoriaCustom) -> Unit,
    onDeleteCustom: (Int) -> Unit,
    onEditCustom: (CategoriaCustom) -> Unit,
    onCreateNew: () -> Unit,
    onBack: () -> Unit
) {
    var deleteConfirmId by remember { mutableStateOf<Int?>(null) }

    if (deleteConfirmId != null) {
        AlertDialog(
            onDismissRequest = { deleteConfirmId = null },
            containerColor = SpyGray,
            title = {
                Text(
                    "CONFIRMAR EXCLUSÃO",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = SpyRed
                )
            },
            text = {
                Text(
                    "Deseja remover este canal permanentemente?",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = SpyTextWhite
                )
            },
            confirmButton = {
                TextButton(onClick = { onDeleteCustom(deleteConfirmId!!); deleteConfirmId = null }) {
                    Text("CONFIRMAR", color = SpyRed, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmId = null }) {
                    Text("CANCELAR", color = SpyTextWhite.copy(alpha = 0.7f), fontFamily = FontFamily.Monospace)
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().navigationBarsPadding().padding(horizontal = 20.dp)) {
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
                "CANAIS DISPONÍVEIS",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = SpyTextWhite,
                fontSize = 16.sp
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(remoteThemes, key = { it.tema }) { tema ->
                val isSelected = selectedCategory !is CategorySelection.Custom &&
                        selectedCategory.displayName == tema.tema
                CategoryCard(
                    name = tema.tema,
                    isSelected = isSelected,
                    isCustom = false,
                    onSelect = { onSelectRemote(tema) },
                    onDeleteRequest = {},
                    onEditRequest = {}
                )
            }
            items(customCategories, key = { it.id }) { cat ->
                val isSelected = selectedCategory is CategorySelection.Custom &&
                        selectedCategory.id == cat.id
                CategoryCard(
                    name = cat.nome,
                    isSelected = isSelected,
                    isCustom = true,
                    onSelect = { onSelectCustom(cat) },
                    onDeleteRequest = { deleteConfirmId = cat.id },
                    onEditRequest = { onEditCustom(cat) }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Button(
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            border = BorderStroke(1.dp, SpyGreen),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            onClick = onCreateNew
        ) {
            Text(
                "+ CRIAR NOVO CANAL",
                color = SpyGreen,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
fun CategoryCard(
    name: String,
    isSelected: Boolean,
    isCustom: Boolean,
    onSelect: () -> Unit,
    onDeleteRequest: () -> Unit,
    onEditRequest: () -> Unit
) {
    val borderColor = if (isSelected) SpyGreen else SpyGreen.copy(alpha = 0.2f)
    val bgColor = if (isSelected) SpyGreen.copy(alpha = 0.12f) else SpyGray

    Card(
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth().aspectRatio(1f).clickable { onSelect() }
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = name.uppercase(),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) SpyGreen else SpyTextWhite,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                if (isSelected) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "● ATIVO",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = SpyGreen
                    )
                }
            }
            if (isCustom) {
                Row(
                    modifier = Modifier.align(Alignment.TopEnd),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        "[✎]",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = SpyGreen,
                        modifier = Modifier.clickable { onEditRequest() }.padding(4.dp)
                    )
                    Text(
                        "[X]",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SpyRed,
                        modifier = Modifier.clickable { onDeleteRequest() }.padding(4.dp)
                    )
                }
            }
        }
    }
}
