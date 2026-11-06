package cl.zaror.alertas.domain

object ValidacionesContacto {

    fun validarNombre(nombre: String): String? {
        return if (nombre.trim().length < 3) {
            "El nombre debe tener al menos 3 caracteres"
        } else {
            null
        }
    }

    fun validarTelefono(telefono: String): String? {
        return ValidacionesUsuario.validarTelefono(telefono)
    }

    /** Un usuario no puede agregarse a sí mismo como contacto de confianza. */
    fun validarNoEsPropio(telefono: String, telefonoPropio: String): String? {
        return if (telefono.trim() == telefonoPropio.trim()) {
            "No puedes agregar tu propio número"
        } else {
            null
        }
    }
}
