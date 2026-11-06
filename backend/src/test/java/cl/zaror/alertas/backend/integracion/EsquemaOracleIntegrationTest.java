package cl.zaror.alertas.backend.integracion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * Comprueba que el esquema de db/01_esquema.sql existe y que sus reglas se cumplen.
 * Cada prueba se revierte al terminar, así que no deja datos en la base.
 */
@SpringBootTest
@EnabledIfSystemProperty(named = "oracle.integration", matches = "true")
@Transactional
class EsquemaOracleIntegrationTest {

    private static final String INSERTAR_USUARIO =
            "INSERT INTO USUARIOS (NOMBRE, TELEFONO, DIRECCION, CLAVE_HASH) VALUES (?, ?, ?, ?)";

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void lasTablasDelEsquemaDebenExistir() {
        List<String> tablas = jdbc.queryForList("SELECT TABLE_NAME FROM USER_TABLES", String.class);

        assertThat(tablas).contains("CUADRAS", "USUARIOS", "ALERTAS", "CONTACTOS_CONFIANZA");
    }

    @Test
    void unUsuarioNuevoDebeQuedarComoVecinoPendiente() {
        jdbc.update(INSERTAR_USUARIO, "Vecino Prueba", "911111111", "Calle Ejemplo Uno 101", "hash");

        String rolYEstado = jdbc.queryForObject(
                "SELECT ROL || '/' || ESTADO FROM USUARIOS WHERE TELEFONO = ?",
                String.class, "911111111");

        assertThat(rolYEstado).isEqualTo("VECINO/PENDIENTE");
    }

    @Test
    void noDebePermitirTelefonosDuplicados() {
        jdbc.update(INSERTAR_USUARIO, "Vecino Uno", "922222222", "Calle Ejemplo Uno 101", "hash");

        assertThatThrownBy(() ->
                jdbc.update(INSERTAR_USUARIO, "Vecino Dos", "922222222", "Calle Ejemplo Uno 102", "hash"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void noDebePermitirUnRolInvalido() {
        assertThatThrownBy(() -> jdbc.update(
                "INSERT INTO USUARIOS (NOMBRE, TELEFONO, DIRECCION, CLAVE_HASH, ROL) VALUES (?, ?, ?, ?, ?)",
                "Intruso", "933333333", "Calle Ejemplo Uno 103", "hash", "SUPERADMIN"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void unVecinoAprobadoDebeTenerCuadra() {
        assertThatThrownBy(() -> jdbc.update(
                "INSERT INTO USUARIOS (NOMBRE, TELEFONO, DIRECCION, CLAVE_HASH, ESTADO) VALUES (?, ?, ?, ?, ?)",
                "Sin Cuadra", "944444444", "Calle Ejemplo Uno 104", "hash", "APROBADO"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void unUsuarioNoPuedeSerContactoDeSiMismo() {
        jdbc.update(INSERTAR_USUARIO, "Vecino Solo", "955555555", "Calle Ejemplo Uno 105", "hash");
        Long id = jdbc.queryForObject(
                "SELECT ID FROM USUARIOS WHERE TELEFONO = ?", Long.class, "955555555");

        assertThatThrownBy(() -> jdbc.update(
                "INSERT INTO CONTACTOS_CONFIANZA (USUARIO_ID, CONTACTO_USUARIO_ID) VALUES (?, ?)", id, id))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void noDebePermitirContactosDuplicados() {
        jdbc.update(INSERTAR_USUARIO, "Vecino A", "966666666", "Calle Ejemplo Uno 106", "hash");
        jdbc.update(INSERTAR_USUARIO, "Vecino B", "977777777", "Calle Ejemplo Uno 107", "hash");
        Long a = jdbc.queryForObject("SELECT ID FROM USUARIOS WHERE TELEFONO = ?", Long.class, "966666666");
        Long b = jdbc.queryForObject("SELECT ID FROM USUARIOS WHERE TELEFONO = ?", Long.class, "977777777");
        String sql = "INSERT INTO CONTACTOS_CONFIANZA (USUARIO_ID, CONTACTO_USUARIO_ID) VALUES (?, ?)";

        jdbc.update(sql, a, b);

        assertThatThrownBy(() -> jdbc.update(sql, a, b))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
