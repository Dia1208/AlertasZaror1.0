package cl.zaror.alertas.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PermisosTest {

    @Test
    fun soloAdminVeTodasLasAlertas() {
        assertTrue(Permisos.puedeVerTodas(Rol.ADMINISTRADOR))
        assertFalse(Permisos.puedeVerTodas(Rol.VECINO))
    }

    @Test
    fun soloAdminMarcaAtendida() {
        assertTrue(Permisos.puedeMarcarAtendida(Rol.ADMINISTRADOR))
        assertFalse(Permisos.puedeMarcarAtendida(Rol.VECINO))
    }

    @Test
    fun soloAdminElimina() {
        assertTrue(Permisos.puedeEliminar(Rol.ADMINISTRADOR))
        assertFalse(Permisos.puedeEliminar(Rol.VECINO))
    }

    @Test
    fun vecinoCancelaSuAlertaActiva() {
        assertTrue(Permisos.puedeCancelar(Rol.VECINO, 5, 5, Permisos.ESTADO_ACTIVA))
    }

    @Test
    fun vecinoNoCancelaAlertaAjena() {
        assertFalse(Permisos.puedeCancelar(Rol.VECINO, 5, 6, Permisos.ESTADO_ACTIVA))
    }

    @Test
    fun vecinoNoCancelaAlertaYaAtendida() {
        assertFalse(Permisos.puedeCancelar(Rol.VECINO, 5, 5, Permisos.ESTADO_ATENDIDA))
    }

    @Test
    fun adminNoUsaCancelarDeVecino() {
        assertFalse(Permisos.puedeCancelar(Rol.ADMINISTRADOR, 1, 1, Permisos.ESTADO_ACTIVA))
    }

    @Test
    fun telefonoEnListaEsAdministrador() {
        assertEquals(Rol.ADMINISTRADOR, AdminsFijos.rolPara("912345678", setOf("912345678")))
    }

    @Test
    fun telefonoFueraDeListaEsVecino() {
        assertEquals(Rol.VECINO, AdminsFijos.rolPara("987654321", setOf("912345678")))
    }

    @Test
    fun listaVaciaDejaATodosComoVecinos() {
        assertEquals(Rol.VECINO, AdminsFijos.rolPara("912345678", emptySet()))
    }
}
