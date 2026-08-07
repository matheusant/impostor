package com.game.impostor.ui.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.game.impostor.ui.theme.SpyGray
import com.game.impostor.ui.theme.SpyRed
import com.game.impostor.ui.theme.SpyTextWhite

@Composable
fun ImpostorAlertDialog(
    modifier: Modifier = Modifier,
    title: String,
    bodyText: String,
    onConfirm: () -> Unit,
    confirmText: String,
    onDismiss: () -> Unit,
    dismissText: String = "CANCELAR"
    ) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SpyGray,
        modifier = modifier,
        title = {
            Text(
                title,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = SpyRed
            )
        },
        text = {
            Text(
                bodyText,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                color = SpyTextWhite
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText, color = SpyRed, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText, color = SpyTextWhite.copy(alpha = 0.7f), fontFamily = FontFamily.Monospace)
            }
        }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF121212, showSystemUi = true)
@Composable
private fun ImpostorAlertDialogPrev() {
    ImpostorAlertDialog(
        title = "CONFIRMAR EXCLUSÃO",
        bodyText = "Deseja excluir o canal atual?",
        onConfirm = {},
        confirmText = "CONFIRMAR",
        onDismiss = {},
    )
}