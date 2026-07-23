#!/usr/bin/env bash
# PreToolUse (matcher Bash) do agent qa-tester.
# Permite APENAS comandos de teste (gradle test/connected/check/lint) ou adb.
# Sem jq: extrai o campo "command" do JSON.
INPUT="$(cat)"
CMD="$(printf '%s' "$INPUT" | grep -oE '"command"[[:space:]]*:[[:space:]]*"([^"\\]|\\.)*"' | head -1)"
[ -z "$CMD" ] && CMD="$INPUT"

if printf '%s' "$CMD" | grep -Eiq 'gradlew(\.bat)?[^"]*(test|connected|check|lint)|(^|[^a-z])adb[[:space:]]'; then
  exit 0
fi

echo "qa-tester: via Bash, apenas comandos de teste são permitidos — ./gradlew testDebugUnitTest, ./gradlew connectedDebugAndroidTest, ./gradlew lintDebug ou adb." >&2
exit 2
