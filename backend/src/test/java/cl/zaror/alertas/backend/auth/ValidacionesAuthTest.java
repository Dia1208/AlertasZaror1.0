package cl.zaror.alertas.backend.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ValidacionesAuthTest {

    @Test
    void nombreCortoDebeTenerError() {
        assertThat(ValidacionesAuth.validarNombre("Al")).isEqualTo("El nombre debe tener al menos 3 caracteres");
    }

    @Test
    void nombreNuloDebeTenerError() {
        assertThat(ValidacionesAuth.validarNombre(null)).isNotNull();
    }

    @Test
    void nombreValidoNoDebeTenerError() {
        assertThat(ValidacionesAuth.validarNombre("Marta Pérez")).isNull();
    }

    @Test
    void telefonoConFormatoIncorrectoDebeTenerError() {
        assertThat(ValidacionesAuth.validarTelefono("812345678")).isNotNull();
        assertThat(ValidacionesAuth.validarTelefono("91234")).isNotNull();
        assertThat(ValidacionesAuth.validarTelefono("")).isEqualTo("El teléfono es obligatorio");
    }

    @Test
    void telefonoValidoNoDebeTenerError() {
        assertThat(ValidacionesAuth.validarTelefono("912345678")).isNull();
    }

    @Test
    void direccionCortaDebeTenerError() {
        assertThat(ValidacionesAuth.validarDireccion("Casa")).isNotNull();
        assertThat(ValidacionesAuth.validarDireccion("Calle Ejemplo 101")).isNull();
    }

    @Test
    void claveCortaDebeTenerError() {
        assertThat(ValidacionesAuth.validarClave("abc123")).isEqualTo("La contraseña debe tener al menos 8 caracteres");
    }

    @Test
    void claveSinNumerosOSinLetrasDebeTenerError() {
        assertThat(ValidacionesAuth.validarClave("abcdefgh")).isEqualTo("La contraseña debe incluir letras y números");
        assertThat(ValidacionesAuth.validarClave("12345678")).isEqualTo("La contraseña debe incluir letras y números");
    }

    @Test
    void claveMuyLargaDebeTenerError() {
        assertThat(ValidacionesAuth.validarClave("a1" + "x".repeat(80))).isNotNull();
    }

    @Test
    void claveValidaNoDebeTenerError() {
        assertThat(ValidacionesAuth.validarClave("clave1234")).isNull();
    }

    @Test
    void registroValidoNoDebeTenerErrores() {
        var solicitud = new RegistroRequest("Vecino Uno", "912345678", "Calle Ejemplo 101", "clave1234");
        assertThat(ValidacionesAuth.validarRegistro(solicitud)).isEmpty();
    }

    @Test
    void registroInvalidoDebeListarCadaCampo() {
        var solicitud = new RegistroRequest("", "1", null, "x");
        assertThat(ValidacionesAuth.validarRegistro(solicitud))
                .containsOnlyKeys("nombre", "telefono", "direccion", "clave");
    }
}
