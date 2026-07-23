---
paths:
  - "app/src/main/java/com/game/impostor/data/**"
---
# Regras da camada de dados (Room)

- Nome do DB é `impostor_db` (singleton em `AppDatabase.getInstance`). Não crie um segundo DB.
- Leituras retornam `Flow` (reativas); escritas são `suspend`. Não exponha chamadas bloqueantes.
- Relação: `custom_categories` 1—N `custom_rounds` (FK `categoryId`). Excluir categoria deve
  remover suas rodadas (mantenha a consistência já existente no DAO/repository).
- O acesso ao DB fica atrás de um **repository** (`data/repository/`) que expõe modelos de
  `domain/model`; a UI/ViewModel não fala com o DAO diretamente. O DAO/DB é provido via Hilt (`di/`).
- Ao alterar qualquer `@Entity`, **incremente `version`** em `@Database` e forneça `Migration`.
  `fallbackToDestructiveMigration` só com aprovação explícita (apaga dados do usuário).
- Modelos de domínio (`ThemeConfig`, `RoundData`, `CategoriaCustom`) ficam em
  `com.game.impostor.domain.model` — mantenha os campos `tema`, `rodadas`, `grupo`, `impostor`
  exatamente com esses nomes. O parse do JSON de assets é puro (`data/mapper/ThemeJsonParser`).
- Prefira validação/serialização/mapeamento como funções puras testáveis (JUnit em `src/test`).
