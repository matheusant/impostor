---
name: atualizar-readme
description: >
  Atualiza o README.md público do projeto (página do app no GitHub) para refletir o estado
  atual do Impostor — versão, funcionalidades, categorias, stack, roadmap e screenshots.
  Use sempre que o app mudar: nova tela/feature, nova categoria, mudança de versão ou de
  stack, ou quando o usuário pedir para "atualizar o README / a documentação / a página do
  GitHub". Uso: /atualizar-readme.
---

# /atualizar-readme — Manter o README em dia

Objetivo: deixar o `README.md` da raiz fiel ao código atual, **sem inventar features**.
Só descreva o que existe de fato; o que está planejado vai para o **Roadmap**.

## 1. LEVANTAR O ESTADO ATUAL
Releia as fontes de verdade e anote o que mudou desde a última atualização do README:

- `app/build.gradle.kts` → `versionName`, `minSdk`, `targetSdk`, `compileSdk`.
- `gradle/libs.versions.toml` → versões de Kotlin, Compose BOM, Room, AGP, Gradle.
- `app/src/main/java/com/game/impostor/ui/screen/` → telas existentes e o fluxo de navegação
  (novas telas? mudou a ordem?).
- `app/src/main/assets/*.json` + `DEFAULT_CATEGORIES` em `GameEngine.kt` → categorias padrão
  (nomes e nº de rodadas).
- `app/src/main/java/com/game/impostor/ui/theme/Color.kt` → paleta `Spy*` (hex).
- `SPEC.md` (funcionalidades/escopo) e `PLAN.md` (o que virou "pronto" e o que segue no roadmap).

## 2. RECAPTURAR SCREENSHOTS (só se a UI mudou)
Conjunto **enxuto** em `docs/screenshots/`: `setup.png`, `revelacao.png`, `debate.png`.
Só refaça se as telas mudaram visualmente. Procedimento (precisa de device/emulador — veja `adb devices`):

1. `.\gradlew.bat assembleDebug` (Windows) ou `./gradlew assembleDebug`.
2. `adb install -r app/build/outputs/apk/debug/app-debug.apk`
3. `adb shell am start -n com.game.impostor/.MainActivity`
4. Navegue pela UI com `adb shell input tap <x> <y>` e capture cada tela com
   `adb exec-out screencap -p > docs/screenshots/<nome>.png`. Para `revelacao.png`, avance
   os agentes ("CRIPTOGRAFAR E PROSSEGUIR") até cair no **impostor** (card vermelho "INFILTRADO").
5. Confira cada PNG com a ferramenta Read antes de referenciá-lo.

## 3. ATUALIZAR O README
Edite apenas as seções afetadas do `README.md`, preservando a linguagem e a estética espião:

- **Badge de versão** (`vers%C3%A3o-<x.y>`) e versões da stack.
- **Funcionalidades** e **Como jogar** (novas telas/fluxos).
- **Categorias incluídas** (novos canais / nº de rodadas).
- **Estética** (mudança na paleta `Spy*`).
- **Roadmap**: marque `[x]` o que foi entregue e mantenha `[ ]` o que segue pendente.

Regras: caminhos de imagem **relativos** (`docs/screenshots/...`); nada de descrever como
"pronto" algo que ainda está no `PLAN.md` como pendente.

## 4. NÃO VERSIONAR SOZINHO
**Não** rode `git add`/`commit`/`push` — o usuário versiona manualmente (regra de segurança
do projeto). Ao terminar, reporte o que mudou no README e lembre o usuário de commitar/dar
push quando quiser publicar no GitHub.
