package cl.zaror.alertas.domain

object ValidacionesFalla {

    fun validarTipoFalla(tipoFalla: String): String? {
        return if (tipoFalla.isBlank()) {
            "Debes seleccionar el tipo de falla"
        } else {
            null
        }
    }

    fun validarEquipo(equipo: String): String? {
        return if (equipo.trim().length < 3) {
            "El equipo o sector debe tener al menos 3 caracteres"
        } else {
            null
        }
    }
}
