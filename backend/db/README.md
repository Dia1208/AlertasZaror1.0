# Base de datos (Oracle Autonomous)

Scripts para crear el esquema. Se ejecutan en orden, con el usuario de la aplicación (no `ADMIN`),
en Database Actions > SQL, usando **Run Script** (F5):

1. `01_esquema.sql`: tablas `CUADRAS`, `USUARIOS`, `ALERTAS` y `CONTACTOS_CONFIANZA`.
2. `02_cuadras_ficticias.sql`: cuadras de ejemplo (datos ficticios).

`99_borrar_esquema.sql` borra todo para empezar de cero.

## Primer administrador

Los vecinos se registran como `PENDIENTE` y los aprueba un administrador. Para crear el primero,
regístralo desde la API y luego, en la base:

```sql
UPDATE USUARIOS
   SET ROL = 'ADMINISTRADOR', ESTADO = 'APROBADO'
 WHERE TELEFONO = '9XXXXXXXX';
COMMIT;
```

## Pruebas contra la base real

```powershell
.\mvnw.cmd test -Doracle.integration=true
```

Requieren las variables de entorno `DB_ALIAS`, `DB_WALLET_DIR`, `DB_USER` y `DB_PASSWORD`.
Sin la opción `-Doracle.integration=true` esas pruebas se omiten.
