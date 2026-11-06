-- Cuadras FICTICIAS para pruebas (el proyecto usa datos ficticios, solo con fines academicos).
-- El sector agrupa cuadras cercanas entre si.

-- Se ejecuta en el esquema de la aplicacion aunque estes conectado como ADMIN.
ALTER SESSION SET CURRENT_SCHEMA = ALERTAS_APP;

INSERT INTO CUADRAS (CALLE, TRAMO, SECTOR) VALUES ('Calle Ejemplo Uno', '100-199', 'Norte');
INSERT INTO CUADRAS (CALLE, TRAMO, SECTOR) VALUES ('Calle Ejemplo Uno', '200-299', 'Norte');
INSERT INTO CUADRAS (CALLE, TRAMO, SECTOR) VALUES ('Calle Ejemplo Dos', '100-199', 'Norte');
INSERT INTO CUADRAS (CALLE, TRAMO, SECTOR) VALUES ('Calle Ejemplo Dos', '200-299', 'Sur');
INSERT INTO CUADRAS (CALLE, TRAMO, SECTOR) VALUES ('Calle Ejemplo Tres', '100-199', 'Sur');
INSERT INTO CUADRAS (CALLE, TRAMO, SECTOR) VALUES ('Calle Ejemplo Tres', '200-299', 'Sur');

COMMIT;
