package cl.zaror.alertas.backend.auth;

public record TokenResponse(String token, String tipo, long expiraEnSegundos) {
}
