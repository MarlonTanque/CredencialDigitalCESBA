# Credencial Digital CESBA

Aplicación Android para crear una credencial estudiantil digital con vista previa en vivo, foto, QR y colores personalizables por carrera.

## Características

- Interfaz React empaquetada dentro de la aplicación Android.
- Tarjeta con tema glass, animaciones y color ligado a la carrera.
- Formulario para nombre, matrícula, carrera, periodo, grupo, correo y tipo de usuario.
- Captura de fotografía mediante la cámara del dispositivo.
- Reverso de la tarjeta con código QR que incluye los datos académicos.
- Gestión local de carreras y sus colores.
- Datos de formulario, carreras y foto guardados localmente en el dispositivo.

## Abrir en Android Studio

1. Abre la carpeta raíz `CredencialDigitalCESBA` en Android Studio.
2. Permite que Gradle sincronice el proyecto.
3. Ejecuta la configuración `app` en un emulador o dispositivo Android.

La aplicación carga los recursos web desde `app/src/main/assets`, incluidos en el repositorio, por lo que la interfaz se puede ejecutar sin conexión.

## Modificar la interfaz React

El código React está en `app/src/main/web`. Para editar la interfaz y volver a generar los recursos Android:

```powershell
cd app/src/main/web
npm ci
npm run build
```

Vite genera el resultado en `app/src/main/assets`. Android Studio también vuelve a generar estos archivos al compilar cuando detecta cambios en el código web.

## Compilar el APK

```powershell
./gradlew assembleDebug
```

En Windows también puedes ejecutar `gradlew.bat assembleDebug`. El APK de depuración queda en `app/build/outputs/apk/debug/app-debug.apk`.

## Tecnologías

- Android nativo: Java, AndroidX AppCompat y WebViewAssetLoader.
- Interfaz: React 19 y Vite.
- Códigos QR: ZXing Core.
