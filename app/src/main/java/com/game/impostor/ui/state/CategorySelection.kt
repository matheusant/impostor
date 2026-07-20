package com.game.impostor.ui.state

import com.game.impostor.domain.model.DefaultCategory
import com.game.impostor.domain.model.RoundData

/** Categoria atualmente selecionada na UI (padrão embarcada, remota/Firestore ou customizada). */
sealed class CategorySelection {
    data class Default(val category: DefaultCategory) : CategorySelection()
    data class Remote(val tema: String, val rounds: List<RoundData>) : CategorySelection()
    data class Custom(val id: Int, val name: String, val rounds: List<RoundData>) : CategorySelection()

    val displayName: String
        get() = when (this) {
            is Default -> category.name
            is Remote -> tema
            is Custom -> name
        }
}
