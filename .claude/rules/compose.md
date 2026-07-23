---
paths:
  - "app/src/main/java/com/game/impostor/ui/**"
---
# Regras de UI (Jetpack Compose)

- Use **somente** as cores de `ui/theme/Color.kt`: `SpyBlack`, `SpyGray`, `SpyGreen`,
  `SpyRed`, `SpyTextWhite`. Nunca escreva `Color(0x...)` numa tela.
- `SpyGreen` = canal seguro / grupo; `SpyRed` = alerta / impostor / infiltrado. Respeite esse significado.
- Composables são **stateless**: recebem estado + callbacks (`on...`). O estado do jogo vive no
  `GameViewModel` (`StateFlow<GameUiState>`), o de categorias no `CategoryViewModel` — ambos
  `@HiltViewModel`, obtidos via `hiltViewModel()`. Telas não acessam ViewModel/DB diretamente.
- Navegação usa **Navigation-Compose**: `NavHost` + rotas em `ui/navigation/ImpostorNavHost.kt`
  (constantes em `Routes`). Não volte a navegar por `backStack` manual de strings.
- Fonte padrão da UI é `FontFamily.Monospace` (assinatura visual). Textos em português.
- Telas ficam em `ui/screen/`; cada tela em seu arquivo, exportando um `@Composable` público.
