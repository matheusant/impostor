# Restrições de segurança

- Nunca committar nem editar `local.properties`, `*.jks`, `*.keystore` (contêm caminhos/segredos de assinatura).
- Nunca versionar `app/build/`, `build/`, `.gradle/` (artefatos gerados).
- Nunca ligar `fallbackToDestructiveMigration()` no Room sem aprovação explícita — apaga os dados do usuário.
- A permissão `INTERNET` é **permitida** (necessária para Firebase Auth e Cloud Firestore). Nunca adicionar outras permissões desnecessárias (localização, contatos, câmera, microfone, etc.).
- Rede é usada **somente** para Firebase Auth (login) e Cloud Firestore (temas). Nunca adicionar analytics/telemetria nem enviar dados de jogo/PII além do necessário para autenticar e ler temas.
- `google-services.json` contém config do Firebase (identificadores de projeto, não segredos de servidor); a proteção dos dados depende das **Firestore Security Rules** — exija `request.auth != null`.
- Não faça `git push`, `git commit` nem `git reset --hard` por conta própria — só quando o usuário pedir.
