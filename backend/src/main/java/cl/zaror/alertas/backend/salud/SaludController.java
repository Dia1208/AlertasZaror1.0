package cl.zaror.alertas.backend.salud;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SaludController {

    @GetMapping("/salud")
    public Map<String, String> salud() {
        return Map.of(
                "estado", "OK",
                "servicio", "alertas-backend"
        );
    }
}