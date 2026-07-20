package com.game.impostor.domain.model

/**
 * Modelo de domínio de um tema (categoria) e suas rodadas.
 * Campos `tema`, `rodadas`, `grupo`, `impostor` são canônicos (ver rules/room.md).
 */
data class ThemeConfig(
    val tema: String,
    val rodadas: List<RoundData>
)

data class RoundData(
    val grupo: String,
    val impostor: String
)
