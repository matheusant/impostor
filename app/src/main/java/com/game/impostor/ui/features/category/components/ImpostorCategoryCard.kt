package com.game.impostor.ui.features.category.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.impostor.ui.theme.SpyGray
import com.game.impostor.ui.theme.SpyGreen
import com.game.impostor.ui.theme.SpyRed
import com.game.impostor.ui.theme.SpyTextWhite

@Composable
fun ImpostorCategoryCard(
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
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onSelect() }
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)) {
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
                        modifier = Modifier
                            .clickable { onEditRequest() }
                            .padding(4.dp)
                    )
                    Text(
                        "[X]",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SpyRed,
                        modifier = Modifier
                            .clickable { onDeleteRequest() }
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}