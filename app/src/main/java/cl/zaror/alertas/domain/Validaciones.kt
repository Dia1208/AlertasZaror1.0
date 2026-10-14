package cl.zaror.alertas.domain

object Validaciones {

    fun validarTipo(tipo: String): String? {
        return if (tipo.isBlank()) {
            "Debes seleccionar un tipo de alerta"
        } else {
            null
        }
    }

    fun validarDescripcion(descripcion: String): String? {
        return when {
            descripcion.isBlank() ->
                "La descripción es obligatoria"

            descripcion.length < 10 ->
                "La descripción debe tener al menos 10 caracteres"

            descripcion.length > 240 ->
                "La descripción no puede superar 240 caracteres"

            else -> null
        }
    }

    fun validarUbicacion(ubicacion: String): String? {
        return if (ubicacion.isBlank()) {
            "La ubicación es obligatoria"
        } else {
            null
        }
    }

    fun validarReportante(reportante: String): String? {
        return if (reportante.trim().length < 3) {
            "El nombre debe tener al menos 3 caracteres"
        } else {
            null
        }
    }
}