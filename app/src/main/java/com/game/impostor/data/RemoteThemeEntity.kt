package com.game.impostor.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Cache local (Room) de um tema vindo do Firestore. Separado de `custom_*`
 * (categorias do usuário): estes são só espelho offline dos temas remotos.
 * A chave é o próprio `tema` (nome único do documento no Firestore).
 */
@Entity(tableName = "remote_themes")
data class RemoteThemeEntity(
    @PrimaryKey val tema: String
)
