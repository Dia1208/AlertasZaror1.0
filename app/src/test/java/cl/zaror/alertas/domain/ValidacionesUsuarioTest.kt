package cl.zaror.alertas.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidacionesUsuarioTest {

    @Test
    fun nombreCortoDebeMostrarError() {
        assertEquals(
            "El nombre debe tener al menos 3 caracteres",
            ValidacionesUsuario.validarNombre("Al")
        )
    }

    @Test
    fun nombreValidoNoDebeTenerError() {
        assertNull(ValidacionesUsuario.validarNombre("Ana Rojas"))
    }

    @Test
    fun telefonoVacioDebeMostrarError() {
        assertEquals(
            "El teléfono es obligatorio",
            ValidacionesUsuario.validarTelefono("")
        )
    }

    @Test
    fun telefonoConFormatoIncorrectoDebeMostrarError() {
        assertEquals(
            "El teléfono debe tener 9 dígitos y comenzar con 9 (ej. 912345678)",
            ValidacionesUsuario.validarTelefono("12345")
        )
    }

    @Test
    fun telefonoValidoNoDebeTenerError() {
        assertNull(ValidacionesUsuario.validarTelefono("912345678"))
    }

    @Test
    fun direccionCortaDebeMostrarError() {
        assertEquals(
            "La dirección debe tener al menos 5 caracteres",
            ValidacionesUsuario.validarDireccion("Bl 3")
        )
    }

    @Test
    fun claveCortaDebeMostrarError() {
        assertEquals(
            "La contraseña debe tener al menos 6 caracteres",
            ValidacionesUsuario.validarClave("abc12")
        )
    }

    @Test
    fun confirmacionDistintaDebeMostrarError() {
        assertEquals(
            "Las contraseñas no coinciden",
            ValidacionesUsuario.validarConfirmacion("abc123", "abc124")
        )
    }

    @Test
    fun confirmacionIgualNoDebeTenerError() {
        assertNull(ValidacionesUsuario.validarConfirmacion("abc123", "abc123"))
    }
}