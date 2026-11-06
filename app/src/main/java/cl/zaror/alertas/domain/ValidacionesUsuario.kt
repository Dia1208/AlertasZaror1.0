package cl.zaror.alertas.domain

object ValidacionesUsuario {

    private val formatoTelefono = Regex("^9\\d{8}$")

    fun validarNombre(nombre: String): String? {
        return if (nombre.trim().length < 3) {
            "El nombre debe tener al menos 3 caracteres"
        } else {
            null
        }
    }

    fun validarTelefono(telefono: String): String? {
        return when {
            telefono.isBlank() ->
                "El teléfono es obligatorio"

            !formatoTelefono.matches(telefono.trim()) ->
                "El teléfono debe tener 9 dígitos y comenzar con 9 (ej. 912345678)"

            else -> null
        }
    }

    fun validarDireccion(direccion: String): String? {
        return if (direccion.trim().length < 5) {
            "La dirección debe tener al menos 5 caracteres"
        } else {
            null
        }
    }

    fun validarClave(clave: String): String? {
        return when {
            clave.length < 8 ->
                "La contraseña debe tener al menos 8 caracteres"

            !clave.any { it.isLetter() } || !clave.any { it.isDigit() } ->
                "La contraseña debe incluir letras y números"

            else -> null
        }
    }

    fun validarConfirmacion(clave: String, confirmacion: String): String? {
        return if (clave != confirmacion) {
            "Las contraseñas no coinciden"
        } else {
            null
        }
    }
}