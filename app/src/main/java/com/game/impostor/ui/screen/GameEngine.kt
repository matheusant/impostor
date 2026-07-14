package com.game.impostor.ui.screen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.game.impostor.data.CustomCategoryWithRounds
import com.game.impostor.data.RoundData
import com.game.impostor.data.ThemeConfig
import com.game.impostor.ui.CategoryViewModel
import com.game.impostor.ui.theme.SpyBlack
import org.json.JSONObject

data class DefaultCategory(val name: String, val fileName: String)

val DEFAULT_CATEGORIES = listOf(
    DefaultCategory("Cotidiano", "cotidiano.json"),
    DefaultCategory("Cultura Pop", "cultura_pop.json"),
    DefaultCategory("Relacionamentos", "relacionamentos.json")
)

sealed class CategorySelection {
    data class Default(val category: DefaultCategory) : CategorySelection()
    data class Custom(val id: Int, val name: String, val rounds: List<RoundData>) : CategorySelection()

    val displayName: String
        get() = when (this) {
            is Default -> category.name
            is Custom -> name
        }

    fun loadRounds(context: Context): List<RoundData> = when (this) {
        is Default -> loadThemeFromAssets(context, category.fileName).rodadas
        is Custom -> rounds
    }
}

fun loadThemeFromAssets(context: Context, fileName: String): ThemeConfig {
    return try {
        val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(jsonString)
        val temaNome = jsonObject.getString("tema")
        val rodadasArray = jsonObject.getJSONArray("rodadas")
        val listData = mutableListOf<RoundData>()
        for (i in 0 until rodadasArray.length()) {
            val item = rodadasArray.getJSONObject(i)
            listData.add(RoundData(grupo = item.getString("grupo"), impostor = item.getString("impostor")))
        }
        ThemeConfig(tema = temaNome, rodadas = listData)
    } catch (e: Exception) {
        e.printStackTrace()
        ThemeConfig(
            tema = "Erro",
            rodadas = listOf(RoundData(grupo = "Arquivo não encontrado.", impostor = "Arquivo não encontrado."))
        )
    }
}

@Composable
fun GameEngine(vm: CategoryViewModel = viewModel()) {
    val context = LocalContext.current

    val backStack = remember { mutableStateListOf("setup") }
    val currentScreen = backStack.last()

    var totalPlayers by remember { mutableIntStateOf(4) }
    var selectedCategory by remember { mutableStateOf<CategorySelection>(CategorySelection.Default(DEFAULT_CATEGORIES[0])) }
    var currentRoundData by remember { mutableStateOf<RoundData?>(null) }
    var impostorIndex by remember { mutableIntStateOf(0) }
    var currentPlayerView by remember { mutableIntStateOf(0) }
    var isTextVisible by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<CustomCategoryWithRounds?>(null) }

    val customCategories by vm.customCategories.collectAsState()

    BackHandler(enabled = backStack.size > 1) {
        backStack.removeAt(backStack.lastIndex)
    }

    Box(modifier = Modifier.fillMaxSize().background(SpyBlack)) {
        when (currentScreen) {
            "setup" -> SetupScreen(
                totalPlayers = totalPlayers,
                onTotalPlayersChange = { totalPlayers = it },
                selectedCategory = selectedCategory,
                onOpenCategories = { backStack.add("category_select") },
                onStart = {
                    val rounds = selectedCategory.loadRounds(context)
                    if (rounds.isEmpty()) return@SetupScreen
                    currentRoundData = rounds.random()
                    impostorIndex = (0 until totalPlayers).random()
                    currentPlayerView = 0
                    isTextVisible = false
                    backStack.add("pass_phone")
                }
            )

            "category_select" -> CategorySelectScreen(
                customCategories = customCategories,
                selectedCategory = selectedCategory,
                onSelectDefault = { selectedCategory = CategorySelection.Default(it); backStack.removeAt(backStack.lastIndex) },
                onSelectCustom = { cat ->
                    selectedCategory = CategorySelection.Custom(
                        id = cat.category.id,
                        name = cat.category.name,
                        rounds = cat.rounds.map { RoundData(it.grupo, it.impostor) }
                    )
                    backStack.removeAt(backStack.lastIndex)
                },
                onDeleteCustom = { id ->
                    if ((selectedCategory as? CategorySelection.Custom)?.id == id) {
                        selectedCategory = CategorySelection.Default(DEFAULT_CATEGORIES[0])
                    }
                    vm.deleteCategory(id)
                },
                onEditCustom = { cat -> editingCategory = cat; backStack.add("category_edit") },
                onCreateNew = { backStack.add("category_create") },
                onBack = { backStack.removeAt(backStack.lastIndex) }
            )

            "category_create" -> CategoryFormScreen(
                initial = null,
                onSave = { name, rounds -> vm.saveCategory(name, rounds); backStack.removeAt(backStack.lastIndex) },
                onBack = { backStack.removeAt(backStack.lastIndex) }
            )

            "category_edit" -> CategoryFormScreen(
                initial = editingCategory,
                onSave = { name, rounds ->
                    val cat = editingCategory ?: return@CategoryFormScreen
                    vm.updateCategory(cat.category.id, name, rounds)
                    if ((selectedCategory as? CategorySelection.Custom)?.id == cat.category.id) {
                        selectedCategory = CategorySelection.Custom(
                            id = cat.category.id,
                            name = name,
                            rounds = rounds.map { RoundData(it.first, it.second) }
                        )
                    }
                    backStack.removeAt(backStack.lastIndex)
                },
                onBack = { backStack.removeAt(backStack.lastIndex) }
            )

            "pass_phone" -> PassPhoneScreen(
                currentPlayerView = currentPlayerView,
                impostorIndex = impostorIndex,
                currentRoundData = currentRoundData,
                isTextVisible = isTextVisible,
                onReveal = { isTextVisible = true },
                onNext = {
                    if (currentPlayerView < totalPlayers - 1) {
                        currentPlayerView++
                        isTextVisible = false
                    } else {
                        backStack[backStack.lastIndex] = "game_play"
                    }
                }
            )

            "game_play" -> GamePlayScreen(onRestart = {
                backStack.clear()
                backStack.add("setup")
            })
        }
    }
}
