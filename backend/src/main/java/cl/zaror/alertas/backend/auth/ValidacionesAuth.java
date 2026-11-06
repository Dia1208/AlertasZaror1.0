package cl.zaror.alertas.backend.auth;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/** Mismas reglas que la app Android, validadas también en el servidor. */
public final class ValidacionesAuth {

    /** BCrypt solo usa los primeros 72 bytes de la clave. */
    public static final int MAX_BYTES_CLAVE = 72;

    private static final Pattern TELEFONO = Pattern.compile("^9\\d{8}$");

    private ValidacionesAuth() {
    }

    public static String validarNombre(String nombre) {
        String valor = nombre == null ? "" : nombre.trim();
        if (valor.length() < 3) {
            return "El nombre debe tener al menos 3 caracteres";
        }
        if (valor.length() > 100) {
            return "El nombre no puede superar 100 caracteres";
        }
        return null;
    }

    public static String validarTelefono(String telefono) {
        String valor = telefono == null ? "" : telefono.trim();
        if (valor.isEmpty()) {
            return "El teléfono es obligatorio";
        }
        if (!TELEFONO.matcher(valor).matches()) {
            return "El teléfono debe tener 9 dígitos y comenzar con 9 (ej. 912345678)";
        }
        return null;
    }

    public static String validarDireccion(String direccion) {
        String valor = direccion == null ? "" : direccion.trim();
        if (valor.length() < 5) {
            return "La dirección debe tener al menos 5 caracteres";
        }
        if (valor.length() > 150) {
            return "La dirección no puede superar 150 caracteres";
        }
        return null;
    }

    public static String validarClave(String clave) {
        String valor = clave == null ? "" : clave;
        if (valor.length() < 8) {
            return "La contraseña debe tener al menos 8 caracteres";
        }
        if (valor.getBytes(StandardCharsets.UTF_8).length > MAX_BYTES_CLAVE) {
            return "La contraseña no puede superar 72 caracteres";
        }
        if (valor.chars().noneMatch(Character::isLetter) || valor.chars().noneMatch(Character::isDigit)) {
            return "La contraseña debe incluir letras y números";
        }
        return null;
    }

    /** Devuelve campo -> mensaje con los errores encontrados; vacío si todo está bien. */
    public static Map<String, String> validarRegistro(RegistroRequest solicitud) {
        Map<String, String> errores = new LinkedHashMap<>();
        agregar(errores, "nombre", validarNombre(solicitud.nombre()));
        agregar(errores, "telefono", validarTelefono(solicitud.telefono()));
        agregar(errores, "direccion", validarDireccion(solicitud.direccion()));
        agregar(errores, "clave", validarClave(solicitud.clave()));
        return errores;
    }

    private static void agregar(Map<String, String> errores, String campo, String mensaje) {
        if (mensaje != null) {
            errores.put(campo, mensaje);
        }
    }
}
