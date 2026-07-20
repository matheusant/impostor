package com.game.impostor

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.game.impostor.data.AppDatabase
import com.game.impostor.data.RemoteRoundEntity
import com.game.impostor.data.RemoteThemeDao
import com.game.impostor.data.RemoteThemeEntity
import com.game.impostor.data.mapper.ThemeJsonParser
import com.game.impostor.data.remote.FirestoreThemeMapper
import com.game.impostor.data.repository.ThemeRepositoryImpl
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Sem rede (Firestore com network desabilitada), observarTemas() deve emitir
 * os temas gravados no cache do Room (fallback offline).
 */
@RunWith(AndroidJUnit4::class)
class ThemeCacheInstrumentedTest {

    private lateinit var context: Context
    private lateinit var db: AppDatabase
    private lateinit var dao: RemoteThemeDao

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.remoteThemeDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun sem_rede_emite_temas_do_cache() = runBlocking {
        dao.substituirTudo(
            themes = listOf(RemoteThemeEntity(tema = "Cotidiano")),
            rounds = listOf(RemoteRoundEntity(temaId = "Cotidiano", grupo = "g1", impostor = "i1"))
        )

        val firestore = FirebaseFirestore.getInstance()
        Tasks.await(firestore.disableNetwork())
        try {
            val repo = ThemeRepositoryImpl(
                context = context,
                parser = ThemeJsonParser(),
                auth = FirebaseAuth.getInstance(),
                firestore = firestore,
                remoteThemeDao = dao,
                firestoreMapper = FirestoreThemeMapper(),
                io = Dispatchers.IO
            )

            val temas = repo.observarTemas().first()

            assertEquals(1, temas.size)
            assertEquals("Cotidiano", temas.first().tema)
            assertEquals(1, temas.first().rodadas.size)
        } finally {
            Tasks.await(firestore.enableNetwork())
        }
    }
}
