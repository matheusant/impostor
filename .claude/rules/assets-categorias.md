---
paths:
  - "app/src/main/assets/**"
---
# Regras das categorias padrão (assets JSON)

- Schema fixo: `{ "tema": string, "rodadas": [ { "grupo": string, "impostor": string } ] }`.
  Sem campos extras; use exatamente essas chaves (em português).
- `tema` não vazio; `rodadas` com ≥ 1 item; cada `grupo`/`impostor` não vazio.
- `grupo` e `impostor` devem ser perguntas **parecidas mas com divergência sutil** (mesmo
  assunto, deslocamento de tempo/lugar/contexto) — o jogo depende disso.
- Todo texto em **português**.
- Ao adicionar um `.json` aqui, **registre-o** em `DEFAULT_CATEGORIES` (`ui/screen/GameEngine.kt`)
  com `DefaultCategory("<Nome>", "<arquivo>.json")` — senão a categoria não aparece no app.
- Nome do arquivo em `snake_case` sem acentos (ex.: `cultura_pop.json`).
