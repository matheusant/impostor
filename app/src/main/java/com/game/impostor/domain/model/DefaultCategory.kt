package com.game.impostor.domain.model

/** Tema padrão embarcado nos assets do app. */
data class DefaultCategory(val name: String, val fileName: String)

/**
 * Registro dos temas padrão. Ao adicionar um JSON em assets/, registre-o aqui
 * (ver CLAUDE.md / rules/assets-categorias.md).
 */
val DEFAULT_CATEGORIES = listOf(
    DefaultCategory("Cotidiano", "cotidiano.json"),
    DefaultCategory("Cultura Pop", "cultura_pop.json"),
    DefaultCategory("Relacionamentos", "relacionamentos.json")
)
