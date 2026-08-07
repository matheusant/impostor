package com.game.impostor.ui.features.category

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.impostor.domain.model.CategoriaCustom
import com.game.impostor.domain.model.ThemeConfig
import com.game.impostor.ui.components.dialogs.ImpostorAlertDialog
import com.game.impostor.ui.features.category.components.ImpostorCategoryCard
import com.game.impostor.ui.theme.SpyGreen
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
        ImpostorAlertDialog(
            title = "CONFIRMAR EXCLUSÃO",
            bodyText = "Deseja remover este canal permanentemente?",
            onConfirm = { onDeleteCustom(deleteConfirmId!!); deleteConfirmId = null },
            confirmText = "CONFIRMAR",
            onDismiss = { deleteConfirmId = null }
        )
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .navigationBarsPadding()
        .padding(horizontal = 20.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 56.dp, bottom = 24.dp),
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
                ImpostorCategoryCard(
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
                ImpostorCategoryCard(
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
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
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
