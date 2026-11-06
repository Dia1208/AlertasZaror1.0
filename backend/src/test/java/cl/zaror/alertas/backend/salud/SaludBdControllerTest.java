package cl.zaror.alertas.backend.salud;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;

class SaludBdControllerTest {

    private static final String CONSULTA = "SELECT 1 FROM DUAL";

    @Test
    void conBaseDisponibleDebeResponderOk() {
        JdbcTemplate jdbc = mock(JdbcTemplate.class);
        when(jdbc.queryForObject(CONSULTA, Integer.class)).thenReturn(1);

        var respuesta = new SaludBdController(jdbc).saludBd();

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).containsEntry("estado", "OK");
    }

    @Test
    void sinConexionDebeResponderServicioNoDisponible() {
        JdbcTemplate jdbc = mock(JdbcTemplate.class);
        when(jdbc.queryForObject(CONSULTA, Integer.class))
                .thenThrow(new CannotGetJdbcConnectionException("sin conexion"));

        var respuesta = new SaludBdController(jdbc).saludBd();

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(respuesta.getBody()).containsEntry("estado", "ERROR");
    }

    @Test
    void elErrorNoDebeFiltrarDetallesDeLaConexion() {
        JdbcTemplate jdbc = mock(JdbcTemplate.class);
        when(jdbc.queryForObject(CONSULTA, Integer.class))
                .thenThrow(new CannotGetJdbcConnectionException("usuario=ADMIN host=secreto"));

        var respuesta = new SaludBdController(jdbc).saludBd();

        assertThat(respuesta.getBody().toString()).doesNotContain("ADMIN").doesNotContain("secreto");
    }
}
