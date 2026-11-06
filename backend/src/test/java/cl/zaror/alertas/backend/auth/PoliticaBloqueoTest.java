package cl.zaror.alertas.backend.auth;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class PoliticaBloqueoTest {

    private static final Instant AHORA = Instant.parse("2026-11-06T12:00:00Z");

    @Test
    void primerFalloSoloSumaUnIntento() {
        var estado = PoliticaBloqueo.tras(0, AHORA);
        assertThat(estado.intentosFallidos()).isEqualTo(1);
        assertThat(estado.bloqueadoHasta()).isNull();
    }

    @Test
    void alLlegarAlMaximoSeBloqueaYReiniciaElConteo() {
        var estado = PoliticaBloqueo.tras(PoliticaBloqueo.MAX_INTENTOS - 1, AHORA);
        assertThat(estado.intentosFallidos()).isZero();
        assertThat(estado.bloqueadoHasta()).isEqualTo(AHORA.plus(PoliticaBloqueo.DURACION));
    }

    @Test
    void estaBloqueadoSoloAntesDelInstanteLimite() {
        assertThat(PoliticaBloqueo.estaBloqueado(AHORA.plusSeconds(1), AHORA)).isTrue();
        assertThat(PoliticaBloqueo.estaBloqueado(AHORA, AHORA)).isFalse();
        assertThat(PoliticaBloqueo.estaBloqueado(null, AHORA)).isFalse();
    }

    @Test
    void segundosRestantesSeRedondeaHaciaArriba() {
        assertThat(PoliticaBloqueo.segundosRestantes(AHORA.plusMillis(1001), AHORA)).isEqualTo(2);
        assertThat(PoliticaBloqueo.segundosRestantes(AHORA.minusSeconds(5), AHORA)).isZero();
        assertThat(PoliticaBloqueo.segundosRestantes(null, AHORA)).isZero();
    }
}
