package cl.zaror.alertas.domain

import org.junit.Assert.assertEquals
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

    @Test
    fun hashAntiguoVersion1SigueVerificandose() {
        val sal = Seguridad.generarSal()
        val hashViejo = Seguridad.hashear("abc123", sal, version = 1)
        assertTrue(Seguridad.verificar("abc123", sal, hashViejo, version = 1))
    }

    @Test
    fun hashDeVersionesDistintasNoCoincide() {
        val sal = Seguridad.generarSal()
        val v1 = Seguridad.hashear("abc123", sal, version = 1)
        val v2 = Seguridad.hashear("abc123", sal, version = 2)
        assertNotEquals(v1, v2)
        assertFalse(Seguridad.verificar("abc123", sal, v1, version = 2))
    }

    @Test
    fun cuentaAntiguaNecesitaActualizarse() {
        assertTrue(Seguridad.necesitaActualizar(1))
        assertFalse(Seguridad.necesitaActualizar(Seguridad.VERSION_ACTUAL))
    }

    @Test
    fun versionDesconocidaDebeFallar() {
        val resultado = runCatching { Seguridad.hashear("abc123", Seguridad.generarSal(), version = 99) }
        assertTrue(resultado.isFailure)
        assertEquals(IllegalArgumentException::class, resultado.exceptionOrNull()!!::class)
    }
}
