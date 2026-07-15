<div align="center">

# 🕵️ Linha_Cruzada

**Jogo de festa local para Android — descubra quem recebeu a pergunta diferente.**

![Versão](https://img.shields.io/badge/vers%C3%A3o-1.1-00FF66?style=flat-square)
![Plataforma](https://img.shields.io/badge/Android-minSdk%2026-3DDC84?style=flat-square&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?style=flat-square&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)
![Offline](https://img.shields.io/badge/100%25-offline-FF3333?style=flat-square)

</div>

---

## 📡 Sobre

**Linha_Cruzada** é um jogo de festa **local** (passa-o-celular) — sem internet, sem contas,
sem back-end. Um único aparelho é passado de mão em mão entre os jogadores.

A cada partida o app sorteia **um impostor** entre os agentes e distribui duas perguntas
**parecidas, mas sutilmente diferentes**:

- os jogadores do **grupo** recebem a pergunta `grupo` (canal seguro 🟢);
- o **impostor** recebe a pergunta `impostor` (infiltrado 🔴).

Depois que cada um lê sua diretriz em segredo, o grupo debate e tenta descobrir **quem
recebeu a pergunta divergente** pelas respostas fora do padrão.

> **Exemplo:** o grupo recebe _"Diga algo que você faz quase todo dia de manhã logo após
> acordar"_ e o impostor recebe _"Diga algo que você faz quase toda noite logo antes de
> dormir"_. As respostas parecem plausíveis — até alguém escorregar.

## 🎮 Como jogar

1. **Setup** — escolha o número de agentes (3 a 8) e o canal (categoria).
2. **Transmissão** — o app sorteia o impostor e uma rodada.
3. **Passa-telefone** — cada agente lê sua diretriz em segredo e passa o aparelho.
4. **Debate** — todos respondem em uma palavra, por vez, e discutem para achar o infiltrado.
5. **Reboot** — finalize a missão e recomece com um novo sorteio.

## 📸 Telas

| Triagem de agentes | Diretriz secreta | Debate |
|:---:|:---:|:---:|
| ![Tela de setup](docs/screenshots/setup.png) | ![Revelação do infiltrado](docs/screenshots/revelacao.png) | ![Tela de debate](docs/screenshots/debate.png) |
| Escolha de agentes e canal | O infiltrado recebe a pergunta divergente | Interrogatório em andamento |

## ✨ Funcionalidades

- **Setup rápido:** slider de 3 a 8 agentes e seleção do canal ativo.
- **Categorias padrão + customizadas:** use as embutidas ou crie as suas (nome + rodadas
  `grupo`/`impostor`), com criação, edição e exclusão — persistidas localmente via **Room**.
- **Sorteio justo:** exatamente **um** impostor e **uma** rodada aleatória por partida.
- **Revelação sigilosa por jogador:** o texto só aparece depois de decodificar; verde =
  canal seguro (grupo), vermelho = infiltrado (impostor).
- **Debate + reboot:** tela de discussão e reinício limpo para a próxima rodada.
- **100% offline:** nenhum dado sai do aparelho.

## 🗂️ Categorias incluídas

Três canais embutidos, cada um com 5 rodadas:

| Canal | Tema |
|---|---|
| **Cotidiano** | Rotina do dia a dia |
| **Cultura Pop** | Filmes, séries, heróis e vilões |
| **Relacionamentos** | Encontros, presentes e situações |

Novas categorias padrão são arquivos JSON em [`app/src/main/assets/`](app/src/main/assets/)
no formato `{ "tema": string, "rodadas": [ { "grupo": string, "impostor": string } ] }`.

## 🎨 Estética

Identidade visual de **espionagem**: fonte monospace, rótulos como `AGENTE 0X`,
`CANAL SEGURO` e `INSPEÇÃO DE SEGURANÇA`, e a paleta neon sobre fundo escuro:

| Cor | Hex | Uso |
|---|---|---|
| 🟩 `SpyGreen` | `#00FF66` | canal seguro / grupo / acento principal |
| 🟥 `SpyRed` | `#FF3333` | alerta / impostor / infiltrado |
| ⬛ `SpyBlack` | `#0D0F12` | fundo |
| ▪️ `SpyGray` | `#1A1F26` | cartões / superfícies |
| ⬜ `SpyTextWhite` | `#E2E8F0` | texto |

## 🛠️ Stack técnica

- **Linguagem:** Kotlin `2.2.10` (Java 11)
- **UI:** Jetpack Compose (BOM `2026.02.01`) + Material 3
- **Persistência:** Room `2.7.1` via KSP
- **Build:** Android Gradle Plugin `9.2.1`, Gradle `9.4.1`
- **SDK:** `minSdk 26` · `targetSdk 36` · `compileSdk 36`
- **Namespace / applicationId:** `com.game.impostor`
- Sem bibliotecas de rede, injeção de dependência ou analytics.

## 🚀 Como compilar e instalar

**Pré-requisitos:** JDK 11+, Android SDK 36 e um device/emulador com Android 8.0 (API 26) ou superior.

| Objetivo | PowerShell | bash |
|---|---|---|
| Compilar o APK debug | `.\gradlew.bat assembleDebug` | `./gradlew assembleDebug` |
| Rodar os testes unitários | `.\gradlew.bat testDebugUnitTest` | `./gradlew testDebugUnitTest` |
| Instalar no device/emulador | `adb install -r app/build/outputs/apk/debug/app-debug.apk` | idem |

O APK gerado fica em `app/build/outputs/apk/debug/app-debug.apk`.

## 🗺️ Roadmap

Planejado (ainda **não** implementado):

- [ ] Exportar / importar categorias customizadas em JSON.
- [ ] Temporizador opcional de rodada durante o debate.

## 🔒 Privacidade

Linha_Cruzada é **totalmente offline**: não usa rede, não coleta dados, não tem anúncios nem
telemetria e não pede permissões além do necessário para o jogo local.

---

<div align="center">
<sub>Feito com Kotlin + Jetpack Compose · interface em português 🇧🇷</sub>
</div>
