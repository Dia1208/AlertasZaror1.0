package cl.zaror.alertas.backend.salud;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SaludControllerTest {

    private final SaludController controlador = new SaludController();

    @Test
    void saludDebeIndicarEstadoOk() {
        assertThat(controlador.salud()).containsEntry("estado", "OK");
    }

    @Test
    void saludDebeIdentificarAlServicio() {
        assertThat(controlador.salud()).containsEntry("servicio", "alertas-backend");
    }
}
