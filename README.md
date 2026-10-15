# Alertas Zaror

App Android para la gestión de alarmas comunitarias de la Junta de Vecinos Población Zaror (Cerrillos). Evaluación Parcial 2, DSY1105 Desarrollo de Aplicaciones Móviles, DUOC UC.

## Integrantes
- Diego Alejandro González
- Stefani Francisca Verdugo Toledo
- Camilo Andrés Contreras Silva
- Emanuel Orellana Gaete
- Renato Alexander Vargas González

## Requerimientos del MVP (adaptados al caso)
1. Registrar alertas comunitarias (perfil Vecino/a) con formulario validado.
2. Reportar fallas del sistema de alarmas (perfil Encargado/a) con formulario validado.
3. Consultar el historial de alertas, marcarlas como atendidas y eliminarlas con confirmación.
4. Persistencia local de alertas e incidentes.
5. Acceso a al menos dos recursos nativos del dispositivo.

Fuera del alcance de esta entrega: comunicados de la Directiva, notificaciones push y microservicio Spring Boot con Retrofit (etapas posteriores).

## Funcionalidades implementadas
- Registro de alertas: tipo (Emergencia, Falla de alarma, Solicitud de apoyo), descripción, ubicación, quién reporta y foto opcional.
- Reporte de falla del sistema: tipo de falla, equipo o sector, descripción y quién reporta.
- Listado de alertas con cambio de estado (Activa a Atendida) y eliminación con confirmación.
- Pantalla de inicio con contador de alertas activas.
- Validaciones por campo con íconos y mensajes de error.
- Animaciones: transición entre pantallas, botón de alarma, color de tarjeta según estado y mensajes de error.
- Datos ficticios, solo con fines académicos.

## Recursos nativos
- Vibración del dispositivo (permiso VIBRATE).
- Selector de imágenes de la galería para adjuntar evidencia.
- La ubicación es ficticia (de prueba), conforme a la restricción de no capturar datos reales.

## Arquitectura
MVVM con persistencia local en Room (SQLite): `data` (Entity, DAO, Repository), `domain` (validaciones), `ui` (pantallas y componentes) y `viewmodel`.

## Requisitos
Android Studio reciente (el proyecto usa Android Gradle Plugin 9.4.1 y Gradle 9.6.0), JDK 17 o superior (el que trae Android Studio), Android SDK 36 instalado y conexión a internet en la primera sincronización.

## Cómo ejecutar
1. Clonar: `git clone https://github.com/Dia1208/AlertasZaror1.0.git`
2. Abrir la carpeta en Android Studio y esperar el Gradle Sync.
3. Elegir un emulador en Device Manager y presionar Run.
4. Pruebas y compilación por terminal (Windows): `.\gradlew.bat testDebugUnitTest assembleDebug`

## Solución de problemas
Si Gradle indica que no encuentra el SDK, falta el archivo `local.properties`, que es específico de cada equipo y por eso no se versiona. Android Studio lo crea al abrir el proyecto. Si no, créalo en la raíz con la ruta de tu SDK, por ejemplo: `sdk.dir=C\:\\Users\\USUARIO\\AppData\\Local\\Android\\Sdk`

## Seguimiento del trabajo
Planificación con GitHub Issues y GitHub Projects del repositorio, con responsables y estados.