package cl.zaror.alertas.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidacionesTest {

    @Test
    fun tipoVacioDebeMostrarError() {
        assertEquals(
            "Debes seleccionar un tipo de alerta",
            Validaciones.validarTipo("")
        )
    }

    @Test
    fun tipoSeleccionadoDebeSerValido() {
        assertNull(Validaciones.validarTipo("Emergencia"))
    }

    @Test
    fun descripcionVaciaDebeMostrarError() {
        assertEquals(
            "La descripción es obligatoria",
            Validaciones.validarDescripcion("")
        )
    }

    @Test
    fun descripcionMuyCortaDebeMostrarError() {
        assertEquals(
            "La descripción debe tener al menos 10 caracteres",
            Validaciones.validarDescripcion("Hay fuego")
        )
    }

    @Test
    fun descripcionDeDiezCaracteresDebeSerValida() {
        assertNull(Validaciones.validarDescripcion("Hay peligro"))
    }

    @Test
    fun descripcionMuyLargaDebeMostrarError() {
        assertEquals(
            "La descripción no puede superar 240 caracteres",
            Validaciones.validarDescripcion("A".repeat(241))
        )
    }

    @Test
    fun ubicacionVaciaDebeMostrarError() {
        assertEquals(
            "La ubicación es obligatoria",
            Validaciones.validarUbicacion("")
        )
    }

    @Test
    fun ubicacionConContenidoDebeSerValida() {
        assertNull(Validaciones.validarUbicacion("Block 3, casa 15"))
    }

    @Test
    fun reportanteMuyCortoDebeMostrarError() {
        assertEquals(
            "El nombre debe tener al menos 3 caracteres",
            Validaciones.validarReportante("Al")
        )
    }

    @Test
    fun reportanteValidoDebeSerAceptado() {
        assertNull(Validaciones.validarReportante("Ana"))
    }

    @Test
    fun reportanteConSoloEspaciosDebeMostrarError() {
        assertEquals(
            "El nombre debe tener al menos 3 caracteres",
            Validaciones.validarReportante("   ")
        )
    }
}
