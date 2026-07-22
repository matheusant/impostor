# PLAN.md

> Contrato de execução entre você e os agents. Escrito para ser autocontido: o agent que
> executa uma task tem **zero contexto** da conversa de planejamento. Ordem das fases
> respeita dependências reais; tasks da mesma fase são genuinamente independentes.
> Só o orquestrador marca os checkboxes. Base (scaffold) já existe e o smoke test passa
> (`assembleDebug` + `testDebugUnitTest`).

## Sprint 0 — Arquitetura (Clean + MVVM + Hilt) + Firebase (auth + temas) (PRIORIDADE — antes da Sprint 1)

> **Escopo expandido (aprovado pelo usuário).** Além do Firebase (login **Google** + **e-mail/senha**
> e Cloud Firestore com **cache offline** via Room), a Sprint 0 primeiro reestrutura o app em
> **Clean Architecture + MVVM** com **Hilt** (DI), **camada de domínio (UseCases)**, migração do
> `GameEngine` para **`GameViewModel`** e **Navigation-Compose** — com um **baseline de testes**
> (unitários + instrumentados). Decisões travadas: pacotes `domain/ data/ di/ ui/`; Navigation-Compose.
> **Mudança de escopo aprovada:** rede via Firebase contraria o "100% offline" original — `SPEC.md`
> e `rules/seguranca.md` já permitem. `CLAUDE.md` e `rules/*` foram atualizados para a nova arquitetura.
>
> Pré-requisitos de setup (uma vez):
> - [x] Version catalog: `firebase-bom`, `firebase-auth`, `firebase-firestore`, `play-services-auth`,
>   Hilt (`hilt-android`/`hilt-android-compiler`/`hilt-navigation-compose`, `2.59.2`), `navigation-compose`,
>   libs de teste (`kotlinx-coroutines-test`, `turbine`, `arch-core-testing`, `hilt-android-testing`, `room-testing`, `org.json`).
> - [x] Plugins `com.google.gms.google-services` + `com.google.dagger.hilt.android`; `app/google-services.json` presente.
> - [x] Permissão `INTERNET` no `AndroidManifest.xml`; `ImpostorApplication` (`@HiltAndroidApp`) registrada.
> - [x] Configurar Firestore Security Rules exigindo `request.auth != null` (no console Firebase, na Fase 0.B).

### Fase 0.0 — Fundação: Clean Architecture + MVVM + Hilt + Navigation (CONCLUÍDA)
> Dependências: pré-requisitos de setup. Objetivo: endireitar a base **preservando o comportamento atual**
> (setup → passa-telefone → game-play; CRUD de categorias).

#### Task 0.0.1 — Hilt + camadas base (domain/data/di) + CategoryViewModel
- [x] `domain/model` (ThemeConfig, RoundData, CategoriaCustom, DefaultCategory); `domain/repository`
  (CategoryRepository, ThemeRepository); `domain/usecase` (categoria); `data/repository` (impls);
  `di/` (DatabaseModule, RepositoryModule, DispatcherModule).
- [x] `CategoryViewModel` → `@HiltViewModel` puro (`StateFlow<List<CategoriaCustom>>`), via UseCases (não mais `AndroidViewModel`).
- Testes: [x] `CategoryRepositoryImplTest` (fake DAO: observar/salvar/atualizar/excluir);
  [x] (instrumentado) `DiGraphSmokeTest` monta o grafo de DI e provê os repositories.

#### Task 0.0.2 — GameEngine → GameViewModel + UseCases de sorteio + Navigation-Compose
- [x] `domain/usecase/SortearImpostorUseCase` e `SortearRodadaUseCase` (puros, `Random` injetável) — **cobre a antiga Task 1.1**.
- [x] `data/mapper/ThemeJsonParser` (parse puro do JSON) + `ThemeRepositoryImpl` (I/O de assets isolado).
- [x] `GameViewModel` (`@HiltViewModel`, `GameUiState`); `ui/navigation/ImpostorNavHost` (NavHost + `Routes`) + `ImpostorApp`;
  telas viraram destinos stateless; `GameEngine.kt` removido.
- Testes: [x] `SortearImpostorUseCaseTest`, `SortearRodadaUseCaseTest`, `ThemeJsonParserTest`, `GameViewModelTest` (JVM);
  [x] (instrumentado) `ThemeRepositoryInstrumentedTest` (assets reais parseiam). [ ] (instrumentado) navegação E2E setup→pass_phone→game_play (não escrito; coberto pela Task 4.1 da Sprint 1).

#### Gate 0.0 (atingido)
- [x] `./gradlew assembleDebug` verde; [x] `./gradlew testDebugUnitTest` verde (18 testes);
  [x] `./gradlew assembleDebugAndroidTest` compila os instrumentados. [x] `connectedDebugAndroidTest` verde (5 instrumentados, 0 falhas — emulador `Pixel_6_Pro` API 15, 2026-07-21).

### Fase 0.A — Autenticação Firebase (Google + e-mail/senha) (CONCLUÍDA — código; login manual pendente de device + config Firebase)
> Dependências: Fase 0.0 (arquitetura) + pré-requisitos de setup
> Paralelismo: nenhum (task única)
> Caminhos seguem a nova arquitetura (Clean): validação em `domain/usecase`, repository em `domain/repository` + impl em `data/repository`, tela em `ui/screen` + `ui/viewmodel`, navegação em `ui/navigation`.

#### Task 0.1 — Login com Google e e-mail/senha
- Agent: compose-ui
- Input: SPEC §1 (autenticação); Firebase Auth; `GameEngine.kt` (back stack/navegação); paleta `Spy*`
- Output:
  - `domain/usecase/ValidarCredenciaisUseCase.kt` (puro, testável):
    `operator fun invoke(email: String, senha: String): CredsValidationResult`
    (sealed `Valido` / `Invalido(mensagens: List<String>)`; e-mail com formato válido, senha ≥ 6).
  - `domain/repository/AuthRepository.kt` (interface: `usuarioAtual: String?`, `suspend fun entrarComEmail(...)`,
    `suspend fun cadastrarComEmail(...)`, `suspend fun entrarComGoogle(idToken)`, `fun sair()`, devolvendo
    `sealed AuthResult { Sucesso(uid) / Erro(msg) }`) + `data/repository/FirebaseAuthRepository.kt` (impl sobre `FirebaseAuth`);
    `di/FirebaseModule` provê `FirebaseAuth`. Nada de `Context`/Firebase no `domain/`.
  - `ui/viewmodel/LoginViewModel.kt` (`@HiltViewModel`, `LoginUiState`) + `ui/screen/LoginScreen.kt` (stateless, monospace,
    paleta `Spy*`): campos e-mail/senha + botões "ENTRAR", "CADASTRAR", "ENTRAR COM GOOGLE"; mensagens de erro; sem hardcode de cor.
  - `ui/navigation/ImpostorNavHost`: sem usuário autenticado, `LoginScreen` é o destino inicial; após sucesso → `SetupScreen`.
- Testes críticos:
  - [x] `validarCredenciais("", "123456")` → `Invalido` (mensagem sobre e-mail)
  - [x] `validarCredenciais("a@b.com", "123")` → `Invalido` (senha < 6)
  - [x] `validarCredenciais("a@b.com", "123456")` → `Valido`
  - [x] (instrumentado) `LoginScreen` exibe os três botões e o campo de e-mail (`connectedDebugAndroidTest` verde no emulador)

### Fase 0.B — Temas via Firestore com cache offline
> Dependências: Fase 0.A (Firestore exige `request.auth != null`)
> Paralelismo: nenhum (task única)

#### Task 0.2 — Buscar temas do Firestore com fallback offline (Room) (CONCLUÍDA — código; refresh reativo ao login; execução em device pendente)
- Agent: data-room
- Input: schema de §5 do `SPEC.md`; `domain/model` (`ThemeConfig`/`RoundData`); `AuthRepository` (Task 0.1); `AppDatabase`
- Output:
  - `data/remote/FirestoreThemeMapper.kt` (puro, testável):
    `fun documentoParaThemeConfig(tema: String?, rodadas: List<Map<String, Any?>>?): ThemeConfig?`
    (aplica §5: `tema` não vazio, ≥ 1 rodada com `grupo`/`impostor` não vazios; caso inválido → `null`).
  - `data/RemoteThemeEntity.kt` + `RemoteRoundEntity.kt`: entidades Room de **cache** dos temas remotos
    (separadas de `custom_*`) + `RemoteThemeDao` lendo em `Flow`.
  - Atualizar `AppDatabase`: **bumpe a `version`** e adicione `Migration` para as novas tabelas (nunca `fallbackToDestructiveMigration` sem aprovação).
  - Evoluir `domain/repository/ThemeRepository` com `fun observarTemas(): Flow<List<ThemeConfig>>` +
    `data/repository/ThemeRepositoryImpl` (ou `FirestoreThemeRepositoryImpl`): lê a collection `temas` do Firestore
    (mapeando via `FirestoreThemeMapper`), atualiza o cache Room e emite; **offline**: emite do cache; sem cache: assets (`DEFAULT_CATEGORIES`).
    Exposto por `domain/usecase/ObservarTemasUseCase` e consumido pelo `GameViewModel`/UI.
- Testes críticos:
  - [x] `documentoParaThemeConfig("Cotidiano", [rodadaValida])` → `ThemeConfig` com o tema e 1 rodada
  - [x] `documentoParaThemeConfig("Cotidiano", null)` → `null` (doc sem `rodadas` é descartado)
  - [x] `documentoParaThemeConfig("", [rodadaValida])` → `null` (tema vazio, §5)
  - [x] (instrumentado) sem rede, `observarTemas()` emite os temas gravados no cache do Room (`connectedDebugAndroidTest` verde no emulador)

### Gate da Sprint 0
- [x] `./gradlew testDebugUnitTest` verde — 31 testes (sorteio/validação, mappers e ViewModels; inclui `ValidarCredenciaisUseCase`, `LoginViewModel` e `FirestoreThemeMapper`).
- [x] `./gradlew assembleDebug` verde com `google-services.json` presente; [x] `assembleDebugAndroidTest` compila os instrumentados.
- [x] (Com device) login Google e e-mail/senha funcionam e os temas do Firestore aparecem; em modo avião, aparecem os do cache. — **validado em device em 2026-07-21** (config do Firebase Console concluída).
- **Status:** **SPRINT 0 CONCLUÍDA (2026-07-21).** Fases 0.0, 0.A e 0.B com código pronto e todos os gates verdes: `testDebugUnitTest` (31), `assembleDebug`, `assembleDebugAndroidTest` e `connectedDebugAndroidTest` (5 instrumentados). Firebase configurado; login e temas do Firestore validados em device. Backlog não-bloqueante: (1) teste instrumentado E2E de navegação setup→pass_phone→game_play (será coberto pela Task 4.1 da Sprint 1); (2) teste de `MIGRATION_1_2` com `MigrationTestHelper` (exige `exportSchema=true`).

---

## Sprint 1 — Rodada com tempo, exportar/importar categorias e lógica de jogo testável, com `assembleDebug` e `testDebugUnitTest` verdes.

> **Ajuste de caminhos (nova arquitetura, pós-Sprint 0).** Lógica pura vive em
> `com.game.impostor.domain.usecase` (não mais `game/`); modelos em `domain/model`;
> validação/serialização como UseCases/mappers testáveis. A **Task 1.1 (sorteio) já foi
> implementada na Fase 0.0** (`SortearImpostorUseCase`/`SortearRodadaUseCase`).

### Fase 1 — Núcleo de lógica pura testável
> Dependências: nenhuma
> Paralelismo: Task 1.1 e Task 1.2 rodam em paralelo (arquivos diferentes)

#### Task 1.1 — Extrair lógica de sorteio para função pura — **CONCLUÍDA na Fase 0.0**
- Implementada como `domain/usecase/SortearImpostorUseCase` e `SortearRodadaUseCase` (puros, `Random` injetável),
  consumidos pelo `GameViewModel`. Testes em `SortearImpostorUseCaseTest`/`SortearRodadaUseCaseTest`.
- Testes críticos:
  - [ ] `sortearImpostor(4, Random(0))` retorna um índice em `0..3` (determinístico com seed)
  - [ ] `sortearRodada(emptyList())` retorna `null` (categoria vazia não avança)
  - [ ] `sortearRodada(listaComUmItem)` retorna sempre esse item

#### Task 1.2 — Validador puro de categoria customizada
- Agent: data-room
- Input: regras de §5 do `SPEC.md` (tema/rodadas não vazios)
- Output: novo arquivo `app/src/main/java/com/game/impostor/domain/usecase/ValidarCategoriaUseCase.kt` com
  `operator fun invoke(nome: String, rodadas: List<Pair<String, String>>): CategoryValidationResult`
  (sealed result: `Valido` ou `Invalido(mensagens: List<String>)`)
- Testes críticos:
  - [ ] nome vazio → `Invalido` com mensagem sobre nome
  - [ ] rodada com `grupo` ou `impostor` vazio → `Invalido`
  - [ ] nome "Festa" + 1 rodada válida → `Valido`

### Fase 2 — Persistência e dados
> Dependências: Fase 1 completa (usa `RoundData`/validação estáveis)
> Paralelismo: Task 2.1 e Task 2.2 rodam em paralelo (arquivos diferentes)

#### Task 2.1 — Exportar/importar categoria como JSON
- Agent: data-room
- Input: schema de §5 do `SPEC.md`; `CategoryValidator` (Task 1.2)
- Output: novo arquivo `app/src/main/java/com/game/impostor/data/CategoryJson.kt` com
  `fun categoriaParaJson(nome: String, rodadas: List<RoundData>): String` e
  `fun categoriaDeJson(json: String): ThemeConfig` (mesmo formato dos assets; usa `org.json`)
- Testes críticos:
  - [ ] round-trip: `categoriaDeJson(categoriaParaJson("X", rodadas))` reproduz `tema` e `rodadas`
  - [ ] JSON inválido/sem `rodadas` → lança exceção tratável ou retorna `ThemeConfig` de erro (documente qual)

#### Task 2.2 — Máquina de contagem regressiva da rodada
- Agent: game-logic
- Input: SPEC §4 (rodada); requisito novo: rodada pode ter tempo opcional
- Output: novo arquivo `app/src/main/java/com/game/impostor/domain/usecase/RoundTimer.kt` com um
  estado puro `data class TimerState(val restanteSeg: Int, val rodando: Boolean)` e
  `fun tick(state: TimerState): TimerState` (decrementa até 0, não fica negativo)
- Testes críticos:
  - [ ] `tick(TimerState(1, true))` → `TimerState(0, false)` (para em zero)
  - [ ] `tick(TimerState(0, false))` → inalterado
  - [ ] `tick` com `rodando = false` não decrementa

### Fase 3 — UI (Compose)
> Dependências: Fase 2 completa (usa `CategoryJson` e `RoundTimer`)
> Paralelismo: Task 3.1 e Task 3.2 rodam em paralelo (telas/arquivos diferentes)

#### Task 3.1 — Ação de exportar/importar na tela de categorias
- Agent: compose-ui
- Input: `CategoryJson` (Task 2.1); `CategorySelectScreen.kt`
- Output: botões "Exportar"/"Importar" na tela de categorias, usando a paleta `Spy*` e
  fonte monospace; exportar gera a string JSON, importar valida com `CategoryValidator`
  e salva via `CategoryViewModel`. Sem hardcode de cores.
- Testes críticos:
  - [ ] teste instrumentado: botão "Importar" com JSON válido adiciona a categoria à lista
  - [ ] importar JSON inválido exibe mensagem de erro e não adiciona categoria

#### Task 3.2 — Exibir tempo da rodada no jogo
- Agent: compose-ui
- Input: `RoundTimer` (Task 2.2); `GamePlayScreen.kt`
- Output: contador regressivo opcional visível em `GamePlayScreen`, estilo espião
  (monospace, `SpyGreen`/`SpyRed`), controlado pelo estado `TimerState`.
- Testes críticos:
  - [ ] teste instrumentado: com tempo configurado, o contador aparece e decrementa
  - [ ] com tempo desligado, nenhum contador é exibido

### Fase 4 — Integração e QA
> Dependências: Fase 3 completa
> Paralelismo: nenhum (task única de integração)

#### Task 4.1 — Teste de fluxo ponta a ponta
- Agent: qa-tester
- Input: telas das Fases anteriores
- Output: teste instrumentado em `app/src/androidTest/.../FluxoJogoTest.kt` cobrindo
  setup → passa-telefone (revelar diretriz) → game-play → reiniciar
- Testes críticos:
  - [ ] o fluxo completo navega sem crash com 3 jogadores e a categoria padrão
  - [ ] o jogador impostor vê texto diferente dos demais (verificado por conteúdo)

---

## Regras de execução deste plano

1. Toda task tem ≥ 2 testes críticos escritos **antes** do código (RED → GREEN).
2. Nenhum output vago: arquivos e assinaturas exatos acima.
3. Tasks da mesma fase não tocam os mesmos arquivos.
4. **Gate de cada fase (verificável por terminal):** `./gradlew testDebugUnitTest`
   passa para Fases 1–2; `./gradlew assembleDebug connectedDebugAndroidTest` passa para
   Fases 3–4 (com device/emulador). Só avance com o gate verde.
5. Ao concluir cada task, o orquestrador marca o checkbox aqui e dispara `/review`.

## Próxima sessão
_(preenchido pelo comando `/entrega` ao final de cada sessão)_