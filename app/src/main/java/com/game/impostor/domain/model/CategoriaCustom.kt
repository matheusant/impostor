package com.game.impostor.domain.model

/** Categoria customizada criada pelo usuário e persistida localmente. */
data class CategoriaCustom(
    val id: Int,
    val nome: String,
    val rodadas: List<RoundData>
)
