# Hooks do Impostor

Guardrails determinísticos. Escritos em **bash** (sem dependência de `jq`) — requerem o
**Git Bash** no PATH (confirmado neste ambiente; os hooks disparam via `bash <script>.sh`).
Para não dar falso-positivo, os scripts extraem apenas o campo relevante do JSON de stdin
(`file_path` ou `command`), nunca o conteúdo inteiro do arquivo.

## Globais (`.claude/settings.json`)
- `global/protect-sensitive.sh` — PreToolUse `Edit|Write`: bloqueia escrita cujo `file_path`
  aponte para arquivos sensíveis/gerados (segredos, diretórios de build, config de IDE).
- `global/git-add-created.sh` — PostToolUse `Write`: faz `git add` no `file_path` recém-criado,
  para que todo desenvolvimento deixe os arquivos criados já em stage. **Nunca bloqueia**
  (sempre exit 0). Usa `git add` sem `-f`, então arquivos no `.gitignore` (segredos, `build/`)
  não são adicionados. Não faz `commit`/`push` — só stage (respeita `rules/seguranca.md`).

## Por agent (declarados no frontmatter de `.claude/agents/*.md`)
- `reviewer/block-writes.sh` — `code-reviewer` (PreToolUse `Write|Edit|Bash`): reviewer é
  somente leitura -> exit 2.
- `common/block-dangerous-bash.sh` — implementadores (PreToolUse `Bash`): bloqueia comandos
  destrutivos no `command` (apagar em massa, push/commit/reset --hard, etc.).
- `qa/allow-only-tests.sh` — `qa-tester` (PreToolUse `Bash`): permite só comandos de teste
  do Gradle e adb.

## Contrato
- exit 2 = bloqueia a ação e devolve a mensagem (stderr) ao agent. exit 0 = permite.
- Hooks de frontmatter disparam quando o agent roda como **sub-agent** (via Task tool /
  @mention), não quando ele é a sessão principal.

## Como validar
Peça ao `@code-reviewer` para editar um arquivo -> bloqueado ("somente leitura").
Peça a um implementador para rodar um comando destrutivo -> bloqueado.
