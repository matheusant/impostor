package com.game.impostor.data

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Aguarda um [Task] do Google Play Services / Firebase de forma suspensa,
 * sem depender de `kotlinx-coroutines-play-services`. Os callbacks do Task
 * são não bloqueantes (postados no looper principal), então é seguro chamar
 * de qualquer dispatcher.
 */
internal suspend fun <T> Task<T>.awaitResult(): T = suspendCancellableCoroutine { cont ->
    addOnCompleteListener { task ->
        if (task.isSuccessful) {
            cont.resume(task.result)
        } else {
            cont.resumeWithException(task.exception ?: RuntimeException("Falha desconhecida no Firebase."))
        }
    }
}
