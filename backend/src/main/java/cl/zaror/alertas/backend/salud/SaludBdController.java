package cl.zaror.alertas.backend.salud;

import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Comprueba que el servidor logra hablar con la base de datos Oracle. */
@RestController
@RequestMapping("/api")
public class SaludBdController {

    private final JdbcTemplate jdbcTemplate;

    public SaludBdController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/salud/bd")
    public ResponseEntity<Map<String, String>> saludBd() {
        try {
            jdbcTemplate.queryForObject("SELECT 1 FROM DUAL", Integer.class);
            return ResponseEntity.ok(Map.of("estado", "OK", "base", "oracle"));
        } catch (DataAccessException e) {
            // No se devuelve el detalle del error para no filtrar datos de la conexion.
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("estado", "ERROR", "base", "oracle"));
        }
    }
}
