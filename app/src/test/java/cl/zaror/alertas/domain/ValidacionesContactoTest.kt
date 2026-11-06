package cl.zaror.alertas.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidacionesContactoTest {

    @Test
    fun nombreCortoDebeMostrarError() {
        assertEquals(
            "El nombre debe tener al menos 3 caracteres",
            ValidacionesContacto.validarNombre("Al")
        )
    }

    @Test
    fun nombreValidoNoDebeTenerError() {
        assertNull(ValidacionesContacto.validarNombre("Marta Pérez"))
    }

    @Test
    fun telefonoInvalidoDebeMostrarError() {
        assertNotNull(ValidacionesContacto.validarTelefono("12345"))
    }

    @Test
    fun telefonoValidoNoDebeTenerError() {
        assertNull(ValidacionesContacto.validarTelefono("912345678"))
    }

    @Test
    fun telefonoPropioDebeMostrarError() {
        assertEquals(
            "No puedes agregar tu propio número",
            ValidacionesContacto.validarNoEsPropio("912345678", "912345678")
        )
    }

    @Test
    fun telefonoDeOtraPersonaNoDebeTenerError() {
        assertNull(ValidacionesContacto.validarNoEsPropio("987654321", "912345678"))
    }
}
