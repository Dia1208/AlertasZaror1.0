# Backend de Alertas Zaror

Servidor Spring Boot (Java 17) con Oracle Autonomous. El esquema está en `db/`.

## Variables de entorno

No se guardan en Git. Se declaran en la terminal antes de ejecutar:

| Variable | Contenido |
|---|---|
| `DB_ALIAS` | Alias del `tnsnames.ora`, por ejemplo `duocuc2_high` |
| `DB_WALLET_DIR` | Carpeta del wallet, con barras normales (`C:/ruta/wallet`) |
| `DB_USER` / `DB_PASSWORD` | Usuario de la aplicación y su clave (no `ADMIN`) |
| `JWT_SECRET` | Clave para firmar los tokens, mínimo 32 caracteres |

Para generar una `JWT_SECRET` en PowerShell:

```powershell
$b = New-Object byte[] 48
[Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($b)
$env:JWT_SECRET = [Convert]::ToBase64String($b)
```

## Ejecutar y probar

```powershell
.\mvnw.cmd spring-boot:run
.\mvnw.cmd test
.\mvnw.cmd test "-Doracle.integration=true"
```

La última incluye las pruebas contra la base real y requiere todas las variables.

## Endpoints

| Método y ruta | Acceso | Descripción |
|---|---|---|
| `GET /api/salud` | público | Estado del servidor |
| `GET /api/salud/bd` | administrador | Estado de la conexión a Oracle |
| `POST /api/auth/registro` | público | Crea un vecino `PENDIENTE` |
| `POST /api/auth/login` | público | Devuelve un token JWT si la cuenta está `APROBADA` |
| `GET /api/auth/yo` | token | Datos del usuario del token |

Los tokens duran 2 horas. Tras 5 intentos fallidos de login, la cuenta se bloquea 5 minutos.
