package cl.zaror.alertas.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidacionesFallaTest {

    @Test
    fun tipoFallaVacioDebeMostrarError() {
        assertEquals(
            "Debes seleccionar el tipo de falla",
            ValidacionesFalla.validarTipoFalla("")
        )
    }

    @Test
    fun tipoFallaSeleccionadoDebeSerValido() {
        assertNull(ValidacionesFalla.validarTipoFalla("Sirena sin sonido"))
    }

    @Test
    fun equipoVacioDebeMostrarError() {
        assertEquals(
            "El equipo o sector debe tener al menos 3 caracteres",
            ValidacionesFalla.validarEquipo("")
        )
    }

    @Test
    fun equipoConTresCaracteresDebeSerValido() {
        assertNull(ValidacionesFalla.validarEquipo("Bloque 3"))
    }
}
