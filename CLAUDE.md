# CLAUDE.md — Onboarding do projeto Impostor

> Este arquivo é o **contexto vivo** que todo agent (sessão principal ou sub-agent)
> recebe ao entrar no projeto. Ele responde: *"como se trabalha neste projeto?"*.
> Faz parte do **Harness** do projeto — leia também `SPEC.md` (o que construir) e
> `PLAN.md` (em que ordem e com quais critérios).

## O que é o Impostor

Jogo de festa **local** (passa-o-celular) para Android. Um jogador é o **impostor**.
Cada rodada tem duas perguntas parecidas: o grupo recebe a pergunta `grupo` e o
impostor recebe a pergunta `impostor`. Na discussão, o grupo tenta descobrir quem
recebeu a pergunta diferente. Interface em **português**, estética de **espionagem**
(fonte monospace, "AGENTE 0X", verde/vermelho neon).

## Stack (exata — não trocar sem aprovação)

- **Linguagem:** Kotlin `2.2.10`
- **UI:** Jetpack Compose (BOM `2026.02.01`) + Material 3
- **Persistência:** Room `2.7.1` via **KSP** (não kapt para novo código)
- **Arquitetura:** telas Composable + `GameEngine` (navegação por back stack em memória) + `CategoryViewModel` (`AndroidViewModel`) + Room
- **Build:** Android Gradle Plugin `9.2.1`, Gradle Wrapper, `minSdk 26`, `targetSdk 36`, `compileSdk 36`
- **Namespace / applicationId:** `com.game.impostor`
- **JVM:** Java 11 (source/target compatibility)
- Dependências centralizadas em `gradle/libs.versions.toml` (version catalog). **Sempre** adicionar libs por lá.

## Estrutura de pastas

```
app/src/main/
├── java/com/game/impostor/
│   ├── MainActivity.kt          # entry point; instala splash, aplica ImpostorTheme, chama GameEngine
│   ├── data/                    # camada de dados (Room + modelos)
│   │   ├── AppDatabase.kt        # Room DB singleton ("impostor_db")
│   │   ├── CategoryDao.kt        # DAO (Flow para leitura reativa)
│   │   ├── CustomCategoryEntity.kt / CustomRoundEntity.kt / CustomCategoryWithRounds.kt
│   │   └── ThemeConfig.kt        # data classes ThemeConfig / RoundData (categorias padrão em JSON)
│   └── ui/
│       ├── CategoryViewModel.kt  # StateFlow de categorias customizadas; save/update/delete
│       ├── screen/               # telas Composable + GameEngine (orquestrador de navegação)
│       │   ├── GameEngine.kt      # backStack + estado do jogo + carregamento de categorias
│       │   ├── SetupScreen.kt / CategorySelectScreen.kt / CategoryFormScreen.kt
│       │   └── PassPhoneScreen.kt / GamePlayScreen.kt
│       └── theme/                # Color.kt (paleta Spy*), Theme.kt, Type.kt
├── assets/                       # categorias padrão em JSON (cotidiano/cultura_pop/relacionamentos)
└── res/                          # strings, ícones, temas XML (splash)
app/src/test/                     # testes unitários JVM (JUnit)
app/src/androidTest/              # testes instrumentados (Compose UI test / Espresso)
```

## Comandos de build e teste (Windows)

Use PowerShell (`gradlew.bat`) ou bash (`./gradlew`). Sempre rode a partir da raiz do projeto.

| Objetivo | PowerShell | bash |
|---|---|---|
| Compilar (smoke test) | `.\gradlew.bat assembleDebug` | `./gradlew assembleDebug` |
| Só compilar Kotlin (rápido) | `.\gradlew.bat compileDebugKotlin` | `./gradlew compileDebugKotlin` |
| Testes unitários | `.\gradlew.bat testDebugUnitTest` | `./gradlew testDebugUnitTest` |
| Testes instrumentados (device/emulador) | `.\gradlew.bat connectedDebugAndroidTest` | `./gradlew connectedDebugAndroidTest` |
| Lint | `.\gradlew.bat lintDebug` | `./gradlew lintDebug` |
| Instalar APK debug | `adb install -r app/build/outputs/apk/debug/app-debug.apk` | idem |
| Screenshot do device | `adb exec-out screencap -p > shot.png` | idem |

**Critério de smoke test / gate mínimo:** `assembleDebug` retorna sucesso **e**
`testDebugUnitTest` passa. Nenhuma fase do `PLAN.md` avança sem isso.

## Convenções (regras de ouro)

- **Idioma:** todo texto visível ao usuário é em **português**. Código, nomes de
  símbolos e comentários técnicos em português/inglês conforme o padrão já existente
  (nomes de domínio como `grupo`, `impostor`, `rodadas`, `tema` são em português — mantenha).
- **Cores:** use **somente** a paleta de `ui/theme/Color.kt` (`SpyBlack`, `SpyGray`,
  `SpyGreen`, `SpyRed`, `SpyTextWhite`). Nunca hardcode `Color(0x...)` nas telas.
- **Composables:** mantenha-os **stateless** (state hoisting). O estado do jogo vive no
  `GameEngine`; o estado de categorias no `CategoryViewModel`. Telas recebem valores +
  callbacks, não acessam `ViewModel`/DB diretamente (exceto onde já é o padrão).
- **Room:** leituras retornam `Flow`; escritas são `suspend` e rodam em `viewModelScope`.
  Ao mudar entidades, **bumpe a `version`** do `@Database` e trate migração (ou
  `fallbackToDestructiveMigration` **apenas** com aprovação explícita).
- **Categorias padrão:** ao adicionar um JSON em `assets/`, registre-o em
  `DEFAULT_CATEGORIES` (`GameEngine.kt`). Schema do JSON: `{ "tema": string, "rodadas": [ { "grupo": string, "impostor": string } ] }`.
- **Dependências:** declare em `gradle/libs.versions.toml` e referencie via `libs.*`.
- **Nunca** committe `local.properties`, keystores (`*.jks`/`*.keystore`) ou a pasta `app/build/`.

## TDD

Fluxo padrão de qualquer task do `PLAN.md`: escreva o teste crítico **primeiro** (RED),
confirme que falha, implemente o mínimo para passar (GREEN), rode `testDebugUnitTest`.
Lógica de jogo (seleção de impostor, sorteio de rodada, validação de formulário) deve
ser **extraível para funções puras testáveis** em `src/test`.

## O Harness deste projeto (mapa de contexto)

Este projeto usa a arquitetura de agentes da *Imersão ExponencIA 2026*. Artefatos:

- `SPEC.md` — o que construir (problema, features, critérios de aceitação, escopo).
- `CLAUDE.md` (este arquivo) — como trabalhar aqui.
- `PLAN.md` — sprints/fases/tasks com gates verificáveis e testes críticos.
- `.claude/agents/` — sub-agents especializados (menor privilégio por domínio).
- `.claude/skills/` — workflows sob demanda que também viram slash commands
  (`/orquestrar`, `/implementar`, `/review`, `/entrega`, `/nova-categoria`,
  `/build-instalar`, `/redesign-ui`).
- `.claude/rules/` — regras modulares (incondicionais + escopadas por `paths`).
- `.claude/hooks/` — guardrails determinísticos (o reviewer não escreve; o qa só roda testes; arquivos sensíveis são protegidos).
- `.claude/settings.json` — hooks globais e config compartilhada do projeto.

## Restrições (nunca faça)

- Nunca modifique `SPEC.md`, `CLAUDE.md` ou `PLAN.md` durante a execução de uma task
  (só o orquestrador marca checkboxes no `PLAN.md`).
- Nunca implemente algo fora do escopo do `PLAN.md`.
- Nunca troque a stack/versões sem aprovação explícita.
- Nunca edite arquivos gerados (`app/build/`, `.gradle/`) nem segredos (`local.properties`).