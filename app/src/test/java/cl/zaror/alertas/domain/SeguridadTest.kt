package cl.zaror.alertas.domain

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SeguridadTest {

    @Test
    fun claveCorrectaDebeVerificarse() {
        val sal = Seguridad.generarSal()
        val hash = Seguridad.hashear("abc123", sal)
        assertTrue(Seguridad.verificar("abc123", sal, hash))
    }

    @Test
    fun claveIncorrectaNoDebeVerificarse() {
        val sal = Seguridad.generarSal()
        val hash = Seguridad.hashear("abc123", sal)
        assertFalse(Seguridad.verificar("otraClave", sal, hash))
    }

    @Test
    fun hashNoDebeContenerLaClave() {
        val sal = Seguridad.generarSal()
        assertNotEquals("abc123", Seguridad.hashear("abc123", sal))
    }

    @Test
    fun salesDistintasDebenGenerarHashesDistintos() {
        val hash1 = Seguridad.hashear("abc123", Seguridad.generarSal())
        val hash2 = Seguridad.hashear("abc123", Seguridad.generarSal())
        assertNotEquals(hash1, hash2)
    }
}