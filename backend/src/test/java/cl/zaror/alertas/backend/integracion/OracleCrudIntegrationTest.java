package cl.zaror.alertas.backend.integracion;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

@SpringBootTest
@EnabledIfSystemProperty(named = "oracle.integration", matches = "true")
class OracleCrudIntegrationTest {

    private static final String TABLA = "CRUD_PRUEBA_TEMP";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void debeEjecutarOperacionesCrudEnOracle() {

        Integer existentes = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM USER_TABLES WHERE TABLE_NAME = ?",
                Integer.class,
                TABLA
        );

        if (existentes != null && existentes > 0) {
            throw new IllegalStateException(
                    "La tabla " + TABLA +
                    " ya existe. Se cancela la prueba para proteger sus datos."
            );
        }

        boolean creada = false;

        try {
            // CREATE
            jdbcTemplate.execute(
                    "CREATE TABLE " + TABLA +
                    " (ID NUMBER PRIMARY KEY, MENSAJE VARCHAR2(100) NOT NULL)"
            );
            creada = true;

            // INSERT
            int insertados = jdbcTemplate.update(
                    "INSERT INTO " + TABLA + " (ID, MENSAJE) VALUES (?, ?)",
                    1,
                    "PRUEBA CRUD ALERTAS ZAROR"
            );

            assertThat(insertados).isEqualTo(1);

            // SELECT
            String mensaje = jdbcTemplate.queryForObject(
                    "SELECT MENSAJE FROM " + TABLA + " WHERE ID = ?",
                    String.class,
                    1
            );

            assertThat(mensaje).isEqualTo("PRUEBA CRUD ALERTAS ZAROR");

            // UPDATE
            int actualizados = jdbcTemplate.update(
                    "UPDATE " + TABLA + " SET MENSAJE = ? WHERE ID = ?",
                    "REGISTRO ACTUALIZADO",
                    1
            );

            assertThat(actualizados).isEqualTo(1);

            String mensajeActualizado = jdbcTemplate.queryForObject(
                    "SELECT MENSAJE FROM " + TABLA + " WHERE ID = ?",
                    String.class,
                    1
            );

            assertThat(mensajeActualizado).isEqualTo("REGISTRO ACTUALIZADO");

            // DELETE
            int eliminados = jdbcTemplate.update(
                    "DELETE FROM " + TABLA + " WHERE ID = ?",
                    1
            );

            assertThat(eliminados).isEqualTo(1);

        } finally {
            // DROP: solo elimina la tabla creada por esta ejecución.
            if (creada) {
                jdbcTemplate.execute("DROP TABLE " + TABLA);
            }
        }
    }
}
