package cl.zaror.alertas.domain

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/** Manejo de contraseñas: nunca se guarda la clave, solo su hash con sal. */

object Seguridad {

    private const val ITERACIONES = 10_000
    private const val BITS_CLAVE = 256

    fun generarSal(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return aHex(bytes)
    }

    fun hashear(clave: String, sal: String): String {
        val especificacion = PBEKeySpec(clave.toCharArray(), desdeHex(sal), ITERACIONES, BITS_CLAVE)
        val fabrica = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        return aHex(fabrica.generateSecret(especificacion).encoded)
    }

    fun verificar(clave: String, sal: String, hashGuardado: String): Boolean {
        val calculado = hashear(clave, sal)
        return MessageDigest.isEqual(calculado.toByteArray(), hashGuardado.toByteArray())
    }

    private fun aHex(bytes: ByteArray): String =
        bytes.joinToString("") { "%02x".format(it.toInt() and 0xff) }

    private fun desdeHex(hex: String): ByteArray =
        ByteArray(hex.length / 2) { i -> hex.substring(i * 2, i * 2 + 2).toInt(16).toByte() }
}