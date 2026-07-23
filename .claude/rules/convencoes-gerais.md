# Convenções gerais do Impostor

- Nomes de domínio ficam em **português** e são canônicos: `grupo`, `impostor`, `rodadas`,
  `tema`, `categoria`. Não traduza nem renomeie (`round`, `group`, etc.).
- Todo texto visível ao usuário é em português.
- Dependências **só** via version catalog `gradle/libs.versions.toml` (referenciadas como `libs.*`).
  Não adicione `implementation("grupo:artefato:versão")` literal no `build.gradle.kts`.
- Novo código de anotação (Room, **Hilt**) usa **KSP** (não kapt).
- **Clean Architecture + MVVM.** Camadas: `domain/` (regra de negócio pura — `model`,
  `repository` interfaces, `usecase`), `data/` (Room, Firestore, impls de repository, mappers),
  `di/` (módulos Hilt) e `ui/` (Compose + ViewModels + estado). A dependência aponta sempre para
  dentro: `ui → domain` e `data → domain`; **nada de `Context`/Room/Android dentro de `domain/`**.
- Injeção de dependência via **Hilt** (`@HiltAndroidApp`, `@AndroidEntryPoint`, `@HiltViewModel`,
  módulos em `di/`). Não instancie repositories/DB manualmente na UI.
- Mantenha a estética espião: `FontFamily.Monospace` e a paleta `Spy*` são a identidade do app.
- **Idioma de Commits:** Todas as sugestões de mensagens de commit e documentação técnica devem ser em **português**.
