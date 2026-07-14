# SPEC.md — Impostor

> Especificação do sistema: **o que** o Impostor deve fazer. Escrita para alta
> aderência (exemplos concretos, critérios testáveis, casos de borda e escopo
> explícitos). Todo agent lê esta SPEC antes de qualquer ação.

## 1. Visão geral

O Impostor é um jogo de festa **local** para Android (um único aparelho passado entre
os jogadores). O app sorteia um **impostor** entre N jogadores e distribui, por rodada,
duas perguntas parecidas: os jogadores do grupo recebem a pergunta `grupo`; o impostor
recebe a pergunta `impostor`. Após todos verem sua diretriz em segredo, o grupo debate e
tenta identificar o impostor pelas respostas divergentes.

- **Plataforma:** Android (`minSdk 26`), Kotlin + Jetpack Compose + Material 3.
- **Idioma da interface:** português.
- **Estética:** espionagem — fonte monospace, rótulos como "AGENTE 0X", "INSPEÇÃO DE
  SEGURANÇA", "CANAL SEGURO", cores neon sobre fundo escuro.
- **Sem back-end, sem rede, sem conta.** Todo estado é local (memória + Room).

## 2. Usuários e uso

Grupos de 3+ pessoas em ambiente presencial. Um aparelho é passado de jogador em jogador.
Sessão típica: escolher número de jogadores → escolher categoria → passar o telefone →
cada um lê sua diretriz em segredo → discutir → reiniciar.

## 3. Domínio (glossário — nomes canônicos)

- **tema / categoria:** conjunto de rodadas com um nome (ex.: "Cotidiano").
- **rodada (`RoundData`):** par `{ grupo, impostor }` de duas perguntas parecidas.
- **grupo:** pergunta que os jogadores não-impostores recebem.
- **impostor:** pergunta (diferente) que o impostor recebe.
- **categoria padrão:** carregada de um JSON em `assets/` (read-only).
- **categoria customizada:** criada pelo usuário e persistida no Room.

## 4. Funcionalidades (estado atual — o contrato a preservar)

### 4.1 Setup do jogo (`SetupScreen`)
- Seleção do número total de jogadores (padrão **4**), mínimo coerente com o jogo (≥ 3).
- Seleção da categoria ativa (padrão: primeira categoria padrão, "Cotidiano").
- Botão iniciar → sorteia uma rodada e um índice de impostor, vai para passa-telefone.
- **Critério de aceitação:** ao iniciar com categoria de rodadas vazias, o jogo **não**
  avança (permanece no setup). Exemplo: categoria customizada sem rodadas → botão iniciar não navega.

### 4.2 Seleção/gestão de categorias (`CategorySelectScreen`, `CategoryFormScreen`)
- Listar categorias padrão + customizadas (ordem alfabética das customizadas).
- Criar categoria customizada: nome + lista de rodadas (`grupo`, `impostor`).
- Editar e excluir categoria customizada. Ao excluir a categoria ativa, volta para a padrão "Cotidiano".
- **Critério de aceitação:** salvar uma categoria com nome "Festa" e 2 rodadas cria 1
  `CustomCategoryEntity` + 2 `CustomRoundEntity` associados; ela aparece na lista.

### 4.3 Sorteio (regras centrais do jogo — `GameEngine`)
- **Impostor:** exatamente **um** índice em `[0, totalPlayers)` escolhido aleatoriamente.
- **Rodada:** uma `RoundData` escolhida aleatoriamente entre as rodadas da categoria ativa.
- **Critério de aceitação (testável):** para `totalPlayers = 4`, o índice do impostor
  está sempre em `{0,1,2,3}`. Para uma categoria com 1 rodada, a rodada sorteada é sempre aquela.

### 4.4 Passa-telefone (`PassPhoneScreen`)
- Para cada jogador (AGENTE 01..0N): tela pede sigilo → botão "DECODIFICAR DIRETRIZ"
  revela o texto → botão "CRIPTOGRAFAR E PROSSEGUIR" passa ao próximo.
- O impostor vê a pergunta `impostor` (borda/rótulo vermelho, "INFILTRADO"); os demais
  veem `grupo` (verde, "CANAL SEGURO").
- **Critério de aceitação:** o jogador de índice == `impostorIndex` vê `rodada.impostor`;
  todos os outros veem `rodada.grupo`. O texto só aparece após o revelar.

### 4.5 Jogo em andamento (`GamePlayScreen`)
- Tela de discussão + botão reiniciar (limpa o back stack e volta ao setup).

### 4.6 Carregamento de dados
- Categorias padrão: parse de JSON em `assets/` (`tema`, `rodadas[]`). Em erro de arquivo,
  retorna um `ThemeConfig` de erro seguro (não crasha).
- Categorias customizadas: `Flow` do Room exposto como `StateFlow` no `ViewModel`.

## 5. Schema de dados (contratos exatos)

**JSON de categoria padrão** (`app/src/main/assets/<nome>.json`):
```json
{
  "tema": "Cotidiano",
  "rodadas": [
    { "grupo": "Diga algo que você faz de manhã.", "impostor": "Diga algo que você faz à noite." }
  ]
}
```
Regras: `tema` não vazio; `rodadas` com ≥ 1 item; cada item com `grupo` e `impostor` não
vazios. Novo JSON **deve** ser registrado em `DEFAULT_CATEGORIES` (`GameEngine.kt`).

**Room:** `custom_categories(id, name)` 1—N `custom_rounds(id, categoryId, grupo, impostor)`.
Leitura via `getAllWithRounds(): Flow<List<CustomCategoryWithRounds>>` ordenada por `name ASC`.

## 6. Casos de borda (o que acontece quando dá errado)

- Categoria sem rodadas → iniciar não avança (§4.1).
- Arquivo JSON ausente/corrompido → `ThemeConfig` de erro, sem crash (§4.6).
- Excluir a categoria ativa → seleção volta para "Cotidiano" (§4.2).
- `totalPlayers` alterado depois do sorteio não deve reindexar o impostor da rodada em curso.
- Botão voltar (BackHandler): recua uma tela no back stack quando há histórico.

## 7. Stack e dependências explícitas

Ver `CLAUDE.md` (fonte única de versões). Resumo: Kotlin 2.2.10, Compose BOM 2026.02.01,
Material 3, Room 2.7.1 (KSP), AGP 9.2.1, minSdk 26 / targetSdk 36. Sem libs de rede/DI/analytics.

## 8. Fora de escopo (não construir sem nova SPEC)

- Multiplayer online / rede / contas / nuvem.
- Back-end ou API.
- Anúncios, analytics, telemetria.
- Suporte a idiomas além do português (a menos que uma task de i18n seja adicionada ao PLAN).
- Mais de um impostor por rodada (a menos que explicitamente planejado).

## 9. Checklist de qualidade desta SPEC

- [x] Cada funcionalidade tem ao menos 1 exemplo concreto.
- [x] Sem requisitos implícitos relevantes (comportamento de erro documentado).
- [x] Dividida em seções verificáveis independentemente.
- [x] Critérios de aceitação testáveis (§4.1, §4.3, §4.4).
- [x] Casos de borda documentados (§6).
- [x] Stack e dependências explícitas (§7 + `CLAUDE.md`).
- [x] Escopo-fora delimitado (§8).