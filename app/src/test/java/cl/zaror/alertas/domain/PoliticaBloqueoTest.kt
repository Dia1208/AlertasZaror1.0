package cl.zaror.alertas.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PoliticaBloqueoTest {

    private val ahora = 1_000_000L

    @Test
    fun primerFalloSoloSumaUnIntento() {
        val estado = PoliticaBloqueo.tras(0, ahora)
        assertEquals(1, estado.intentosFallidos)
        assertEquals(0L, estado.bloqueadoHasta)
    }

    @Test
    fun alLlegarAlMaximoSeBloqueaYReiniciaElConteo() {
        val estado = PoliticaBloqueo.tras(PoliticaBloqueo.MAX_INTENTOS - 1, ahora)
        assertEquals(0, estado.intentosFallidos)
        assertEquals(ahora + PoliticaBloqueo.DURACION_MS, estado.bloqueadoHasta)
    }

    @Test
    fun estaBloqueadoMientrasNoPaseElTiempo() {
        assertTrue(PoliticaBloqueo.estaBloqueado(ahora + 1000, ahora))
        assertFalse(PoliticaBloqueo.estaBloqueado(ahora, ahora))
        assertFalse(PoliticaBloqueo.estaBloqueado(0L, ahora))
    }

    @Test
    fun segundosRestantesSeRedondeaHaciaArriba() {
        assertEquals(2L, PoliticaBloqueo.segundosRestantes(ahora + 1001, ahora))
        assertEquals(0L, PoliticaBloqueo.segundosRestantes(ahora - 5, ahora))
    }
}
