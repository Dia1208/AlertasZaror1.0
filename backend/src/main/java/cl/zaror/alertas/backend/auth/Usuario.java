package cl.zaror.alertas.backend.auth;

import java.time.Instant;

public record Usuario(
        long id,
        String nombre,
        String telefono,
        String direccion,
        String claveHash,
        String rol,
        String estado,
        int intentosFallidos,
        Instant bloqueadoHasta) {

    public static final String ROL_VECINO = "VECINO";
    public static final String ESTADO_PENDIENTE = "PENDIENTE";
    public static final String ESTADO_APROBADO = "APROBADO";
}
