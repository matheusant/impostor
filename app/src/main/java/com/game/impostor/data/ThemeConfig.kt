package com.game.impostor.data

data class ThemeConfig(
    val tema: String,
    val rodadas: List<RoundData>
)

data class RoundData(
    val grupo: String,
    val impostor: String
)