---
name: nova-categoria
description: >
  Cria uma nova categoria (tema) do Impostor no formato correto — seja um JSON padrão em
  assets/ (registrado em DEFAULT_CATEGORIES) ou o conteúdo de uma categoria customizada.
  Use sempre que o usuário quiser adicionar/criar uma categoria, tema, ou novas rodadas
  de perguntas (grupo/impostor). Uso: /nova-categoria [nome do tema].
---

# /nova-categoria — Cria uma categoria no formato do Impostor

Objetivo: gerar uma categoria válida. Nome/tema do argumento: `$ARGUMENTS`.

## Regras do formato (SPEC.md §5)
Cada rodada é um par de perguntas **parecidas mas diferentes**:
- `grupo`: pergunta que os jogadores não-impostores recebem.
- `impostor`: pergunta análoga, mas com um deslocamento sutil (tempo, lugar, contexto),
  de modo que a resposta do impostor destoe sem ser óbvia.

Boas rodadas: mesma estrutura de frase, mesmo "assunto guarda-chuva", divergência sutil.
Exemplo (tema Cotidiano):
```json
{ "grupo": "Diga algo que você faz logo após acordar.", "impostor": "Diga algo que você faz logo antes de dormir." }
```

## Passo a passo

### 1. Definir o tema e gerar rodadas
Se o usuário não deu conteúdo, proponha 5 rodadas coerentes com o tema `$ARGUMENTS`, em
**português**, seguindo o padrão de divergência sutil acima. Confirme com o usuário.

### 2. Escolher o destino
- **Categoria padrão (embarcada no app):** crie `app/src/main/assets/<slug>.json` no schema:
  ```json
  { "tema": "<Nome>", "rodadas": [ { "grupo": "...", "impostor": "..." } ] }
  ```
  E **registre** em `DEFAULT_CATEGORIES` (`ui/screen/GameEngine.kt`):
  `DefaultCategory("<Nome>", "<slug>.json")`.
- **Categoria customizada (exemplo/seed):** apenas gere o JSON no mesmo schema para o
  usuário importar via UI (não mexe em código).

### 3. Validar
- `tema` não vazio; `rodadas` com ≥ 1 item; cada `grupo`/`impostor` não vazios.
- Se `data/CategoryValidator.kt` existir, use-o como referência das regras.
- Rode `./gradlew assembleDebug` se tiver alterado assets/código, para garantir que o app compila.

## Quando NÃO usar
- Para alterar a UI de categorias (use o agent `compose-ui`).
- Para lógica de sorteio (use `game-logic`).
