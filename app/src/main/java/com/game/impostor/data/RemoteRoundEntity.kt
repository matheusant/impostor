package com.game.impostor.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/** Rodada de um tema remoto em cache (1—N com [RemoteThemeEntity]). */
@Entity(
    tableName = "remote_rounds",
    foreignKeys = [ForeignKey(
        entity = RemoteThemeEntity::class,
        parentColumns = ["tema"],
        childColumns = ["temaId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class RemoteRoundEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(index = true) val temaId: String,
    val grupo: String,
    val impostor: String
)
