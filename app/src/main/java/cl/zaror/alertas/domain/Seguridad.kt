package cl.zaror.alertas.domain

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Manejo de contraseñas: nunca se guarda la clave, solo su hash con sal.
 *
 * Cada usuario guarda la versión del algoritmo con que se calculó su hash,
 * así las cuentas antiguas siguen funcionando y se actualizan al iniciar sesión.
 *  - Versión 1: PBKDF2-HMAC-SHA1, 10.000 iteraciones (cuentas antiguas).
 *  - Versión 2: PBKDF2-HMAC-SHA256, 600.000 iteraciones (recomendación OWASP).
 */
object Seguridad {

    const val VERSION_ACTUAL = 2

    private const val BITS_CLAVE = 256

    private fun algoritmo(version: Int): String = when (version) {
        1 -> "PBKDF2WithHmacSHA1"
        2 -> "PBKDF2WithHmacSHA256"
        else -> throw IllegalArgumentException("Versión de hash desconocida: $version")
    }

    private fun iteraciones(version: Int): Int = when (version) {
        1 -> 10_000
        2 -> 600_000
        else -> throw IllegalArgumentException("Versión de hash desconocida: $version")
    }

    fun generarSal(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return aHex(bytes)
    }

    fun hashear(clave: String, sal: String, version: Int = VERSION_ACTUAL): String {
        val especificacion = PBEKeySpec(
            clave.toCharArray(),
            desdeHex(sal),
            iteraciones(version),
            BITS_CLAVE
        )
        try {
            val fabrica = SecretKeyFactory.getInstance(algoritmo(version))
            return aHex(fabrica.generateSecret(especificacion).encoded)
        } finally {
            especificacion.clearPassword()
        }
    }

    fun verificar(
        clave: String,
        sal: String,
        hashGuardado: String,
        version: Int = VERSION_ACTUAL
    ): Boolean {
        val calculado = hashear(clave, sal, version)
        return MessageDigest.isEqual(calculado.toByteArray(), hashGuardado.toByteArray())
    }

    /** True si el hash guardado usa un algoritmo más débil que el actual. */
    fun necesitaActualizar(version: Int): Boolean = version < VERSION_ACTUAL

    private fun aHex(bytes: ByteArray): String =
        bytes.joinToString("") { "%02x".format(it.toInt() and 0xff) }

    private fun desdeHex(hex: String): ByteArray =
        ByteArray(hex.length / 2) { i -> hex.substring(i * 2, i * 2 + 2).toInt(16).toByte() }
}
