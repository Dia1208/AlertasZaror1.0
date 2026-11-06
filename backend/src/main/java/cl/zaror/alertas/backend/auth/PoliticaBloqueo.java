package cl.zaror.alertas.backend.auth;

import java.time.Duration;
import java.time.Instant;

/** Bloqueo temporal del login tras varios intentos fallidos. */
public final class PoliticaBloqueo {

    public static final int MAX_INTENTOS = 5;
    public static final Duration DURACION = Duration.ofMinutes(5);

    public record Estado(int intentosFallidos, Instant bloqueadoHasta) {
    }

    private PoliticaBloqueo() {
    }

    public static boolean estaBloqueado(Instant bloqueadoHasta, Instant ahora) {
        return bloqueadoHasta != null && ahora.isBefore(bloqueadoHasta);
    }

    public static long segundosRestantes(Instant bloqueadoHasta, Instant ahora) {
        if (bloqueadoHasta == null || !ahora.isBefore(bloqueadoHasta)) {
            return 0;
        }
        long milisegundos = Duration.between(ahora, bloqueadoHasta).toMillis();
        return (milisegundos + 999) / 1000;
    }

    /** Estado nuevo tras un intento fallido. Al llegar al máximo se bloquea y se reinicia el conteo. */
    public static Estado tras(int intentosActuales, Instant ahora) {
        int nuevos = intentosActuales + 1;
        if (nuevos >= MAX_INTENTOS) {
            return new Estado(0, ahora.plus(DURACION));
        }
        return new Estado(nuevos, null);
    }
}
