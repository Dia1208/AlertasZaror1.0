package cl.zaror.alertas.backend.auth;

import java.util.Map;

/** Errores de negocio de registro e inicio de sesión. */
public final class AuthExcepciones {

    private AuthExcepciones() {
    }

    public static class ValidacionException extends RuntimeException {
        private final transient Map<String, String> errores;

        public ValidacionException(Map<String, String> errores) {
            super("Datos inválidos");
            this.errores = errores;
        }

        public Map<String, String> errores() {
            return errores;
        }
    }

    public static class TelefonoYaRegistradoException extends RuntimeException {
        public TelefonoYaRegistradoException() {
            super("Ese teléfono ya está registrado");
        }
    }

    /** Teléfono o clave incorrectos. No se distingue cuál, a propósito. */
    public static class CredencialesInvalidasException extends RuntimeException {
        public CredencialesInvalidasException() {
            super("Teléfono o contraseña incorrectos");
        }
    }

    public static class CuentaBloqueadaException extends RuntimeException {
        private final long segundos;

        public CuentaBloqueadaException(long segundos) {
            super("Demasiados intentos fallidos");
            this.segundos = segundos;
        }

        public long segundos() {
            return segundos;
        }
    }

    public static class CuentaNoAprobadaException extends RuntimeException {
        private final String estado;

        public CuentaNoAprobadaException(String estado) {
            super("Cuenta no aprobada");
            this.estado = estado;
        }

        public String estado() {
            return estado;
        }
    }
}
