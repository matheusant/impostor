# PLAN.md

> Contrato de execução entre você e os agents. Escrito para ser autocontido: o agent que
> executa uma task tem **zero contexto** da conversa de planejamento. Ordem das fases
> respeita dependências reais; tasks da mesma fase são genuinamente independentes.
> Só o orquestrador marca os checkboxes. Base (scaffold) já existe e o smoke test passa
> (`assembleDebug` + `testDebugUnitTest`).

## Sprint 1 — Rodada com tempo, exportar/importar categorias e lógica de jogo testável, com `assembleDebug` e `testDebugUnitTest` verdes.

### Fase 1 — Núcleo de lógica pura testável
> Dependências: nenhuma
> Paralelismo: Task 1.1 e Task 1.2 rodam em paralelo (arquivos diferentes)

#### Task 1.1 — Extrair lógica de sorteio para função pura
- Agent: game-logic
- Input: `GameEngine.kt` (regras atuais de sorteio de impostor e de rodada)
- Output: novo arquivo `app/src/main/java/com/game/impostor/game/GameLogic.kt` com:
  - `fun sortearImpostor(totalPlayers: Int, rng: kotlin.random.Random = Random): Int`
  - `fun sortearRodada(rodadas: List<RoundData>, rng: kotlin.random.Random = Random): RoundData?`
  `GameEngine.kt` passa a delegar a essas funções (sem mudar comportamento).
- Testes críticos:
  - [ ] `sortearImpostor(4, Random(0))` retorna um índice em `0..3` (determinístico com seed)
  - [ ] `sortearRodada(emptyList())` retorna `null` (categoria vazia não avança)
  - [ ] `sortearRodada(listaComUmItem)` retorna sempre esse item

#### Task 1.2 — Validador puro de categoria customizada
- Agent: data-room
- Input: regras de §5 do `SPEC.md` (tema/rodadas não vazios)
- Output: novo arquivo `app/src/main/java/com/game/impostor/data/CategoryValidator.kt` com
  `fun validarCategoria(nome: String, rodadas: List<Pair<String, String>>): CategoryValidationResult`
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
- Output: novo arquivo `app/src/main/java/com/game/impostor/game/RoundTimer.kt` com um
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