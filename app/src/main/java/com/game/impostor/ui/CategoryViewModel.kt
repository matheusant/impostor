package com.game.impostor.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.game.impostor.data.AppDatabase
import com.game.impostor.data.CustomCategoryEntity
import com.game.impostor.data.CustomCategoryWithRounds
import com.game.impostor.data.CustomRoundEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).categoryDao()

    val customCategories: StateFlow<List<CustomCategoryWithRounds>> = dao.getAllWithRounds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveCategory(name: String, rounds: List<Pair<String, String>>) {
        viewModelScope.launch {
            val id = dao.insertCategory(CustomCategoryEntity(name = name)).toInt()
            dao.insertRounds(rounds.map { (grupo, impostor) ->
                CustomRoundEntity(categoryId = id, grupo = grupo, impostor = impostor)
            })
        }
    }

    fun deleteCategory(id: Int) {
        viewModelScope.launch {
            dao.deleteCategory(id)
        }
    }

    fun updateCategory(id: Int, name: String, rounds: List<Pair<String, String>>) {
        viewModelScope.launch {
            dao.updateCategoryName(id, name)
            dao.deleteRoundsForCategory(id)
            dao.insertRounds(rounds.map { (grupo, impostor) ->
                CustomRoundEntity(categoryId = id, grupo = grupo, impostor = impostor)
            })
        }
    }
}
