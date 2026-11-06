-- CUIDADO: borra todas las tablas de Alertas Zaror y sus datos. Solo para empezar de cero.
-- Si una tabla no existe, Oracle muestra un error en esa linea; se puede ignorar.

DROP TABLE CONTACTOS_CONFIANZA CASCADE CONSTRAINTS PURGE;
DROP TABLE ALERTAS CASCADE CONSTRAINTS PURGE;
DROP TABLE USUARIOS CASCADE CONSTRAINTS PURGE;
DROP TABLE CUADRAS CASCADE CONSTRAINTS PURGE;
