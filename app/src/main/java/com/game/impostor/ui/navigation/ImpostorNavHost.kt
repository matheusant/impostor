package com.game.impostor.ui.navigation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.game.impostor.R
import com.game.impostor.domain.model.CategoriaCustom
import com.game.impostor.domain.model.DEFAULT_CATEGORIES
import com.game.impostor.domain.model.RoundData
import com.game.impostor.ui.screen.CategoryFormScreen
import com.game.impostor.ui.screen.CategorySelectScreen
import com.game.impostor.ui.screen.GamePlayScreen
import com.game.impostor.ui.screen.LoginScreen
import com.game.impostor.ui.screen.PassPhoneScreen
import com.game.impostor.ui.screen.SetupScreen
import com.game.impostor.ui.state.CategorySelection
import com.game.impostor.ui.theme.SpyBlack
import com.game.impostor.ui.viewmodel.CategoryViewModel
import com.game.impostor.ui.viewmodel.GameViewModel
import com.game.impostor.ui.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

object Routes {
    const val LOGIN = "login"
    const val SETUP = "setup"
    const val CATEGORY_SELECT = "category_select"
    const val CATEGORY_CREATE = "category_create"
    const val CATEGORY_EDIT = "category_edit"
    const val PASS_PHONE = "pass_phone"
    const val GAME_PLAY = "game_play"
}

@Composable
fun ImpostorApp(
    gameViewModel: GameViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val gameState by gameViewModel.uiState.collectAsState()
    val customCategories by categoryViewModel.categorias.collectAsState()
    val remoteThemes by gameViewModel.temasRemotos.collectAsState()

    // Categoria sendo editada (equivalente ao antigo estado editingCategory do GameEngine).
    var editing by remember { mutableStateOf<CategoriaCustom?>(null) }

    // Já autenticado (sessão Firebase ativa) → pula o login; senão, login é o destino inicial.
    val startDestination = remember {
        if (loginViewModel.uiState.value.autenticado) Routes.SETUP else Routes.LOGIN
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.fillMaxSize().background(SpyBlack)
    ) {
        composable(Routes.LOGIN) {
            val loginState by loginViewModel.uiState.collectAsState()
            val context = LocalContext.current
            val webClientId = stringResource(R.string.firebase_web_client_id)

            val googleLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { resultado ->
                try {
                    val conta = GoogleSignIn
                        .getSignedInAccountFromIntent(resultado.data)
                        .getResult(ApiException::class.java)
                    val idToken = conta.idToken
                    if (idToken != null) {
                        loginViewModel.entrarComGoogle(idToken)
                    } else {
                        loginViewModel.reportarErro("Não foi possível obter o token do Google.")
                    }
                } catch (e: ApiException) {
                    loginViewModel.reportarErro("Login com Google cancelado ou indisponível.")
                }
            }

            LaunchedEffect(loginState.autenticado) {
                if (loginState.autenticado) {
                    navController.navigate(Routes.SETUP) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }

            LoginScreen(
                state = loginState,
                onEmailChange = loginViewModel::onEmailChange,
                onSenhaChange = loginViewModel::onSenhaChange,
                onEntrar = loginViewModel::entrar,
                onCadastrar = loginViewModel::cadastrar,
                onEntrarComGoogle = {
                    if (webClientId.isBlank()) {
                        loginViewModel.reportarErro("Google Sign-In ainda não configurado (defina o web client id).")
                    } else {
                        val opcoes = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(webClientId)
                            .requestEmail()
                            .build()
                        val cliente = GoogleSignIn.getClient(context, opcoes)
                        googleLauncher.launch(cliente.signInIntent)
                    }
                }
            )
        }

        composable(Routes.SETUP) {
            SetupScreen(
                totalPlayers = gameState.totalPlayers,
                onTotalPlayersChange = gameViewModel::definirTotalJogadores,
                selectedCategory = gameState.selectedCategory,
                onOpenCategories = { navController.navigate(Routes.CATEGORY_SELECT) },
                onStart = {
                    if (gameViewModel.iniciar()) navController.navigate(Routes.PASS_PHONE)
                }
            )
        }

        composable(Routes.CATEGORY_SELECT) {
            CategorySelectScreen(
                remoteThemes = remoteThemes,
                customCategories = customCategories,
                selectedCategory = gameState.selectedCategory,
                onSelectRemote = { tema ->
                    gameViewModel.selecionarCategoria(CategorySelection.Remote(tema.tema, tema.rodadas))
                    navController.popBackStack()
                },
                onSelectCustom = { cat ->
                    gameViewModel.selecionarCategoria(
                        CategorySelection.Custom(cat.id, cat.nome, cat.rodadas)
                    )
                    navController.popBackStack()
                },
                onDeleteCustom = { id ->
                    val selecionada = gameState.selectedCategory
                    if (selecionada is CategorySelection.Custom && selecionada.id == id) {
                        gameViewModel.selecionarCategoria(CategorySelection.Default(DEFAULT_CATEGORIES.first()))
                    }
                    categoryViewModel.excluir(id)
                },
                onEditCustom = { cat ->
                    editing = cat
                    navController.navigate(Routes.CATEGORY_EDIT)
                },
                onCreateNew = { navController.navigate(Routes.CATEGORY_CREATE) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.CATEGORY_CREATE) {
            CategoryFormScreen(
                initial = null,
                onSave = { nome, rodadas ->
                    categoryViewModel.salvar(nome, rodadas)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.CATEGORY_EDIT) {
            CategoryFormScreen(
                initial = editing,
                onSave = { nome, rodadas ->
                    val cat = editing
                    if (cat != null) {
                        categoryViewModel.atualizar(cat.id, nome, rodadas)
                        val selecionada = gameState.selectedCategory
                        if (selecionada is CategorySelection.Custom && selecionada.id == cat.id) {
                            gameViewModel.selecionarCategoria(
                                CategorySelection.Custom(
                                    cat.id,
                                    nome,
                                    rodadas.map { RoundData(it.first, it.second) }
                                )
                            )
                        }
                    }
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PASS_PHONE) {
            PassPhoneScreen(
                currentPlayerView = gameState.currentPlayerView,
                impostorIndex = gameState.impostorIndex,
                currentRoundData = gameState.currentRoundData,
                isTextVisible = gameState.isTextVisible,
                onReveal = gameViewModel::revelar,
                onNext = {
                    if (!gameViewModel.proximo()) {
                        navController.navigate(Routes.GAME_PLAY) {
                            popUpTo(Routes.PASS_PHONE) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Routes.GAME_PLAY) {
            GamePlayScreen(
                onRestart = {
                    gameViewModel.reiniciar()
                    navController.navigate(Routes.SETUP) {
                        popUpTo(Routes.SETUP) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
