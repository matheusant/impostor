---
name: build-instalar
description: >
  Compila o APK debug, instala no device/emulador conectado via adb e (opcional) abre o app.
  Use quando o usuário quiser buildar, instalar, rodar no aparelho, testar no device,
  ou gerar o APK do Impostor. Uso: /build-instalar (opcional: "e abrir").
---

# /build-instalar — Build + instalação no device

## 1. Verificar device
```
adb devices
```
Se nenhum device/emulador aparecer, avise o usuário para conectar um aparelho (depuração
USB) ou iniciar um emulador, e pare.

## 2. Compilar o APK debug
```
./gradlew assembleDebug
```
(no Windows/PowerShell: `.\gradlew.bat assembleDebug`). Se falhar, reporte a saída do
Gradle e pare — não tente contornar.

## 3. Instalar
```
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## 4. (Opcional) Abrir o app
Se `$ARGUMENTS` mencionar abrir/lançar:
```
adb shell am start -n com.game.impostor/.MainActivity
```

## 5. (Opcional) Screenshot para conferência
```
adb exec-out screencap -p > shot.png
```
Depois use Read em `shot.png` para conferir visualmente a tela.

## Restrições
- Não altere código de produção aqui — esta skill só builda/instala.
- Reporte claramente sucesso/falha de cada etapa.
