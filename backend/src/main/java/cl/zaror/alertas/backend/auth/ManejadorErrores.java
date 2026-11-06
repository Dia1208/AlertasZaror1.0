package cl.zaror.alertas.backend.auth;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cl.zaror.alertas.backend.auth.AuthExcepciones.CredencialesInvalidasException;
import cl.zaror.alertas.backend.auth.AuthExcepciones.CuentaBloqueadaException;
import cl.zaror.alertas.backend.auth.AuthExcepciones.CuentaNoAprobadaException;
import cl.zaror.alertas.backend.auth.AuthExcepciones.TelefonoYaRegistradoException;
import cl.zaror.alertas.backend.auth.AuthExcepciones.ValidacionException;

@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(ValidacionException.class)
    public ResponseEntity<Map<String, Object>> validacion(ValidacionException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Datos inválidos", "detalle", e.errores()));
    }

    @ExceptionHandler(TelefonoYaRegistradoException.class)
    public ResponseEntity<Map<String, Object>> telefonoDuplicado(TelefonoYaRegistradoException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, Object>> credenciales(CredencialesInvalidasException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(CuentaBloqueadaException.class)
    public ResponseEntity<Map<String, Object>> bloqueada(CuentaBloqueadaException e) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("Retry-After", String.valueOf(e.segundos()))
                .body(Map.of(
                        "error", "Demasiados intentos fallidos. Intenta de nuevo más tarde",
                        "segundosRestantes", e.segundos()));
    }

    @ExceptionHandler(CuentaNoAprobadaException.class)
    public ResponseEntity<Map<String, Object>> noAprobada(CuentaNoAprobadaException e) {
        String mensaje = Usuario.ESTADO_PENDIENTE.equals(e.estado())
                ? "Tu cuenta está pendiente de aprobación por un administrador"
                : "Tu cuenta no fue aprobada. Contacta a la directiva";
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", mensaje, "estado", e.estado()));
    }
}
